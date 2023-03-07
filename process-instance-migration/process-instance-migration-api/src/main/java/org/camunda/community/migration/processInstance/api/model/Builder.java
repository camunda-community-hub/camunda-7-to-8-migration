package org.camunda.community.migration.processInstance.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.camunda.community.migration.processInstance.api.model.BusinessRuleTaskData.BusinessRuleTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.CallActivityData.CallActivityDataBuilder;
import org.camunda.community.migration.processInstance.api.model.ManualTaskData.ManualTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.MultiInstanceData.MultiInstanceDataBuilder;
import org.camunda.community.migration.processInstance.api.model.ProcessInstanceData.ProcessInstanceDataBuilder;
import org.camunda.community.migration.processInstance.api.model.ReceiveTaskData.ReceiveTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.ScriptTaskData.ScriptTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.SendTaskData.SendTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.ServiceTaskData.ServiceTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.SubProcessData.SubProcessDataBuilder;
import org.camunda.community.migration.processInstance.api.model.TaskData.TaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.UserTaskData.UserTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.impl.builder.BusinessRuleTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.impl.builder.CallActivityBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.impl.builder.ManualTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.impl.builder.MultiInstanceDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.impl.builder.ProcessInstanceDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.impl.builder.ReceiveTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.impl.builder.ScriptTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.impl.builder.SendTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.impl.builder.ServiceTaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.impl.builder.SubProcessDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.impl.builder.TaskDataBuilderImpl;
import org.camunda.community.migration.processInstance.api.model.impl.builder.UserTaskDataBuilderImpl;

public class Builder {
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
