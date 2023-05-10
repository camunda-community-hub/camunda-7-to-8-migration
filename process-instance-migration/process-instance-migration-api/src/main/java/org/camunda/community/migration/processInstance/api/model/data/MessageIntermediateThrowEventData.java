package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface MessageIntermediateThrowEventData extends CommonActivityNodeData {
  interface MessageIntermediateThrowEventDataBuilder
      extends CommonActivityNodeDataBuilder<
          MessageIntermediateThrowEventDataBuilder, MessageIntermediateThrowEventData> {}
}
