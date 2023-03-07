package org.camunda.community.migration.processInstance.api.model.impl;

import java.util.Map;
import org.camunda.community.migration.processInstance.api.model.SubProcessData;
import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.impl.chunk.ActivityNodeDataImpl;

public class SubProcessDataImpl extends ActivityNodeDataImpl implements SubProcessData {
  private Map<String, ActivityNodeData> activities;

  @Override
  public Map<String, ActivityNodeData> getActivities() {
    return activities;
  }

  public void setActivities(Map<String, ActivityNodeData> activities) {
    this.activities = activities;
  }
}
