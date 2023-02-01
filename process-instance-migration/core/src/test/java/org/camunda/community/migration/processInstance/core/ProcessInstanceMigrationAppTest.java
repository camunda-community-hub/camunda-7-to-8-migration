package org.camunda.community.migration.processInstance.core;

import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.*;
import static org.assertj.core.api.Assertions.*;
import static org.camunda.community.migration.processInstance.core.TestUtil.*;
import static org.mockito.Mockito.*;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.exception.OperateException;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.filters.RecordStream;
import io.camunda.zeebe.process.test.inspections.model.InspectedProcessInstance;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeoutException;
import java.util.stream.StreamSupport;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.community.migration.processInstance.core.dto.Camunda7Version;
import org.camunda.community.migration.processInstance.core.dto.HistoricActivityInstance.HistoricActivityInstanceQueryResultDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ZeebeSpringTest
public class ProcessInstanceMigrationAppTest {

  @Autowired ProcessInstanceMigrationStarter processInstanceMigrationStarter;
  @MockBean CamundaOperateClient operateClient;
  @Autowired Camunda7Client camunda7Client;
  @Autowired ProcessEngine processEngine;

  @Autowired ZeebeTestEngine zeebeTestEngine;

  @Autowired ZeebeClient zeebeClient;

  private static void deployCamunda7Process(String resourceName) {
    ProcessEngines.getDefaultProcessEngine()
        .getRepositoryService()
        .createDeployment()
        .addClasspathResource("user-tasks-linear.bpmn")
        .deploy();
  }

  @BeforeEach
  void setup() throws OperateException {
    TestUtil.zeebeClient = zeebeClient;
    when(operateClient.searchProcessDefinitions(any()))
        .thenReturn(Collections.singletonList(new ProcessDefinition()));
  }

  @AfterEach
  void clean() {
    processEngine
        .getRepositoryService()
        .createDeploymentQuery()
        .list()
        .forEach(
            deployment ->
                processEngine
                    .getRepositoryService()
                    .deleteDeployment(deployment.getId(), true, true, true));
  }

  @Test
  void shouldRunProcess() throws InterruptedException, TimeoutException {
    // setup c7 engine
    deployCamunda7Process("user-tasks-linear.bpmn");
    ProcessInstance c7Pi =
        processEngine.getRuntimeService().startProcessInstanceByKey("UserTasksLinearProcess");
    processEngine
        .getTaskService()
        .complete(processEngine.getTaskService().createTaskQuery().singleResult().getId());
    // setup c8 engine
    deployCamunda7ProcessToZeebe("user-tasks-linear.bpmn");
    // start migration process
    ProcessInstanceEvent processInstance =
        processInstanceMigrationStarter.startProcessInstanceMigration("UserTasksLinearProcess");
    waitForProcessInstanceHasPassedElement(processInstance, "SuspendProcessDefinitionTask");
    zeebeTestEngine.waitForIdleState(Duration.ofMinutes(5));
    // select the started c7 instance
    completeUserTask(
        Collections.singletonMap(
            "camunda7ProcessInstanceIds", Collections.singletonList(c7Pi.getProcessInstanceId())));
    // wait until migration is done
    waitForProcessInstanceCompleted(processInstance, Duration.ofMinutes(5));
    // TODO complete the started process instance in c8
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
        Long.parseLong(
            StreamSupport.stream(
                    RecordStream.of(zeebeTestEngine.getRecordStreamSource())
                        .variableRecords()
                        .spliterator(),
                    false)
                .filter(
                    record ->
                        record.getValue().getProcessInstanceKey()
                            == processInstance.getProcessInstanceKey())
                .filter(record -> record.getValue().getName().equals("camunda8ProcessInstanceKey"))
                .filter(record -> !record.getValue().getValue().equals("null"))
                .findFirst()
                .get()
                .getValue()
                .getValue());
    // assert that the c8 process instance key was set as variable before
    HistoricVariableInstance c8PiKeyInC7 =
        processEngine
            .getHistoryService()
            .createHistoricVariableInstanceQuery()
            .processInstanceId(c7Pi.getId())
            .variableName("camunda8ProcessInstanceKey")
            .singleResult();
    assertThat(c8PiKeyInC7.getValue()).isEqualTo(camunda8ProcessInstanceKey);
    // assert that the c8 process instance is waiting where the c7 process instance was stopped
    BpmnAssert.assertThat(new InspectedProcessInstance(Long.valueOf(camunda8ProcessInstanceKey)))
        .isWaitingAtElements("UserTask2Task");
    // TODO assert that the camunda7ProcessInstanceId is available as variable in c8 process
    // instance
  }

  @Test
  void shouldConnectToCamunda7() {
    Camunda7Version version = camunda7Client.getVersion();
    assertThat(version.getVersion()).isEqualTo("7.18.0");
  }

  @Test
  void shouldFindActivityHistory() {
    deployCamunda7Process("user-tasks-linear.bpmn");
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
}
