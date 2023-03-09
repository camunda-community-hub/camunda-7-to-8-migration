package org.camunda.community.migration.processInstance.api.model.data.impl;

import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceMetadata;

public class ProcessInstanceMetadataImpl implements ProcessInstanceMetadata {
  private String id;
  private String processDefinitionKey;
  private String bpmnProcessId;
  private String name;

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void setProcessDefinitionKey(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
  }

  @Override
  public String getBpmnProcessId() {
    return bpmnProcessId;
  }

  public void setBpmnProcessId(String bpmnProcessId) {
    this.bpmnProcessId = bpmnProcessId;
  }
}
