package org.camunda.community.converter.convertible;

public class UserTaskConvertible extends AbstractActivityConvertible {
  private final ZeebeFormDefinition zeebeFormDefinition = new ZeebeFormDefinition();
  private final ZeebeAssignmentDefinition zeebeAssignmentDefinition =
      new ZeebeAssignmentDefinition();

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
}
