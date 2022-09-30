package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.visitor.AbstractRemoveAttributeVisitor;

public class HistoryTimeToLiveVisitor extends AbstractRemoveAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "historyTimeToLive";
  }
}
