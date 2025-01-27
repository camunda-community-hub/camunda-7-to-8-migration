package org.camunda.community.migration.processInstance.importer;

import java.util.List;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface Camunda8ProcessInstanceImporter {
  Camunda8ProcessInstance createProcessInstance(ProcessInstanceData processInstanceData);

  ProcessInstanceDataForPostProcessing updateProcessInstanceToStatus(
      Camunda8ProcessInstance camunda8ProcessInstance,
      ProcessInstanceData data,
      Camunda8ProcessInstanceState stateToTerminate);

  record Camunda8ProcessInstance(Long processInstanceKey) {}

  record Camunda8ProcessInstanceState(List<Long> elementInstanceKeys) {}

  record ProcessInstanceDataForPostProcessing(List<ActivityNodeData> nodesToPostProcess) {}
}
