package org.camunda.community.migration.processInstance.api.model.data;

import java.util.List;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface MultiInstanceData extends CommonActivityNodeData {
  List<ActivityNodeData> getInstances();

  List<Integer> getCompletedInstanceLoopCounters();

  interface MultiInstanceDataBuilder
      extends CommonActivityNodeDataBuilder<MultiInstanceDataBuilder, MultiInstanceData> {
    MultiInstanceDataBuilder withInstance(ActivityNodeData instance);

    MultiInstanceDataBuilder withInstances(Iterable<? extends ActivityNodeData> instances);

    MultiInstanceDataBuilder withCompletedInstanceLoopCounter(Integer loopCounter);

    MultiInstanceDataBuilder withCompletedInstanceLoopCounters(List<Integer> loopCounters);
  }
}
