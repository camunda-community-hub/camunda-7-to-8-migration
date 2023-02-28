package org.camunda.community.migration.processInstance;

public class MigrationTestProcessDefinitionInput {
  private final String c7DiagramResourceName;
  private final String c8DiagramResourceName;
  private final String bpmnProcessId;

  public MigrationTestProcessDefinitionInput(
      String c7DiagramResourceName, String c8DiagramResourceName, String bpmnProcessId) {
    this.c7DiagramResourceName = c7DiagramResourceName;
    this.c8DiagramResourceName = c8DiagramResourceName;
    this.bpmnProcessId = bpmnProcessId;
  }

  public String getC7DiagramResourceName() {
    return c7DiagramResourceName;
  }

  public String getC8DiagramResourceName() {
    return c8DiagramResourceName;
  }

  public String getBpmnProcessId() {
    return bpmnProcessId;
  }
}
