package org.camunda.community.migration.processInstance.exporter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.management.JobDefinition;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.TransitionInstance;
import org.camunda.community.migration.processInstance.exporter.api.ActivityInstance;
import org.camunda.community.migration.processInstance.exporter.api.ProcessDefinition;
import org.camunda.community.migration.processInstance.exporter.api.ProcessEngineService;
import org.camunda.community.migration.processInstance.exporter.api.ProcessInstance;
import org.camunda.community.migration.processInstance.exporter.api.VariableInstance;

public class EmbeddedProcessEngineService implements ProcessEngineService {
  private final ProcessEngine processEngine;
  private final ObjectMapper objectMapper;

  public EmbeddedProcessEngineService(ProcessEngine processEngine, ObjectMapper objectMapper) {
    this.processEngine = processEngine;
    this.objectMapper = objectMapper;
  }

  @Override
  public ProcessDefinition getLatestProcessDefinition(String bpmnProcessId) {
    return map(
        processEngine
            .getRepositoryService()
            .createProcessDefinitionQuery()
            .processDefinitionKey(bpmnProcessId)
            .latestVersion()
            .singleResult());
  }

  @Override
  public ProcessDefinition getProcessDefinition(String processDefinitionId) {
    return map(processEngine.getRepositoryService().getProcessDefinition(processDefinitionId));
  }

  @Override
  public long getProcessInstanceCount(String processDefinitionId) {
    return processEngine
        .getRuntimeService()
        .createProcessInstanceQuery()
        .processDefinitionId(processDefinitionId)
        .count();
  }

  @Override
  public List<ProcessInstance> getProcessInstances(
      String processDefinitionId, int offset, int limit) {
    return processEngine
        .getRuntimeService()
        .createProcessInstanceQuery()
        .processDefinitionId(processDefinitionId)
        .listPage(offset, limit)
        .stream()
        .map(this::map)
        .collect(Collectors.toList());
  }

  @Override
  public ProcessInstance getProcessInstance(String processInstanceId) {
    return map(
        processEngine
            .getRuntimeService()
            .createProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .singleResult());
  }

  @Override
  public ActivityInstance getActivityInstance(String processInstanceId) {
    return map(processEngine.getRuntimeService().getActivityInstance(processInstanceId));
  }

  @Override
  public List<ProcessInstance> getProcessInstancesBySuperProcessInstanceId(
      String superProcessInstanceId) {
    return processEngine
        .getRuntimeService()
        .createProcessInstanceQuery()
        .superProcessInstanceId(superProcessInstanceId)
        .unlimitedList()
        .stream()
        .map(this::map)
        .collect(Collectors.toList());
  }

  @Override
  public List<VariableInstance> getVariableInstances(String activityInstanceId) {
    return processEngine
        .getRuntimeService()
        .createVariableInstanceQuery()
        .activityInstanceIdIn(activityInstanceId)
        .unlimitedList()
        .stream()
        .map(this::map)
        .collect(Collectors.toList());
  }

  @Override
  public void setVariable(String processInstanceId, String variableName, Long variableValue) {
    processEngine.getRuntimeService().setVariable(processInstanceId, variableName, variableValue);
  }

  @Override
  public void suspendProcessInstance(String processInstanceId) {
    processEngine.getRuntimeService().suspendProcessInstanceById(processInstanceId);
  }

  @Override
  public void deleteProcessInstance(String processInstanceId, String reason) {
    processEngine.getRuntimeService().deleteProcessInstance(processInstanceId, reason);
  }

  @Override
  public void activateProcessInstance(String processInstanceId) {
    processEngine.getRuntimeService().activateProcessInstanceById(processInstanceId);
  }

  @Override
  public boolean isTransitionExecuted(String executionId) {
    Job job =
        processEngine
            .getManagementService()
            .createJobQuery()
            .executionId(executionId)
            .singleResult();
    JobDefinition jobDefinition =
        processEngine
            .getManagementService()
            .createJobDefinitionQuery()
            .jobDefinitionId(job.getJobDefinitionId())
            .singleResult();
    return jobDefinition.getJobConfiguration().contains("after");
  }

  private ProcessDefinition map(
      org.camunda.bpm.engine.repository.ProcessDefinition processDefinition) {
    return new ProcessDefinition() {
      @Override
      public String getId() {
        return processDefinition.getId();
      }

      @Override
      public String getName() {
        return processDefinition.getName();
      }

      @Override
      public String getKey() {
        return processDefinition.getKey();
      }
    };
  }

  private ProcessInstance map(org.camunda.bpm.engine.runtime.ProcessInstance processInstance) {
    return new ProcessInstance() {
      @Override
      public String getId() {
        return processInstance.getId();
      }

      @Override
      public String getProcessDefinitionId() {
        return processInstance.getProcessDefinitionId();
      }

      @Override
      public String getSuperExecutionId() {
        return ((ExecutionEntity) processInstance).getSuperExecutionId();
      }
    };
  }

  private ActivityInstance map(org.camunda.bpm.engine.runtime.ActivityInstance activityInstance) {
    return new ActivityInstance() {
      @Override
      public List<ActivityInstance> getChildActivityInstances() {
        return Stream.of(
                Arrays.stream(activityInstance.getChildTransitionInstances()).map(a -> map(a)),
                Arrays.stream(activityInstance.getChildActivityInstances()).map(a -> map(a)))
            .flatMap(s -> s)
            .collect(Collectors.toList());
      }

      @Override
      public String getActivityId() {
        return activityInstance.getActivityId();
      }

      @Override
      public String getActivityType() {
        return activityInstance.getActivityType();
      }

      @Override
      public String getActivityName() {
        return activityInstance.getActivityName();
      }

      @Override
      public String getProcessInstanceId() {
        return activityInstance.getProcessInstanceId();
      }

      @Override
      public List<String> getExecutionIds() {
        return Arrays.asList(activityInstance.getExecutionIds());
      }

      @Override
      public String getExecutionId() {
        if (activityInstance.getExecutionIds() == null
            || activityInstance.getExecutionIds().length == 0) {
          return null;
        }
        if (activityInstance.getExecutionIds().length == 1) {
          return activityInstance.getExecutionIds()[0];
        }
        throw new IllegalStateException(
            "There is more than 1 execution id in the activity instance");
      }

      @Override
      public String getId() {
        return activityInstance.getId();
      }

      @Override
      public boolean isTransition() {
        return false;
      }
    };
  }

  private ActivityInstance map(TransitionInstance transitionInstance) {
    return new ActivityInstance() {
      @Override
      public List<ActivityInstance> getChildActivityInstances() {
        return Collections.emptyList();
      }

      @Override
      public String getActivityId() {
        return transitionInstance.getActivityId();
      }

      @Override
      public String getActivityType() {
        return transitionInstance.getActivityType();
      }

      @Override
      public String getActivityName() {
        return transitionInstance.getActivityName();
      }

      @Override
      public String getProcessInstanceId() {
        return transitionInstance.getProcessInstanceId();
      }

      @Override
      public List<String> getExecutionIds() {
        return Collections.singletonList(transitionInstance.getExecutionId());
      }

      @Override
      public String getExecutionId() {
        return transitionInstance.getExecutionId();
      }

      @Override
      public String getId() {
        return transitionInstance.getId();
      }

      @Override
      public boolean isTransition() {
        return true;
      }
    };
  }

  private VariableInstance map(org.camunda.bpm.engine.runtime.VariableInstance variableInstance) {
    return new VariableInstance() {
      @Override
      public String getName() {
        return variableInstance.getName();
      }

      @Override
      public JsonNode getValue() {
        return objectMapper.valueToTree(variableInstance.getValue());
      }
    };
  }
}
