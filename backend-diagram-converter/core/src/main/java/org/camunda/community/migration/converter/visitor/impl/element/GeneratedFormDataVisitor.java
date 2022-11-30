package org.camunda.community.migration.converter.visitor.impl.element;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractCamundaElementVisitor;

public abstract class GeneratedFormDataVisitor extends AbstractCamundaElementVisitor {

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    return MessageFactory.generatedFormData();
  }

  @Override
  protected boolean isSilent() {
    return true;
  }

  public static class FormDataVisitor extends GeneratedFormDataVisitor {

    @Override
    public String localName() {
      return "formData";
    }

    @Override
    protected Message visitCamundaElement(DomElementVisitorContext context) {
      return MessageFactory.formData(context.getElement().getLocalName());
    }

    @Override
    protected boolean isSilent() {
      return false;
    }
  }

  public static class FormFieldVisitor extends GeneratedFormDataVisitor {
    @Override
    public String localName() {
      return "formField";
    }
  }

  public static class ValidationVisitor extends GeneratedFormDataVisitor {
    @Override
    public String localName() {
      return "validation";
    }
  }

  public static class ConstraintVisitor extends GeneratedFormDataVisitor {
    @Override
    public String localName() {
      return "constraint";
    }
  }

  public static class FormPropertyVisitor extends GeneratedFormDataVisitor {
    @Override
    public String localName() {
      return "formProperty";
    }
  }
}
