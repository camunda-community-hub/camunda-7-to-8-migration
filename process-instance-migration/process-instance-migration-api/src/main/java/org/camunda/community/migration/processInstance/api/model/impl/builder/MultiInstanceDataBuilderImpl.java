package org.camunda.community.migration.processInstance.api.model.impl.builder;

import static org.camunda.community.migration.processInstance.api.model.impl.builder.BuilderUtil.*;

import com.fasterxml.jackson.databind.JsonNode;
import org.camunda.community.migration.processInstance.api.model.MultiInstanceData;
import org.camunda.community.migration.processInstance.api.model.MultiInstanceData.MultiInstanceDataBuilder;
import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.impl.MultiInstanceDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.chunk.ActivityNodeDataImpl;

public class MultiInstanceDataBuilderImpl
    extends ActivityNodeDataBuilderImpl<
        MultiInstanceDataBuilder, MultiInstanceData, MultiInstanceDataImpl>
    implements MultiInstanceDataBuilder {
  @Override
  public MultiInstanceDataBuilder instance(ActivityNodeData instance) {
    addListEntry(data::getInstances, data::setInstances, instance);
    return this;
  }

  @Override
  public MultiInstanceDataBuilder inputElementName(String inputElementName) {
    data.setInputElementName(inputElementName);
    return this;
  }

  @Override
  public MultiInstanceDataBuilder completedInstanceElementValue(
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
