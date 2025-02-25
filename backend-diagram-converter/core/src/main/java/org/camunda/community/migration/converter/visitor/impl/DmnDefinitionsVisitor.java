package org.camunda.community.migration.converter.visitor.impl;

import static org.camunda.community.migration.converter.NamespaceUri.*;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractDmnElementVisitor;

public class DmnDefinitionsVisitor extends AbstractDmnElementVisitor {
  private static final String VERSION_HEADER = "executionPlatformVersion";
  private static final String PLATFORM_HEADER = "executionPlatform";
  private static final String CONVERTER_VERSION_HEADER = "converterVersion";
  private static final String PLATFORM_VALUE = "Camunda Cloud";
  private static final String ZEEBE_NAMESPACE_NAME = "zeebe";
  private static final String CONVERSION_NAMESPACE_NAME = "conversion";
  private static final String MODELER_NAMESPACE_NAME = "modeler";

  @Override
  protected void visitDmnElement(DomElementVisitorContext context) {
    SemanticVersion desiredVersion =
        SemanticVersion.parse(context.getProperties().getPlatformVersion());
    DomElement element = context.getElement();
    String executionPlatform = element.getAttribute(NamespaceUri.MODELER, VERSION_HEADER);
    if (executionPlatform != null && executionPlatform.startsWith("8")) {
      throw new RuntimeException("This diagram is already a Camunda 8 diagram");
    }
    element.registerNamespace(MODELER_NAMESPACE_NAME, NamespaceUri.MODELER);
    element.registerNamespace(ZEEBE_NAMESPACE_NAME, NamespaceUri.ZEEBE);
    element.registerNamespace(CONVERSION_NAMESPACE_NAME, CONVERSION);
    element.setAttribute(NamespaceUri.MODELER, PLATFORM_HEADER, PLATFORM_VALUE);
    element.setAttribute(
        NamespaceUri.MODELER, VERSION_HEADER, desiredVersion.getPatchZeroVersion());
    element.setAttribute(
        CONVERSION, CONVERTER_VERSION_HEADER, getClass().getPackage().getImplementationVersion());
  }

  @Override
  public String localName() {
    return "definitions";
  }
}
