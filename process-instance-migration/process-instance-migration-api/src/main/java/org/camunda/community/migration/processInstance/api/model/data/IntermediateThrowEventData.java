package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface IntermediateThrowEventData extends CommonActivityNodeData {
  interface IntermediateThrowEventDataBuilder
      extends CommonActivityNodeDataBuilder<
          IntermediateThrowEventDataBuilder, IntermediateThrowEventData> {}
}
