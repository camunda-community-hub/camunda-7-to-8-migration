package org.camunda.community.migration.processInstance.api.model.data;

import java.util.List;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface MultiInstanceData extends ActivityNodeData {
  List<ActivityNodeData> getInstances();

  List<Integer> getCompletedInstanceLoopCounters();

  interface MultiInstanceDataBuilder
      extends ActivityNodeDataBuilder<MultiInstanceDataBuilder, MultiInstanceData> {
    MultiInstanceDataBuilder withInstance(ActivityNodeData instance);

    MultiInstanceDataBuilder withInstances(Iterable<ActivityNodeData> instances);

    MultiInstanceDataBuilder withCompletedInstanceLoopCounter(Integer loopCounter);

    MultiInstanceDataBuilder withCompletedInstanceLoopCounters(List<Integer> loopCounters);
  }
}
