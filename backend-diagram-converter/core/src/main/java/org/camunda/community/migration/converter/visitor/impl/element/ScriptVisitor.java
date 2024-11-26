package org.camunda.community.migration.converter.visitor.impl.element;

import static org.camunda.community.migration.converter.NamespaceUri.*;

import java.util.List;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.AbstractDataMapperConvertible;
import org.camunda.community.migration.converter.convertible.AbstractDataMapperConvertible.MappingDirection;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractCamundaElementVisitor;

public class ScriptVisitor extends AbstractCamundaElementVisitor {
  private static final String LOCAL_NAME_INPUT_PARAMETER = "inputParameter";
  private static final String LOCAL_NAME_OUTPUT_PARAMETER = "outputParameter";

  @Override
  public String localName() {
    return "script";
  }

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    DomElement script = context.getElement();
    if (isInputOrOutput(script)) {
      MappingDirection mappingDirection = findMappingDirection(script);
      String targetName = findTargetName(script);
      if (!isResource(script) && isFeelScript(script)) {
        String feelScript = extractFeelScript(script);
        context.addConversion(
            AbstractDataMapperConvertible.class,
            abstractTaskConversion ->
                abstractTaskConversion.addZeebeIoMapping(mappingDirection, feelScript, targetName));
        return MessageFactory.inputOutputParameterFeelScript(localName(), targetName, feelScript);
      }
    }
    String scriptFormat = context.getElement().getAttribute("scriptFormat");
    String scriptContent = detectScript(context);
    return MessageFactory.camundaScript(
        scriptContent, scriptFormat, context.getElement().getParentElement().getLocalName());
  }

  private boolean isResource(DomElement script) {
    return script.hasAttribute("resource");
  }

  private boolean isFeelScript(DomElement script) {
    String scriptFormat = script.getAttribute("scriptFormat");
    return "feel".equalsIgnoreCase(scriptFormat);
  }

  private boolean isInputOrOutput(DomElement element) {
    if (element.getParentElement().getNamespaceURI().equals(CAMUNDA)) {
      return List.of(LOCAL_NAME_INPUT_PARAMETER, LOCAL_NAME_OUTPUT_PARAMETER)
          .contains(element.getParentElement().getLocalName());
    }
    return false;
  }

  private MappingDirection findMappingDirection(DomElement element) {
    if (element.getParentElement().getLocalName().equals(LOCAL_NAME_INPUT_PARAMETER)) {
      return MappingDirection.INPUT;
    } else if (element.getParentElement().getLocalName().equals(LOCAL_NAME_OUTPUT_PARAMETER)) {
      return MappingDirection.OUTPUT;
    } else {
      throw new IllegalStateException(
          String.format(
              "Unknown parent for input/output mapping: %s",
              element.getParentElement().getLocalName()));
    }
  }

  private String findTargetName(DomElement element) {
    return element.getParentElement().getAttribute(CAMUNDA, "name");
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

  private String extractFeelScript(DomElement script) {
    return "=" + script.getTextContent();
  }
}
