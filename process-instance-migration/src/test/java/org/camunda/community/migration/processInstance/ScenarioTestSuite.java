package org.camunda.community.migration.processInstance;

import static io.camunda.zeebe.protocol.Protocol.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.camunda.community.migration.processInstance.TestUtil.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.model.ProcessDefinition;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivateJobsResponse;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.filters.RecordStream;
import io.camunda.zeebe.process.test.filters.StreamFilter;
import io.camunda.zeebe.process.test.inspections.model.InspectedProcessInstance;
import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.RejectionType;
import io.camunda.zeebe.protocol.record.intent.MessageStartEventSubscriptionIntent;
import io.camunda.zeebe.protocol.record.value.VariableRecordValue;
import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import org.awaitility.Awaitility;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.community.migration.processInstance.ProcessConstants.JobType;
import org.camunda.community.migration.processInstance.dto.task.JobDefinitionSelectionTaskData;
import org.camunda.community.migration.processInstance.dto.task.UserTask;
import org.camunda.community.migration.processInstance.service.Camunda8Service;
import org.camunda.community.migration.processInstance.service.MigrationTaskService;
import org.camunda.community.migration.processInstance.variables.ProcessInstanceMigrationVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScenarioTestSuite {
  public static final Duration TIMEOUT = Duration.ofMinutes(5);
  public static final String PROCESS_INSTANCE_STATE = "INTERNALLY_TERMINATED";

  private static final Logger LOG = LoggerFactory.getLogger(ScenarioTestSuite.class);
  private final MigrationTestProcessDefinitionInput pdInput;
  private final MigrationTestProcessInstanceInput piInput;
  private final CamundaOperateClient operateClient;
  private final Camunda8Service camunda8Service;
  private final MigrationTaskService taskService;
  private final ZeebeClient zeebeClient;
  private final ZeebeTestEngine zeebeTestEngine;
  private final ZeebeJobClient zeebeJobClient;

  public ScenarioTestSuite(
      MigrationTestProcessDefinitionInput pdInput,
      MigrationTestProcessInstanceInput piInput,
      CamundaOperateClient operateClient,
      Camunda8Service camunda8Service,
      MigrationTaskService taskService,
      ZeebeClient zeebeClient,
      ZeebeTestEngine zeebeTestEngine,
      ZeebeJobClient zeebeJobClient) {
    this.pdInput = pdInput;
    this.piInput = piInput;
    this.operateClient = operateClient;
    this.camunda8Service = camunda8Service;
    this.taskService = taskService;
    this.zeebeClient = zeebeClient;
    this.zeebeTestEngine = zeebeTestEngine;
    this.zeebeJobClient = zeebeJobClient;
  }

  private static Function<Record<VariableRecordValue>, String> variableName() {
    return r -> r.getValue().getName();
  }

  private static ToLongFunction<Record<VariableRecordValue>> position() {
    return Record::getPosition;
  }

  private static Function<Optional<Record<VariableRecordValue>>, JsonNode> variableValue(
      ObjectMapper objectMapper) {
    return r -> {
      try {
        return objectMapper.readTree(r.get().getValue().getValue());
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    };
  }

  private static void waitForIdleState(ProcessInstance processInstance) {
    long activeJobs = jobQuery().active().processInstanceId(processInstance.getId()).count();
    while (activeJobs > 0) {
      execute(jobQuery().active().processInstanceId(processInstance.getId()).list().get(0));
      activeJobs = jobQuery().active().processInstanceId(processInstance.getId()).count();
    }
  }

  private Map<String, JsonNode> getVariableValues(long processInstanceKey) {
    // find process instances
    List<Long> processInstanceKeys =
        StreamFilter.processInstance(RecordStream.of(zeebeTestEngine.getRecordStreamSource()))
            .withParentProcessInstanceKey(processInstanceKey)
            .stream()
            .map(r -> r.getValue().getProcessInstanceKey())
            .collect(Collectors.toList());
    processInstanceKeys.add(processInstanceKey);
    ObjectMapper objectMapper = new ObjectMapper();
    return StreamFilter.variable(RecordStream.of(zeebeTestEngine.getRecordStreamSource())).stream()
        .filter(r -> processInstanceKeys.contains(r.getValue().getProcessInstanceKey()))
        .collect(
            Collectors.groupingBy(
                variableName(),
                Collectors.collectingAndThen(
                    Collectors.maxBy(Comparator.comparingLong(position())),
                    variableValue(objectMapper))));
  }

  private JsonNode getVariableValue(long processInstanceKey, String variableName) {
    return Objects.requireNonNull(getVariableValues(processInstanceKey).get(variableName));
  }

  private InspectedProcessInstance getProcessInstanceKeysForCorrelatedMessage(
      PublishMessageResponse event) {
    RecordStream recordStream = RecordStream.of(zeebeTestEngine.getRecordStreamSource());
    return new InspectedProcessInstance(
        StreamFilter.messageStartEventSubscription(recordStream)
            .withMessageKey(event.getMessageKey())
            .withRejectionType(RejectionType.NULL_VAL)
            .withIntent(MessageStartEventSubscriptionIntent.CORRELATED)
            .stream()
            .map(record -> record.getValue().getProcessInstanceKey())
            .findFirst()
            .get());
  }

  private UserTask getTask() throws InterruptedException, TimeoutException {
    zeebeTestEngine.waitForIdleState(TIMEOUT);
    ActivateJobsResponse response =
        zeebeClient
            .newActivateJobsCommand()
            .jobType(USER_TASK_JOB_TYPE)
            .maxJobsToActivate(1)
            .send()
            .join();
    assertThat(response.getJobs()).hasSize(1);
    ActivatedJob job = response.getJobs().get(0);
    zeebeJobClient.userTask(job, job.getVariablesAsType(ProcessInstanceMigrationVariables.class));
    UserTask task = taskService.getTask(job.getKey());
    assertThat(task).isNotNull();
    return task;
  }

  public void testAdHocMigration() throws Exception {
    TestUtil.zeebeClient = zeebeClient;
    TestUtil.zeebeTestEngine = zeebeTestEngine;
    deployProcessToZeebe("process-instance-migration.bpmn");
    deployProcessToZeebe("single-process-instance-migration.bpmn");
    when(operateClient.searchProcessDefinitions(any()))
        .thenReturn(Collections.singletonList(new ProcessDefinition()));
    when(operateClient.getProcessDefinitionXml(any()))
        .thenReturn(
            new String(
                Files.readAllBytes(
                    new File(
                            ScenarioTestSuite.class
                                .getClassLoader()
                                .getResource(pdInput.getC8DiagramResourceName())
                                .toURI())
                        .toPath())));
    deployCamunda7Process(pdInput.getC7DiagramResourceName());
    ProcessInstance c7Pi =
        runtimeService()
            .startProcessInstanceByKey(pdInput.getBpmnProcessId(), piInput.getVariables());
    BpmnAwareTests.assertThat(c7Pi);
    LOG.info("Started C7 process instance {}", c7Pi.getId());
    piInput
        .getProcessSteps()
        .forEach(
            c -> {
              waitForIdleState(c7Pi);
              c.accept(c7Pi);
              waitForIdleState(c7Pi);
            });
    List<String> elementIds = runtimeService().getActiveActivityIds(c7Pi.getId());
    LOG.info("Advanced to activities {}", elementIds);
    // setup c8 engine
    deployProcessToZeebe(pdInput.getC8DiagramResourceName());
    // start migration process
    PublishMessageResponse startMessage =
        camunda8Service.startProcessInstanceMigration(pdInput.getBpmnProcessId());
    zeebeTestEngine.waitForIdleState(TIMEOUT);
    InspectedProcessInstance processInstance =
        getProcessInstanceKeysForCorrelatedMessage(startMessage);
    completeJob(JobType.CAMUNDA7_VERSIONED_INFORMATION, extractVersionInformation());
    completeJob(JobType.CAMUNDA8_CHECK_PROCESS_DEFINITION, checkProcessDefinition());
    completeJob(JobType.CAMUNDA7_SUSPEND, suspendProcessDefinition());
    // select the started c7 instance
    UserTask selectProcessInstancesToMigrate = getTask();
    taskService.complete(
        selectProcessInstancesToMigrate.getKey(),
        result(
            Collections.singletonList(
                v ->
                    v.setCamunda7ProcessInstanceIds(
                        Collections.singletonList(c7Pi.getProcessInstanceId())))));
    // wait until migration is done
    migrateProcessInstance();
    completeJob(JobType.CAMUNDA7_CONTINUE, continueProcessDefinition());
    // assert that the c7 instance was terminated
    HistoricProcessInstance historicProcessInstance =
        historyService()
            .createHistoricProcessInstanceQuery()
            .processInstanceId(c7Pi.getId())
            .singleResult();
    assertThat(historicProcessInstance.getState()).isEqualTo(PROCESS_INSTANCE_STATE);
    // find created c8 instance
    long camunda8ProcessInstanceKey =
        getVariableValue(processInstance.getProcessInstanceKey(), "camunda8ProcessInstanceKey")
            .asLong();
    LOG.info("Created C8 process instance {}", camunda8ProcessInstanceKey);
    // assert that the c8 process instance key was set as variable before
    HistoricVariableInstance c8PiKeyInC7 =
        historyService()
            .createHistoricVariableInstanceQuery()
            .processInstanceId(c7Pi.getId())
            .variableName("camunda8ProcessInstanceKey")
            .singleResult();
    assertThat(c8PiKeyInC7.getValue()).isEqualTo(camunda8ProcessInstanceKey);
    // assert that the c8 process instance is waiting where the c7 process instance was
    // stopped
    BpmnAssert.assertThat(new InspectedProcessInstance(camunda8ProcessInstanceKey))
        .isWaitingAtElements(elementIds.toArray(new String[] {}));
    //  assert that the camunda7ProcessInstanceId is available as variable in c8 process
    String camunda7ProcessInstanceId =
        getVariableValue(camunda8ProcessInstanceKey, "camunda7ProcessInstanceId").asText();
    assertThat(camunda7ProcessInstanceId).isEqualTo(c7Pi.getId());
    zeebeClient.newCancelInstanceCommand(camunda8ProcessInstanceKey).send().join();
    deleteDeployments();
  }

  private JobHandler continueProcessDefinition() {
    return handlerMethod(zeebeJobClient::continueProcessDefinition);
  }

  private void migrateProcessInstance() throws Exception {
    zeebeTestEngine.waitForIdleState(TIMEOUT);
    completeJob(JobType.CAMUNDA7_EXTRACT, extractProcessData());
    completeJob(JobType.CAMUNDA8_START, startCamunda8ProcessInstance());
    completeJob(JobType.CAMUNDA7_CANCEL, cancelProcessInstance());
    zeebeTestEngine.waitForIdleState(TIMEOUT);
  }

  private JobHandler startCamunda8ProcessInstance() {
    return handlerMethod(zeebeJobClient::startCamunda8ProcessInstance);
  }

  private JobHandler cancelProcessInstance() {
    return handlerMethod(zeebeJobClient::cancelProcessInstance);
  }

  private JobHandler extractProcessData() {
    return handlerMethod(zeebeJobClient::extractProcessData);
  }

  private ProcessInstanceMigrationVariables result(
      List<Consumer<ProcessInstanceMigrationVariables>> setter) {
    ProcessInstanceMigrationVariables result = new ProcessInstanceMigrationVariables();
    setter.forEach(s -> s.accept(result));
    return result;
  }

  private JobHandler suspendProcessDefinition() {
    return handlerMethod(zeebeJobClient::suspendProcessDefinition);
  }

  private JobHandler extractVersionInformation() {
    return handlerMethod(zeebeJobClient::extractVersionInformation);
  }

  private JobHandler checkProcessDefinition() {
    return handlerMethod(zeebeJobClient::checkProcessDefinition);
  }

  private JobHandler handlerMethod(
      Function<ProcessInstanceMigrationVariables, ProcessInstanceMigrationVariables> method) {
    return (client, job) -> {
      ProcessInstanceMigrationVariables variables =
          method.apply(job.getVariablesAsType(ProcessInstanceMigrationVariables.class));
      client.newCompleteCommand(job).variables(variables).send().join();
    };
  }

  private JobHandler handlerMethod(Consumer<ProcessInstanceMigrationVariables> method) {
    return (client, job) -> {
      method.accept(job.getVariablesAsType(ProcessInstanceMigrationVariables.class));
      client.newCompleteCommand(job).send().join();
    };
  }

  private void completeJob(String jobType, JobHandler jobHandler) throws Exception {
    zeebeTestEngine.waitForIdleState(TIMEOUT);
    ActivateJobsResponse response =
        Awaitility.await()
            .timeout(TIMEOUT)
            .until(
                () ->
                    zeebeClient
                        .newActivateJobsCommand()
                        .jobType(jobType)
                        .maxJobsToActivate(1)
                        .send()
                        .join(),
                r -> r.getJobs().size() == 1);
    jobHandler.handle(zeebeClient, response.getJobs().get(0));
    zeebeTestEngine.waitForIdleState(TIMEOUT);
  }

  public void testRoutedMigration() throws Exception {
    TestUtil.zeebeClient = zeebeClient;
    TestUtil.zeebeTestEngine = zeebeTestEngine;
    deployProcessToZeebe("routed-process-instance-migration.bpmn");
    deployProcessToZeebe("single-process-instance-migration.bpmn");
    deployCamunda7Process(pdInput.getC7DiagramResourceName());
    deployProcessToZeebe(pdInput.getC8DiagramResourceName());
    ProcessInstance c7instance =
        runtimeService().startProcessInstanceByKey(pdInput.getBpmnProcessId());
    BpmnAwareTests.assertThat(c7instance);
    LOG.info("Started C7 process instance {}", c7instance.getId());
    ProcessInstanceEvent processInstance =
        camunda8Service.startProcessInstanceMigrationRouter(pdInput.getBpmnProcessId());
    LOG.info("Started Router instance");
    completeJob(JobType.CAMUNDA7_VERSIONED_INFORMATION, extractVersionInformation());
    UserTask selectJobDefinitionsToMigrate = getTask();
    ProcessInstanceMigrationVariables result =
        result(
            Collections.singletonList(
                v ->
                    v.setSelectedJobDefinitions(
                        selectJobDefinitionsToMigrate
                            .getData()
                            .as(JobDefinitionSelectionTaskData.class)
                            .getCamunda7JobDefinitions()
                            .entrySet()
                            .stream()
                            .filter(e -> piInput.getRoutingActivities().contains(e.getValue()))
                            .collect(Collectors.toMap(Entry::getKey, Entry::getValue)))));
    taskService.complete(selectJobDefinitionsToMigrate.getKey(), result);
    completeJob(JobType.CAMUNDA7_SUSPEND_JOB, suspendJobDefinition());
    UserTask cancelTask = getTask();
    piInput
        .getProcessSteps()
        .forEach(
            c -> {
              waitForIdleState(c7instance);
              c.accept(c7instance);
              waitForIdleState(c7instance);
            });
    List<String> elementIds = runtimeService().getActiveActivityIds(c7instance.getId());
    LOG.info("Advanced to activities {}", elementIds);
    zeebeTestEngine.waitForIdleState(TIMEOUT);
    zeebeTestEngine.increaseTime(Duration.parse("PT11M"));
    // zeebeTestEngine.waitForIdleState(TIMEOUT);
    zeebeTestEngine.waitForBusyState(TIMEOUT);
    zeebeTestEngine.waitForIdleState(TIMEOUT);
    completeJob(JobType.CAMUNDA7_QUERY_ROUTABLE_INSTANCES, queryRoutableInstances());
    migrateProcessInstance();
    taskService.complete(cancelTask.getKey(), new ProcessInstanceMigrationVariables());
    // wait until router is completed
    completeJob(JobType.CAMUNDA7_CONTINUE_JOB, continueJobDefinition());
    zeebeTestEngine.waitForIdleState(TIMEOUT);
    BpmnAssert.assertThat(processInstance).isCompleted();
    // assert that the c7 instance was terminated
    HistoricProcessInstance historicProcessInstance =
        historyService()
            .createHistoricProcessInstanceQuery()
            .processInstanceId(c7instance.getId())
            .singleResult();
    assertThat(historicProcessInstance.getState()).isEqualTo(PROCESS_INSTANCE_STATE);
    // find created c8 instance
    long camunda8ProcessInstanceKey =
        getVariableValue(processInstance.getProcessInstanceKey(), "camunda8ProcessInstanceKey")
            .asLong();
    LOG.info("Created C8 process instance {}", camunda8ProcessInstanceKey);
    // assert that the c8 process instance key was set as variable before
    HistoricVariableInstance c8PiKeyInC7 =
        historyService()
            .createHistoricVariableInstanceQuery()
            .processInstanceId(c7instance.getId())
            .variableName("camunda8ProcessInstanceKey")
            .singleResult();
    assertThat(c8PiKeyInC7.getValue()).isEqualTo(camunda8ProcessInstanceKey);
    // assert that the c8 process instance is waiting where the c7 process instance was
    // stopped
    BpmnAssert.assertThat(new InspectedProcessInstance(camunda8ProcessInstanceKey))
        .isWaitingAtElements(elementIds.toArray(new String[] {}));
    //  assert that the camunda7ProcessInstanceId is available as variable in c8 process
    String camunda7ProcessInstanceId =
        getVariableValue(camunda8ProcessInstanceKey, "camunda7ProcessInstanceId").asText();
    assertThat(camunda7ProcessInstanceId).isEqualTo(c7instance.getId());
    zeebeClient.newCancelInstanceCommand(camunda8ProcessInstanceKey).send().join();
    deleteDeployments();
  }

  private JobHandler continueJobDefinition() {
    return handlerMethod(zeebeJobClient::continueJob);
  }

  private JobHandler suspendJobDefinition() {
    return handlerMethod(zeebeJobClient::suspendJob);
  }

  private JobHandler queryRoutableInstances() {
    return handlerMethod(zeebeJobClient::queryRoutableInstances);
  }
}
