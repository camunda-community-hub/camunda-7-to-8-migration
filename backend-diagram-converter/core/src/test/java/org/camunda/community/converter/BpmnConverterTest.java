package org.camunda.community.converter;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.community.converter.BpmnDiagramCheckResult.BpmnElementCheckMessage;
import org.camunda.community.converter.BpmnDiagramCheckResult.BpmnElementCheckResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BpmnConverterTest {
  private static final Logger LOG = LoggerFactory.getLogger(BpmnConverterTest.class);

  @ParameterizedTest
  @CsvSource(
      value = {
        "example-c7.bpmn",
        "example-c7_2.bpmn",
        "java-delegate-class-c7.bpmn",
        "old-process.bpmn20.xml"
      })
  public void shouldConvert(String bpmnFile) {
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();
    BpmnModelInstance modelInstance =
        Bpmn.readModelFromStream(this.getClass().getClassLoader().getResourceAsStream(bpmnFile));
    printModel(converter, modelInstance);
    BpmnDiagramCheckResult result = converter.check(bpmnFile, modelInstance, false, properties);
    printModel(converter, modelInstance);
    StringWriter writer = new StringWriter();
    converter.printXml(modelInstance.getDocument(), true, writer);
    ByteArrayInputStream stream = new ByteArrayInputStream(writer.toString().getBytes());
    io.camunda.zeebe.model.bpmn.Bpmn.readModelFromStream(stream);
  }

  private void printModel(BpmnConverter converter, BpmnModelInstance modelInstance) {
    StringWriter writer = new StringWriter();
    converter.printXml(modelInstance.getDocument(), true, writer);
    String[] processModel = writer.toString().split("\n");
    for (int i = 0; i < processModel.length; i++) {
      LOG.debug("" + i + "     " + processModel[i]);
    }
  }

  @Test
  public void shouldNotConvertC8() {
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();
    BpmnModelInstance modelInstance =
        Bpmn.readModelFromStream(
            this.getClass().getClassLoader().getResourceAsStream("c8_simple.bpmn"));
    Assertions.assertThrows(
        RuntimeException.class, () -> converter.convert(modelInstance, false, properties));
  }

  @Test
  public void testDelegateHint() {
    String bpmnFile = "java-delegate-class-c7.bpmn";
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();
    BpmnModelInstance modelInstance =
        Bpmn.readModelFromStream(this.getClass().getClassLoader().getResourceAsStream(bpmnFile));
    BpmnDiagramCheckResult result = converter.check(bpmnFile, modelInstance, false, properties);
    BpmnElementCheckResult delegateClassServiceTask = result.getResult("DelegateClassServiceTask");
    assertNotNull(delegateClassServiceTask);
    assertThat(delegateClassServiceTask.getMessages()).hasSize(1);
    BpmnElementCheckMessage message = delegateClassServiceTask.getMessages().get(0);
    assertThat(message.getMessage())
        .isEqualTo(
            "Attribute 'class' on 'serviceTask' was mapped. Delegate call to 'com.camunda.consulting.MyDelegate' was transformed to job type 'camunda-7-adapter'. Please review your implementation.");
  }

  @Test
  public void testTaskListenerHints() {
    String bpmnFile = "user-task-listener-implementations.bpmn";
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();
    BpmnModelInstance modelInstance =
        Bpmn.readModelFromStream(this.getClass().getClassLoader().getResourceAsStream(bpmnFile));
    BpmnDiagramCheckResult result = converter.check(bpmnFile, modelInstance, false, properties);
    BpmnElementCheckResult javaClassCheckResult = result.getResult("UserTaskUseJavaClass");
    assertThat(javaClassCheckResult.getMessages()).hasSize(1);
    assertThat(javaClassCheckResult.getMessages().get(0).getMessage())
        .isEqualTo(
            "Element 'taskListener' with implementation 'com.camunda.consulting.TaskListenerExample' cannot be transformed. Task Listeners do not exist in Zeebe.");

    BpmnElementCheckResult delegateExpressionCheckResult =
        result.getResult("UserTaskUseDelegateExpression");
    assertThat(delegateExpressionCheckResult.getMessages()).hasSize(1);
    assertThat(delegateExpressionCheckResult.getMessages().get(0).getMessage())
        .isEqualTo(
            "Element 'taskListener' with implementation '${taskListenerExample}' cannot be transformed. Task Listeners do not exist in Zeebe.");

    BpmnElementCheckResult expressionCheckResult = result.getResult("UserTaskUseExpression");
    assertThat(expressionCheckResult.getMessages()).hasSize(1);
    assertThat(expressionCheckResult.getMessages().get(0).getMessage())
        .isEqualTo(
            "Element 'taskListener' with implementation '${delegateTask.setName(\"my expression name\")}' cannot be transformed. Task Listeners do not exist in Zeebe.");

    BpmnElementCheckResult inlineScriptCheckResult = result.getResult("UserTaskUseInlineScript");
    assertThat(inlineScriptCheckResult.getMessages()).hasSize(2);
    assertThat(inlineScriptCheckResult.getMessages().get(0).getMessage())
        .isEqualTo(
            "Element 'taskListener' with implementation 'javascript' cannot be transformed. Task Listeners do not exist in Zeebe.");
    assertThat(inlineScriptCheckResult.getMessages().get(1).getMessage())
        .isEqualTo(
            "Element 'script' cannot be transformed. Script 'delegateTask.setName(\"my script name\");' with format 'javascript' on 'taskListener'.");
  }

  @Test
  void testOrGateways() {
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    BpmnModelInstance modelInstance =
        Bpmn.readModelFromStream(
            getClass().getClassLoader().getResourceAsStream("or-gateways.bpmn"));
    DefaultConverterProperties properties = new DefaultConverterProperties();
    properties.setPlatformVersion("8.0.0");
    BpmnDiagramCheckResult result =
        converter.check(
            "or-gateways.bpmn",
            modelInstance,
            false,
            ConverterPropertiesFactory.getInstance().merge(properties));
    BpmnElementCheckResult forkGateway = result.getResult("ForkGateway");
    assertThat(forkGateway.getMessages()).hasSize(1);
    assertThat(forkGateway.getMessages().get(0).getMessage())
        .isEqualTo(
            "Element 'inclusiveGateway' is not supported in Zeebe version '8.0.0'. Please review.");
    BpmnElementCheckResult joinGateway = result.getResult("JoinGateway");
    assertThat(joinGateway.getMessages()).hasSize(1);
    assertThat(joinGateway.getMessages().get(0).getMessage())
        .isEqualTo(
            "Element 'inclusiveGateway' is not supported in Zeebe version '8.0.0'. Please review.");
  }

  @Test
  void testOrGateways_8_1() {
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    BpmnModelInstance modelInstance =
        Bpmn.readModelFromStream(
            getClass().getClassLoader().getResourceAsStream("or-gateways.bpmn"));
    DefaultConverterProperties properties = new DefaultConverterProperties();
    properties.setPlatformVersion("8.1.0");
    BpmnDiagramCheckResult result =
        converter.check(
            "or-gateways.bpmn",
            modelInstance,
            false,
            ConverterPropertiesFactory.getInstance().merge(properties));
    BpmnElementCheckResult joinGateway = result.getResult("JoinGateway");
    assertThat(joinGateway.getMessages()).hasSize(1);
    assertThat(joinGateway.getMessages().get(0).getMessage())
        .isEqualTo("A joining inclusive gateway is not supported.");
  }
}
