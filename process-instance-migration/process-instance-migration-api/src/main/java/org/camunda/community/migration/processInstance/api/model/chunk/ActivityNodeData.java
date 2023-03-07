package org.camunda.community.migration.processInstance.api.model.chunk;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.camunda.community.migration.processInstance.api.model.impl.BusinessRuleTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.CallActivityDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.ManualTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.MultiInstanceDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.ReceiveTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.ScriptTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.SendTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.ServiceTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.SubProcessDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.TaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.UserTaskDataImpl;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = CallActivityDataImpl.class, name = "callActivity"),
  @JsonSubTypes.Type(value = UserTaskDataImpl.class, name = "userTask"),
  @JsonSubTypes.Type(value = MultiInstanceDataImpl.class, name = "multiInstance"),
  @JsonSubTypes.Type(value = TaskDataImpl.class, name = "task"),
  @JsonSubTypes.Type(value = ServiceTaskDataImpl.class, name = "serviceTask"),
  @JsonSubTypes.Type(value = ScriptTaskDataImpl.class, name = "scriptTask"),
  @JsonSubTypes.Type(value = SendTaskDataImpl.class, name = "sendTask"),
  @JsonSubTypes.Type(value = ReceiveTaskDataImpl.class, name = "receiveTask"),
  @JsonSubTypes.Type(value = ManualTaskDataImpl.class, name = "manualTask"),
  @JsonSubTypes.Type(value = BusinessRuleTaskDataImpl.class, name = "businessRuleTask"),
  @JsonSubTypes.Type(value = SubProcessDataImpl.class, name = "subProcess")
})
public interface ActivityNodeData extends NamedNodeData, VariableScopeData {

  interface ActivityNodeDataBuilder<
          T extends ActivityNodeDataBuilder<T, R>, R extends ActivityNodeData>
      extends FinalBuildStep<R>, NamedNodeDataBuilder<T>, VariableScopeDataBuilder<T> {}
}
