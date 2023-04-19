package org.camunda.community.migration.processInstance.api.model.data.chunk;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.camunda.community.migration.processInstance.api.model.FinalBuildStep;
import org.camunda.community.migration.processInstance.api.model.data.impl.BusinessRuleTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.CallActivityDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.ExclusiveGatewayDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.ManualTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.MessageIntermediateCatchEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.MultiInstanceDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.ParallelGatewayDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.ReceiveTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.ScriptTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.SendTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.ServiceTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.StartEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.SubProcessDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.TaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.TransactionDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.UserTaskDataImpl;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @Type(value = CallActivityDataImpl.class, name = ActivityNodeData.CALL_ACTIVITY),
  @Type(value = UserTaskDataImpl.class, name = ActivityNodeData.USER_TASK),
  @Type(value = MultiInstanceDataImpl.class, name = ActivityNodeData.MULTI_INSTANCE),
  @Type(value = TaskDataImpl.class, name = ActivityNodeData.TASK),
  @Type(value = ServiceTaskDataImpl.class, name = ActivityNodeData.SERVICE_TASK),
  @Type(value = ScriptTaskDataImpl.class, name = ActivityNodeData.SCRIPT_TASK),
  @Type(value = SendTaskDataImpl.class, name = ActivityNodeData.SEND_TASK),
  @Type(value = ReceiveTaskDataImpl.class, name = ActivityNodeData.RECEIVE_TASK),
  @Type(value = ManualTaskDataImpl.class, name = ActivityNodeData.MANUAL_TASK),
  @Type(value = BusinessRuleTaskDataImpl.class, name = ActivityNodeData.BUSINESS_RULE_TASK),
  @Type(value = SubProcessDataImpl.class, name = ActivityNodeData.SUB_PROCESS),
  @Type(
      value = MessageIntermediateCatchEventDataImpl.class,
      name = ActivityNodeData.MESSAGE_INTERMEDIATE_CATCH_EVENT),
  @Type(value = StartEventDataImpl.class, name = ActivityNodeData.START_EVENT),
  @Type(value = TransactionDataImpl.class, name = ActivityNodeData.TRANSACTION),
  @Type(value = ExclusiveGatewayDataImpl.class, name = ActivityNodeData.EXCLUSIVE_GATEWAY),
  @Type(value = ParallelGatewayDataImpl.class, name = ActivityNodeData.PARALLEL_GATEWAY)
})
public interface ActivityNodeData extends NamedNodeData, VariableScopeData, ExecutionData {
  // Tasks
  String USER_TASK = "userTask";
  String TASK = "task";
  String SERVICE_TASK = "serviceTask";
  String SCRIPT_TASK = "scriptTask";
  String SEND_TASK = "sendTask";
  String RECEIVE_TASK = "receiveTask";
  String MANUAL_TASK = "manualTask";
  String BUSINESS_RULE_TASK = "businessRuleTask";
  // Subprocesses
  String SUB_PROCESS = "subProcess";
  String TRANSACTION = "transaction";
  String CALL_ACTIVITY = "callActivity";
  // Gateways
  String EXCLUSIVE_GATEWAY = "exclusiveGateway";
  String PARALLEL_GATEWAY = "parallelGateway"; // TODO define parallel gateway
  String EVENT_BASED_GATEWAY = ""; // TODO define event based gateway
  String INCLUSIVE_GATEWAY = ""; // TODO define inclusive gateway
  // Markers
  String MULTI_INSTANCE = "multiInstance";
  // Events
  String START_EVENT = "startEvent"; // TODO define start event
  String INTERMEDIATE_EVENT = ""; // TODO define intermediate event
  String END_EVENT = ""; // TODO define end event
  String MESSAGE_START_EVENT = ""; // TODO define message start event
  String MESSAGE_INTERMEDIATE_CATCH_EVENT = "intermediateMessageCatch";
  String MESSAGE_INTERMEDIATE_THROW_EVENT = ""; // TODO define message intermediate throw event
  String MESSAGE_END_EVENT = ""; // TODO define message end event

  default <T extends ActivityNodeData> T as(Class<T> type) {
    return type.cast(this);
  }

  interface ActivityNodeDataBuilder<
          T extends ActivityNodeDataBuilder<T, R>, R extends ActivityNodeData>
      extends FinalBuildStep<R>,
          NamedNodeDataBuilder<T>,
          VariableScopeDataBuilder<T>,
          ExecutionDataBuilder<T> {}
}
