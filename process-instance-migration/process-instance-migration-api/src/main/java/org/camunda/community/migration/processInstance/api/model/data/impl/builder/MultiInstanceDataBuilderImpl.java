package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import static org.camunda.community.migration.processInstance.api.model.data.impl.builder.BuilderUtil.*;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collection;
import org.camunda.community.migration.processInstance.api.model.data.MultiInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.MultiInstanceData.MultiInstanceDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.impl.MultiInstanceDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

public class MultiInstanceDataBuilderImpl
    extends ActivityNodeDataBuilderImpl<
        MultiInstanceDataBuilder, MultiInstanceData, MultiInstanceDataImpl>
    implements MultiInstanceDataBuilder {
  @Override
  public MultiInstanceDataBuilder withInstance(ActivityNodeData instance) {
    addListEntry(data::getInstances, data::setInstances, instance);
    return this;
  }

  @Override
  public MultiInstanceDataBuilder withInstances(Collection<ActivityNodeData> instances) {
    instances.forEach(this::withInstance);
    return this;
  }

  @Override
  public MultiInstanceDataBuilder withInputElementName(String inputElementName) {
    data.setInputElementName(inputElementName);
    return this;
  }

  @Override
  public MultiInstanceDataBuilder withCompletedInstanceElementValue(
      JsonNode completedInstanceElementValue) {
    addListEntry(
        data::getCompletedInstanceElementValues,
        data::setCompletedInstanceElementValues,
        completedInstanceElementValue);
    return this;
  }

  @Override
  protected MultiInstanceDataImpl createData() {
    return new MultiInstanceDataImpl();
  }

  @Override
  protected MultiInstanceDataBuilder builder() {
    return this;
  }

  @Override
  protected ActivityNodeDataImpl data() {
    return data;
  }
}
