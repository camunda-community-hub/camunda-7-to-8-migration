package org.camunda.community.migration.processInstance.api.model.data.chunk;

public interface NamedNodeData {
  String getName();

  interface NamedNodeDataBuilder<T> {
    T withName(String name);
  }
}
