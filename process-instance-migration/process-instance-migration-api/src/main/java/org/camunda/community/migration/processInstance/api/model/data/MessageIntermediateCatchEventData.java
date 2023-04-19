package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface MessageIntermediateCatchEventData extends ActivityNodeData {
  interface MessageIntermediateCatchEventDataBuilder
      extends ActivityNodeDataBuilder<
          MessageIntermediateCatchEventDataBuilder, MessageIntermediateCatchEventData> {}
}
