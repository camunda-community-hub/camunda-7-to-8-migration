package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.CallActivityData;
import org.camunda.community.migration.processInstance.api.model.data.CallActivityData.CallActivityDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.impl.CallActivityDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class CallActivityBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        CallActivityDataBuilder, CallActivityData, CallActivityDataImpl>
    implements CallActivityDataBuilder {
  @Override
  public CallActivityDataBuilder withProcessInstance(ProcessInstanceData processInstance) {
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
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
