package org.camunda.community.migration.processInstance.api.model.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.camunda.community.migration.processInstance.api.model.FinalBuildStep;
import org.camunda.community.migration.processInstance.api.model.data.chunk.NamedNodeData;
import org.camunda.community.migration.processInstance.api.model.data.impl.ProcessInstanceMetadataImpl;

@JsonDeserialize(as = ProcessInstanceMetadataImpl.class)
public interface ProcessInstanceMetadata extends NamedNodeData {
  String getId();

  String getProcessDefinitionKey();

  String getBpmnProcessId();

  interface GenericProcessInstanceMetadataBuilder<T> extends NamedNodeDataBuilder<T> {
    T withId(String id);

    T withBpmnProcessId(String bpmnProcessId);

    T withProcessDefinitionKey(String processDefinitionKey);
  }

  interface ProcessInstanceMetadataBuilder
      extends GenericProcessInstanceMetadataBuilder<ProcessInstanceMetadataBuilder>,
          FinalBuildStep<ProcessInstanceMetadata> {}
}
