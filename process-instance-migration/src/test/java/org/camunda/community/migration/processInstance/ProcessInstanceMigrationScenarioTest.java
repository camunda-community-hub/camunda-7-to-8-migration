package org.camunda.community.migration.processInstance;

import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.camunda.community.migration.processInstance.TestUtil.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.exception.OperateException;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.filters.RecordStream;
import io.camunda.zeebe.process.test.filters.StreamFilter;
import io.camunda.zeebe.process.test.inspections.model.InspectedProcessInstance;
import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.RejectionType;
import io.camunda.zeebe.protocol.record.intent.MessageStartEventSubscriptionIntent;
import io.camunda.zeebe.protocol.record.value.VariableRecordValue;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.community.migration.processInstance.client.Camunda7Client;
import org.camunda.community.migration.processInstance.dto.task.JobDefinitionSelectionTaskData;
import org.camunda.community.migration.processInstance.dto.task.UserTask;
import org.camunda.community.migration.processInstance.service.Camunda7Service;
import org.camunda.community.migration.processInstance.service.Camunda8Service;
import org.camunda.community.migration.processInstance.service.MigrationTaskService;
import org.camunda.community.migration.processInstance.service.ProcessDefinitionMigrationHintService;
import org.camunda.community.migration.processInstance.service.ProcessInstanceMigrationHintService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@ZeebeSpringTest
@SpringBootTest
public class ProcessInstanceMigrationScenarioTest {
  public static final Duration TIMEOUT = Duration.ofMinutes(10);
  public static final String PROCESS_INSTANCE_STATE = "INTERNALLY_TERMINATED";
  private static final Logger LOG =
      LoggerFactory.getLogger(ProcessInstanceMigrationScenarioTest.class);
  @Autowired Camunda8Service camunda8Service;
  @MockBean CamundaOperateClient operateClient;
  @Autowired Camunda7Client camunda7Client;
  @Autowired Camunda7Service camunda7Service;
  @Autowired ZeebeTestEngine zeebeTestEngine;
  @Autowired ZeebeClient zeebeClient;
  @Autowired MigrationTaskService taskService;

  @Autowired JsonMapper jsonMapper;

  @Autowired ProcessDefinitionMigrationHintService processDefinitionMigrationHintService;
  @Autowired ProcessInstanceMigrationHintService processInstanceMigrationHintService;

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

  private static void waitForIdleState(ProcessInstance processInstance, Duration timeout) {
    LocalDateTime t = LocalDateTime.now().plus(timeout);
    while (t.isAfter(LocalDateTime.now())) {
      if (jobQuery().active().processInstanceId(processInstance.getId()).count() == 0) {
        return;
      }
      try {
        Thread.sleep(500L);
      } catch (InterruptedException ignored) {
      }
    }
    throw new RuntimeException("Timed out");
  }

  @BeforeEach
  void setup() throws OperateException {
    TestUtil.zeebeClient = zeebeClient;
    TestUtil.zeebeTestEngine = zeebeTestEngine;
    when(operateClient.searchProcessDefinitions(any()))
        .thenReturn(Collections.singletonList(new ProcessDefinition()));
  }

  @AfterEach
  void clean() {
    deleteDeployments();
  }

  private List<MigrationTestProcessDefinitionInput> createInput() {
    List<MigrationTestProcessDefinitionInput> input = new ArrayList<>();
    input.add(
        new MigrationTestProcessDefinitionInput(
            "bpmn/c7/user-tasks-linear.bpmn",
            "bpmn/c8/user-tasks-linear.bpmn",
            "UserTasksLinearProcess",
            Arrays.asList(
                new MigrationTestProcessInstanceInput(
                    "Advanced to step 2",
                    new HashMap<>(),
                    Arrays.asList(pi -> complete(task())),
                    Arrays.asList("UserTask2Task")),
                new MigrationTestProcessInstanceInput(
                    "Just started",
                    new HashMap<>(),
                    new ArrayList<>(),
                    Arrays.asList("UserTask1Task")))));
    input.add(
        new MigrationTestProcessDefinitionInput(
            "bpmn/c7/user-tasks-parallel.bpmn",
            "bpmn/c8/user-tasks-parallel.bpmn",
            "UserTasksParallelProcess",
            Arrays.asList(
                new MigrationTestProcessInstanceInput(
                    "Advanced on both branches",
                    new HashMap<>(),
                    Arrays.asList(
                        pi -> complete(task(taskQuery().taskName("Task 1A"))),
                        pi -> complete(task(taskQuery().taskName("Task 1B")))),
                    Arrays.asList("Task2ATask", "Task2BTask")))));
    //    input.add(
    //        new MigrationTestProcessDefinitionInput(
    //            "bpmn/c7/parallel-fork-join.bpmn",
    //            "bpmn/c8/parallel-fork-join.bpmn",
    //            "ParallelForkJoinProcess",
    //            Arrays.asList(
    //                new MigrationTestProcessInstanceInput(
    //                    "One token on joining gateway",
    //                    new HashMap<>(),
    //                    Arrays.asList(pi -> complete(task(taskQuery().taskName("Task A")))),
    //                    Arrays.asList("TaskBTask", "Gateway_1rvv8vt")))));
    input.add(
        new MigrationTestProcessDefinitionInput(
            "bpmn/c7/subprocess.bpmn",
            "bpmn/c8/subprocess.bpmn",
            "SubProcess",
            Arrays.asList(
                new MigrationTestProcessInstanceInput(
                    "Moved in subprocess",
                    Collections.singletonMap("a", "b"),
                    Arrays.asList(pi -> complete(task())),
                    Arrays.asList("InSubprocessTaskTask")))));
    return input;
  }

  @TestFactory
  Stream<DynamicContainer> shouldMigrateProcessInstances() {
    List<MigrationTestProcessDefinitionInput> inputs = createInput();
    return inputs.stream()
        .map(
            (pdInput) ->
                DynamicContainer.dynamicContainer(
                    pdInput.getC7DiagramResourceName()
                        + " -> "
                        + pdInput.getC8DiagramResourceName(),
                    pdInput.getScenarios().stream()
                        .map(
                            piInput ->
                                DynamicContainer.dynamicContainer(
                                    piInput.getScenarioName(),
                                    Stream.of(
                                        DynamicTest.dynamicTest(
                                            "Ad-Hoc Migration",
                                            () -> testAdHocMigration(pdInput, piInput)),
                                        DynamicTest.dynamicTest(
                                            "Routed Migration",
                                            () -> testRoutedMigration(pdInput, piInput)))))));
  }

  private void testAdHocMigration(
      MigrationTestProcessDefinitionInput pdInput, MigrationTestProcessInstanceInput piInput)
      throws InterruptedException, TimeoutException, OperateException, IOException,
          URISyntaxException {
    when(operateClient.getProcessDefinitionXml(any()))
        .thenReturn(
            new String(
                Files.readAllBytes(
                    new File(
                            getClass()
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
              c.accept(c7Pi);
              waitForIdleState(c7Pi, TIMEOUT);
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
    waitForProcessInstanceHasPassedElement(
        processInstance, "SuspendProcessDefinitionTask", TIMEOUT);
    zeebeTestEngine.waitForIdleState(TIMEOUT);
    // select the started c7 instance
    long jobKey = getTask(processInstance.getProcessInstanceKey(), TIMEOUT).getKey();
    taskService.complete(
        jobKey,
        Collections.singletonMap(
            "camunda7ProcessInstanceIds", Collections.singletonList(c7Pi.getProcessInstanceId())));
    // wait until migration is done
    waitForProcessInstanceCompleted(processInstance, TIMEOUT);
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

  private void testRoutedMigration(
      MigrationTestProcessDefinitionInput pdInput, MigrationTestProcessInstanceInput piInput)
      throws InterruptedException, TimeoutException {
    deployCamunda7Process(pdInput.getC7DiagramResourceName());
    deployProcessToZeebe(pdInput.getC8DiagramResourceName());
    ProcessInstance c7instance =
        runtimeService().startProcessInstanceByKey(pdInput.getBpmnProcessId());
    BpmnAwareTests.assertThat(c7instance);
    LOG.info("Started C7 process instance {}", c7instance.getId());
    ProcessInstanceEvent processInstance =
        camunda8Service.startProcessInstanceMigrationRouter(pdInput.getBpmnProcessId());
    waitForProcessInstanceHasPassedElement(
        processInstance, "GetVersionedProcessInformationTask", TIMEOUT);
    zeebeTestEngine.waitForIdleState(TIMEOUT);
    UserTask task = getTask(processInstance.getProcessInstanceKey(), TIMEOUT);
    Map<String, Map<String, String>> response =
        Collections.singletonMap(
            "selectedJobDefinitions",
            task
                .getData()
                .as(JobDefinitionSelectionTaskData.class)
                .getCamunda7JobDefinitions()
                .entrySet()
                .stream()
                .filter(e -> piInput.getRoutingActivities().contains(e.getValue()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
    taskService.complete(task.getKey(), response);
    waitForProcessInstanceHasPassedElement(processInstance, "Event_1322oq3", TIMEOUT);
    piInput
        .getProcessSteps()
        .forEach(
            c -> {
              c.accept(c7instance);
              waitForIdleState(c7instance, TIMEOUT);
            });
    List<String> elementIds = runtimeService().getActiveActivityIds(c7instance.getId());
    LOG.info("Advanced to activities {}", elementIds);
    zeebeTestEngine.increaseTime(Duration.ofMinutes(10));
    zeebeTestEngine.waitForBusyState(TIMEOUT);
    zeebeTestEngine.waitForIdleState(TIMEOUT);
    UserTask cancelTask = getTask(processInstance.getProcessInstanceKey(), TIMEOUT);
    taskService.complete(cancelTask.getKey(), Collections.emptyMap());
    // wait until router is completed
    waitForProcessInstanceCompleted(processInstance, TIMEOUT);
    zeebeTestEngine.waitForIdleState(TIMEOUT);
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
  }

  private UserTask getTask(long processInstanceKey, Duration timeout) {
    AtomicReference<UserTask> task = new AtomicReference<>();
    LocalDateTime started = LocalDateTime.now();
    while (task.get() == null && started.plus(timeout).isAfter(LocalDateTime.now())) {
      taskService.getTasks(false).stream()
          .filter(t -> t.getProcessInstanceKey() == processInstanceKey)
          .findFirst()
          .ifPresent(task::set);
    }
    return task.get();
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
}
