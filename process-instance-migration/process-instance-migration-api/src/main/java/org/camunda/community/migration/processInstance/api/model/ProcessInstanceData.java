package org.camunda.community.migration.processInstance.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.chunk.FinalBuildStep;
import org.camunda.community.migration.processInstance.api.model.chunk.NamedNodeData;
import org.camunda.community.migration.processInstance.api.model.chunk.VariableScopeData;
import org.camunda.community.migration.processInstance.api.model.impl.ProcessInstanceDataImpl;

@JsonDeserialize(as = ProcessInstanceDataImpl.class)
public interface ProcessInstanceData extends NamedNodeData, VariableScopeData {
  String getBpmnProcessId();

  Map<String, ActivityNodeData> getActivities();

  String getId();

  interface ProcessInstanceDataBuilder
      extends NamedNodeDataBuilder<ProcessInstanceDataBuilder>,
          VariableScopeDataBuilder<ProcessInstanceDataBuilder>,
          FinalBuildStep<ProcessInstanceData> {
    ProcessInstanceDataBuilder bpmnProcessId(String bpmnProcessId);

    ProcessInstanceDataBuilder activity(String activityName, ActivityNodeData activity);

    ProcessInstanceDataBuilder id(String id);
  }
}
