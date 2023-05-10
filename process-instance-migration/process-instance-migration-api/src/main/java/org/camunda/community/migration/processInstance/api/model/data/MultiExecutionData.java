package org.camunda.community.migration.processInstance.api.model.data;

import java.util.List;
import org.camunda.community.migration.processInstance.api.model.FinalBuildStep;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.NamedNodeData;

public interface MultiExecutionData extends ActivityNodeData, NamedNodeData {
  List<CommonActivityNodeData> getActivities();

  interface MultiExecutionDataBuilder
      extends NamedNodeDataBuilder<MultiExecutionDataBuilder>, FinalBuildStep<MultiExecutionData> {
    MultiExecutionDataBuilder withActivity(CommonActivityNodeData activity);

    MultiExecutionDataBuilder withActivities(List<CommonActivityNodeData> activities);
  }
}
