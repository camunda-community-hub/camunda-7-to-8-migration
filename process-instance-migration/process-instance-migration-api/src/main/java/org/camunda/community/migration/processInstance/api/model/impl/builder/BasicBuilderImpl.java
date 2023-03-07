package org.camunda.community.migration.processInstance.api.model.impl.builder;

import org.camunda.community.migration.processInstance.api.model.chunk.FinalBuildStep;

public abstract class BasicBuilderImpl<T, I extends T> implements FinalBuildStep<T> {
  protected final I data;

  public BasicBuilderImpl() {
    this.data = createData();
  }

  protected abstract I createData();

  @Override
  public T build() {
    return data;
  }
}
