package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface MessageBoundaryEventData extends CommonActivityNodeData {
  interface MessageBoundaryEventDataBuilder
      extends CommonActivityNodeDataBuilder<
          MessageBoundaryEventDataBuilder, MessageBoundaryEventData> {}
}
