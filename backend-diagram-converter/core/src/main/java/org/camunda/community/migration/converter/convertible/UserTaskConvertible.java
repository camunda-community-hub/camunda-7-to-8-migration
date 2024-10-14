package org.camunda.community.migration.converter.convertible;

public class UserTaskConvertible extends AbstractActivityConvertible {
  private final ZeebeFormDefinition zeebeFormDefinition = new ZeebeFormDefinition();
  private final ZeebeAssignmentDefinition zeebeAssignmentDefinition =
      new ZeebeAssignmentDefinition();
  private final ZeebeTaskSchedule zeebeTaskSchedule = new ZeebeTaskSchedule();
  private boolean zeebeUserTask;

  public ZeebeTaskSchedule getZeebeTaskSchedule() {
    return zeebeTaskSchedule;
  }

  public ZeebeFormDefinition getZeebeFormDefinition() {
    return zeebeFormDefinition;
  }

  public ZeebeAssignmentDefinition getZeebeAssignmentDefinition() {
    return zeebeAssignmentDefinition;
  }

  public boolean isZeebeUserTask() {
    return zeebeUserTask;
  }

  public void setZeebeUserTask(boolean zeebeUserTask) {
    this.zeebeUserTask = zeebeUserTask;
  }

  public static class ZeebeFormDefinition {
    private String formKey;
    private ZeebeFormDefinitionBindingType bindingType;
    private String versionTag;

    public String getVersionTag() {
      return versionTag;
    }

    public void setVersionTag(String versionTag) {
      this.versionTag = versionTag;
    }

    public ZeebeFormDefinitionBindingType getBindingType() {
      return bindingType;
    }

    public void setBindingType(ZeebeFormDefinitionBindingType bindingType) {
      this.bindingType = bindingType;
    }

    public String getFormKey() {
      return formKey;
    }

    public void setFormKey(String formKey) {
      this.formKey = formKey;
    }

    public enum ZeebeFormDefinitionBindingType {
      deployment,
      versionTag
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
