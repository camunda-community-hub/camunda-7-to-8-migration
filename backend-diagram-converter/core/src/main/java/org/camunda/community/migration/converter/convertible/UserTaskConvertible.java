package org.camunda.community.migration.converter.convertible;

public class UserTaskConvertible extends AbstractActivityConvertible {
  private final ZeebeFormDefinition zeebeFormDefinition = new ZeebeFormDefinition();
  private final ZeebeAssignmentDefinition zeebeAssignmentDefinition =
      new ZeebeAssignmentDefinition();
  private final ZeebeTaskSchedule zeebeTaskSchedule = new ZeebeTaskSchedule();

  public ZeebeTaskSchedule getZeebeTaskSchedule() {
    return zeebeTaskSchedule;
  }

  public ZeebeFormDefinition getZeebeFormDefinition() {
    return zeebeFormDefinition;
  }

  public ZeebeAssignmentDefinition getZeebeAssignmentDefinition() {
    return zeebeAssignmentDefinition;
  }

  public static class ZeebeFormDefinition {
    private String formKey;

    public String getFormKey() {
      return formKey;
    }

    public void setFormKey(String formKey) {
      this.formKey = formKey;
    }
  }

  public static class ZeebeAssignmentDefinition {
    private String assignee;
    private String candidateGroups;
    private String candidateUsers;

    public String getCandidateUsers() {
      return candidateUsers;
    }

    public void setCandidateUsers(String candidateUsers) {
      this.candidateUsers = candidateUsers;
    }

    public String getAssignee() {
      return assignee;
    }

    public void setAssignee(String assignee) {
      this.assignee = assignee;
    }

    public String getCandidateGroups() {
      return candidateGroups;
    }

    public void setCandidateGroups(String candidateGroup) {
      this.candidateGroups = candidateGroup;
    }
  }

  public static class ZeebeTaskSchedule {
    private String dueDate;
    private String followUpDate;

    public String getDueDate() {
      return dueDate;
    }

    public void setDueDate(String dueDate) {
      this.dueDate = dueDate;
    }

    public String getFollowUpDate() {
      return followUpDate;
    }

    public void setFollowUpDate(String followUpDate) {
      this.followUpDate = followUpDate;
    }
  }
}
