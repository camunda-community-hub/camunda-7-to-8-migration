package org.camunda.community.migration.converter.visitor.impl.gateway;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.InclusiveGatewayConvertible;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractGatewayVisitor;

public class InclusiveGatewayVisitor extends AbstractGatewayVisitor {
  private static final SemanticVersion FORK_AVAILABLE_VERSION = SemanticVersion._8_1;
  public static final String ELEMENT_LOCAL_NAME = "inclusiveGateway";

  @Override
  public String localName() {
    return ELEMENT_LOCAL_NAME;
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
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    if (isNotJoining(context.getElement())) {
      return FORK_AVAILABLE_VERSION;
    }
    return null;
  }

  @Override
  protected Message cannotBeConvertedMessage(DomElementVisitorContext context) {
    boolean forkAvailable =
        !isNotSupportedInDesiredVersion(
            FORK_AVAILABLE_VERSION,
            SemanticVersion.parse(context.getProperties().getPlatformVersion()));
    if (forkAvailable && !isNotJoining(context.getElement())) {
      return MessageFactory.inclusiveGatewayJoin();
    }
    return super.cannotBeConvertedMessage(context);
  }
}
