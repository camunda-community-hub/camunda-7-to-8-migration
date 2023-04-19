package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityContainerData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface TransactionData extends ActivityNodeData, ActivityContainerData {
  interface TransactionDataBuilder
      extends ActivityNodeDataBuilder<TransactionDataBuilder, TransactionData>,
          ActivityContainerDataBuilder<TransactionDataBuilder> {}
}
