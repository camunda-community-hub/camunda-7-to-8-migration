package org.camunda.community.migration.converter;

import static org.assertj.core.api.Assertions.*;
import static org.camunda.community.migration.converter.NamespaceUri.*;
import static org.camunda.community.migration.converter.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.instance.DomElement;
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
        "collaboration.bpmn",
        "internal-script.bpmn",
        "collaboration.bpmn",
        "empty-input-parameter.bpmn",
        "flexible-timer-event.bpmn"
      })
  public void shouldConvert(String bpmnFile) {
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();
    BpmnModelInstance modelInstance =
        Bpmn.readModelFromStream(this.getClass().getClassLoader().getResourceAsStream(bpmnFile));
    printModel(modelInstance);
    BpmnDiagramCheckResult result = converter.check(bpmnFile, modelInstance, properties);
    printModel(modelInstance);
    StringWriter writer = new StringWriter();
    converter.printXml(modelInstance.getDocument(), true, writer);
    ByteArrayInputStream stream = new ByteArrayInputStream(writer.toString().getBytes());
    io.camunda.zeebe.model.bpmn.Bpmn.readModelFromStream(stream);
  }

  private void printModel(BpmnModelInstance modelInstance) {
    StringWriter writer = new StringWriter();
    BpmnConverterFactory.getInstance().get().printXml(modelInstance.getDocument(), true, writer);
    String[] processModel = writer.toString().split("\n");
    for (int i = 1; i < processModel.length; i++) {
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
        RuntimeException.class, () -> converter.convert(modelInstance, properties));
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
            "Element 'Inclusive Gateway' is not supported in Zeebe version '8.0.0'. It is available in version '8.1.0'.");
    BpmnElementCheckResult joinGateway = result.getResult("JoinGateway");
    assertThat(joinGateway.getMessages()).hasSize(1);
    assertThat(joinGateway.getMessages().get(0).getMessage())
        .isEqualTo(
            "Element 'Inclusive Gateway' is not supported in Zeebe version '8.0.0'. Please review.");
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
    assertThat(callActivityResult.getMessages().get(0).getSeverity()).isEqualTo(Severity.REVIEW);
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
    assertThat(callActivityResult.getMessages().get(1).getSeverity()).isEqualTo(Severity.REVIEW);
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

  @Test
  void testConditionalFlow() {
    BpmnDiagramCheckResult result = loadAndCheck("conditional-flow.bpmn");
    BpmnElementCheckResult checkResult = result.getResult("SomethingWorkedSequenceFlow");
    List<BpmnElementCheckMessage> messages = checkResult.getMessages();
    assertThat(messages).hasSize(2);
    assertThat(messages.get(0).getMessage()).isEqualTo("A Conditional flow is not supported.");
  }

  @Test
  void testInternalScript_8_2() {
    BpmnDiagramCheckResult result = loadAndCheckAgainstVersion("internal-script.bpmn", "8.2.0");
    assertThat(result)
        .isNotNull()
        .extracting(r -> r.getResult("FeelScriptTask"))
        .isNotNull()
        .extracting(BpmnElementCheckResult::getMessages)
        .asList()
        .hasSize(2);
    assertThat(result.getResult("FeelScriptTask").getMessages().get(0).getMessage())
        .isEqualTo("Result variable is set to Zeebe script result variable.");
    assertThat(result.getResult("FeelScriptTask").getMessages().get(1).getMessage())
        .isEqualTo("Script is transformed to Zeebe script.");
  }

  @Test
  void testInternalScript_8_1() {
    BpmnDiagramCheckResult result = loadAndCheckAgainstVersion("internal-script.bpmn", "8.1.0");
    assertThat(result)
        .isNotNull()
        .extracting(r -> r.getResult("FeelScriptTask"))
        .isNotNull()
        .extracting(BpmnElementCheckResult::getMessages)
        .asList()
        .hasSize(4);
    assertThat(result.getResult("FeelScriptTask").getMessages().get(0).getMessage())
        .isEqualTo("Script format 'feel' was set to header 'scriptFormat'. Please review.");
    assertThat(result.getResult("FeelScriptTask").getMessages().get(1).getMessage())
        .isEqualTo(
            "Element 'scriptTask' was transformed. Currently, script tasks are implemented like service tasks with job type 'script'. Please review your implementation.");
    assertThat(result.getResult("FeelScriptTask").getMessages().get(2).getMessage())
        .isEqualTo(
            "Attribute 'resultVariable' on 'scriptTask' was mapped. Is now available as header 'resultVariable'.");
    assertThat(result.getResult("FeelScriptTask").getMessages().get(3).getMessage())
        .isEqualTo("Script was set to header 'script'. Please review.");
  }

  @Test
  void testExpressionWithMethodInvocation() {
    BpmnDiagramCheckResult result = loadAndCheck("expression-method-invocation.bpmn");
    List<BpmnElementCheckMessage> easyExpressionMessage =
        result.getResult("EasyExpressionSequenceFlow").getMessages();
    assertThat(easyExpressionMessage).hasSize(1);
    assertThat(easyExpressionMessage.get(0).getSeverity()).isEqualTo(Severity.REVIEW);

    List<BpmnElementCheckMessage> executionIsUsedMessage =
        result.getResult("ExecutionIsUsedSequenceFlow").getMessages();
    assertThat(executionIsUsedMessage).hasSize(1);
    assertThat(executionIsUsedMessage.get(0).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(executionIsUsedMessage.get(0).getMessage())
        .contains("'execution' is not available in FEEL");

    List<BpmnElementCheckMessage> methodInvocationIsUsedMessage =
        result.getResult("MethodInvocationIsUsedSequenceFlow").getMessages();
    assertThat(methodInvocationIsUsedMessage).hasSize(1);
    assertThat(methodInvocationIsUsedMessage.get(0).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(methodInvocationIsUsedMessage.get(0).getMessage())
        .contains("Method invocation is not possible in FEEL");
  }

  @Test
  void testInMappingWithMethodInvocationAndExecution() {
    BpmnDiagramCheckResult result = loadAndCheck("expression-method-invocation.bpmn");
    List<BpmnElementCheckMessage> inMappingMessages =
        result.getResult("TaskWithInMappingsServiceTask").getMessages();
    assertThat(inMappingMessages).hasSize(3);

    assertThat(inMappingMessages.get(0).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(inMappingMessages.get(0).getMessage())
        .contains(
            Arrays.asList("taskForMethodExpected", "Method invocation is not possible in FEEL"));

    assertThat(inMappingMessages.get(1).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(inMappingMessages.get(1).getMessage())
        .contains(
            Arrays.asList("taskForExecutionExpected", "'execution' is not available in FEEL"));

    assertThat(inMappingMessages.get(2).getSeverity()).isEqualTo(Severity.REVIEW);
    assertThat(inMappingMessages.get(2).getMessage()).contains("reviewExpected");
  }

  @Test
  void testOutputMappingWithMethodInvocationAndExecution() {
    BpmnDiagramCheckResult result = loadAndCheck("expression-method-invocation.bpmn");
    List<BpmnElementCheckMessage> outMappingMessages =
        result.getResult("TaskWithOutMappingsServiceTask").getMessages();
    assertThat(outMappingMessages).hasSize(3);

    assertThat(outMappingMessages.get(0).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(outMappingMessages.get(0).getMessage())
        .contains(
            Arrays.asList("taskForMethodExpected", "Method invocation is not possible in FEEL"));

    assertThat(outMappingMessages.get(1).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(outMappingMessages.get(1).getMessage())
        .contains(
            Arrays.asList("taskForExecutionExpected", "'execution' is not available in FEEL"));

    assertThat(outMappingMessages.get(2).getSeverity()).isEqualTo(Severity.REVIEW);
    assertThat(outMappingMessages.get(2).getMessage()).contains("reviewExpected");
  }

  @Test
  void testMultiInstanceConfigurationWithExecution() {
    BpmnDiagramCheckResult result = loadAndCheck("expression-method-invocation.bpmn");
    List<BpmnElementCheckMessage> messages =
        result.getResult("MultiInstanceConfigurationWithExecutionServiceTask").getMessages();
    assertThat(messages).hasSize(4);
    assertThat(messages.get(0).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(messages.get(0).getMessage()).contains("Collecting results");

    assertThat(messages.get(1).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(messages.get(1).getMessage())
        .contains(Arrays.asList("collection", "'execution' is not available in FEEL"));

    assertThat(messages.get(2).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(messages.get(2).getMessage())
        .contains(Arrays.asList("Completion condition", "'execution' is not available in FEEL"));

    assertThat(messages.get(3).getSeverity()).isEqualTo(Severity.INFO);
  }

  @Test
  void testMultiInstanceConfigurationWithMethodInvocation() {
    BpmnDiagramCheckResult result = loadAndCheck("expression-method-invocation.bpmn");
    List<BpmnElementCheckMessage> messages =
        result.getResult("MultiInstanceConfigurationWithMethodInvocationServiceTask").getMessages();
    assertThat(messages).hasSize(4);
    assertThat(messages.get(0).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(messages.get(0).getMessage()).contains("Collecting results");

    assertThat(messages.get(1).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(messages.get(1).getMessage())
        .contains(Arrays.asList("collection", "Method invocation is not possible in FEEL"));

    assertThat(messages.get(2).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(messages.get(2).getMessage())
        .contains(
            Arrays.asList("Completion condition", "Method invocation is not possible in FEEL"));

    assertThat(messages.get(3).getSeverity()).isEqualTo(Severity.INFO);
  }

  @Test
  void testEmptyInputParameterMapping() {
    BpmnDiagramCheckResult result = loadAndCheck("empty-input-parameter.bpmn");
    List<BpmnElementCheckMessage> messages = result.getResult("AUserTaskTask").getMessages();
    assertThat(messages).hasSize(1);
    BpmnElementCheckMessage message = messages.get(0);
    assertThat(message.getMessage())
        .isEqualTo(
            "Element 'inputParameter' was transformed. Parameter 'inputParameterName': Please review transformed expression: '' -> '=null'.");
  }

  @Test
  void testExecutionListenerWithoutImpl() {
    BpmnDiagramCheckResult result = loadAndCheck("execution-listener-no-impl.bpmn");
    List<BpmnElementCheckMessage> messages =
        result.getResult("ListenerWithoutImplTask").getMessages();
    assertThat(messages).hasSize(1);
    BpmnElementCheckMessage message = messages.get(0);
    assertThat(message.getMessage())
        .isEqualTo(
            "Listener at 'start' with implementation 'null' cannot be transformed. Execution Listeners do not exist in Zeebe.");
  }

  @Test
  void testErrorCodeShouldPersist() {
    BpmnModelInstance modelInstance = loadAndConvert("error-code.bpmn");
    assertThat(
            modelInstance.getDocument().getElementById("Error_16zktjx").getAttribute("errorCode"))
        .isEqualTo("someCode");
  }

  @Test
  void testErrorCodeMayBeNull() {
    BpmnModelInstance modelInstance = loadAndConvert("null-error-code.bpmn");
    assertThat(
            modelInstance.getDocument().getElementById("Error_16zktjx").getAttribute("errorCode"))
        .isNull();
  }

  @ParameterizedTest
  @CsvSource(
      value = {
        "FlexibleDurationEvent,=duration",
        "FlexibleDateEvent,=date",
        "FlexibleCycleEvent,=cycle",
        "FixedDurationEvent,PT1S",
        "FixedDateEvent,2019-10-01T12:00:00Z",
        "FixedCycleEvent,R3/PT1S"
      })
  void testTimerEvents(String elementId, String expectedExpression) {
    BpmnModelInstance modelInstance = loadAndConvert("flexible-timer-event.bpmn");
    String timerExpression =
        modelInstance
            .getDocument()
            .getElementById(elementId)
            .getChildElementsByNameNs(BPMN, "timerEventDefinition")
            .get(0)
            .getChildElements()
            .get(0)
            .getTextContent();
    assertThat(timerExpression).isEqualTo(expectedExpression);
  }

  @ParameterizedTest
  @CsvSource(
      value = {
        // start events
        "CycleStartEvent,true",
        "DurationStartEvent1,false",
        "DateStartEvent,true",
        // intermediate events
        "FlexibleDurationEvent,true",
        "FlexibleDateEvent,false",
        "FlexibleCycleEvent,false",
        // boundary events
        "DurationBoundaryEvent,true",
        "DateBoundaryEvent,false",
        "CycleBoundaryEvent,false",
        // event sub process
        "DateStartEvent1,true",
        "DurationStartEvent,false",
        "CycleStartEvent1,false"
      })
  void testTimerEventMessages(String elementId, boolean allowed) {
    BpmnDiagramCheckResult result = loadAndCheck("flexible-timer-event.bpmn");
    BpmnElementCheckResult elementResult = result.getResult(elementId);
    assertThat(elementResult).isNotNull();
    List<BpmnElementCheckMessage> warningMessages =
        elementResult.getMessages().stream()
            .filter(m -> m.getSeverity().equals(Severity.WARNING))
            .collect(Collectors.toList());
    if (allowed) {
      assertThat(warningMessages).isEmpty();
    } else {
      assertThat(warningMessages).hasSize(1);
    }
  }

  @Test
  void testAdapterDisabled() {
    DefaultConverterProperties modified = new DefaultConverterProperties();
    modified.setDefaultJobTypeEnabled(false);
    ConverterProperties properties = ConverterPropertiesFactory.getInstance().merge(modified);
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    BpmnModelInstance modelInstance =
        Bpmn.readModelFromStream(getClass().getClassLoader().getResourceAsStream("delegate.bpmn"));
    converter.convert(modelInstance, properties);
    List<DomElement> taskDefinition =
        modelInstance
            .getDocument()
            .getElementById("serviceTask")
            .getChildElementsByNameNs(BPMN, "extensionElements")
            .get(0)
            .getChildElementsByNameNs(ZEEBE, "taskDefinition");
    assertThat(taskDefinition).isEmpty();
  }

  @Test
  void testConditionalSequenceFlowWithLanguages() {
    BpmnDiagramCheckResult result = loadAndCheck("conditional-flow-many-languages.bpmn");
    assertThat(result.getResult("JuelSequenceFlow"))
        .isNotNull()
        .extracting(BpmnElementCheckResult::getMessages)
        .matches(l -> !l.isEmpty())
        .extracting(l -> l.get(0).getMessage())
        .isEqualTo(
            "Condition expression: Please review transformed expression: '${x == 1}' -> '=x = 1'.");
    assertThat(result.getResult("ExternalScriptSequenceFlow"))
        .isNotNull()
        .extracting(BpmnElementCheckResult::getMessages)
        .matches(l -> !l.isEmpty())
        .extracting(l -> l.get(0).getMessage())
        .isEqualTo(
            "Please translate the content from 'some-resource.js' to a valid FEEL expression.");
    assertThat(result.getResult("InternalScriptSequenceFlow"))
        .isNotNull()
        .extracting(BpmnElementCheckResult::getMessages)
        .matches(l -> !l.isEmpty())
        .extracting(l -> l.get(0).getMessage())
        .isEqualTo(
            "Please translate the javascript script from 'return x === 3;' to a valid FEEL expression.");
    assertThat(result.getResult("FeelScriptSequenceFlow"))
        .isNotNull()
        .extracting(BpmnElementCheckResult::getMessages)
        .matches(l -> !l.isEmpty())
        .extracting(l -> l.get(0).getMessage())
        .isEqualTo(
            "FEEL Condition expression: Please review transformed expression: 'x=4' -> '=x=4'. Check for custom FEEL functions as they are not supported by Zeebe.");
  }
}
