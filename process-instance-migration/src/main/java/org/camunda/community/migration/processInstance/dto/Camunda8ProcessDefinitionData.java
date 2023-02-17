package org.camunda.community.migration.processInstance.dto;

import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;

public class Camunda8ProcessDefinitionData {
  private ProcessDefinition processDefinition;
  private BpmnModelInstance bpmnModelInstance;

  public ProcessDefinition getProcessDefinition() {
    return processDefinition;
  }

  public void setProcessDefinition(ProcessDefinition processDefinition) {
    this.processDefinition = processDefinition;
  }

  public BpmnModelInstance getBpmnModelInstance() {
    return bpmnModelInstance;
  }

  public void setBpmnModelInstance(BpmnModelInstance bpmnModelInstance) {
    this.bpmnModelInstance = bpmnModelInstance;
  }
}
