package org.camunda.community.migration.converter.cli;

public class ProcessDefinitionDiagramDto {
  private String bpmn20Xml;

  public String getBpmn20Xml() {
    return bpmn20Xml;
  }

  public void setBpmn20Xml(String bpmn20Xml) {
    this.bpmn20Xml = bpmn20Xml;
  }
}
