package org.camunda.community.migration.processInstance.api.model.impl;

import org.camunda.community.migration.processInstance.api.model.CallActivityData;
import org.camunda.community.migration.processInstance.api.model.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.impl.chunk.ActivityNodeDataImpl;

public class CallActivityDataImpl extends ActivityNodeDataImpl implements CallActivityData {

  private ProcessInstanceData processInstance;

  @Override
  public ProcessInstanceData getProcessInstance() {
    return processInstance;
  }

  public void setProcessInstance(ProcessInstanceData processInstance) {
    this.processInstance = processInstance;
  }
}
