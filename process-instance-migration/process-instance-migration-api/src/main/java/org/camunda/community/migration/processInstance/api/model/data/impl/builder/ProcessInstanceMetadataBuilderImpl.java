package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.FinalBuildStep.FinalBuildStepImpl;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceMetadata;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceMetadata.ProcessInstanceMetadataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.ProcessInstanceMetadataImpl;

public class ProcessInstanceMetadataBuilderImpl
    extends FinalBuildStepImpl<ProcessInstanceMetadata, ProcessInstanceMetadataImpl>
    implements ProcessInstanceMetadataBuilder {
  @Override
  public ProcessInstanceMetadataBuilder withId(String id) {
    data.setId(id);
    return this;
  }

  @Override
  public ProcessInstanceMetadataBuilder withBpmnProcessId(String bpmnProcessId) {
    data.setBpmnProcessId(bpmnProcessId);
    return this;
  }

  @Override
  public ProcessInstanceMetadataBuilder withProcessDefinitionKey(String processDefinitionKey) {
    data.setProcessDefinitionKey(processDefinitionKey);
    return this;
  }

  @Override
  public ProcessInstanceMetadataBuilder withName(String name) {
    data.setName(name);
    return this;
  }

  @Override
  protected ProcessInstanceMetadataImpl createData() {
    return new ProcessInstanceMetadataImpl();
  }
}
