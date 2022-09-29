package org.camunda.community.converter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.camunda.bpm.model.xml.instance.DomElement;

public class BpmnDiagramCheckContext {
  private final Set<DomElement> elementsToRemove = new HashSet<>();

  private final Map<DomElement, Map<String, Set<String>>> attributesToRemove = new HashMap<>();

  public Set<DomElement> getElementsToRemove() {
    return elementsToRemove;
  }

  public Map<DomElement, Map<String, Set<String>>> getAttributesToRemove() {
    return attributesToRemove;
  }

  public void addAttributeToRemove(DomElement element, String namespaceUri, String localName) {
    attributesToRemove
        .computeIfAbsent(element, e -> new HashMap<>())
        .computeIfAbsent(namespaceUri, n -> new HashSet<>())
        .add(localName);
  }
}
