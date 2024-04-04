package org.camunda.community.migration.converter.conversion;

import static org.camunda.community.migration.converter.BpmnElementFactory.*;
import static org.camunda.community.migration.converter.NamespaceUri.*;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.convertible.UserTaskConvertible;

public class UserTaskConversion extends AbstractTypedConversion<UserTaskConvertible> {
  @Override
  protected void convertTyped(DomElement element, UserTaskConvertible convertible) {
    DomElement extensionElements = getExtensionElements(element);
    if (convertible.isZeebeUserTask()) {
      extensionElements.appendChild(createZeebeUserTask(element.getDocument()));
    }
    if (canAddFormDefinition(convertible)) {
      extensionElements.appendChild(createFormDefinition(element.getDocument(), convertible));
    }
    if (canAddAssignmentDefinition(convertible)) {
      extensionElements.appendChild(createAssignmentDefinition(element.getDocument(), convertible));
    }
    if (canAddTaskSchedule(convertible)) {
      extensionElements.appendChild(createTaskSchedule(element.getDocument(), convertible));
    }
  }

  private DomElement createZeebeUserTask(DomDocument document) {
    return document.createElement(ZEEBE, "userTask");
  }

  private DomElement createTaskSchedule(DomDocument document, UserTaskConvertible convertible) {
    DomElement taskSchedule = document.createElement(ZEEBE, "taskSchedule");
    taskSchedule.setAttribute("dueDate", convertible.getZeebeTaskSchedule().getDueDate());
    taskSchedule.setAttribute("followUpDate", convertible.getZeebeTaskSchedule().getFollowUpDate());
    return taskSchedule;
  }

  private boolean canAddTaskSchedule(UserTaskConvertible convertible) {
    return convertible.getZeebeTaskSchedule().getDueDate() != null
        || convertible.getZeebeTaskSchedule().getFollowUpDate() != null;
  }

  private DomElement createFormDefinition(DomDocument document, UserTaskConvertible convertible) {
    DomElement formDefinition = document.createElement(ZEEBE, "formDefinition");
    formDefinition.setAttribute("formKey", convertible.getZeebeFormDefinition().getFormKey());
    return formDefinition;
  }

  private DomElement createAssignmentDefinition(
      DomDocument document, UserTaskConvertible convertible) {
    DomElement assignmentDefinition = document.createElement(ZEEBE, "assignmentDefinition");
    assignmentDefinition.setAttribute(
        "assignee", convertible.getZeebeAssignmentDefinition().getAssignee());
    assignmentDefinition.setAttribute(
        "candidateGroups", convertible.getZeebeAssignmentDefinition().getCandidateGroups());
    assignmentDefinition.setAttribute(
        "candidateUsers", convertible.getZeebeAssignmentDefinition().getCandidateUsers());
    return assignmentDefinition;
  }

  private boolean canAddFormDefinition(UserTaskConvertible convertible) {
    return convertible.getZeebeFormDefinition().getFormKey() != null;
  }

  private boolean canAddAssignmentDefinition(UserTaskConvertible convertible) {
    return convertible.getZeebeAssignmentDefinition().getAssignee() != null
        || convertible.getZeebeAssignmentDefinition().getCandidateGroups() != null
        || convertible.getZeebeAssignmentDefinition().getCandidateUsers() != null;
  }

  @Override
  protected Class<UserTaskConvertible> type() {
    return UserTaskConvertible.class;
  }
}
