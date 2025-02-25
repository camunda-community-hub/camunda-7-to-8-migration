package org.camunda.community.migration.converter.visitor.impl;

import static org.camunda.community.migration.converter.NamespaceUri.*;

import java.util.function.Consumer;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractBpmnElementVisitor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BpmnDefinitionsVisitor extends AbstractBpmnElementVisitor {
  private static final String VERSION_HEADER = "executionPlatformVersion";
  private static final String PLATFORM_HEADER = "executionPlatform";
  private static final String CONVERTER_VERSION_HEADER = "converterVersion";
  private static final String PLATFORM_VALUE = "Camunda Cloud";
  private static final String ZEEBE_NAMESPACE_NAME = "zeebe";
  private static final String CONVERSION_NAMESPACE_NAME = "conversion";
  private static final String MODELER_NAMESPACE_NAME = "modeler";

  @Override
  protected void visitBpmnElement(DomElementVisitorContext context) {
    SemanticVersion desiredVersion =
        SemanticVersion.parse(context.getProperties().getPlatformVersion());
    DomElement element = context.getElement();
    String executionPlatform = element.getAttribute(NamespaceUri.MODELER, VERSION_HEADER);
    if (executionPlatform != null && executionPlatform.startsWith("8")) {
      throw new RuntimeException("This diagram is already a Camunda 8 diagram");
    }
    // TODO in case there is a requirement to override the BPMN namespace name, this can be enabled
    //    String prefix = element.getPrefix();
    //    if (prefix == null || !prefix.equals("bpmn")) {
    //      element.registerNamespace("bpmn", NamespaceUri.BPMN);
    //      setNamespace(element.getDocument().getDomSource().getNode(), prefix == null ? "" :
    // prefix);
    //    }
    element.registerNamespace(MODELER_NAMESPACE_NAME, NamespaceUri.MODELER);
    element.registerNamespace(ZEEBE_NAMESPACE_NAME, NamespaceUri.ZEEBE);
    element.registerNamespace(CONVERSION_NAMESPACE_NAME, CONVERSION);
    element.setAttribute(NamespaceUri.MODELER, PLATFORM_HEADER, PLATFORM_VALUE);
    element.setAttribute(
        NamespaceUri.MODELER, VERSION_HEADER, desiredVersion.getPatchZeroVersion());
    element.setAttribute(
        CONVERSION, CONVERTER_VERSION_HEADER, getClass().getPackage().getImplementationVersion());
  }

  private void setNamespace(Node root, String oldPrefix) {
    visitChildren(
        root.getChildNodes(),
        element -> {
          if (element.getPrefix() == null || element.getPrefix().trim().equals(oldPrefix)) {
            element.setPrefix("bpmn");
          }
          String type = element.getAttributeNS(NamespaceUri.XSI, "type");
          if (!type.equals("")) {
            String[] split = type.split(":");
            element.setAttributeNS(NamespaceUri.XSI, "type", "bpmn:" + split[split.length - 1]);
          }
        });
    String xmlns = oldPrefix.equals("") ? "xmlns" : "xmlns:" + oldPrefix;
    visitChildren(root.getChildNodes(), element -> element.removeAttribute(xmlns));
  }

  private void visitChildren(NodeList nodeList, Consumer<Element> action) {
    for (int i = 0; i < nodeList.getLength(); i++) {
      visitChild(nodeList.item(i), action);
      visitChildren(nodeList.item(i).getChildNodes(), action);
    }
  }

  private void visitChild(Node node, Consumer<Element> action) {
    if (node instanceof Element) {
      Element element = (Element) node;
      action.accept(element);
    }
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0;
  }

  @Override
  public String localName() {
    return "definitions";
  }
}
