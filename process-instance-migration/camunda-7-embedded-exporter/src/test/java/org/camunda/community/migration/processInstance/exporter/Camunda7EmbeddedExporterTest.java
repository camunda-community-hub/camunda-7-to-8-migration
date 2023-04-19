package org.camunda.community.migration.processInstance.exporter;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.camunda.community.migration.processInstance.api.model.data.Builder.Json.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.function.Consumer;
import org.camunda.bpm.engine.repository.DeploymentWithDefinitions;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.StartEventBuilder;
import org.camunda.community.migration.processInstance.api.model.data.MessageIntermediateCatchEventData;
import org.camunda.community.migration.processInstance.api.model.data.MultiInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.ServiceTaskData;
import org.camunda.community.migration.processInstance.api.model.data.StartEventData;
import org.camunda.community.migration.processInstance.api.model.data.SubProcessData;
import org.camunda.community.migration.processInstance.api.model.data.UserTaskData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;

@ExtendWith(ProcessEngineExtension.class)
public class Camunda7EmbeddedExporterTest {

  @Test
  void shouldExportUserTask() throws JsonProcessingException {
    Camunda7Exporter exporter =
        new Camunda7Exporter(new EmbeddedProcessEngineService(processEngine(), new ObjectMapper()));
    ProcessDefinition processDefinition =
        createAndDeployModel(b -> b.userTask("userTask").name("User Task"));
    ProcessInstance processInstance =
        runtimeService()
            .startProcessInstanceById(processDefinition.getId(), withVariables("foo", "bar"));
    ProcessInstanceData processInstanceData = exporter.get(processInstance.getId());
    wipe();
    testSerialization(processInstanceData);

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

  @Test
  void shouldExportServiceTask() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    Camunda7Exporter exporter =
        new Camunda7Exporter(new EmbeddedProcessEngineService(processEngine(), objectMapper));
    ProcessDefinition processDefinition =
        createAndDeployModel(
            b -> b.serviceTask("serviceTask").name("Service Task").camundaExternalTask("external"));
    ProcessInstance processInstance =
        runtimeService()
            .startProcessInstanceById(processDefinition.getId(), withVariables("foo", "bar"));
    ProcessInstanceData processInstanceData = exporter.get(processInstance.getId());
    wipe();
    testSerialization(processInstanceData);

    assertThat(processInstanceData).isNotNull();
    assertThat(processInstanceData.getBpmnProcessId()).isEqualTo("testProcess");
    assertThat(processInstanceData.getId()).isEqualTo(processInstance.getId());
    assertThat(processInstanceData.getProcessDefinitionKey())
        .isEqualTo(processInstance.getProcessDefinitionId());
    assertThat(processInstanceData.getName()).isEqualTo("Test Process");
    assertThat(processInstanceData.getVariables()).contains(entry("foo", text("bar")));
    assertThat(processInstanceData.getActivities()).containsKey("serviceTask");
    ActivityNodeData activityNodeData = processInstanceData.getActivities().get("serviceTask");
    assertThat(activityNodeData).isNotNull().isInstanceOf(ServiceTaskData.class);
    ServiceTaskData serviceTaskData = activityNodeData.as(ServiceTaskData.class);
    assertThat(serviceTaskData.getName()).isEqualTo("Service Task");
    assertThat(serviceTaskData.getVariables()).isNull();
  }

  @Test
  void shouldExportSubProcess() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    Camunda7Exporter exporter =
        new Camunda7Exporter(new EmbeddedProcessEngineService(processEngine(), objectMapper));
    ProcessDefinition processDefinition =
        createAndDeployModel(
            b ->
                b.subProcess("subProcess")
                    .name("Sub Process")
                    .embeddedSubProcess()
                    .startEvent()
                    .serviceTask("serviceTask")
                    .name("Service Task")
                    .camundaExternalTask("external")
                    .endEvent()
                    .subProcessDone());
    ProcessInstance processInstance =
        runtimeService()
            .startProcessInstanceById(processDefinition.getId(), withVariables("foo", "bar"));
    ProcessInstanceData processInstanceData = exporter.get(processInstance.getId());
    wipe();
    testSerialization(processInstanceData);

    assertThat(processInstanceData).isNotNull();
    assertThat(processInstanceData.getBpmnProcessId()).isEqualTo("testProcess");
    assertThat(processInstanceData.getId()).isEqualTo(processInstance.getId());
    assertThat(processInstanceData.getProcessDefinitionKey())
        .isEqualTo(processInstance.getProcessDefinitionId());
    assertThat(processInstanceData.getName()).isEqualTo("Test Process");
    assertThat(processInstanceData.getVariables()).contains(entry("foo", text("bar")));
    assertThat(processInstanceData.getActivities()).containsKey("subProcess");
    ActivityNodeData activityNodeData = processInstanceData.getActivities().get("subProcess");
    assertThat(activityNodeData).isNotNull().isInstanceOf(SubProcessData.class);
    SubProcessData subProcessData = activityNodeData.as(SubProcessData.class);
    assertThat(subProcessData.getName()).isEqualTo("Sub Process");
    assertThat(subProcessData.getActivities()).containsKey("serviceTask");
    ActivityNodeData task = subProcessData.getActivities().get("serviceTask");
    assertThat(task).isInstanceOf(ServiceTaskData.class);
    ServiceTaskData serviceTaskData = task.as(ServiceTaskData.class);
    assertThat(serviceTaskData.getName()).isEqualTo("Service Task");
    assertThat(serviceTaskData.getVariables()).isNull();
  }

  @RepeatedTest(100)
  void shouldExportMultiInstanceUserTask() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    Camunda7Exporter exporter =
        new Camunda7Exporter(new EmbeddedProcessEngineService(processEngine(), objectMapper));
    ProcessDefinition processDefinition =
        createAndDeployModel(
            b ->
                b.userTask("userTask")
                    .multiInstance()
                    .camundaCollection("collection")
                    .camundaElementVariable("element")
                    .multiInstanceDone()
                    .name("User Task"));
    ProcessInstance processInstance =
        runtimeService()
            .startProcessInstanceById(
                processDefinition.getId(),
                withVariables("foo", "bar", "collection", Arrays.asList("A", "B", "C")));
    complete(taskQuery().list().get(0));
    ProcessInstanceData processInstanceData = exporter.get(processInstance.getId());
    wipe();
    testSerialization(processInstanceData);

    assertThat(processInstanceData).isNotNull();
    assertThat(processInstanceData.getBpmnProcessId()).isEqualTo("testProcess");
    assertThat(processInstanceData.getId()).isEqualTo(processInstance.getId());
    assertThat(processInstanceData.getProcessDefinitionKey())
        .isEqualTo(processInstance.getProcessDefinitionId());
    assertThat(processInstanceData.getName()).isEqualTo("Test Process");
    assertThat(processInstanceData.getVariables()).contains(entry("foo", text("bar")));
    assertThat(processInstanceData.getActivities()).containsKey("userTask");
    ActivityNodeData activityNodeData = processInstanceData.getActivities().get("userTask");
    assertThat(activityNodeData).isNotNull().isInstanceOf(MultiInstanceData.class);
    MultiInstanceData multiInstanceData = activityNodeData.as(MultiInstanceData.class);
    assertThat(multiInstanceData.getName()).isEqualTo("User Task");
    assertThat(multiInstanceData.getCompletedInstanceLoopCounters()).hasSize(1).contains(0);
    assertThat(multiInstanceData.getInstances()).hasSize(2);
    // 1
    ActivityNodeData task1 = findForLoopCounter(multiInstanceData, 1);
    assertThat(task1).isInstanceOf(UserTaskData.class);
    UserTaskData userTaskData1 = task1.as(UserTaskData.class);
    assertThat(userTaskData1.getName()).isEqualTo("User Task");
    assertThat(userTaskData1.getVariables())
        .contains(entry("loopCounter", number(1)), entry("element", text("B")));
    // 2
    ActivityNodeData task2 = findForLoopCounter(multiInstanceData, 2);
    assertThat(task2).isInstanceOf(UserTaskData.class);
    UserTaskData userTaskData2 = task2.as(UserTaskData.class);
    assertThat(userTaskData2.getName()).isEqualTo("User Task");
    assertThat(userTaskData2.getVariables())
        .contains(entry("loopCounter", number(2)), entry("element", text("C")));
  }

  private ActivityNodeData findForLoopCounter(
      MultiInstanceData multiInstanceData, int loopCounter) {
    return multiInstanceData.getInstances().stream()
        .filter(a -> a.getVariables().get("loopCounter").asInt() == loopCounter)
        .findFirst()
        .get();
  }

  @Test
  void shouldExportAsyncBefore() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    Camunda7Exporter exporter =
        new Camunda7Exporter(new EmbeddedProcessEngineService(processEngine(), objectMapper));
    ProcessDefinition processDefinition =
        createAndDeployModel(
            b ->
                b.serviceTask("serviceTask")
                    .name("Service Task")
                    .camundaAsyncBefore()
                    .camundaDelegateExpression("${myDelegate}"));
    ProcessInstance processInstance =
        runtimeService().startProcessInstanceById(processDefinition.getId());
    ProcessInstanceData processInstanceData = exporter.get(processInstance.getId());
    wipe();
    testSerialization(processInstanceData);

    assertThat(processInstanceData).isNotNull();
    assertThat(processInstanceData.getBpmnProcessId()).isEqualTo("testProcess");
    assertThat(processInstanceData.getId()).isEqualTo(processInstance.getId());
    assertThat(processInstanceData.getProcessDefinitionKey())
        .isEqualTo(processInstance.getProcessDefinitionId());
    assertThat(processInstanceData.getName()).isEqualTo("Test Process");
    assertThat(processInstanceData.getVariables()).isNull();
    assertThat(processInstanceData.getActivities()).containsKey("serviceTask");
    ActivityNodeData activityNodeData = processInstanceData.getActivities().get("serviceTask");
    assertThat(activityNodeData).isNotNull().isInstanceOf(ServiceTaskData.class);
    ServiceTaskData serviceTaskData = activityNodeData.as(ServiceTaskData.class);
    assertThat(serviceTaskData.getName()).isEqualTo("Service Task");
    assertThat(serviceTaskData.getExecuted()).isFalse();
    assertThat(serviceTaskData.getVariables()).isNull();
  }

  @Test
  void shouldExportAsyncAfter() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    Camunda7Exporter exporter =
        new Camunda7Exporter(new EmbeddedProcessEngineService(processEngine(), objectMapper));
    ProcessDefinition processDefinition =
        createAndDeployModel(
            b ->
                b.serviceTask("serviceTask")
                    .name("Service Task")
                    .camundaAsyncAfter()
                    .camundaClass(SampleDelegate.class));
    ProcessInstance processInstance =
        runtimeService().startProcessInstanceById(processDefinition.getId());
    ProcessInstanceData processInstanceData = exporter.get(processInstance.getId());
    wipe();
    testSerialization(processInstanceData);

    assertThat(processInstanceData).isNotNull();
    assertThat(processInstanceData.getBpmnProcessId()).isEqualTo("testProcess");
    assertThat(processInstanceData.getId()).isEqualTo(processInstance.getId());
    assertThat(processInstanceData.getProcessDefinitionKey())
        .isEqualTo(processInstance.getProcessDefinitionId());
    assertThat(processInstanceData.getName()).isEqualTo("Test Process");
    assertThat(processInstanceData.getVariables()).isNull();
    assertThat(processInstanceData.getActivities()).containsKey("serviceTask");
    ActivityNodeData activityNodeData = processInstanceData.getActivities().get("serviceTask");
    assertThat(activityNodeData).isNotNull().isInstanceOf(ServiceTaskData.class);
    ServiceTaskData serviceTaskData = activityNodeData.as(ServiceTaskData.class);
    assertThat(serviceTaskData.getName()).isEqualTo("Service Task");
    assertThat(serviceTaskData.getExecuted()).isTrue();
    assertThat(serviceTaskData.getVariables()).isNull();
  }

  @Test
  void shouldExportMessageCatchEvent() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    Camunda7Exporter exporter =
        new Camunda7Exporter(new EmbeddedProcessEngineService(processEngine(), objectMapper));
    ProcessDefinition processDefinition =
        createAndDeployModel(
            b -> b.intermediateCatchEvent("messageCatch").message("msg").name("Message Catch"));
    ProcessInstance processInstance =
        runtimeService().startProcessInstanceById(processDefinition.getId());
    ProcessInstanceData processInstanceData = exporter.get(processInstance.getId());
    wipe();
    testSerialization(processInstanceData);
    assertThat(processInstanceData).isNotNull();
    assertThat(processInstanceData.getBpmnProcessId()).isEqualTo("testProcess");
    assertThat(processInstanceData.getId()).isEqualTo(processInstance.getId());
    assertThat(processInstanceData.getProcessDefinitionKey())
        .isEqualTo(processInstance.getProcessDefinitionId());
    assertThat(processInstanceData.getName()).isEqualTo("Test Process");
    assertThat(processInstanceData.getVariables()).isNull();
    assertThat(processInstanceData.getActivities()).containsKey("messageCatch");
    ActivityNodeData activityNodeData = processInstanceData.getActivities().get("messageCatch");
    assertThat(activityNodeData).isNotNull().isInstanceOf(MessageIntermediateCatchEventData.class);
    MessageIntermediateCatchEventData serviceTaskData =
        activityNodeData.as(MessageIntermediateCatchEventData.class);
    assertThat(serviceTaskData.getName()).isEqualTo("Message Catch");
    assertThat(serviceTaskData.getExecuted()).isFalse();
    assertThat(serviceTaskData.getVariables()).isNull();
  }

  @Test
  void shouldExportStartEvent() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    Camunda7Exporter exporter =
        new Camunda7Exporter(new EmbeddedProcessEngineService(processEngine(), objectMapper));
    ProcessDefinition processDefinition =
        createAndDeployModel(b -> b.id("startEvent").name("Process started").camundaAsyncBefore());
    ProcessInstance processInstance =
        runtimeService().startProcessInstanceById(processDefinition.getId());
    ProcessInstanceData processInstanceData = exporter.get(processInstance.getId());
    wipe();
    testSerialization(processInstanceData);
    assertThat(processInstanceData).isNotNull();
    assertThat(processInstanceData.getBpmnProcessId()).isEqualTo("testProcess");
    assertThat(processInstanceData.getId()).isEqualTo(processInstance.getId());
    assertThat(processInstanceData.getProcessDefinitionKey())
        .isEqualTo(processInstance.getProcessDefinitionId());
    assertThat(processInstanceData.getName()).isEqualTo("Test Process");
    assertThat(processInstanceData.getVariables()).isNull();
    assertThat(processInstanceData.getActivities()).containsKey("startEvent");
    ActivityNodeData activityNodeData = processInstanceData.getActivities().get("startEvent");
    assertThat(activityNodeData).isNotNull().isInstanceOf(StartEventData.class);
    StartEventData serviceTaskData = activityNodeData.as(StartEventData.class);
    assertThat(serviceTaskData.getName()).isEqualTo("Process started");
    assertThat(serviceTaskData.getExecuted()).isFalse();
    assertThat(serviceTaskData.getVariables()).isNull();
  }

  private void testSerialization(ProcessInstanceData processInstanceData)
      throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    String data =
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(processInstanceData);
    LoggerFactory.getLogger(getClass()).info(data);
    objectMapper.readValue(data, ProcessInstanceData.class);
  }

  private ProcessDefinition createAndDeployModel(
      Consumer<AbstractFlowNodeBuilder<?, ?>> taskApplier) {
    StartEventBuilder builder =
        Bpmn.createExecutableProcess("testProcess").name("Test Process").startEvent();
    taskApplier.accept(builder);
    BpmnModelInstance modelInstance = builder.endEvent().done();
    DeploymentWithDefinitions deploymentWithDefinitions =
        repositoryService()
            .createDeployment()
            .addModelInstance("test.bpmn", modelInstance)
            .deployWithResult();
    return deploymentWithDefinitions.getDeployedProcessDefinitions().get(0);
  }

  private void wipe() {
    repositoryService()
        .createDeploymentQuery()
        .list()
        .forEach(
            deployment ->
                repositoryService().deleteDeployment(deployment.getId(), true, true, true));
  }
}
