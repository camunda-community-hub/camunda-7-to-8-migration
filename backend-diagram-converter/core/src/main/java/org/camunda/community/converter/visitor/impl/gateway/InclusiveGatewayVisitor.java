package org.camunda.community.converter.visitor.impl.gateway;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.InclusiveGatewayConvertible;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.version.SemanticVersion;
import org.camunda.community.converter.version.VersionComparison;
import org.camunda.community.converter.visitor.AbstractGatewayVisitor;

public class InclusiveGatewayVisitor extends AbstractGatewayVisitor {
  @Override
  public String localName() {
    return "inclusiveGateway";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return isNotJoining(context.getElement()) && isRequiredVersion(context);
  }

  private boolean isRequiredVersion(DomElementVisitorContext context) {
    return VersionComparison.isSupported(
        context.getProperties().getPlatformVersion(), SemanticVersion._8_1_0.toString());
  }

  private boolean isNotJoining(DomElement element) {
    return element.getChildElements().stream()
            .filter(e -> e.getNamespaceURI().equals(NamespaceUri.BPMN))
            .filter(e -> e.getLocalName().equals("incoming"))
            .count()
        <= 1;
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new InclusiveGatewayConvertible();
  }

  @Override
  protected void addCannotBeConvertedMessage(DomElementVisitorContext context) {
    if (!isRequiredVersion(context)) {
      super.addCannotBeConvertedMessage(context);
    } else {
      context.addMessage(Severity.WARNING, MessageFactory.inclusiveGatewayJoin());
    }
  }
}
