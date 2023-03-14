package org.camunda.community.migration.processInstance.api.model.data.chunk;

public interface IdentifiedActivityData {
  String getActivityId();

  interface IdentifiedActivityDataBuilder<T> {
    T withActivityId(String activityId);
  }
}
