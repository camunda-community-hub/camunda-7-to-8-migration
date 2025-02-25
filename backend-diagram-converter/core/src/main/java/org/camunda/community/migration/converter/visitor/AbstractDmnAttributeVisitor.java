package org.camunda.community.migration.converter.visitor;

import static org.camunda.community.migration.converter.NamespaceUri.*;

import java.util.Arrays;
import java.util.List;
import org.camunda.community.migration.converter.DomElementVisitorContext;

public abstract class AbstractDmnAttributeVisitor extends AbstractAttributeVisitor {
  @Override
  protected List<String> namespaceUri() {
    return Arrays.asList(DMN);
  }

  @Override
  protected boolean removeAttribute(DomElementVisitorContext context) {
    return false;
  }
}
