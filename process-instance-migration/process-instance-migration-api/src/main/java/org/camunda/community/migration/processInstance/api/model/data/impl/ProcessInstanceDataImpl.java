package org.camunda.community.migration.processInstance.api.model.data.impl;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public final class ProcessInstanceDataImpl extends ProcessInstanceMetadataImpl
    implements ProcessInstanceData {
  private Map<String, ActivityNodeData> activities;
  private Map<String, JsonNode> variables;

  @Override
  public Map<String, ActivityNodeData> getActivities() {
    return activities;
  }

  public void setActivities(Map<String, ActivityNodeData> activities) {
    this.activities = activities;
  }

  @Override
  public Map<String, JsonNode> getVariables() {
    return variables;
  }

  public void setVariables(Map<String, JsonNode> variables) {
    this.variables = variables;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ProcessInstanceDataImpl that = (ProcessInstanceDataImpl) o;
    return Objects.equals(activities, that.activities) && Objects.equals(variables, that.variables);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), activities, variables);
  }
}
