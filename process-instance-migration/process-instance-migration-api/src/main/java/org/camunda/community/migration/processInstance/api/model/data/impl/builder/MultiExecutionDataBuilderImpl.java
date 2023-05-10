package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import static org.camunda.community.migration.processInstance.api.model.data.impl.builder.BuilderUtil.*;

import java.util.List;
import org.camunda.community.migration.processInstance.api.model.data.MultiExecutionData;
import org.camunda.community.migration.processInstance.api.model.data.MultiExecutionData.MultiExecutionDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.impl.MultiExecutionDataImpl;

public class MultiExecutionDataBuilderImpl implements MultiExecutionDataBuilder {
  private final MultiExecutionDataImpl data = new MultiExecutionDataImpl();

  @Override
  public MultiExecutionData build() {
    return data;
  }

  @Override
  public MultiExecutionDataBuilder withName(String name) {
    data.setName(name);
    return this;
  }

  @Override
  public MultiExecutionDataBuilder withActivity(CommonActivityNodeData activity) {
    addListEntry(data::getActivities, data::setActivities, activity);
    return this;
  }

  @Override
  public MultiExecutionDataBuilder withActivities(List<CommonActivityNodeData> activities) {
    activities.forEach(this::withActivity);
    return this;
  }
}
