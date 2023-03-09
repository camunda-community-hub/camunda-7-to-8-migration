package org.camunda.community.migration.processInstance.api.model.data;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collection;
import java.util.List;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface MultiInstanceData extends ActivityNodeData {
  List<ActivityNodeData> getInstances();

  String getInputElementName();

  List<JsonNode> getCompletedInstanceElementValues();

  interface MultiInstanceDataBuilder
      extends ActivityNodeDataBuilder<MultiInstanceDataBuilder, MultiInstanceData> {
    MultiInstanceDataBuilder withInstance(ActivityNodeData instance);

    MultiInstanceDataBuilder withInstances(Collection<ActivityNodeData> instances);

    MultiInstanceDataBuilder withInputElementName(String inputElementName);

    MultiInstanceDataBuilder withCompletedInstanceElementValue(
        JsonNode completedInstanceElementValue);
  }
}
