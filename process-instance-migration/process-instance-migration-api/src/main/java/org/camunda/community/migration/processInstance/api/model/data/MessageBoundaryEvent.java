package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface MessageBoundaryEvent extends CommonActivityNodeData {
  interface MessageBoundaryEventBuilder
      extends CommonActivityNodeDataBuilder<MessageBoundaryEventBuilder, MessageBoundaryEvent> {}
}
