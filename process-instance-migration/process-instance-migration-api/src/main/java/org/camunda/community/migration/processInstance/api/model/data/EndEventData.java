package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface EndEventData extends CommonActivityNodeData {
  interface EndEventDataBuilder
      extends CommonActivityNodeDataBuilder<EndEventDataBuilder, EndEventData> {}
}
