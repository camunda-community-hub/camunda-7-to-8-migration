package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class ScriptVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "script";
  }

  @Override
  protected String visitCamundaElement(DomElementVisitorContext context) {
    String response = "";
    String scriptFormat = context.getElement().getAttribute("scriptFormat");
    if (scriptFormat == null || scriptFormat.trim().length() == 0) {
      response += "No script format could be detected, ";
    } else {
      response += "Script format is '" + scriptFormat + "', ";
    }
    String resource = context.getElement().getAttribute("resource");
    if (resource == null) {
      String script = context.getElement().getTextContent();
      response += "script is: '" + script + "'";
    } else {
      response += "script resource is '" + resource + "'";
    }
    return response;
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }
}
