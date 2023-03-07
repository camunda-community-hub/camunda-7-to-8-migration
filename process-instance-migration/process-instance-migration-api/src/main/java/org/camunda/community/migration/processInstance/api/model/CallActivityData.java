package org.camunda.community.migration.processInstance.api.model;

import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;

public interface CallActivityData extends ActivityNodeData {
  ProcessInstanceData getProcessInstance();

  interface CallActivityDataBuilder
      extends ActivityNodeDataBuilder<CallActivityDataBuilder, CallActivityData> {
    CallActivityDataBuilder processInstance(ProcessInstanceData processInstance);
  }
}
