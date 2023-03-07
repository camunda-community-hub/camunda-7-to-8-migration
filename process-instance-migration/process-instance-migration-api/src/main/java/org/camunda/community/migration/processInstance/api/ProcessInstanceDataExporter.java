package org.camunda.community.migration.processInstance.api;

import org.camunda.community.migration.processInstance.api.model.ProcessInstanceData;

public interface ProcessInstanceDataExporter {
  Page<ProcessInstanceData> list(long start, long limit);

  ProcessInstanceData get(String id);

  void delete(String id);
}
