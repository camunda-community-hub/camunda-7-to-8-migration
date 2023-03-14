package org.camunda.community.migration.processInstance.api.model.data.impl;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Objects;
import org.camunda.community.migration.processInstance.api.model.data.MultiInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

public final class MultiInstanceDataImpl extends ActivityNodeDataImpl implements MultiInstanceData {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MultiInstanceDataImpl that = (MultiInstanceDataImpl) o;
    return Objects.equals(instances, that.instances)
        && Objects.equals(inputElementName, that.inputElementName)
        && Objects.equals(completedInstanceElementValues, that.completedInstanceElementValues);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), instances, inputElementName, completedInstanceElementValues);
  }
}
