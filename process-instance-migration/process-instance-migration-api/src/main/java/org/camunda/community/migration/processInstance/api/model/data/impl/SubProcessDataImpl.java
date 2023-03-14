package org.camunda.community.migration.processInstance.api.model.data.impl;

import java.util.Map;
import java.util.Objects;
import org.camunda.community.migration.processInstance.api.model.data.SubProcessData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

public final class SubProcessDataImpl extends ActivityNodeDataImpl implements SubProcessData {
  private Map<String, ActivityNodeData> activities;

  @Override
  public Map<String, ActivityNodeData> getActivities() {
    return activities;
  }

  public void setActivities(Map<String, ActivityNodeData> activities) {
    this.activities = activities;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SubProcessDataImpl that = (SubProcessDataImpl) o;
    return Objects.equals(activities, that.activities) && super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), activities);
  }
}
