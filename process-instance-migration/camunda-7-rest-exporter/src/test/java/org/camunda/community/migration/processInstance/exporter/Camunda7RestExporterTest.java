package org.camunda.community.migration.processInstance.exporter;

import static org.assertj.core.api.Assertions.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.camunda.community.migration.processInstance.api.model.data.Builder.*;
import static org.camunda.community.migration.processInstance.api.model.data.Builder.Json.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.UserTaskData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.rest.client.api.ProcessDefinitionApi;
import org.camunda.community.rest.client.api.ProcessInstanceApi;
import org.camunda.community.rest.client.api.VariableInstanceApi;
import org.camunda.community.rest.client.invoker.ApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class Camunda7RestExporterTest {
  @LocalServerPort int port;

  @Test
  void shouldFetchUserTaskProcessData() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath("http://localhost:" + port + "/engine-rest");
    Camunda7RestService camunda7RestService =
        new Camunda7RestService(
            new ProcessDefinitionApi(apiClient),
            new ProcessInstanceApi(apiClient),
            new VariableInstanceApi(apiClient));
    Camunda7RestExporter exporter =
        new Camunda7RestExporter(camunda7RestService, new ObjectMapper());
    BpmnModelInstance testProcess =
        Bpmn.createExecutableProcess("testProcess")
            .name("Test Process")
            .startEvent()
            .userTask("userTask")
            .name("User Task")
            .endEvent()
            .done();
    repositoryService().createDeployment().addModelInstance("test.bpmn", testProcess).deploy();
    ProcessInstance processInstance =
        runtimeService().startProcessInstanceByKey("testProcess", withVariables("foo", "bar"));
    ProcessInstanceData processInstanceData = exporter.get(processInstance.getId());
    assertThat(processInstanceData).isNotNull();
    assertThat(processInstanceData.getBpmnProcessId()).isEqualTo("testProcess");
    assertThat(processInstanceData.getId()).isEqualTo(processInstance.getId());
    assertThat(processInstanceData.getProcessDefinitionKey())
        .isEqualTo(processInstance.getProcessDefinitionId());
    assertThat(processInstanceData.getName()).isEqualTo("Test Process");
    assertThat(processInstanceData.getVariables()).contains(entry("foo", text("bar")));
    assertThat(processInstanceData.getActivities()).containsKey("userTask");
    ActivityNodeData activityNodeData = processInstanceData.getActivities().get("userTask");
    assertThat(activityNodeData).isNotNull().isInstanceOf(UserTaskData.class);
    UserTaskData userTaskData = activityNodeData.as(UserTaskData.class);
    assertThat(userTaskData.getName()).isEqualTo("User Task");
    assertThat(userTaskData.getVariables()).isNull();
  }
}
