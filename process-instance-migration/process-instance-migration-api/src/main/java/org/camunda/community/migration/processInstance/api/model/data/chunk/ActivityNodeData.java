package org.camunda.community.migration.processInstance.api.model.data.chunk;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.camunda.community.migration.processInstance.api.model.data.impl.BusinessRuleTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.CallActivityDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.EndEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.EventBasedGatewayDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.ExclusiveGatewayDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.InclusiveGatewayDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.IntermediateThrowEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.ManualTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.MessageBoundaryEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.MessageEndEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.MessageIntermediateCatchEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.MessageIntermediateThrowEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.MessageStartEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.MultiExecutionDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.MultiInstanceDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.ParallelGatewayDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.ReceiveTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.ScriptTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.SendTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.ServiceTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.SignalBoundaryEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.SignalEndEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.SignalIntermediateCatchEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.SignalIntermediateThrowEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.SignalStartEventDataImpl;
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
  @Type(value = ParallelGatewayDataImpl.class, name = ActivityNodeData.PARALLEL_GATEWAY),
  @Type(value = MultiExecutionDataImpl.class, name = ActivityNodeData.MULTI_EXECUTION),
  @Type(value = EventBasedGatewayDataImpl.class, name = ActivityNodeData.EVENT_BASED_GATEWAY),
  @Type(value = InclusiveGatewayDataImpl.class, name = ActivityNodeData.INCLUSIVE_GATEWAY),
  @Type(value = IntermediateThrowEventDataImpl.class, name = ActivityNodeData.INTERMEDIATE_EVENT),
  @Type(value = EndEventDataImpl.class, name = ActivityNodeData.END_EVENT),
  @Type(value = MessageStartEventDataImpl.class, name = ActivityNodeData.MESSAGE_START_EVENT),
  @Type(
      value = MessageIntermediateThrowEventDataImpl.class,
      name = ActivityNodeData.MESSAGE_INTERMEDIATE_THROW_EVENT),
  @Type(value = MessageEndEventDataImpl.class, name = ActivityNodeData.MESSAGE_END_EVENT),
  @Type(value = MessageBoundaryEventDataImpl.class, name = ActivityNodeData.MESSAGE_BOUNDARY_EVENT),
  @Type(value = SignalStartEventDataImpl.class, name = ActivityNodeData.SIGNAL_START_EVENT),
  @Type(
      value = SignalIntermediateThrowEventDataImpl.class,
      name = ActivityNodeData.SIGNAL_INTERMEDIATE_THROW_EVENT),
  @Type(
      value = SignalIntermediateCatchEventDataImpl.class,
      name = ActivityNodeData.SIGNAL_INTERMEDIATE_CATCH_EVENT),
  @Type(value = SignalEndEventDataImpl.class, name = ActivityNodeData.SIGNAL_END_EVENT),
  @Type(value = SignalBoundaryEventDataImpl.class, name = ActivityNodeData.SIGNAL_BOUNDARY_EVENT)
})
public interface ActivityNodeData {
  // Helper
  String MULTI_EXECUTION = "multipleExecution";
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
  String PARALLEL_GATEWAY = "parallelGateway";
  String EVENT_BASED_GATEWAY = "eventBasedGateway";
  String INCLUSIVE_GATEWAY = "inclusiveGateway";
  // Markers
  String MULTI_INSTANCE = "multiInstance";
  // Events
  String START_EVENT = "startEvent";
  String INTERMEDIATE_EVENT = "intermediateNoneThrowEvent";
  String END_EVENT = "noneEndEvent";
  String MESSAGE_START_EVENT = "messageStartEvent";
  String MESSAGE_INTERMEDIATE_CATCH_EVENT = "intermediateMessageCatch";
  String MESSAGE_INTERMEDIATE_THROW_EVENT = "intermediateMessageThrowEvent";
  String MESSAGE_END_EVENT = "messageEndEvent";
  String MESSAGE_BOUNDARY_EVENT = "boundaryMessage";
  String SIGNAL_START_EVENT = "signalStartEvent";
  String SIGNAL_INTERMEDIATE_THROW_EVENT = "intermediateSignalThrow";
  String SIGNAL_INTERMEDIATE_CATCH_EVENT = "intermediateSignalCatch";
  String SIGNAL_END_EVENT = "signalEndEvent";
  String SIGNAL_BOUNDARY_EVENT = "boundarySignal";

  default <T extends ActivityNodeData> T as(Class<T> type) {
    return type.cast(this);
  }
}
