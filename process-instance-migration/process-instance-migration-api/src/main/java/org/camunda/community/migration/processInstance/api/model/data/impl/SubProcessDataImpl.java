package org.camunda.community.migration.processInstance.api.model.data.impl;

import java.util.Map;
import org.camunda.community.migration.processInstance.api.model.data.SubProcessData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

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
