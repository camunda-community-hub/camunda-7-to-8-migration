package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.BusinessRuleTaskData;
import org.camunda.community.migration.processInstance.api.model.data.BusinessRuleTaskData.BusinessRuleTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.BusinessRuleTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class BusinessRuleTaskDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        BusinessRuleTaskDataBuilder, BusinessRuleTaskData, BusinessRuleTaskDataImpl>
    implements BusinessRuleTaskDataBuilder {
  @Override
  protected BusinessRuleTaskDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }

  @Override
  protected BusinessRuleTaskDataImpl createData() {
    return new BusinessRuleTaskDataImpl();
  }
}
