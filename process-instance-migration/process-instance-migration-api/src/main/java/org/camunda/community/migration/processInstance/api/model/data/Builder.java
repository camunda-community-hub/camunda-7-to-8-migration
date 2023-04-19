package org.camunda.community.migration.processInstance.api.model.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.camunda.community.migration.processInstance.api.model.data.BusinessRuleTaskData.BusinessRuleTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.CallActivityData.CallActivityDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.ExclusiveGatewayData.ExclusiveGatewayDataBuilder;import org.camunda.community.migration.processInstance.api.model.data.ManualTaskData.ManualTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.MessageIntermediateCatchEventData.MessageIntermediateCatchEventDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.MultiInstanceData.MultiInstanceDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.ParallelGatewayData.ParallelGatewayDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData.ProcessInstanceDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceMetadata.ProcessInstanceMetadataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.ReceiveTaskData.ReceiveTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.ScriptTaskData.ScriptTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.SendTaskData.SendTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.ServiceTaskData.ServiceTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.StartEventData.StartEventDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.SubProcessData.SubProcessDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.TaskData.TaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.TransactionData.TransactionDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.UserTaskData.UserTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.BusinessRuleTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.CallActivityBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.ExclusiveGatewayDataBuilderImpl;import org.camunda.community.migration.processInstance.api.model.data.impl.builder.ManualTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.MessageIntermediateCatchEventDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.MultiInstanceDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.ParallelGatewayDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.ProcessInstanceDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.ProcessInstanceMetadataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.ReceiveTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.ScriptTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.SendTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.ServiceTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.StartEventDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.SubProcessDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.TaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.TransactionDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.UserTaskDataBuilderImpl;

public interface Builder {
  static ProcessInstanceMetadataBuilder processInstanceMetadata() {
    return new ProcessInstanceMetadataBuilderImpl();
  }

  static ProcessInstanceDataBuilder processInstanceData() {
    return new ProcessInstanceDataBuilderImpl();
  }

  static UserTaskDataBuilder userTaskData() {
    return new UserTaskDataBuilderImpl();
  }

  static CallActivityDataBuilder callActivityData() {
    return new CallActivityBuilderImpl();
  }

  static MultiInstanceDataBuilder multiInstanceData() {
    return new MultiInstanceDataBuilderImpl();
  }

  static TaskDataBuilder taskData() {
    return new TaskDataBuilderImpl();
  }

  static ServiceTaskDataBuilder serviceTaskData() {
    return new ServiceTaskDataBuilderImpl();
  }

  static ScriptTaskDataBuilder scriptTaskData() {
    return new ScriptTaskDataBuilderImpl();
  }

  static SendTaskDataBuilder sendTaskData() {
    return new SendTaskDataBuilderImpl();
  }

  static ReceiveTaskDataBuilder receiveTaskData() {
    return new ReceiveTaskDataBuilderImpl();
  }

  static ManualTaskDataBuilder manualTaskData() {
    return new ManualTaskDataBuilderImpl();
  }

  static BusinessRuleTaskDataBuilder businessRuleTaskData() {
    return new BusinessRuleTaskDataBuilderImpl();
  }

  static SubProcessDataBuilder subProcessData() {
    return new SubProcessDataBuilderImpl();
  }

  static MessageIntermediateCatchEventDataBuilder messageIntermediateCatchEventData() {
    return new MessageIntermediateCatchEventDataBuilderImpl();
  }

  static StartEventDataBuilder startEventData() {
    return new StartEventDataBuilderImpl();
  }

  static TransactionDataBuilder transactionData() {
    return new TransactionDataBuilderImpl();
  }

  static ExclusiveGatewayDataBuilder exclusiveGatewayData(){
    return new ExclusiveGatewayDataBuilderImpl();
  }

  static ParallelGatewayDataBuilder parallelGatewayData(){
    return new ParallelGatewayDataBuilderImpl();
  }

  interface Json {

    static TextNode text(String text) {
      return JsonNodeFactory.instance.textNode(text);
    }

    static NumericNode number(long number) {
      return JsonNodeFactory.instance.numberNode(number);
    }

    static NumericNode number(int number) {
      return JsonNodeFactory.instance.numberNode(number);
    }

    static NumericNode number(double number) {
      return JsonNodeFactory.instance.numberNode(number);
    }

    static BooleanNode bool(boolean bool) {
      return JsonNodeFactory.instance.booleanNode(bool);
    }

    @SafeVarargs
    static <T extends JsonNode> ArrayNode array(T... elements) {
      ArrayNode array = JsonNodeFactory.instance.arrayNode();
      for (JsonNode element : elements) {
        array.add(element);
      }
      return array;
    }

    static NullNode nul() {
      return JsonNodeFactory.instance.nullNode();
    }
  }
}
