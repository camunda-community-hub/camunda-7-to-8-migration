package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.BusinessRuleTaskData;
import org.camunda.community.migration.processInstance.api.model.data.BusinessRuleTaskData.BusinessRuleTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.BusinessRuleTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

public class BusinessRuleTaskDataBuilderImpl
    extends ActivityNodeDataBuilderImpl<
        BusinessRuleTaskDataBuilder, BusinessRuleTaskData, BusinessRuleTaskDataImpl>
    implements BusinessRuleTaskDataBuilder {
  @Override
  protected BusinessRuleTaskDataBuilder builder() {
    return this;
  }

  @Override
  protected ActivityNodeDataImpl data() {
    return data;
  }

  @Override
  protected BusinessRuleTaskDataImpl createData() {
    return new BusinessRuleTaskDataImpl();
  }
}
