package org.camunda.community.migration.processInstance.api.model.impl.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BuilderUtil {
  static <T> void addListEntry(Supplier<List<T>> getter, Consumer<List<T>> setter, T value) {
    addEntry(getter, setter, ArrayList::new, list -> list.add(value));
  }

  static <K, V> void addMapEntry(
      Supplier<Map<K, V>> getter, Consumer<Map<K, V>> setter, K key, V value) {
    addEntry(getter, setter, HashMap::new, map -> map.put(key, value));
  }

  private static <T> void addEntry(
      Supplier<T> getter, Consumer<T> setter, Supplier<T> initializer, Consumer<T> adder) {
    if (getter.get() == null) {
      setter.accept(initializer.get());
    }
    adder.accept(getter.get());
  }
}
