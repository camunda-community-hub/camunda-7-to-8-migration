package org.camunda.community.migration.converter.conversion;

import java.util.List;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DiagramCheckResult.ElementCheckMessage;
import org.camunda.community.migration.converter.convertible.Convertible;

public interface Conversion {
  void convert(DomElement element, Convertible convertible, List<ElementCheckMessage> messages);
}
