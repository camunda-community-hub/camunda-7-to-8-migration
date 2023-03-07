package org.camunda.community.migration.processInstance.api.model.impl;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.camunda.community.migration.processInstance.api.model.MultiInstanceData;
import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.impl.chunk.ActivityNodeDataImpl;

public class MultiInstanceDataImpl extends ActivityNodeDataImpl implements MultiInstanceData {
  private List<ActivityNodeData> instances;
  private String inputElementName;
  private List<JsonNode> completedInstanceElementValues;

  @Override
  public List<JsonNode> getCompletedInstanceElementValues() {
    return completedInstanceElementValues;
  }

  public void setCompletedInstanceElementValues(List<JsonNode> completedInstanceElementValues) {
    this.completedInstanceElementValues = completedInstanceElementValues;
  }

  @Override
  public String getInputElementName() {
    return inputElementName;
  }

  public void setInputElementName(String inputElementName) {
    this.inputElementName = inputElementName;
  }

  @Override
  public List<ActivityNodeData> getInstances() {
    return instances;
  }

  public void setInstances(List<ActivityNodeData> instances) {
    this.instances = instances;
  }
}
