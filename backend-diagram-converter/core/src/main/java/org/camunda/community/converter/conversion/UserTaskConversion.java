package org.camunda.community.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.ConverterProperties;
import org.camunda.community.converter.convertible.UserTaskConvertible;

public class UserTaskConversion extends AbstractTypedConversion<UserTaskConvertible> {
  @Override
  protected void convertTyped(
      DomElement element, UserTaskConvertible convertible, ConverterProperties properties) {
    DomElement extensionElements = super.getExtensionElements(element, properties);
    if (canAddFormDefinition(convertible)) {
      extensionElements.appendChild(
          createFormDefinition(element.getDocument(), convertible, properties));
    }
    if (canAddAssignmentDefinition(convertible)) {
      extensionElements.appendChild(
          createAssignmentDefinition(element.getDocument(), convertible, properties));
    }
  }

  private DomElement createFormDefinition(
      DomDocument document, UserTaskConvertible convertible, ConverterProperties properties) {
    DomElement formDefinition =
        document.createElement(properties.getZeebeNamespace().getUri(), "formDefinition");
    formDefinition.setAttribute("formKey", convertible.getZeebeFormDefinition().getFormKey());
    return formDefinition;
  }

  private DomElement createAssignmentDefinition(
      DomDocument document, UserTaskConvertible convertible, ConverterProperties properties) {
    DomElement assignmentDefinition =
        document.createElement(properties.getZeebeNamespace().getUri(), "assignmentDefinition");
    assignmentDefinition.setAttribute(
        "assignee", convertible.getZeebeAssignmentDefinition().getAssignee());
    assignmentDefinition.setAttribute(
        "candidateGroups", convertible.getZeebeAssignmentDefinition().getCandidateGroups());
    return assignmentDefinition;
  }

  private boolean canAddFormDefinition(UserTaskConvertible convertible) {
    return convertible.getZeebeFormDefinition().getFormKey() != null;
  }

  private boolean canAddAssignmentDefinition(UserTaskConvertible convertible) {
    return convertible.getZeebeAssignmentDefinition().getAssignee() != null
        || convertible.getZeebeAssignmentDefinition().getCandidateGroups() != null;
  }

  @Override
  protected Class<UserTaskConvertible> type() {
    return UserTaskConvertible.class;
  }
}
