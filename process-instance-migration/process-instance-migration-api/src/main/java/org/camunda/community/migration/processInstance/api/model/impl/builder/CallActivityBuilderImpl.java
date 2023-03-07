package org.camunda.community.migration.processInstance.api.model.impl.builder;

import org.camunda.community.migration.processInstance.api.model.CallActivityData;
import org.camunda.community.migration.processInstance.api.model.CallActivityData.CallActivityDataBuilder;
import org.camunda.community.migration.processInstance.api.model.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.impl.CallActivityDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.chunk.ActivityNodeDataImpl;

public class CallActivityBuilderImpl
    extends ActivityNodeDataBuilderImpl<
        CallActivityDataBuilder, CallActivityData, CallActivityDataImpl>
    implements CallActivityDataBuilder {
  @Override
  public CallActivityDataBuilder processInstance(ProcessInstanceData processInstance) {
    data.setProcessInstance(processInstance);
    return this;
  }

  @Override
  protected CallActivityDataImpl createData() {
    return new CallActivityDataImpl();
  }

  @Override
  protected CallActivityDataBuilder builder() {
    return this;
  }

  @Override
  protected ActivityNodeDataImpl data() {
    return data;
  }
}
