package org.camunda.community.converter.message;

import java.util.HashMap;
import java.util.Map;

public class ContextBuilder {
  private final Map<String, String> context = new HashMap<>();

  static ContextBuilder builder() {
    return new ContextBuilder();
  }

  public ContextBuilder entry(String key, String value) {
    if (context.containsKey(key) && !context.get(key).equals(value)) {
      throw new IllegalStateException("Must not override entries while building context");
    }
    context.put(key, value == null ? "null" : value);
    return this;
  }

  public ContextBuilder context(Map<String, String> context) {
    context.forEach(this::entry);
    return this;
  }

  public Map<String, String> build() {
    return context;
  }
}
