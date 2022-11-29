package org.camunda.community.converter.visitor;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.version.SemanticVersion;

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
      context.addMessage(Severity.WARNING, cannotBeConvertedMessage(context));
    } else if (isNotSupportedInDesiredVersion(
        availableFrom, SemanticVersion.parse(context.getProperties().getPlatformVersion()))) {
      context.addMessage(Severity.WARNING, supportedInFutureVersionMessage(context, availableFrom));
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
