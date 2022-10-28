package org.camunda.community.converter.exception;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.visitor.DomElementVisitor;

public class VisitorException extends RuntimeException {
  private final Class<? extends DomElementVisitor> exceptionCausingVisitor;
  private final DomElement element;

  public VisitorException(
      Class<? extends DomElementVisitor> exceptionCausingVisitor,
      DomElement element,
      Throwable cause) {
    super(cause);
    this.exceptionCausingVisitor = exceptionCausingVisitor;
    this.element = element;
  }

  @Override
  public String getMessage() {
    return "Exception in visitor "
        + exceptionCausingVisitor.getName()
        + " while visiting element "
        + buildPath(element)
        + " caused by: "
        + getCause().getMessage();
  }

  private String buildPath(DomElement element) {
    if (element == null) {
      return "";
    }
    String thisElement = element.getPrefix() + ":" + element.getLocalName();
    if (element.getAttribute("id") != null) {
      thisElement += " (" + element.getAttribute("id") + ")";
    }
    return buildPath(element.getParentElement()) + " -> " + thisElement;
  }
}
