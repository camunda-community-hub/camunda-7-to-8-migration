package org.camunda.community.migration.processInstance.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;

public interface MultiInstanceData extends ActivityNodeData {
  List<ActivityNodeData> getInstances();

  String getInputElementName();

  List<JsonNode> getCompletedInstanceElementValues();

  interface MultiInstanceDataBuilder
      extends ActivityNodeDataBuilder<MultiInstanceDataBuilder, MultiInstanceData> {
    MultiInstanceDataBuilder instance(ActivityNodeData instance);

    MultiInstanceDataBuilder inputElementName(String inputElementName);

    MultiInstanceDataBuilder completedInstanceElementValue(JsonNode completedInstanceElementValue);
  }
}
