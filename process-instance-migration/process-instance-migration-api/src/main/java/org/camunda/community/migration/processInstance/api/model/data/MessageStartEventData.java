package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface MessageStartEventData extends CommonActivityNodeData {
  interface MessageStartEventDataBuilder
      extends CommonActivityNodeDataBuilder<MessageStartEventDataBuilder, MessageStartEventData> {}
}
