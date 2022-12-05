package org.camunda.community.migration.converter;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.BpmnElementCheckMessage;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.BpmnElementCheckResult;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.Severity;
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
        "old-process.bpmn20.xml",
        "collaboration.bpmn"
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
    BpmnDiagramCheckResult result = loadAndCheck("java-delegate-class-c7.bpmn");
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
    BpmnDiagramCheckResult result = loadAndCheck("user-task-listener-implementations.bpmn");
    BpmnElementCheckResult javaClassCheckResult = result.getResult("UserTaskUseJavaClass");
    assertThat(javaClassCheckResult.getMessages()).hasSize(1);
    assertThat(javaClassCheckResult.getMessages().get(0).getMessage())
        .isEqualTo(
            "Listener at 'create' with implementation 'com.camunda.consulting.TaskListenerExample' cannot be transformed. Task Listeners do not exist in Zeebe.");

    BpmnElementCheckResult delegateExpressionCheckResult =
        result.getResult("UserTaskUseDelegateExpression");
    assertThat(delegateExpressionCheckResult.getMessages()).hasSize(1);
    assertThat(delegateExpressionCheckResult.getMessages().get(0).getMessage())
        .isEqualTo(
            "Listener at 'assignment' with implementation '${taskListenerExample}' cannot be transformed. Task Listeners do not exist in Zeebe.");

    BpmnElementCheckResult expressionCheckResult = result.getResult("UserTaskUseExpression");
    assertThat(expressionCheckResult.getMessages()).hasSize(1);
    assertThat(expressionCheckResult.getMessages().get(0).getMessage())
        .isEqualTo(
            "Listener at 'complete' with implementation '${delegateTask.setName(\"my expression name\")}' cannot be transformed. Task Listeners do not exist in Zeebe.");

    BpmnElementCheckResult inlineScriptCheckResult = result.getResult("UserTaskUseInlineScript");
    assertThat(inlineScriptCheckResult.getMessages()).hasSize(2);
    assertThat(inlineScriptCheckResult.getMessages().get(0).getMessage())
        .isEqualTo(
            "Listener at 'delete' with implementation 'javascript' cannot be transformed. Task Listeners do not exist in Zeebe.");
    assertThat(inlineScriptCheckResult.getMessages().get(1).getMessage())
        .isEqualTo(
            "Element 'script' cannot be transformed. Script 'delegateTask.setName(\"my script name\");' with format 'javascript' on 'taskListener'.");
  }

  @Test
  void testOrGateways() {
    BpmnDiagramCheckResult result = loadAndCheckAgainstVersion("or-gateways.bpmn", "8.0.0");
    BpmnElementCheckResult forkGateway = result.getResult("ForkGateway");
    assertThat(forkGateway.getMessages()).hasSize(1);
    assertThat(forkGateway.getMessages().get(0).getMessage())
        .isEqualTo(
            "Element 'inclusiveGateway' is not supported in Zeebe version '8.0.0'. It is available in version '8.1.0'.");
    BpmnElementCheckResult joinGateway = result.getResult("JoinGateway");
    assertThat(joinGateway.getMessages()).hasSize(1);
    assertThat(joinGateway.getMessages().get(0).getMessage())
        .isEqualTo(
            "Element 'inclusiveGateway' is not supported in Zeebe version '8.0.0'. Please review.");
  }

  @Test
  void testOrGateways_8_1() {
    String bpmnFile = "or-gateways.bpmn";
    BpmnDiagramCheckResult result = loadAndCheckAgainstVersion(bpmnFile, "8.1.0");
    BpmnElementCheckResult joinGateway = result.getResult("JoinGateway");
    assertThat(joinGateway.getMessages()).hasSize(1);
    assertThat(joinGateway.getMessages().get(0).getMessage())
        .isEqualTo("A joining inclusive gateway is not supported.");
  }

  @Test
  void testCallActivityLatest() {
    BpmnDiagramCheckResult result = loadAndCheck("call-activity-latest.bpmn");
    BpmnElementCheckResult callActivityResult = result.getResult("callLatest");
    assertThat(callActivityResult.getMessages()).hasSize(3);
    assertThat(callActivityResult.getMessages().get(0).getMessage())
        .isEqualTo(
            "Attribute 'calledElement' on 'callActivity' was mapped. Please review transformed expression: 'myLatestProcess' -> 'myLatestProcess'.");
    assertThat(callActivityResult.getMessages().get(0).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(callActivityResult.getMessages().get(1).getMessage())
        .isEqualTo(
            "Element 'camunda:in' with attribute 'variables=\"all\"' is removed. It is default in Zeebe.");
    assertThat(callActivityResult.getMessages().get(1).getSeverity()).isEqualTo(Severity.INFO);
    assertThat(callActivityResult.getMessages().get(2).getMessage())
        .isEqualTo(
            "Element 'camunda:out' with attribute 'variables=\"all\"' is mapped to 'propagateAllChildVariables=\"true\"'.");
    assertThat(callActivityResult.getMessages().get(2).getSeverity()).isEqualTo(Severity.INFO);
  }

  @Test
  void testCallActivityDeployment() {
    BpmnDiagramCheckResult result = loadAndCheck("call-activity-deployment.bpmn");
    BpmnElementCheckResult callActivityResult = result.getResult("callDeployment");
    assertThat(callActivityResult.getMessages()).hasSize(4);
    assertThat(callActivityResult.getMessages().get(0).getMessage())
        .isEqualTo(
            "Attribute 'calledElementBinding' with value 'deployment' on 'callActivity' is not supported.");
    assertThat(callActivityResult.getMessages().get(0).getSeverity()).isEqualTo(Severity.WARNING);
    assertThat(callActivityResult.getMessages().get(1).getMessage())
        .isEqualTo(
            "Attribute 'calledElement' on 'callActivity' was mapped. Please review transformed expression: 'myLatestProcess' -> 'myLatestProcess'.");
    assertThat(callActivityResult.getMessages().get(1).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(callActivityResult.getMessages().get(2).getMessage())
        .isEqualTo(
            "Element 'camunda:in' with attribute 'variables=\"all\"' is removed. It is default in Zeebe.");
    assertThat(callActivityResult.getMessages().get(2).getSeverity()).isEqualTo(Severity.INFO);
    assertThat(callActivityResult.getMessages().get(3).getMessage())
        .isEqualTo(
            "Element 'camunda:out' with attribute 'variables=\"all\"' is mapped to 'propagateAllChildVariables=\"true\"'.");
    assertThat(callActivityResult.getMessages().get(3).getSeverity()).isEqualTo(Severity.INFO);
  }

  @Test
  void testReferences() {
    BpmnDiagramCheckResult result = loadAndCheck("message-example.bpmn");
    BpmnElementCheckResult receiveTask = result.getResult("Receive1Task");
    assertThat(receiveTask).isNotNull();
    assertThat(receiveTask.getReferences()).hasSize(1);
    assertThat(receiveTask.getReferences().get(0)).isEqualTo("Receive1Message");
    BpmnElementCheckResult message = result.getResult("Receive1Message");
    assertThat(message).isNotNull();
    assertThat(message.getReferencedBy()).hasSize(1);
    assertThat(message.getReferencedBy().get(0)).isEqualTo("Receive1Task");
  }

  @Test
  void testExecutionListener() {
    BpmnDiagramCheckResult result = loadAndCheck("execution-listener.bpmn");
    BpmnElementCheckResult serviceTaskWithListenerTask =
        result.getResult("ServiceTaskWithListenerTask");
    assertThat(serviceTaskWithListenerTask).isNotNull();
    assertThat(serviceTaskWithListenerTask.getMessages()).hasSize(7);
    assertThat(serviceTaskWithListenerTask.getMessages().get(0).getMessage())
        .isEqualTo(
            "Listener at 'end' with implementation '${endListener.execute(something)}' cannot be transformed. Execution Listeners do not exist in Zeebe.");
    assertThat(serviceTaskWithListenerTask.getMessages().get(1).getMessage())
        .isEqualTo(
            "Listener at 'start' with implementation '${anotherStartListener}' cannot be transformed. Execution Listeners do not exist in Zeebe.");
    assertThat(serviceTaskWithListenerTask.getMessages().get(2).getMessage())
        .isEqualTo(
            "Listener at 'end' with implementation 'groovy' cannot be transformed. Execution Listeners do not exist in Zeebe.");
    assertThat(serviceTaskWithListenerTask.getMessages().get(3).getMessage())
        .isEqualTo(
            "Element 'script' cannot be transformed. Script 'print(\"something\");' with format 'groovy' on 'executionListener'.");
    assertThat(serviceTaskWithListenerTask.getMessages().get(4).getMessage())
        .isEqualTo(
            "Listener at 'start' with implementation 'com.example.StartListener' cannot be transformed. Execution Listeners do not exist in Zeebe.");
  }

  protected BpmnDiagramCheckResult loadAndCheck(String bpmnFile) {
    ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();
    return loadAndCheckAgainstVersion(bpmnFile, properties.getPlatformVersion());
  }

  protected BpmnDiagramCheckResult loadAndCheckAgainstVersion(
      String bpmnFile, String targetVersion) {
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    BpmnModelInstance modelInstance =
        Bpmn.readModelFromStream(getClass().getClassLoader().getResourceAsStream(bpmnFile));
    DefaultConverterProperties properties = new DefaultConverterProperties();
    properties.setPlatformVersion(targetVersion);
    BpmnDiagramCheckResult result =
        converter.check(
            bpmnFile,
            modelInstance,
            false,
            ConverterPropertiesFactory.getInstance().merge(properties));
    return result;
  }
}
