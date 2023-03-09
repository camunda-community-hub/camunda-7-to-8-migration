package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityContainerData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface SubProcessData extends ActivityNodeData, ActivityContainerData {

  interface SubProcessDataBuilder
      extends ActivityNodeDataBuilder<SubProcessDataBuilder, SubProcessData>,
          ActivityContainerDataBuilder<SubProcessDataBuilder> {}
}
