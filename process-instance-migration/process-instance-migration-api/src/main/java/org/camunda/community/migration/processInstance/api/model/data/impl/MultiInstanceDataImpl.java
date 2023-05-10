package org.camunda.community.migration.processInstance.api.model.data.impl;

import java.util.List;
import java.util.Objects;
import org.camunda.community.migration.processInstance.api.model.data.MultiInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public final class MultiInstanceDataImpl extends CommonActivityNodeDataImpl
    implements MultiInstanceData {
  private List<ActivityNodeData> instances;
  private List<Integer> completedInstanceLoopCounters;

  @Override
  public List<Integer> getCompletedInstanceLoopCounters() {
    return completedInstanceLoopCounters;
  }

  public void setCompletedInstanceLoopCounters(List<Integer> completedInstanceLoopCounters) {
    this.completedInstanceLoopCounters = completedInstanceLoopCounters;
  }

  @Override
  public List<ActivityNodeData> getInstances() {
    return instances;
  }

  public void setInstances(List<ActivityNodeData> instances) {
    this.instances = instances;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MultiInstanceDataImpl that = (MultiInstanceDataImpl) o;
    return Objects.equals(instances, that.instances)
        && Objects.equals(completedInstanceLoopCounters, that.completedInstanceLoopCounters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), instances, completedInstanceLoopCounters);
  }
}
