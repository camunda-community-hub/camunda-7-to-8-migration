package org.camunda.community.migration.processInstance.api.model.data.chunk;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.camunda.community.migration.processInstance.api.model.FinalBuildStep;
import org.camunda.community.migration.processInstance.api.model.data.impl.BusinessRuleTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.CallActivityDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.ManualTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.MultiInstanceDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.ReceiveTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.ScriptTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.SendTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.ServiceTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.SubProcessDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.TaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.UserTaskDataImpl;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = CallActivityDataImpl.class, name = ActivityNodeData.CALL_ACTIVITY),
  @JsonSubTypes.Type(value = UserTaskDataImpl.class, name = ActivityNodeData.USER_TASK),
  @JsonSubTypes.Type(value = MultiInstanceDataImpl.class, name = ActivityNodeData.MULTI_INSTANCE),
  @JsonSubTypes.Type(value = TaskDataImpl.class, name = ActivityNodeData.TASK),
  @JsonSubTypes.Type(value = ServiceTaskDataImpl.class, name = ActivityNodeData.SERVICE_TASK),
  @JsonSubTypes.Type(value = ScriptTaskDataImpl.class, name = ActivityNodeData.SCRIPT_TASK),
  @JsonSubTypes.Type(value = SendTaskDataImpl.class, name = ActivityNodeData.SEND_TASK),
  @JsonSubTypes.Type(value = ReceiveTaskDataImpl.class, name = ActivityNodeData.RECEIVE_TASK),
  @JsonSubTypes.Type(value = ManualTaskDataImpl.class, name = ActivityNodeData.MANUAL_TASK),
  @JsonSubTypes.Type(
      value = BusinessRuleTaskDataImpl.class,
      name = ActivityNodeData.BUSINESS_RULE_TASK),
  @JsonSubTypes.Type(value = SubProcessDataImpl.class, name = ActivityNodeData.SUB_PROCESS)
})
public interface ActivityNodeData extends NamedNodeData, VariableScopeData {
  String CALL_ACTIVITY = "callActivity";
  String USER_TASK = "userTask";
  String MULTI_INSTANCE = "multiInstance";
  String TASK = "task";
  String SERVICE_TASK = "serviceTask";
  String SCRIPT_TASK = "scriptTask";
  String SEND_TASK = "sendTask";
  String RECEIVE_TASK = "receiveTask";
  String MANUAL_TASK = "manualTask";
  String BUSINESS_RULE_TASK = "businessRuleTask";
  String SUB_PROCESS = "subProcess";

  default <T extends ActivityNodeData> T as(Class<T> type) {
    return type.cast(this);
  }

  interface ActivityNodeDataBuilder<
          T extends ActivityNodeDataBuilder<T, R>, R extends ActivityNodeData>
      extends FinalBuildStep<R>, NamedNodeDataBuilder<T>, VariableScopeDataBuilder<T> {}
}
