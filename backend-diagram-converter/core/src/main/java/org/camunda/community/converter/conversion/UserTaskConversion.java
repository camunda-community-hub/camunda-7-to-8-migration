package org.camunda.community.converter.conversion;

import static org.camunda.community.converter.BpmnElementFactory.*;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.convertible.UserTaskConvertible;

public class UserTaskConversion extends AbstractTypedConversion<UserTaskConvertible> {
  @Override
  protected void convertTyped(DomElement element, UserTaskConvertible convertible) {
    DomElement extensionElements = getExtensionElements(element);
    if (canAddFormDefinition(convertible)) {
      extensionElements.appendChild(createFormDefinition(element.getDocument(), convertible));
    }
    if (canAddAssignmentDefinition(convertible)) {
      extensionElements.appendChild(createAssignmentDefinition(element.getDocument(), convertible));
    }
  }

  private DomElement createFormDefinition(DomDocument document, UserTaskConvertible convertible) {
    DomElement formDefinition = document.createElement(NamespaceUri.ZEEBE, "formDefinition");
    formDefinition.setAttribute("formKey", convertible.getZeebeFormDefinition().getFormKey());
    return formDefinition;
  }

  private DomElement createAssignmentDefinition(
      DomDocument document, UserTaskConvertible convertible) {
    DomElement assignmentDefinition =
        document.createElement(NamespaceUri.ZEEBE, "assignmentDefinition");
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
