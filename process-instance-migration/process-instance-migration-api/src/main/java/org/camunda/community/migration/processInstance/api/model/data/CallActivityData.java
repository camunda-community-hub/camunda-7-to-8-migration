package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface CallActivityData extends ActivityNodeData {
  ProcessInstanceData getProcessInstance();

  interface CallActivityDataBuilder
      extends ActivityNodeDataBuilder<CallActivityDataBuilder, CallActivityData> {
    CallActivityDataBuilder withProcessInstance(ProcessInstanceData processInstance);
  }
}
