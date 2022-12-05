package org.camunda.community.migration.converter.visitor.impl;

import org.camunda.community.migration.converter.visitor.AbstractEventRefVisitor;

public class MessageRefVisitor extends AbstractEventRefVisitor {
  @Override
  public String attributeLocalName() {
    return "messageRef";
  }
}
