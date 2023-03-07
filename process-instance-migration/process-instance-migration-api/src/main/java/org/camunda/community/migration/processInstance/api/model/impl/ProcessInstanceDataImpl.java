package org.camunda.community.migration.processInstance.api.model.impl;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.camunda.community.migration.processInstance.api.model.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;

public class ProcessInstanceDataImpl implements ProcessInstanceData {
  private String bpmnProcessId;
  private String id;
  private Map<String, ActivityNodeData> activities;
  private String name;
  private Map<String, JsonNode> variables;

  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getBpmnProcessId() {
    return bpmnProcessId;
  }

  public void setBpmnProcessId(String bpmnProcessId) {
    this.bpmnProcessId = bpmnProcessId;
  }

  @Override
  public Map<String, ActivityNodeData> getActivities() {
    return activities;
  }

  public void setActivities(Map<String, ActivityNodeData> activities) {
    this.activities = activities;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Map<String, JsonNode> getVariables() {
    return variables;
  }

  public void setVariables(Map<String, JsonNode> variables) {
    this.variables = variables;
  }
}
