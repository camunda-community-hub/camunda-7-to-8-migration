package org.camunda.community.migration.converter.visitor.impl.element;

import static org.camunda.community.migration.converter.NamespaceUri.*;

import java.util.List;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractCamundaElementVisitor;

public class ScriptVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "script";
  }

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    if (isInputOrOutput(context.getElement())) {
      return MessageFactory.inputOutputScript();
    }
    String scriptFormat = context.getElement().getAttribute("scriptFormat");
    String script = detectScript(context);
    return MessageFactory.camundaScript(
        script, scriptFormat, context.getElement().getParentElement().getLocalName());
  }

  private boolean isInputOrOutput(DomElement element) {
    if (element.getParentElement().getNamespaceURI().equals(CAMUNDA)) {
      return List.of("inputParameter", "outputParameter")
          .contains(element.getParentElement().getLocalName());
    }
    return false;
  }

  private String detectScript(DomElementVisitorContext context) {
    String resource = context.getElement().getAttribute("resource");
    if (resource == null) {
      return context.getElement().getTextContent();

    } else {
      return resource;
    }
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }
}
