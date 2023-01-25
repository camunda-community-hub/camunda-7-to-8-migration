package org.camunda.community.migration.converter.conversion;

import static org.camunda.community.migration.converter.BpmnElementFactory.*;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.convertible.ScriptTaskConvertible;

public class ScriptTaskConversion extends AbstractTypedConversion<ScriptTaskConvertible> {
  @Override
  protected Class<ScriptTaskConvertible> type() {
    return ScriptTaskConvertible.class;
  }

  @Override
  protected void convertTyped(DomElement element, ScriptTaskConvertible convertible) {
    DomElement extensionElements = getExtensionElements(element);
    extensionElements.appendChild(createScriptElement(element.getDocument(), convertible));
  }

  private DomElement createScriptElement(DomDocument document, ScriptTaskConvertible convertible) {
    DomElement scriptElement = document.createElement(NamespaceUri.ZEEBE, "script");
    if (convertible.getExpression() != null) {
      scriptElement.setAttribute("expression", convertible.getExpression());
    }
    if (convertible.getResultVariable() != null) {
      scriptElement.setAttribute("resultVariable", convertible.getResultVariable());
    }
    return scriptElement;
  }
}
