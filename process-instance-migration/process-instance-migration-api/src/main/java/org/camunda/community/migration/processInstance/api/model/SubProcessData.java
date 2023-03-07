package org.camunda.community.migration.processInstance.api.model;

import java.util.Map;
import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;

public interface SubProcessData extends ActivityNodeData {
  Map<String, ActivityNodeData> getActivities();

  interface SubProcessDataBuilder
      extends ActivityNodeDataBuilder<SubProcessDataBuilder, SubProcessData> {
    SubProcessDataBuilder activity(String name, ActivityNodeData activity);
  }
}
