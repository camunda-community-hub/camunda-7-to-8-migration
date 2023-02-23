package org.camunda.community.migration.processInstance.dto.client;

import java.util.ArrayList;

public class ProcessInstanceDto {
  private String id;
  private String definitionId;
  private String businessKey;

  public String getBusinessKey() {
    return businessKey;
  }

  public void setBusinessKey(String businessKey) {
    this.businessKey = businessKey;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDefinitionId() {
    return definitionId;
  }

  public void setDefinitionId(String definitionId) {
    this.definitionId = definitionId;
  }

  public static class ProcessInstanceQueryResultDto extends ArrayList<ProcessInstanceDto> {}
}
