package org.camunda.community.migration.processInstance.api.model.data.impl;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public class ProcessInstanceDataImpl extends ProcessInstanceMetadataImpl
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
}
