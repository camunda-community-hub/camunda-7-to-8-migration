package org.camunda.community.migration.processInstance.api.model.chunk;

public interface FinalBuildStep<T> {
  T build();
}
