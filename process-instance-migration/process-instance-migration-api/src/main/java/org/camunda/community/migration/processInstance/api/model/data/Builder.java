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
import org.camunda.community.migration.processInstance.api.model.data.ManualTaskData.ManualTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.MultiInstanceData.MultiInstanceDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData.ProcessInstanceDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceMetadata.ProcessInstanceMetadataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.ReceiveTaskData.ReceiveTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.ScriptTaskData.ScriptTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.SendTaskData.SendTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.ServiceTaskData.ServiceTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.SubProcessData.SubProcessDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.TaskData.TaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.UserTaskData.UserTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.BusinessRuleTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.CallActivityBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.ManualTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.MultiInstanceDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.ProcessInstanceDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.ProcessInstanceMetadataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.ReceiveTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.ScriptTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.SendTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.ServiceTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.SubProcessDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.TaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.builder.UserTaskDataBuilderImpl;

public class Builder {
  public static ProcessInstanceMetadataBuilder processInstanceMetadata() {
    return new ProcessInstanceMetadataBuilderImpl();
  }

  public static ProcessInstanceDataBuilder processInstanceData() {
    return new ProcessInstanceDataBuilderImpl();
  }

  public static UserTaskDataBuilder userTaskData() {
    return new UserTaskDataBuilderImpl();
  }

  public static CallActivityDataBuilder callActivityData() {
    return new CallActivityBuilderImpl();
  }

  public static MultiInstanceDataBuilder multiInstanceData() {
    return new MultiInstanceDataBuilderImpl();
  }

  public static TaskDataBuilder taskData() {
    return new TaskDataBuilderImpl();
  }

  public static ServiceTaskDataBuilder serviceTaskData() {
    return new ServiceTaskDataBuilderImpl();
  }

  public static ScriptTaskDataBuilder scriptTaskData() {
    return new ScriptTaskDataBuilderImpl();
  }

  public static SendTaskDataBuilder sendTaskData() {
    return new SendTaskDataBuilderImpl();
  }

  public static ReceiveTaskDataBuilder receiveTaskData() {
    return new ReceiveTaskDataBuilderImpl();
  }

  public static ManualTaskDataBuilder manualTaskData() {
    return new ManualTaskDataBuilderImpl();
  }

  public static BusinessRuleTaskDataBuilder businessRuleTaskData() {
    return new BusinessRuleTaskDataBuilderImpl();
  }

  public static SubProcessDataBuilder subProcessData() {
    return new SubProcessDataBuilderImpl();
  }

  public static TextNode text(String text) {
    return JsonNodeFactory.instance.textNode(text);
  }

  public static NumericNode number(long number) {
    return JsonNodeFactory.instance.numberNode(number);
  }

  public static NumericNode number(double number) {
    return JsonNodeFactory.instance.numberNode(number);
  }

  public static BooleanNode bool(boolean bool) {
    return JsonNodeFactory.instance.booleanNode(bool);
  }

  @SafeVarargs
  public static <T extends JsonNode> ArrayNode array(T... elements) {
    ArrayNode array = JsonNodeFactory.instance.arrayNode();
    for (JsonNode element : elements) {
      array.add(element);
    }
    return array;
  }

  public static NullNode nul() {
    return JsonNodeFactory.instance.nullNode();
  }
}
