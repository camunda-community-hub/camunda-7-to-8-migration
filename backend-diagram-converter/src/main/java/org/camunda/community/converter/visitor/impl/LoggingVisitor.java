package org.camunda.community.converter.visitor.impl;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.DomElementVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingVisitor implements DomElementVisitor {
  private static final Logger LOG = LoggerFactory.getLogger(LoggingVisitor.class);

  @Override
  public void visit(DomElementVisitorContext context) {
    DomElement element = context.getElement();
    String indent =
        IntStream.range(0, getIndent(element) + 1).mapToObj(i -> " ").collect(Collectors.joining());
    String elementId = element.getAttribute("id");
    if (elementId == null) {
      elementId = "";
    }
    String elementString = element.getPrefix() + ":" + element.getLocalName() + ":" + elementId;
    LOG.debug("{}{}", indent, elementString);
  }

  private int getIndent(DomElement element) {
    int indent = 0;
    DomElement current = element;
    while (current != element.getRootElement() && current != null) {
      indent++;
      current = current.getParentElement();
    }
    return indent;
  }
}
