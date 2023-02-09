package org.camunda.community.migration.processInstance.core;

import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.camunda.community.migration.processInstance.core.TestUtil.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.exception.OperateException;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.filters.RecordStream;
import io.camunda.zeebe.process.test.filters.StreamFilter;
import io.camunda.zeebe.process.test.inspections.model.InspectedProcessInstance;
import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.RejectionType;
import io.camunda.zeebe.protocol.record.intent.MessageStartEventSubscriptionIntent;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.community.migration.processInstance.core.dto.Camunda7VersionDto;
import org.camunda.community.migration.processInstance.core.dto.HistoricActivityInstanceDto;
import org.camunda.community.migration.processInstance.core.variables.ProcessInstanceMigrationVariables;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ZeebeSpringTest
public class ProcessInstanceMigrationAppTest {
  private static final Logger LOG = LoggerFactory.getLogger(ProcessInstanceMigrationAppTest.class);

  @Autowired ProcessInstanceMigrationService processInstanceMigrationStarter;
  @MockBean CamundaOperateClient operateClient;
  @Autowired Camunda7Client camunda7Client;

  @Autowired ZeebeTestEngine zeebeTestEngine;

  @Autowired ZeebeClient zeebeClient;
  @Autowired ProcessInstanceSelectionService selectionService;

  @Autowired JsonMapper jsonMapper;

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
                    "Advanced to step 2", new HashMap<>(), Arrays.asList(pi -> complete(task()))),
                new MigrationTestProcessInstanceInput(
                    "Just started", new HashMap<>(), new ArrayList<>()))));
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
                        pi -> complete(task(taskQuery().taskName("Task 1B"))))))));
    input.add(
        new MigrationTestProcessDefinitionInput(
            "bpmn/c7/parallel-fork-join.bpmn",
            "bpmn/c8/parallel-fork-join.bpmn",
            "ParallelForkJoinProcess",
            Arrays.asList(
                new MigrationTestProcessInstanceInput(
                    "One token on joining gateway",
                    new HashMap<>(),
                    Arrays.asList(pi -> complete(task(taskQuery().taskName("Task A"))))))));
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
                                DynamicTest.dynamicTest(
                                    piInput.getScenarioName(), () -> realTest(pdInput, piInput)))));
  }

  private void realTest(
      MigrationTestProcessDefinitionInput pdInput, MigrationTestProcessInstanceInput piInput)
      throws InterruptedException, TimeoutException {
    deployCamunda7Process(pdInput.getC7DiagramResourceName());
    ProcessInstance c7Pi =
        runtimeService()
            .startProcessInstanceByKey(pdInput.getBpmnProcessId(), piInput.getVariables());
    BpmnAwareTests.assertThat(c7Pi);
    LOG.info("Started C7 process instance {}", c7Pi.getId());
    piInput.getProcessSteps().forEach(c -> c.accept(c7Pi));
    List<String> elementIds = runtimeService().getActiveActivityIds(c7Pi.getId());
    LOG.info("Advanced to activities {}", elementIds);
    // setup c8 engine
    deployProcessToZeebe(pdInput.getC8DiagramResourceName());
    // start migration process
    PublishMessageResponse startMessage =
        processInstanceMigrationStarter.startProcessInstanceMigration(pdInput.getBpmnProcessId());
    zeebeTestEngine.waitForIdleState(Duration.ofMinutes(5));
    InspectedProcessInstance processInstance =
        getProcessInstanceKeysForCorrelatedMessage(startMessage);
    waitForProcessInstanceHasPassedElement(processInstance, "SuspendProcessDefinitionTask");
    zeebeTestEngine.waitForIdleState(Duration.ofMinutes(5));
    // select the started c7 instance
    long jobKey = getTask(processInstance, Duration.ofMinutes(5));
    selectionService.complete(jobKey, Collections.singletonList(c7Pi.getProcessInstanceId()));
    // wait until migration is done
    waitForProcessInstanceCompleted(processInstance, Duration.ofMinutes(5));
    // assert that the c7 instance was terminated
    HistoricProcessInstance historicProcessInstance =
        historyService()
            .createHistoricProcessInstanceQuery()
            .processInstanceId(c7Pi.getId())
            .singleResult();
    assertThat(historicProcessInstance.getState()).isEqualTo("EXTERNALLY_TERMINATED");
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

  private long getTask(InspectedProcessInstance processInstance, Duration timeout) {
    AtomicLong task = new AtomicLong(0);
    LocalDateTime started = LocalDateTime.now();
    while (task.get() == 0 && started.plus(timeout).isAfter(LocalDateTime.now())) {
      selectionService.getTasks(false).stream()
          .filter(t -> t.getProcessInstanceKey() == processInstance.getProcessInstanceKey())
          .findFirst()
          .ifPresent(t -> task.set(t.getJobKey()));
    }
    return task.get();
  }

  private JsonNode getVariableValue(long processInstanceKey, String variableName) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readTree(
          StreamFilter.variable(RecordStream.of(zeebeTestEngine.getRecordStreamSource()))
              .withProcessInstanceKey(processInstanceKey)
              .stream()
              .filter(record -> record.getValue().getName().equals(variableName))
              .max(Comparator.comparingLong(Record::getPosition))
              .get()
              .getValue()
              .getValue());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void shouldConnectToCamunda7() {
    Camunda7VersionDto version = camunda7Client.getVersion();
    assertThat(version.getVersion()).isEqualTo("7.18.0");
  }

  @Test
  void shouldFindActivityHistory() {
    deployCamunda7Process("bpmn/c7/user-tasks-linear.bpmn");
    ProcessInstance processInstance =
        runtimeService().startProcessInstanceByKey("UserTasksLinearProcess");
    complete(taskQuery().singleResult());
    List<HistoricActivityInstanceDto> historicActivityInstances =
        camunda7Client.getHistoricActivityInstances(processInstance.getId());
    assertThat(historicActivityInstances)
        .hasSize(3)
        .anyMatch(i -> i.getActivityId().equals("ProcessStartedStartEvent"))
        .anyMatch(i -> i.getActivityId().equals("UserTask1Task"))
        .anyMatch(i -> i.getActivityId().equals("UserTask2Task"));
  }

  @Test
  void shouldMapOnlyNonNullFields() {
    ProcessInstanceMigrationVariables variables = new ProcessInstanceMigrationVariables();
    String json = jsonMapper.toJson(variables);
    Map<String, Object> parsed = jsonMapper.fromJsonAsMap(json);
    assertThat(parsed).isEmpty();
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

  private static class MigrationTestProcessDefinitionInput {
    private final String c7DiagramResourceName;
    private final String c8DiagramResourceName;
    private final String bpmnProcessId;
    private final List<MigrationTestProcessInstanceInput> scenarios;

    public MigrationTestProcessDefinitionInput(
        String c7DiagramResourceName,
        String c8DiagramResourceName,
        String bpmnProcessId,
        List<MigrationTestProcessInstanceInput> scenarios) {
      this.c7DiagramResourceName = c7DiagramResourceName;
      this.c8DiagramResourceName = c8DiagramResourceName;
      this.bpmnProcessId = bpmnProcessId;
      this.scenarios = scenarios;
    }

    public String getC7DiagramResourceName() {
      return c7DiagramResourceName;
    }

    public String getC8DiagramResourceName() {
      return c8DiagramResourceName;
    }

    public String getBpmnProcessId() {
      return bpmnProcessId;
    }

    public List<MigrationTestProcessInstanceInput> getScenarios() {
      return scenarios;
    }
  }

  private static class MigrationTestProcessInstanceInput {
    private final String scenarioName;
    private final Map<String, Object> variables;
    private final List<Consumer<ProcessInstance>> processSteps;

    private MigrationTestProcessInstanceInput(
        String scenarioName,
        Map<String, Object> variables,
        List<Consumer<ProcessInstance>> processSteps) {
      this.scenarioName = scenarioName;
      this.variables = variables;
      this.processSteps = processSteps;
    }

    public String getScenarioName() {
      return scenarioName;
    }

    public Map<String, Object> getVariables() {
      return variables;
    }

    public List<Consumer<ProcessInstance>> getProcessSteps() {
      return processSteps;
    }
  }
}
