package org.camunda.community.migration.processInstance.api.model.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.camunda.community.migration.processInstance.api.model.FinalBuildStep;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityContainerData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.NamedNodeData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.VariableScopeData;
import org.camunda.community.migration.processInstance.api.model.data.impl.ProcessInstanceDataImpl;

@JsonDeserialize(as = ProcessInstanceDataImpl.class)
public interface ProcessInstanceData
    extends NamedNodeData, VariableScopeData, ProcessInstanceMetadata, ActivityContainerData {

  interface ProcessInstanceDataBuilder
      extends GenericProcessInstanceMetadataBuilder<ProcessInstanceDataBuilder>,
          NamedNodeDataBuilder<ProcessInstanceDataBuilder>,
          VariableScopeDataBuilder<ProcessInstanceDataBuilder>,
          FinalBuildStep<ProcessInstanceData>,
          ActivityContainerDataBuilder<ProcessInstanceDataBuilder> {}
}
