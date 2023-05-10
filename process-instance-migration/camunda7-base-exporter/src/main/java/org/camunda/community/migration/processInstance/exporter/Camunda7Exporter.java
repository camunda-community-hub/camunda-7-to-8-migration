package org.camunda.community.migration.processInstance.exporter;

import static org.camunda.community.migration.processInstance.api.model.data.Builder.*;
import static org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData.*;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.camunda.community.migration.processInstance.api.ProcessInstanceDataExporter;
import org.camunda.community.migration.processInstance.api.ProcessInstanceMigrationContext;
import org.camunda.community.migration.processInstance.api.ProcessInstanceMigrationContext.ProcessInstanceMigrationSuccessfulContext;
import org.camunda.community.migration.processInstance.api.model.Page;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceMetadata;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData.CommonActivityNodeDataBuilder;
import org.camunda.community.migration.processInstance.exporter.api.ActivityInstance;
import org.camunda.community.migration.processInstance.exporter.api.ProcessDefinition;
import org.camunda.community.migration.processInstance.exporter.api.ProcessEngineService;
import org.camunda.community.migration.processInstance.exporter.api.ProcessInstance;
import org.camunda.community.migration.processInstance.exporter.api.VariableInstance;

public class Camunda7Exporter implements ProcessInstanceDataExporter {
  private final ProcessEngineService processEngine;

  public Camunda7Exporter(ProcessEngineService processEngine) {
    this.processEngine = processEngine;
  }

  @Override
  public Page<ProcessInstanceMetadata> page(String bpmnProcessId, int start, int limit) {
    ProcessDefinition processDefinition = processEngine.getLatestProcessDefinition(bpmnProcessId);
    return Page.<ProcessInstanceMetadata>builder()
        .withTotal(processEngine.getProcessInstanceCount(processDefinition.getId()))
        .withStart(start)
        .withLimit(limit)
        .withResults(
            processEngine.getProcessInstances(processDefinition.getId(), start, limit).stream()
                .map(
                    processInstance ->
                        processInstanceMetadata()
                            .withBpmnProcessId(bpmnProcessId)
                            .withId(processInstance.getId())
                            .withName(processDefinition.getName())
                            .withProcessDefinitionKey(processDefinition.getId())
                            .build())
                .collect(Collectors.toList()))
        .build();
  }

  @Override
  public ProcessInstanceData get(String processInstanceId) {
    return buildProcessInstance(processInstanceId);
  }

  private ProcessInstanceData buildProcessInstance(String processInstanceId) {
    ProcessInstance processInstance = processEngine.getProcessInstance(processInstanceId);
    ProcessDefinition processDefinition =
        processEngine.getProcessDefinition(processInstance.getProcessDefinitionId());
    ActivityInstance activityInstance = processEngine.getActivityInstance(processInstanceId);
    return processInstanceData()
        .withBpmnProcessId(processDefinition.getKey())
        .withId(processInstance.getId())
        .withName(processDefinition.getName())
        .withProcessDefinitionKey(processDefinition.getId())
        .withVariables(extractVariables(activityInstance))
        .withActivities(buildActivities(activityInstance.getChildActivityInstances()))
        .build();
  }

  private Map<String, ActivityNodeData> buildActivities(List<ActivityInstance> activityInstances) {
    return activityInstances.stream()
        .collect(
            Collectors.groupingBy(
                activityInstance ->
                    activityInstance.getActivityId().replaceAll("#multiInstanceBody", ""),
                Collectors.collectingAndThen(Collectors.toList(), this::buildActivity)));
  }

  private List<CommonActivityNodeData> buildActivitiesWithoutIdAndTransition(
      List<ActivityInstance> activityInstances) {
    return activityInstances.stream()
        .filter(activityInstance -> !activityInstance.isTransition())
        .map(this::buildActivity)
        .collect(Collectors.toList());
  }

  private String equalString(Stream<String> strings) {
    return equalString(strings.collect(Collectors.toList()));
  }

  private String equalString(List<String> strings) {
    String result = null;
    for (String s : strings) {
      if (s != null) {
        if (result == null) {
          result = s;
        } else {
          if (!Objects.equals(s, result)) {
            throw new IllegalStateException("Strings are not equal");
          }
        }
      }
    }
    return result;
  }

  private ActivityNodeData buildActivity(List<ActivityInstance> activityInstances) {
    String activityName =
        equalString(activityInstances.stream().map(ActivityInstance::getActivityName));
    if (activityInstances.size() == 1) {
      return buildActivity(activityInstances.get(0));
    } else {
      return multiExecutionData()
          .withName(activityName)
          .withActivities(
              activityInstances.stream().map(this::buildActivity).collect(Collectors.toList()))
          .build();
    }
  }

  private CommonActivityNodeData buildActivity(ActivityInstance activityInstance) {
    switch (activityInstance.getActivityType()) {
      case "multiInstanceBody":
        List<CommonActivityNodeData> instances =
            buildActivitiesWithoutIdAndTransition(activityInstance.getChildActivityInstances());
        List<Integer> activeLoopCounters =
            instances.stream()
                .map(activityNodeData -> activityNodeData.getVariables().get("loopCounter").asInt())
                .collect(Collectors.toList());
        Map<String, JsonNode> variables = extractVariables(activityInstance);
        List<Integer> completedInstances =
            IntStream.range(0, variables.get("nrOfInstances").asInt())
                .filter(i -> !activeLoopCounters.contains(i))
                .boxed()
                .collect(Collectors.toList());
        return multiInstanceData()
            .withVariables(variables)
            .withExecuted(false)
            .withName(
                activityInstance.getChildActivityInstances().size() > 0
                    ? activityInstance.getChildActivityInstances().get(0).getActivityName()
                    : null)
            .withInstances(instances)
            .withCompletedInstanceLoopCounters(completedInstances)
            .build();
      case USER_TASK:
        return decorate(userTaskData(), activityInstance).build();
      case SERVICE_TASK:
        return decorate(serviceTaskData(), activityInstance).build();
      case SCRIPT_TASK:
        return decorate(scriptTaskData(), activityInstance).build();
      case RECEIVE_TASK:
        return decorate(receiveTaskData(), activityInstance).build();
      case SEND_TASK:
        return decorate(sendTaskData(), activityInstance).build();
      case BUSINESS_RULE_TASK:
        return decorate(businessRuleTaskData(), activityInstance).build();
      case CALL_ACTIVITY:
        return decorate(callActivityData(), activityInstance)
            .withProcessInstance(getCalledProcessInstance(activityInstance))
            .build();
      case TASK:
        return decorate(taskData(), activityInstance).build();
      case MANUAL_TASK:
        return decorate(manualTaskData(), activityInstance).build();
      case SUB_PROCESS:
        return decorate(subProcessData(), activityInstance)
            .withActivities(buildActivities(activityInstance.getChildActivityInstances()))
            .build();
      case MESSAGE_INTERMEDIATE_CATCH_EVENT:
        return decorate(messageIntermediateCatchEventData(), activityInstance).build();
      case START_EVENT:
        return decorate(startEventData(), activityInstance).build();
      case TRANSACTION:
        return decorate(transactionData(), activityInstance)
            .withActivities(buildActivities(activityInstance.getChildActivityInstances()))
            .build();
      case EXCLUSIVE_GATEWAY:
        return decorate(exclusiveGatewayData(), activityInstance).build();
      case PARALLEL_GATEWAY:
        return decorate(parallelGatewayData(), activityInstance).build();
      case EVENT_BASED_GATEWAY:
        return decorate(eventBasedGateway(), activityInstance).build();
      case INCLUSIVE_GATEWAY:
        return decorate(inclusiveGateway(), activityInstance).build();
      case INTERMEDIATE_EVENT:
        return decorate(intermediateThrowEvent(), activityInstance).build();
      case END_EVENT:
        return decorate(endEvent(), activityInstance).build();
      case MESSAGE_START_EVENT:
        return decorate(messageStartEvent(), activityInstance).build();
      case MESSAGE_INTERMEDIATE_THROW_EVENT:
        return decorate(messageIntermediateThrowEvent(), activityInstance).build();
      case MESSAGE_END_EVENT:
        return decorate(messageEndEvent(), activityInstance).build();
      case MESSAGE_BOUNDARY_EVENT:
        return decorate(messageBoundaryEvent(), activityInstance).build();
      case SIGNAL_START_EVENT:
        return decorate(signalStartEvent(), activityInstance).build();
      case SIGNAL_INTERMEDIATE_THROW_EVENT:
        return decorate(signalIntermediateThrowEvent(), activityInstance).build();
      case SIGNAL_INTERMEDIATE_CATCH_EVENT:
        return decorate(signalIntermediateCatchEvent(), activityInstance).build();
      case SIGNAL_END_EVENT:
        return decorate(signalEndEvent(), activityInstance).build();
      case SIGNAL_BOUNDARY_EVENT:
        return decorate(signalBoundaryEvent(), activityInstance).build();
      default:
        throw new RuntimeException(
            "No handler defined for activity type '" + activityInstance.getActivityType() + "'");
    }
  }

  private ProcessInstanceData getCalledProcessInstance(ActivityInstance activityInstance) {
    List<ProcessInstance> processInstances =
        processEngine.getProcessInstancesBySuperProcessInstanceId(
            activityInstance.getProcessInstanceId());
    if (processInstances.isEmpty()) {
      return null;
    }
    if (processInstances.size() > 1) {
      processInstances =
          processInstances.stream()
              .filter(e -> activityInstance.getExecutionIds().contains(e.getSuperExecutionId()))
              .map(ProcessInstance.class::cast)
              .collect(Collectors.toList());
    }
    if (processInstances.size() != 1) {
      throw new RuntimeException("There was no explicit child process instance identified");
    }
    return buildProcessInstance(processInstances.get(0).getId());
  }

  private <T extends CommonActivityNodeDataBuilder<T, ?>> T decorate(
      T builder, ActivityInstance activityInstance) {
    return builder
        .withName(activityInstance.getActivityName())
        .withExecuted(activityInstance.isTransition() && getAsyncForTransition(activityInstance))
        .withVariables(extractVariables(activityInstance));
  }

  private boolean getAsyncForTransition(ActivityInstance activityInstance) {
    return processEngine.isTransitionExecuted(activityInstance.getExecutionId());
  }

  private Map<String, JsonNode> extractVariables(ActivityInstance activityInstance) {
    return processEngine.getVariableInstances(activityInstance.getId()).stream()
        .collect(Collectors.toMap(VariableInstance::getName, VariableInstance::getValue));
  }

  @Override
  public void onMigrationStarted(ProcessInstanceMigrationContext context) {
    processEngine.suspendProcessInstance(context.getProcessInstanceId());
  }

  @Override
  public void onMigrationSuccess(ProcessInstanceMigrationSuccessfulContext context) {
    processEngine.setVariable(
        context.getProcessInstanceId(),
        context.getProcessInstanceKeyVariableName(),
        context.getProcessInstanceKey());
    if (context.isCancelOldInstance()) {
      processEngine.deleteProcessInstance(context.getProcessInstanceId(), "Migrated to Camunda 8");
    }
  }

  @Override
  public void onMigrationFailed(ProcessInstanceMigrationContext context) {
    processEngine.activateProcessInstance(context.getProcessInstanceId());
  }
}
