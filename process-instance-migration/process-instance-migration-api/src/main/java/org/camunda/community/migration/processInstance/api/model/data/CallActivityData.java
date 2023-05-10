package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface CallActivityData extends CommonActivityNodeData {
  ProcessInstanceData getProcessInstance();

  interface CallActivityDataBuilder
      extends CommonActivityNodeDataBuilder<CallActivityDataBuilder, CallActivityData> {
    CallActivityDataBuilder withProcessInstance(ProcessInstanceData processInstance);
  }
}
