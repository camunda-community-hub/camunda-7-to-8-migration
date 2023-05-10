package org.camunda.community.migration.processInstance.api.model.data.chunk;

import org.camunda.community.migration.processInstance.api.model.FinalBuildStep;

public interface CommonActivityNodeData
    extends ActivityNodeData, NamedNodeData, VariableScopeData, ExecutionData {
  interface CommonActivityNodeDataBuilder<
          T extends CommonActivityNodeDataBuilder<T, R>, R extends CommonActivityNodeData>
      extends FinalBuildStep<R>,
          NamedNodeDataBuilder<T>,
          VariableScopeDataBuilder<T>,
          ExecutionDataBuilder<T> {}
}
