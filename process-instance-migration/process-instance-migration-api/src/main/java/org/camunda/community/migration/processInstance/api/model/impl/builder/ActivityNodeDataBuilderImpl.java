package org.camunda.community.migration.processInstance.api.model.impl.builder;

import static org.camunda.community.migration.processInstance.api.model.impl.builder.BuilderUtil.*;

import com.fasterxml.jackson.databind.JsonNode;
import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData.ActivityNodeDataBuilder;
import org.camunda.community.migration.processInstance.api.model.impl.chunk.ActivityNodeDataImpl;

public abstract class ActivityNodeDataBuilderImpl<
        B extends ActivityNodeDataBuilder<B, T>, T extends ActivityNodeData, I extends T>
    extends BasicBuilderImpl<T, I> implements ActivityNodeDataBuilder<B, T> {
  @Override
  public B name(String name) {
    data().setName(name);
    return builder();
  }

  @Override
  public B variable(String variableName, JsonNode variableValue) {
    addMapEntry(data()::getVariables, data()::setVariables, variableName, variableValue);
    return builder();
  }

  protected abstract B builder();

  protected abstract ActivityNodeDataImpl data();
}
