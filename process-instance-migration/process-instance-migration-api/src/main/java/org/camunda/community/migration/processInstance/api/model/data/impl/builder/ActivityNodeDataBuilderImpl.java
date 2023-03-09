package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import static org.camunda.community.migration.processInstance.api.model.data.impl.builder.BuilderUtil.*;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.camunda.community.migration.processInstance.api.model.FinalBuildStep.FinalBuildStepImpl;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData.ActivityNodeDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

public abstract class ActivityNodeDataBuilderImpl<
        B extends ActivityNodeDataBuilder<B, T>, T extends ActivityNodeData, I extends T>
    extends FinalBuildStepImpl<T, I> implements ActivityNodeDataBuilder<B, T> {
  @Override
  public B withName(String name) {
    data().setName(name);
    return builder();
  }

  @Override
  public B withVariable(String variableName, JsonNode variableValue) {
    addMapEntry(data()::getVariables, data()::setVariables, variableName, variableValue);
    return builder();
  }

  @Override
  public B withVariables(Map<String, JsonNode> variables) {
    variables.forEach(this::withVariable);
    return builder();
  }

  protected abstract B builder();

  protected abstract ActivityNodeDataImpl data();
}
