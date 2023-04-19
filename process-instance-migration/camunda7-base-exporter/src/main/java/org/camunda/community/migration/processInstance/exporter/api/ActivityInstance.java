package org.camunda.community.migration.processInstance.exporter.api;

import java.util.List;

public interface ActivityInstance {
  /**
   * Returns <b>all</b> child activities, including transitions
   *
   * @return a list if child instances are present, otherwise an empty list
   */
  List<ActivityInstance> getChildActivityInstances();

  String getActivityId();

  String getActivityType();

  String getActivityName();

  String getProcessInstanceId();

  List<String> getExecutionIds();

  /**
   * Only returns a result when there is only 1 execution id, otherwise throw
   *
   * @return execution id
   */
  String getExecutionId();

  String getId();

  boolean isTransition();
}
