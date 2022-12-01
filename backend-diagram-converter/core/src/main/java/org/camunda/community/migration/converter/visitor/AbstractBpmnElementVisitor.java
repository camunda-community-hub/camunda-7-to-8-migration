package org.camunda.community.migration.converter.visitor;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;

public abstract class AbstractBpmnElementVisitor extends AbstractElementVisitor {
  @Override
  protected final String namespaceUri() {
    return NamespaceUri.BPMN;
  }

  @Override
  protected final void visitFilteredElement(DomElementVisitorContext context) {
    visitBpmnElement(context);
    SemanticVersion availableFrom = availableFrom(context);
    if (availableFrom == null) {
      context.addMessage(cannotBeConvertedMessage(context));
    } else if (isNotSupportedInDesiredVersion(
        availableFrom, SemanticVersion.parse(context.getProperties().getPlatformVersion()))) {
      context.addMessage(supportedInFutureVersionMessage(context, availableFrom));
    }
  }

  protected final boolean isNotSupportedInDesiredVersion(
      SemanticVersion availableFrom, SemanticVersion desiredVersion) {
    return availableFrom.ordinal() > desiredVersion.ordinal();
  }

  protected abstract SemanticVersion availableFrom(DomElementVisitorContext context);

  protected abstract void visitBpmnElement(DomElementVisitorContext context);

  protected Message supportedInFutureVersionMessage(
      DomElementVisitorContext context, SemanticVersion availableFrom) {
    return MessageFactory.elementAvailableInFutureVersion(
        context.getElement().getLocalName(),
        context.getProperties().getPlatformVersion(),
        availableFrom.toString());
  }

  protected Message cannotBeConvertedMessage(DomElementVisitorContext context) {
    return MessageFactory.elementNotSupportedHint(
        context.getElement().getLocalName(), context.getProperties().getPlatformVersion());
  }
}
