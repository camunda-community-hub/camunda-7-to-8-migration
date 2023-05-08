package org.camunda.community.migration.processInstance.api.model.data.chunk;

import java.util.Map;

public interface ActivityContainerData {
  Map<String, ActivityNodeData> getActivities();

  interface ActivityContainerDataBuilder<T> {
    T withActivity(String name, ActivityNodeData activity);

    T withActivities(Map<String, ActivityNodeData> activities);
  }
}
