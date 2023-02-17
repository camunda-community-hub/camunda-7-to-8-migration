package org.camunda.community.migration.processInstance;

import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.camunda.community.migration.processInstance.TestUtil.*;
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
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.filters.RecordStream;
import io.camunda.zeebe.process.test.filters.StreamFilter;
import io.camunda.zeebe.process.test.inspections.model.InspectedProcessInstance;
import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.RejectionType;
import io.camunda.zeebe.protocol.record.intent.MessageStartEventSubscriptionIntent;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import java.io.ByteArrayOutputStream;
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
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.keyvalue.AbstractKeyValue;
import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.community.migration.processInstance.client.Camunda7Client;
import org.camunda.community.migration.processInstance.dto.ActivityInstanceDto;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData;
import org.camunda.community.migration.processInstance.dto.Camunda7VersionDto;
import org.camunda.community.migration.processInstance.dto.Camunda8ProcessDefinitionData;
import org.camunda.community.migration.processInstance.dto.HistoricActivityInstanceDto;
import org.camunda.community.migration.processInstance.dto.VariableInstanceDto;
import org.camunda.community.migration.processInstance.service.Camunda7Service;
import org.camunda.community.migration.processInstance.service.Camunda8Service;
import org.camunda.community.migration.processInstance.service.ProcessDefinitionMigrationHintService;
import org.camunda.community.migration.processInstance.service.ProcessInstanceMigrationHintService;
import org.camunda.community.migration.processInstance.service.ProcessInstanceSelectionService;
import org.camunda.community.migration.processInstance.variables.ProcessInstanceMigrationVariables;
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

  @Autowired Camunda8Service camunda8Service;
  @MockBean CamundaOperateClient operateClient;
  @Autowired Camunda7Client camunda7Client;
  @Autowired Camunda7Service camunda7Service;
  @Autowired ZeebeTestEngine zeebeTestEngine;

  @Autowired ZeebeClient zeebeClient;
  @Autowired ProcessInstanceSelectionService selectionService;

  @Autowired JsonMapper jsonMapper;

  @Autowired ProcessDefinitionMigrationHintService processDefinitionMigrationHintService;
  @Autowired ProcessInstanceMigrationHintService processInstanceMigrationHintService;

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
    input.add(
        new MigrationTestProcessDefinitionInput(
            "bpmn/c7/subprocess.bpmn",
            "bpmn/c8/subprocess.bpmn",
            "SubProcess",
            Arrays.asList(
                new MigrationTestProcessInstanceInput(
                    "Moved in subprocess",
                    Collections.singletonMap("a", "b"),
                    Arrays.asList(pi -> complete(task()))))));
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

  @Test
  void shouldEvaluateProcessDefinitionRules() throws OperateException {
    BpmnModelInstance modelInstance =
        Bpmn.createExecutableProcess()
            .startEvent()
            .messageEventDefinition()
            .message("start-test")
            .messageEventDefinitionDone()
            .endEvent()
            .done();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Bpmn.writeModelToStream(out, modelInstance);
    when(operateClient.getProcessDefinitionXml(any())).thenReturn(out.toString());
    long processDefinitionKey = deployProcessToZeebe(modelInstance).getProcessDefinitionKey();
    Camunda8ProcessDefinitionData data =
        camunda8Service.getProcessDefinitionData(processDefinitionKey);
    List<String> migrationHints = processDefinitionMigrationHintService.getMigrationHints(data);
    assertThat(migrationHints).contains("A process definition must contain a None Start Event");
  }

  @Test
  void shouldEvaluateProcessInstanceRules() {
    org.camunda.bpm.model.bpmn.BpmnModelInstance modelInstance =
        org.camunda.bpm.model.bpmn.Bpmn.createExecutableProcess("test")
            .startEvent()
            .userTask()
            .multiInstance()
            .parallel()
            .cardinality("5")
            .multiInstanceDone()
            .endEvent()
            .done();
    repositoryService().createDeployment().addModelInstance("test.bpmn", modelInstance).deploy();
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("test");
    Camunda7ProcessInstanceData data = camunda7Service.getProcessData(processInstance.getId());
    // TODO do assertions on migration hints
    List<String> migrationHints = processInstanceMigrationHintService.getMigrationHints(data);
  }

  private void realTest(
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
    piInput.getProcessSteps().forEach(c -> c.accept(c7Pi));
    List<String> elementIds = runtimeService().getActiveActivityIds(c7Pi.getId());
    LOG.info("Advanced to activities {}", elementIds);
    // setup c8 engine
    deployProcessToZeebe(pdInput.getC8DiagramResourceName());
    // start migration process
    PublishMessageResponse startMessage =
        camunda8Service.startProcessInstanceMigration(pdInput.getBpmnProcessId());
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
  void shouldMapMultiInstance() {
    deployCamunda7Process("bpmn/c7/multi-instance-subprocess.bpmn");
    ProcessInstance processInstance =
        runtimeService()
            .startProcessInstanceByKey(
                "MultiInstanceProcess", withVariables("collection", Arrays.asList("A", "B", "C")));
    BpmnAwareTests.assertThat(processInstance).isActive();
    complete(task());
    complete(taskQuery().list().get(0), withVariables("result", "D"));
    complete(taskQuery().list().get(0));
    complete(task());
    complete(taskQuery().list().get(0));
    runtimeService().correlateMessage("some");
    List<TaskCompletionQuery> tasksToComplete =
        findTasksToComplete(
            processInstance.getId(),
            camunda7Client.getHistoricActivityInstances(processInstance.getId()),
            getTree(
                camunda7Client.getActivityInstances(processInstance.getId()),
                camunda7Client.getVariableInstances(processInstance.getId())));
    tasksToComplete.forEach(System.out::println);
  }

  private ActivityTreeElement getTree(
      ActivityInstanceDto current, List<VariableInstanceDto> variables) {
    ActivityTreeElement treeElement = new ActivityTreeElement();
    treeElement.setElement(current);
    treeElement.setChildren(
        current.getChildActivityInstances().stream()
            .map(ai -> getTree(ai, variables))
            .collect(Collectors.toList()));
    treeElement.setVariables(
        variables.stream()
            .filter(v -> current.getId().equals(v.getActivityInstanceId()))
            .collect(Collectors.toList()));
    return treeElement;
  }

  private List<TaskCompletionQuery> findTasksToComplete(
      String processInstanceId,
      List<HistoricActivityInstanceDto> historicActivityInstances,
      ActivityTreeElement activityTree) {
    return historicActivityInstances.stream()
        .filter(hai -> isExecutableActivity(hai.getActivityType()))
        .filter(this::isCompleted)
        .map(
            hai -> {
              TaskCompletionQuery query = new TaskCompletionQuery();
              query.setProcessInstanceId(processInstanceId);
              query.setElementId(hai.getActivityId());
              query.setIgnoreVariables(
                  findIgnoreVariables(hai.getActivityId(), activityTree, Collections.emptyMap()));
              return query;
            })
        .collect(Collectors.toList());
  }

  private boolean isExecutableActivity(String activityType) {
    return Stream.of("process", "startevent", "endevent", "gateway", "multiinstance")
        .noneMatch(term -> activityType.toLowerCase().contains(term));
  }

  private boolean isCompleted(HistoricActivityInstanceDto historicActivityInstance) {
    return historicActivityInstance.getDurationInMillis() != null;
  }

  private Map<String, Set<JsonNode>> findIgnoreVariables(
      String activityId,
      ActivityTreeElement activityTree,
      Map<String, Set<JsonNode>> collectedVariables) {
    Map<String, Set<JsonNode>> variables = new HashMap<>(collectedVariables);
    variables.putAll(
        activityTree.getVariables().stream()
            .filter(v -> isNoControlVariable(v.getName()))
            .collect(
                Collectors.groupingBy(
                    VariableInstanceDto::getName,
                    Collectors.mapping(VariableInstanceDto::getValue, Collectors.toSet()))));
    if (activityTree.getElement().getActivityId().equals(activityId)) {
      return variables;
    } else if (activityTree.getChildren().isEmpty()) {
      return Collections.emptyMap();
    } else {
      return activityTree.getChildren().stream()
          .flatMap(
              e ->
                  findIgnoreVariables(activityId, e, variables).entrySet().stream()
                      .flatMap(
                          en ->
                              en.getValue().stream()
                                  .map(v -> new DefaultMapEntry<>(en.getKey(), v))))
          .collect(
              Collectors.groupingBy(
                  AbstractKeyValue::getKey,
                  Collectors.mapping(AbstractKeyValue::getValue, Collectors.toSet())));
    }
  }

  private boolean isNoControlVariable(String variableName) {
    return !Arrays.asList(
            "nrOfActiveInstances", "loopCounter", "nrOfInstances", "nrOfCompletedInstances")
        .contains(variableName);
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

  private static class TaskCompletionQuery {
    private String processInstanceId;
    private String elementId;
    private Map<String, Set<JsonNode>> ignoreVariables;

    public String getProcessInstanceId() {
      return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
      this.processInstanceId = processInstanceId;
    }

    public String getElementId() {
      return elementId;
    }

    public void setElementId(String elementId) {
      this.elementId = elementId;
    }

    public Map<String, Set<JsonNode>> getIgnoreVariables() {
      return ignoreVariables;
    }

    public void setIgnoreVariables(Map<String, Set<JsonNode>> ignoreVariables) {
      this.ignoreVariables = ignoreVariables;
    }

    @Override
    public String toString() {
      return "TaskCompletionQuery{"
          + "processInstanceId='"
          + processInstanceId
          + '\''
          + ", elementId='"
          + elementId
          + '\''
          + ", ignoreVariables="
          + ignoreVariables
          + '}';
    }
  }

  private static class ActivityTreeElement {
    private ActivityInstanceDto element;
    private List<ActivityTreeElement> children;
    private List<VariableInstanceDto> variables;

    public ActivityInstanceDto getElement() {
      return element;
    }

    public void setElement(ActivityInstanceDto element) {
      this.element = element;
    }

    public List<ActivityTreeElement> getChildren() {
      return children;
    }

    public void setChildren(List<ActivityTreeElement> children) {
      this.children = children;
    }

    public List<VariableInstanceDto> getVariables() {
      return variables;
    }

    public void setVariables(List<VariableInstanceDto> variables) {
      this.variables = variables;
    }
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
