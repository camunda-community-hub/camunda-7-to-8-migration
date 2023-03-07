package org.camunda.community.migration.processInstance.dto;

import java.util.List;
import org.camunda.community.migration.processInstance.dto.client.JobDefinitionDto;
import org.camunda.community.migration.processInstance.dto.client.ProcessDefinitionDto;

public class Camunda7ProcessDefinitionData {
  private ProcessDefinitionDto processDefinition;
  private List<JobDefinitionDto> jobDefinitions;

  public ProcessDefinitionDto getProcessDefinition() {
    return processDefinition;
  }

  public void setProcessDefinition(ProcessDefinitionDto processDefinition) {
    this.processDefinition = processDefinition;
  }

  public List<JobDefinitionDto> getJobDefinitions() {
    return jobDefinitions;
  }

  public void setJobDefinitions(List<JobDefinitionDto> jobDefinitions) {
    this.jobDefinitions = jobDefinitions;
  }
}
