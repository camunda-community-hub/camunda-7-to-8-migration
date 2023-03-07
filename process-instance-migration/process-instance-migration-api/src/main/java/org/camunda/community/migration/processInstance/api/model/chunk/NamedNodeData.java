package org.camunda.community.migration.processInstance.api.model.chunk;

public interface NamedNodeData {
  String getName();

  interface NamedNodeDataBuilder<T> {
    T name(String name);
  }
}
