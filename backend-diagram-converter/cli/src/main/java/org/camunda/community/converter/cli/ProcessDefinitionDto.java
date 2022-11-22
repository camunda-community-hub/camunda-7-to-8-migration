package org.camunda.community.converter.cli;

public class ProcessDefinitionDto {
  private String id;
  private String resource;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }
}
