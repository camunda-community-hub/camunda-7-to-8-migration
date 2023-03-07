package org.camunda.community.migration.processInstance.api.model.impl.builder;

import org.camunda.community.migration.processInstance.api.model.SubProcessData;
import org.camunda.community.migration.processInstance.api.model.SubProcessData.SubProcessDataBuilder;
import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.impl.SubProcessDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.chunk.ActivityNodeDataImpl;

public class SubProcessDataBuilderImpl
    extends ActivityNodeDataBuilderImpl<SubProcessDataBuilder, SubProcessData, SubProcessDataImpl>
    implements SubProcessDataBuilder {
  @Override
  public SubProcessDataBuilder activity(String name, ActivityNodeData activity) {
    BuilderUtil.addMapEntry(data::getActivities, data::setActivities, name, activity);
    return this;
  }

  @Override
  protected SubProcessDataBuilder builder() {
    return this;
  }

  @Override
  protected ActivityNodeDataImpl data() {
    return data;
  }

  @Override
  protected SubProcessDataImpl createData() {
    return new SubProcessDataImpl();
  }
}
