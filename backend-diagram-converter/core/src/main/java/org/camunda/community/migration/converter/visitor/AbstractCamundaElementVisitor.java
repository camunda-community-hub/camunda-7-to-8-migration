package org.camunda.community.migration.converter.visitor;

import java.util.List;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.version.SemanticVersion;

public abstract class AbstractCamundaElementVisitor extends AbstractElementVisitor {
  @Override
  protected List<String> namespaceUri() {
    return List.of(NamespaceUri.CAMUNDA);
  }

  @Override
  protected final void visitElement(DomElementVisitorContext context) {
    Message message = visitCamundaElement(context);
    context.addMessage(message);
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0;
  }

  protected abstract Message visitCamundaElement(DomElementVisitorContext context);

  public abstract boolean canBeTransformed(DomElementVisitorContext context);
}
