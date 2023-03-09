package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import java.util.Map;
import org.camunda.community.migration.processInstance.api.model.data.SubProcessData;
import org.camunda.community.migration.processInstance.api.model.data.SubProcessData.SubProcessDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.impl.SubProcessDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

public class SubProcessDataBuilderImpl
    extends ActivityNodeDataBuilderImpl<SubProcessDataBuilder, SubProcessData, SubProcessDataImpl>
    implements SubProcessDataBuilder {
  @Override
  public SubProcessDataBuilder withActivity(String name, ActivityNodeData activity) {
    BuilderUtil.addMapEntry(data::getActivities, data::setActivities, name, activity);
    return this;
  }

  @Override
  public SubProcessDataBuilder withActivities(Map<String, ActivityNodeData> activities) {
    activities.forEach(this::withActivity);
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
