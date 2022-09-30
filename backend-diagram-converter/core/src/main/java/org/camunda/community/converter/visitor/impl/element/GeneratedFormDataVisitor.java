package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public abstract class GeneratedFormDataVisitor extends AbstractCamundaElementVisitor {

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }

  @Override
  protected String visitCamundaElement(DomElementVisitorContext context) {
    return null;
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
    protected String visitCamundaElement(DomElementVisitorContext context) {
      return "Generated forms are not supported in Zeebe. Please define a form key instead";
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
