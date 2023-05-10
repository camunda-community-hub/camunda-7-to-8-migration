package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface MessageIntermediateCatchEventData extends CommonActivityNodeData {
  interface MessageIntermediateCatchEventDataBuilder
      extends CommonActivityNodeDataBuilder<
          MessageIntermediateCatchEventDataBuilder, MessageIntermediateCatchEventData> {}
}
