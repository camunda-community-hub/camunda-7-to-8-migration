package org.camunda.community.migration.processInstance.api.model.data.impl;

import java.util.List;
import java.util.Objects;
import org.camunda.community.migration.processInstance.api.model.data.MultiExecutionData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public class MultiExecutionDataImpl implements MultiExecutionData {
  private List<CommonActivityNodeData> activities;
  private String name;

  @Override
  public List<CommonActivityNodeData> getActivities() {
    return activities;
  }

  public void setActivities(List<CommonActivityNodeData> activities) {
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MultiExecutionDataImpl that = (MultiExecutionDataImpl) o;
    return Objects.equals(activities, that.activities) && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(activities, name);
  }
}
