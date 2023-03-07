package org.camunda.community.migration.processInstance.dto.task;

import java.util.List;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData;
import org.camunda.community.migration.processInstance.dto.task.UserTask.UserTaskData;

public class ProcessInstanceSelectionTaskData extends UserTaskData {
  private final String bpmnProcessId;
  private final String processDefinitionId;
  private final List<Camunda7ProcessInstanceData> availableProcessInstances;

  public ProcessInstanceSelectionTaskData(
      String bpmnProcessId,
      String processDefinitionId,
      List<Camunda7ProcessInstanceData> availableProcessInstances) {
    this.bpmnProcessId = bpmnProcessId;
    this.processDefinitionId = processDefinitionId;
    this.availableProcessInstances = availableProcessInstances;
  }

  public String getProcessDefinitionId() {
    return processDefinitionId;
  }

  public String getBpmnProcessId() {
    return bpmnProcessId;
  }

  public List<Camunda7ProcessInstanceData> getAvailableProcessInstances() {
    return availableProcessInstances;
  }
}
