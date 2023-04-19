package org.camunda.community.migration.processInstance.exporter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.camunda.community.migration.processInstance.exporter.api.ActivityInstance;
import org.camunda.community.migration.processInstance.exporter.api.ProcessDefinition;
import org.camunda.community.migration.processInstance.exporter.api.ProcessEngineService;
import org.camunda.community.migration.processInstance.exporter.api.ProcessInstance;
import org.camunda.community.migration.processInstance.exporter.api.VariableInstance;
import org.camunda.community.rest.client.api.JobApi;
import org.camunda.community.rest.client.api.JobDefinitionApi;
import org.camunda.community.rest.client.api.ProcessDefinitionApi;
import org.camunda.community.rest.client.api.ProcessInstanceApi;
import org.camunda.community.rest.client.api.VariableInstanceApi;
import org.camunda.community.rest.client.dto.ActivityInstanceDto;
import org.camunda.community.rest.client.dto.JobDefinitionDto;
import org.camunda.community.rest.client.dto.JobDefinitionQueryDto;
import org.camunda.community.rest.client.dto.JobDto;
import org.camunda.community.rest.client.dto.JobQueryDto;
import org.camunda.community.rest.client.dto.ProcessDefinitionDto;
import org.camunda.community.rest.client.dto.ProcessInstanceDto;
import org.camunda.community.rest.client.dto.ProcessInstanceQueryDto;
import org.camunda.community.rest.client.dto.SuspensionStateDto;
import org.camunda.community.rest.client.dto.TransitionInstanceDto;
import org.camunda.community.rest.client.dto.VariableInstanceDto;
import org.camunda.community.rest.client.dto.VariableInstanceQueryDto;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.camunda.community.rest.client.invoker.ApiException;

public class RestProcessEngineService implements ProcessEngineService {
  private final ProcessDefinitionApi processDefinitionApi;
  private final ProcessInstanceApi processInstanceApi;
  private final VariableInstanceApi variableInstanceApi;
  private final JobApi jobApi;
  private final JobDefinitionApi jobDefinitionApi;
  private final ObjectMapper objectMapper;

  public RestProcessEngineService(
      ProcessDefinitionApi processDefinitionApi,
      ProcessInstanceApi processInstanceApi,
      VariableInstanceApi variableInstanceApi,
      JobApi jobApi,
      JobDefinitionApi jobDefinitionApi,
      ObjectMapper objectMapper) {
    this.processDefinitionApi = processDefinitionApi;
    this.processInstanceApi = processInstanceApi;
    this.variableInstanceApi = variableInstanceApi;
    this.jobApi = jobApi;
    this.jobDefinitionApi = jobDefinitionApi;
    this.objectMapper = objectMapper;
  }

  @Override
  public ProcessDefinition getLatestProcessDefinition(String bpmnProcessId) {
    try {
      return processDefinitionApi
          .getProcessDefinitions(
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              bpmnProcessId,
              null,
              null,
              null,
              null,
              null,
              true,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null)
          .stream()
          .map(this::map)
          .findFirst()
          .orElseThrow(
              () ->
                  new RuntimeException(
                      "No process definition found for bpmnProcessId '" + bpmnProcessId + "'"));
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ProcessDefinition getProcessDefinition(String processDefinitionId) {
    try {
      return map(processDefinitionApi.getProcessDefinition(processDefinitionId));
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public long getProcessInstanceCount(String processDefinitionId) {
    try {
      return processInstanceApi
          .getProcessInstancesCount(
              null,
              null,
              null,
              null,
              processDefinitionId,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null)
          .getCount();
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<ProcessInstance> getProcessInstances(
      String processDefinitionId, int offset, int limit) {
    ProcessInstanceQueryDto query = new ProcessInstanceQueryDto();
    query.setProcessDefinitionId(processDefinitionId);
    try {
      return processInstanceApi.queryProcessInstances(offset, limit, query).stream()
          .map(this::map)
          .collect(Collectors.toList());
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ProcessInstance getProcessInstance(String processInstanceId) {
    try {
      return map(processInstanceApi.getProcessInstance(processInstanceId));
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ActivityInstance getActivityInstance(String processInstanceId) {
    try {
      return map(processInstanceApi.getActivityInstanceTree(processInstanceId));
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<ProcessInstance> getProcessInstancesBySuperProcessInstanceId(
      String superProcessInstanceId) {
    ProcessInstanceQueryDto query = new ProcessInstanceQueryDto();
    query.setSuperProcessInstance(superProcessInstanceId);
    try {
      return processInstanceApi.queryProcessInstances(0, 10000, query).stream()
          .map(this::map)
          .collect(Collectors.toList());
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<VariableInstance> getVariableInstances(String activityInstanceId) {
    VariableInstanceQueryDto query = new VariableInstanceQueryDto();
    query.setActivityInstanceIdIn(Collections.singletonList(activityInstanceId));
    try {
      return variableInstanceApi.queryVariableInstances(0, 10000, false, query).stream()
          .map(this::map)
          .collect(Collectors.toList());
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setVariable(String processInstanceId, String variableName, Long variableValue) {
    VariableValueDto value = new VariableValueDto();
    value.setValue(variableValue);
    value.setType("Long");
    try {
      processInstanceApi.setProcessInstanceVariable(processInstanceId, variableName, value);
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void suspendProcessInstance(String processInstanceId) {
    try {
      processInstanceApi.updateSuspensionStateById(
          processInstanceId, new SuspensionStateDto().suspended(true));
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteProcessInstance(String processInstanceId, String reason) {
    try {
      processInstanceApi.deleteProcessInstance(processInstanceId, true, true, true, false);
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void activateProcessInstance(String processInstanceId) {
    try {
      processInstanceApi.updateSuspensionStateById(
          processInstanceId, new SuspensionStateDto().suspended(false));
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean isTransitionExecuted(String executionId) {
    try {
      List<JobDto> jobs = jobApi.queryJobs(0, 100, new JobQueryDto().executionId(executionId));
      if (jobs.size() == 1) {
        List<JobDefinitionDto> jobDefinitions =
            jobDefinitionApi.queryJobDefinitions(
                0,
                1000,
                new JobDefinitionQueryDto().jobDefinitionId(jobs.get(0).getJobDefinitionId()));
        if (jobDefinitions.size() == 1) {
          return jobDefinitions.get(0).getJobConfiguration().contains("after");
        }
        throw new IllegalStateException(
            "Found not exactly one job definition for id '"
                + jobs.get(0).getJobDefinitionId()
                + "'");
      }
      throw new IllegalStateException(
          "Found not exactly one job for execution id '" + executionId + "'");
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  private ProcessDefinition map(ProcessDefinitionDto processDefinition) {
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

  private ProcessInstance map(ProcessInstanceDto processInstance) {
    return new ProcessInstance() {
      @Override
      public String getId() {
        return processInstance.getId();
      }

      @Override
      public String getProcessDefinitionId() {
        return processInstance.getDefinitionId();
      }

      @Override
      public String getSuperExecutionId() {
        throw new IllegalStateException(
            "'superExecution' is not yet accessible from Camunda 7 REST API");
      }
    };
  }

  private ActivityInstance map(ActivityInstanceDto activityInstance) {
    return new ActivityInstance() {
      @Override
      public List<ActivityInstance> getChildActivityInstances() {
        return Stream.of(
                activityInstance.getChildActivityInstances().stream().map(a -> map(a)),
                activityInstance.getChildTransitionInstances().stream().map(a -> map(a)))
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
        return activityInstance.getExecutionIds();
      }

      @Override
      public String getExecutionId() {
        if (activityInstance.getExecutionIds() == null
            || activityInstance.getExecutionIds().isEmpty()) {
          return null;
        }
        if (activityInstance.getExecutionIds().size() == 1) {
          return activityInstance.getExecutionIds().get(0);
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

  private ActivityInstance map(TransitionInstanceDto transitionInstance) {
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

  private VariableInstance map(VariableInstanceDto variableInstance) {
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
