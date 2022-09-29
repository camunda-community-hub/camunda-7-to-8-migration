package org.camunda.community.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.convertible.Convertible;

public interface Conversion {
  void convert(DomElement element, Convertible convertible);
}
