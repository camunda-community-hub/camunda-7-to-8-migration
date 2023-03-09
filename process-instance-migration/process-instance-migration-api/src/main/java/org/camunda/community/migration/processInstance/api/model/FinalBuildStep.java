package org.camunda.community.migration.processInstance.api.model;

public interface FinalBuildStep<T> {
  T build();

  abstract class FinalBuildStepImpl<T, I extends T> implements FinalBuildStep<T> {
    protected final I data;

    public FinalBuildStepImpl() {
      this.data = createData();
    }

    protected abstract I createData();

    @Override
    public T build() {
      return data;
    }
  }
}
