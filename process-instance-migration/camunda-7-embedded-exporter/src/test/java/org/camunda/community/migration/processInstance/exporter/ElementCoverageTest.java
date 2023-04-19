package org.camunda.community.migration.processInstance.exporter;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.junit.jupiter.api.DynamicTest.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.community.migration.processInstance.api.model.data.BusinessRuleTaskData;
import org.camunda.community.migration.processInstance.api.model.data.CallActivityData;
import org.camunda.community.migration.processInstance.api.model.data.EventBasedGatewayData;
import org.camunda.community.migration.processInstance.api.model.data.ExclusiveGatewayData;
import org.camunda.community.migration.processInstance.api.model.data.InclusiveGatewayData;
import org.camunda.community.migration.processInstance.api.model.data.ManualTaskData;
import org.camunda.community.migration.processInstance.api.model.data.ParallelGatewayData;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.ReceiveTaskData;
import org.camunda.community.migration.processInstance.api.model.data.ScriptTaskData;
import org.camunda.community.migration.processInstance.api.model.data.SendTaskData;
import org.camunda.community.migration.processInstance.api.model.data.ServiceTaskData;
import org.camunda.community.migration.processInstance.api.model.data.SubProcessData;
import org.camunda.community.migration.processInstance.api.model.data.TaskData;
import org.camunda.community.migration.processInstance.api.model.data.TransactionData;
import org.camunda.community.migration.processInstance.api.model.data.UserTaskData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;

@ExtendWith(ProcessEngineExtension.class)
public class ElementCoverageTest {
  private static List<Consumer<ProcessInstance>> defaultSteps() {
    return Collections.emptyList();
  }

  private static Function<ProcessInstanceData, Class<? extends ActivityNodeData>>
      defaultTypeExtractor() {
    return processInstanceData -> {
      Collection<ActivityNodeData> values = processInstanceData.getActivities().values();
      if (values.size() != 1) {
        throw new RuntimeException("Excepted 1 activity to be present");
      }
      return values.stream().findFirst().get().getClass();
    };
  }

  @TestFactory
  Stream<DynamicTest> taskCoverage() {
    return Stream.of(
        dynamicTest("Task", () -> performTest("bpmn/coverage/task.bpmn", TaskData.class)),
        dynamicTest(
            "User Task", () -> performTest("bpmn/coverage/user-task.bpmn", UserTaskData.class)),
        dynamicTest(
            "Service Task",
            () -> performTest("bpmn/coverage/service-task.bpmn", ServiceTaskData.class)),
        dynamicTest(
            "Receive Task",
            () -> performTest("bpmn/coverage/receive-task.bpmn", ReceiveTaskData.class)),
        dynamicTest(
            "Send Task", () -> performTest("bpmn/coverage/send-task.bpmn", SendTaskData.class)),
        dynamicTest(
            "Business Rule Task",
            () -> performTest("bpmn/coverage/business-rule-task.bpmn", BusinessRuleTaskData.class)),
        dynamicTest(
            "Script Task",
            () -> performTest("bpmn/coverage/script-task.bpmn", ScriptTaskData.class)),
        dynamicTest(
            "Manual Task",
            () -> performTest("bpmn/coverage/manual-task.bpmn", ManualTaskData.class)));
  }

  @TestFactory
  Stream<DynamicTest> subProcessCoverage() {
    return Stream.of(
        dynamicTest(
            "Sub Process",
            () -> performTest("bpmn/coverage/sub-process.bpmn", SubProcessData.class)),
        dynamicTest(
            "Call Activity",
            () -> performTest("bpmn/coverage/call-activity.bpmn", CallActivityData.class)),
        dynamicTest(
            "Event Sub Process",
            () ->
                performTest(
                    "bpmn/coverage/event-sub-process.bpmn",
                    Collections.singletonList(pi -> runtimeService().correlateMessage("msg")),
                    SubProcessData.class)),
        dynamicTest(
            "Transaction",
            () -> performTest("bpmn/coverage/transaction.bpmn", TransactionData.class)));
  }

  @TestFactory
  Stream<DynamicTest> gatewayCoverage() {
    return Stream.of(
        dynamicTest(
            "Exclusive Gateway",
            () -> performTest("bpmn/coverage/exclusive-gateway.bpmn", ExclusiveGatewayData.class)),
        dynamicTest(
            "Parallel Gateway",
            () -> performTest("bpmn/coverage/parallel-gateway.bpmn", ParallelGatewayData.class)),
        dynamicTest(
            "Event Based Gateway",
            () ->
                performTest("bpmn/coverage/event-based-gateway.bpmn", EventBasedGatewayData.class)),
        dynamicTest(
            "Inclusive Gateway",
            () -> performTest("bpmn/coverage/inclusive-gateway.bpmn", InclusiveGatewayData.class)));
  }

  private void performTest(String resourceName, Class<? extends ActivityNodeData> expectedType) {
    performTest(resourceName, defaultSteps(), defaultTypeExtractor(), expectedType);
  }

  private void performTest(
      String resourceName,
      List<Consumer<ProcessInstance>> steps,
      Class<? extends ActivityNodeData> expectedType) {
    performTest(resourceName, steps, defaultTypeExtractor(), expectedType);
  }

  private void performTest(
      String resourceName,
      Function<ProcessInstanceData, Class<? extends ActivityNodeData>> typeExtractor,
      Class<? extends ActivityNodeData> expectedType) {
    performTest(resourceName, defaultSteps(), typeExtractor, expectedType);
  }

  private void performTest(
      String resourceName,
      List<Consumer<ProcessInstance>> steps,
      Function<ProcessInstanceData, Class<? extends ActivityNodeData>> typeExtractor,
      Class<? extends ActivityNodeData> expectedType) {
    ObjectMapper objectMapper = new ObjectMapper();
    Camunda7Exporter exporter =
        new Camunda7Exporter(new EmbeddedProcessEngineService(processEngine(), objectMapper));
    // create process instance
    ProcessDefinition processDefinition =
        repositoryService()
            .createDeployment()
            .addClasspathResource(resourceName)
            .deployWithResult()
            .getDeployedProcessDefinitions()
            .get(0);
    ProcessInstance processInstance =
        runtimeService().startProcessInstanceById(processDefinition.getId());
    // execute steps
    steps.forEach(step -> step.accept(processInstance));
    // export instance
    ProcessInstanceData processInstanceData = exporter.get(processInstance.getId());
    // clean up
    repositoryService()
        .createDeploymentQuery()
        .list()
        .forEach(
            deployment ->
                repositoryService().deleteDeployment(deployment.getId(), true, true, true));
    try {
      String processInstanceDataString =
          objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(processInstanceData);
      LoggerFactory.getLogger(getClass()).info(processInstanceDataString);
      ProcessInstanceData fromString =
          objectMapper.readValue(processInstanceDataString, ProcessInstanceData.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    // assert activity type
    Class<? extends ActivityNodeData> activityNodeDataClass =
        typeExtractor.apply(processInstanceData);
    Assertions.assertThat(expectedType).isAssignableFrom(activityNodeDataClass);
  }
}
