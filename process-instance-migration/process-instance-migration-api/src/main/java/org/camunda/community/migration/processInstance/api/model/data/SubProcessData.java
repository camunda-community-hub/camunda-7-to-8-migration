package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityContainerData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface SubProcessData extends CommonActivityNodeData, ActivityContainerData {

  interface SubProcessDataBuilder
      extends CommonActivityNodeDataBuilder<SubProcessDataBuilder, SubProcessData>,
          ActivityContainerDataBuilder<SubProcessDataBuilder> {}
}
