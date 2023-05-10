package org.camunda.community.migration.processInstance.api.model.data.impl;

import java.util.Objects;
import org.camunda.community.migration.processInstance.api.model.data.CallActivityData;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public final class CallActivityDataImpl extends CommonActivityNodeDataImpl
    implements CallActivityData {

  private ProcessInstanceData processInstance;

  @Override
  public ProcessInstanceData getProcessInstance() {
    return processInstance;
  }

  public void setProcessInstance(ProcessInstanceData processInstance) {
    this.processInstance = processInstance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CallActivityDataImpl that = (CallActivityDataImpl) o;
    return Objects.equals(processInstance, that.processInstance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), processInstance);
  }
}
