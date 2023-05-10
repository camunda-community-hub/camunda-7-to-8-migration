package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityContainerData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface TransactionData extends CommonActivityNodeData, ActivityContainerData {
  interface TransactionDataBuilder
      extends CommonActivityNodeDataBuilder<TransactionDataBuilder, TransactionData>,
          ActivityContainerDataBuilder<TransactionDataBuilder> {}
}
