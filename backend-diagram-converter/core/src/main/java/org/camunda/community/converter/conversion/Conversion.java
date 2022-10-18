package org.camunda.community.converter.conversion;

import java.util.List;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.BpmnDiagramCheckResult.BpmnElementCheckMessage;
import org.camunda.community.converter.convertible.Convertible;

public interface Conversion {
  void convert(DomElement element, Convertible convertible, List<BpmnElementCheckMessage> messages);
}
