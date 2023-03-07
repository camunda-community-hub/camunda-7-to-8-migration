package org.camunda.community.migration.processInstance.dto.client;

import java.util.ArrayList;

public class ProcessDefinitionDto {
  private String id;
  private String key;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public static class ProcessDefinitionQueryResultDto extends ArrayList<ProcessDefinitionDto> {}
}
