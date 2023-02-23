package org.camunda.community.migration.processInstance.dto.rest;

import java.util.ArrayList;

public class JobDto {
  private String id;
  private String jobDefinitionId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getJobDefinitionId() {
    return jobDefinitionId;
  }

  public void setJobDefinitionId(String jobDefinitionId) {
    this.jobDefinitionId = jobDefinitionId;
  }

  public static class JobQueryResultDto extends ArrayList<JobDto> {}
}
