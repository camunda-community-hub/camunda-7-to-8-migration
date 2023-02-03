package org.camunda.community.migration.processInstance.core;

import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.*;
import static org.assertj.core.api.Assertions.*;
import static org.camunda.community.migration.processInstance.core.TestUtil.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.exception.OperateException;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.filters.RecordStream;
import io.camunda.zeebe.process.test.filters.StreamFilter;
import io.camunda.zeebe.process.test.inspections.model.InspectedProcessInstance;
import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.community.migration.processInstance.core.dto.Camunda7Version;
import org.camunda.community.migration.processInstance.core.dto.HistoricActivityInstance.HistoricActivityInstanceQueryResultDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

  @Autowired ProcessInstanceMigrationStarter processInstanceMigrationStarter;
  @MockBean CamundaOperateClient operateClient;
  @Autowired Camunda7Client camunda7Client;
  @Autowired ProcessEngine processEngine;

  @Autowired ZeebeTestEngine zeebeTestEngine;

  @Autowired ZeebeClient zeebeClient;

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
    cancelInstances();
  }

  @TestFactory
  Stream<DynamicTest> shouldMigrateProcess() {
    return DynamicTest.stream(
        Stream.of(
            new MigrationTestInput(
                "bpmn/c7/user-tasks-linear.bpmn",
                "bpmn/c8/user-tasks-linear.bpmn",
                "UserTasksLinearProcess",
                new HashMap<>(),
                Arrays.asList(
                    pi ->
                        processEngine
                            .getTaskService()
                            .complete(
                                processEngine
                                    .getTaskService()
                                    .createTaskQuery()
                                    .singleResult()
                                    .getId()))),
            new MigrationTestInput(
                "bpmn/c7/user-tasks-parallel.bpmn",
                "bpmn/c8/user-tasks-parallel.bpmn",
                "UserTasksParallelProcess",
                new HashMap<>(),
                Arrays.asList(
                    pi ->
                        processEngine
                            .getTaskService()
                            .complete(
                                processEngine
                                    .getTaskService()
                                    .createTaskQuery()
                                    .taskName("Task 1A")
                                    .singleResult()
                                    .getId()),
                    pi ->
                        processEngine
                            .getTaskService()
                            .complete(
                                processEngine
                                    .getTaskService()
                                    .createTaskQuery()
                                    .taskName("Task 1B")
                                    .singleResult()
                                    .getId())))),
        input -> input.getC7DiagramResourceName() + " -> " + input.getC8DiagramResourceName(),
        input -> {
          deployCamunda7Process(input.getC7DiagramResourceName());
          ProcessInstance c7Pi =
              processEngine
                  .getRuntimeService()
                  .startProcessInstanceByKey(input.getProcessDefinitionKey());
          LOG.info("Started C7 process instance {}", c7Pi.getId());
          input.getProcessSteps().forEach(c -> c.accept(c7Pi));
          List<String> elementIds =
              processEngine.getRuntimeService().getActiveActivityIds(c7Pi.getId());
          LOG.info("Advanced to activities {}", elementIds);
          // setup c8 engine
          deployProcessToZeebe(input.getC8DiagramResourceName());
          // start migration process
          ProcessInstanceEvent processInstance =
              processInstanceMigrationStarter.startProcessInstanceMigration(
                  input.getProcessDefinitionKey());
          waitForProcessInstanceHasPassedElement(processInstance, "SuspendProcessDefinitionTask");
          zeebeTestEngine.waitForIdleState(Duration.ofMinutes(5));
          // select the started c7 instance
          completeUserTask(
              Collections.singletonMap(
                  "camunda7ProcessInstanceIds",
                  Collections.singletonList(c7Pi.getProcessInstanceId())));
          // wait until migration is done
          waitForProcessInstanceCompleted(processInstance, Duration.ofMinutes(5));
          // assert that the c7 instance was terminated
          HistoricProcessInstance historicProcessInstance =
              processEngine
                  .getHistoryService()
                  .createHistoricProcessInstanceQuery()
                  .processInstanceId(c7Pi.getId())
                  .singleResult();
          assertThat(historicProcessInstance.getState()).isEqualTo("EXTERNALLY_TERMINATED");
          // find created c8 instance
          long camunda8ProcessInstanceKey =
              getVariableValue(
                      processInstance.getProcessInstanceKey(), "camunda8ProcessInstanceKey")
                  .asLong();
          LOG.info("Started C8 process instance {}", camunda8ProcessInstanceKey);
          // assert that the c8 process instance key was set as variable before
          HistoricVariableInstance c8PiKeyInC7 =
              processEngine
                  .getHistoryService()
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
        });
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

  private JsonNode getVariableValue(long processInstanceKey, long scopeKey, String variableName) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readTree(
          StreamFilter.variable(RecordStream.of(zeebeTestEngine.getRecordStreamSource()))
              .withProcessInstanceKey(processInstanceKey)
              .stream()
              .filter(record -> record.getValue().getName().equals(variableName))
              .filter(record -> record.getValue().getScopeKey() == scopeKey)
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
    Camunda7Version version = camunda7Client.getVersion();
    assertThat(version.getVersion()).isEqualTo("7.18.0");
  }

  @Test
  void shouldFindActivityHistory() {
    deployCamunda7Process("bpmn/c7/user-tasks-linear.bpmn");
    ProcessInstance processInstance =
        processEngine.getRuntimeService().startProcessInstanceByKey("UserTasksLinearProcess");
    processEngine
        .getTaskService()
        .complete(processEngine.getTaskService().createTaskQuery().singleResult().getId());
    HistoricActivityInstanceQueryResultDto historicActivityInstances =
        camunda7Client.getHistoricActivityInstances(processInstance.getId());
    assertThat(historicActivityInstances)
        .hasSize(3)
        .anyMatch(i -> i.getActivityId().equals("ProcessStartedStartEvent"))
        .anyMatch(i -> i.getActivityId().equals("UserTask1Task"))
        .anyMatch(i -> i.getActivityId().equals("UserTask2Task"));
  }

  private static class MigrationTestInput {
    private String c7DiagramResourceName;
    private String c8DiagramResourceName;
    private String processDefinitionKey;
    private Map<String, Object> variables;
    private List<Consumer<ProcessInstance>> processSteps;

    public MigrationTestInput(
        String c7DiagramResourceName,
        String c8DiagramResourceName,
        String processDefinitionKey,
        Map<String, Object> variables,
        List<Consumer<ProcessInstance>> processSteps) {
      this.c7DiagramResourceName = c7DiagramResourceName;
      this.c8DiagramResourceName = c8DiagramResourceName;
      this.processDefinitionKey = processDefinitionKey;
      this.variables = variables;
      this.processSteps = processSteps;
    }

    public String getC7DiagramResourceName() {
      return c7DiagramResourceName;
    }

    public void setC7DiagramResourceName(String c7DiagramResourceName) {
      this.c7DiagramResourceName = c7DiagramResourceName;
    }

    public String getC8DiagramResourceName() {
      return c8DiagramResourceName;
    }

    public void setC8DiagramResourceName(String c8DiagramResourceName) {
      this.c8DiagramResourceName = c8DiagramResourceName;
    }

    public String getProcessDefinitionKey() {
      return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
      this.processDefinitionKey = processDefinitionKey;
    }

    public Map<String, Object> getVariables() {
      return variables;
    }

    public void setVariables(Map<String, Object> variables) {
      this.variables = variables;
    }

    public List<Consumer<ProcessInstance>> getProcessSteps() {
      return processSteps;
    }

    public void setProcessSteps(List<Consumer<ProcessInstance>> processSteps) {
      this.processSteps = processSteps;
    }
  }
}
