package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface MessageEndEventData extends CommonActivityNodeData {
  interface MessageEndEventDataBuilder
      extends CommonActivityNodeDataBuilder<MessageEndEventDataBuilder, MessageEndEventData> {}
}
