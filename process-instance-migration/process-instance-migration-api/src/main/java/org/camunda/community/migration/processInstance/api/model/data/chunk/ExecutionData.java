package org.camunda.community.migration.processInstance.api.model.data.chunk;

public interface ExecutionData {
  Boolean getExecuted();

  interface ExecutionDataBuilder<T> {
    T withExecuted(Boolean executed);
  }
}
