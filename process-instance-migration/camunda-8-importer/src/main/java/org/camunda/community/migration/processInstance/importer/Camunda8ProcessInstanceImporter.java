package org.camunda.community.migration.processInstance.importer;

import java.util.Set;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.importer.visitor.ActivityNodeDataVisitor;

public class Camunda8ProcessInstanceImporter {
  private final Set<ActivityNodeDataVisitor> activityNodeDataHandlers;

  public Camunda8ProcessInstanceImporter(Set<ActivityNodeDataVisitor> activityNodeDataHandlers) {
    this.activityNodeDataHandlers = activityNodeDataHandlers;
  }

  public long importProcessInstance(ProcessInstanceData processInstanceData) {
    // TODO create a process instance and drive it to the described state
    return 0;
  }
}
