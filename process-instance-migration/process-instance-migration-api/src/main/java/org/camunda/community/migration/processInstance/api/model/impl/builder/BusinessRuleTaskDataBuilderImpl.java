package org.camunda.community.migration.processInstance.api.model.impl.builder;

import org.camunda.community.migration.processInstance.api.model.BusinessRuleTaskData;
import org.camunda.community.migration.processInstance.api.model.BusinessRuleTaskData.BusinessRuleTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.impl.BusinessRuleTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.chunk.ActivityNodeDataImpl;

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
