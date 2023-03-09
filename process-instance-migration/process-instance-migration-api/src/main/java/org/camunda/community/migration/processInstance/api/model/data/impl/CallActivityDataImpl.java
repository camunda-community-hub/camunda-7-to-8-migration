package org.camunda.community.migration.processInstance.api.model.data.impl;

import org.camunda.community.migration.processInstance.api.model.data.CallActivityData;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

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
