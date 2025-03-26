package org.camunda.community.migration.converter;

import static org.assertj.core.api.Assertions.*;
import static org.camunda.community.migration.converter.NamespaceUri.*;
import static org.camunda.community.migration.converter.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DiagramCheckResult.ElementCheckMessage;
import org.camunda.community.migration.converter.DiagramCheckResult.ElementCheckResult;
import org.camunda.community.migration.converter.DiagramCheckResult.Severity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiagramConverterTest {
  private static final Logger LOG = LoggerFactory.getLogger(DiagramConverterTest.class);

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
        "flexible-timer-event.bpmn",
        "business-rule-task-as-expression.bpmn",
        "message-event-definition-handling.bpmn",
        "escalation-code.bpmn",
        "execution-listener.bpmn",
        "version-tag.bpmn",
        "form-ref-version.bpmn",
        "form-ref-deployment.bpmn",
        "decision-ref-version.bpmn",
        "decision-ref-deployment.bpmn",
        "feel_expr_not_tranformed.bpmn",
        "decision-ref-deployment.bpmn",
        "delegate.bpmn",
        "decision-ref-deployment.bpmn",
        "delegate-expression-listener.bpmn"
      })
  public void shouldConvertBpmn(String bpmnFile) {
    DiagramConverter converter = DiagramConverterFactory.getInstance().get();
    ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();
    BpmnModelInstance modelInstance =
        Bpmn.readModelFromStream(this.getClass().getClassLoader().getResourceAsStream(bpmnFile));
    printModel(modelInstance);
    DiagramCheckResult result = converter.check(bpmnFile, modelInstance, properties);
    printModel(modelInstance);
    StringWriter writer = new StringWriter();
    converter.printXml(modelInstance.getDocument(), true, writer);
    ByteArrayInputStream stream = new ByteArrayInputStream(writer.toString().getBytes());
    io.camunda.zeebe.model.bpmn.Bpmn.readModelFromStream(stream);
  }

  @ParameterizedTest
  @CsvSource(value = {"first.dmn"})
  public void shouldConvertDmn(String dmnFile) {
    DiagramConverter converter = DiagramConverterFactory.getInstance().get();
    ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();
    DmnModelInstance modelInstance =
        Dmn.readModelFromStream(this.getClass().getClassLoader().getResourceAsStream(dmnFile));
    printModel(modelInstance);
    DiagramCheckResult result = converter.check(dmnFile, modelInstance, properties);
    printModel(modelInstance);
    StringWriter writer = new StringWriter();
    converter.printXml(modelInstance.getDocument(), true, writer);
    ByteArrayInputStream stream = new ByteArrayInputStream(writer.toString().getBytes());
  }

  private void printModel(ModelInstance modelInstance) {
    StringWriter writer = new StringWriter();
    DiagramConverterFactory.getInstance().get().printXml(modelInstance.getDocument(), true, writer);
    String[] processModel = writer.toString().split("\n");
    for (int i = 0; i < processModel.length; i++) {
      LOG.debug("{}     {}", i, processModel[i]);
    }
  }

  @Test
  public void shouldNotConvertC8() {
    DiagramConverter converter = DiagramConverterFactory.getInstance().get();
    ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();
    BpmnModelInstance modelInstance =
        Bpmn.readModelFromStream(
            this.getClass().getClassLoader().getResourceAsStream("c8_simple.bpmn"));
    Assertions.assertThrows(
        RuntimeException.class, () -> converter.convert(modelInstance, properties));
  }

  @Test
  public void testDelegateHint() {
    DiagramCheckResult result = loadAndCheck("java-delegate-class-c7.bpmn");
    ElementCheckResult delegateClassServiceTask = result.getResult("DelegateClassServiceTask");
    assertNotNull(delegateClassServiceTask);
    assertThat(delegateClassServiceTask.getMessages()).hasSize(1);
    ElementCheckMessage message = delegateClassServiceTask.getMessages().get(0);
    assertThat(message.getMessage())
        .isEqualTo(
            "Attribute 'class' on 'serviceTask' was mapped. Delegate call to 'com.camunda.consulting.MyDelegate' was transformed to job type 'camunda-7-adapter'. Please review your implementation.");
  }

  @Test
  public void testTaskListenerHints() {
    DiagramCheckResult result = loadAndCheck("user-task-listener-implementations.bpmn");
    ElementCheckResult javaClassCheckResult = result.getResult("UserTaskUseJavaClass");
    assertThat(javaClassCheckResult.getMessages()).hasSize(1);
    assertThat(javaClassCheckResult.getMessages().get(0).getMessage())
        .isEqualTo(
            "Listener at 'create' with implementation 'com.camunda.consulting.TaskListenerExample' cannot be transformed. Task Listeners do not exist in Zeebe.");

    ElementCheckResult delegateExpressionCheckResult =
        result.getResult("UserTaskUseDelegateExpression");
    assertThat(delegateExpressionCheckResult.getMessages()).hasSize(1);
    assertThat(delegateExpressionCheckResult.getMessages().get(0).getMessage())
        .isEqualTo(
            "Listener at 'assignment' with implementation '${taskListenerExample}' cannot be transformed. Task Listeners do not exist in Zeebe.");

    ElementCheckResult expressionCheckResult = result.getResult("UserTaskUseExpression");
    assertThat(expressionCheckResult.getMessages()).hasSize(1);
    assertThat(expressionCheckResult.getMessages().get(0).getMessage())
        .isEqualTo(
            "Listener at 'complete' with implementation '${delegateTask.setName(\"my expression name\")}' cannot be transformed. Task Listeners do not exist in Zeebe.");

    ElementCheckResult inlineScriptCheckResult = result.getResult("UserTaskUseInlineScript");
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
    DiagramCheckResult result = loadAndCheckAgainstVersion("or-gateways.bpmn", "8.0.0");
    ElementCheckResult forkGateway = result.getResult("ForkGateway");
    assertThat(forkGateway.getMessages()).hasSize(1);
    assertThat(forkGateway.getMessages().get(0).getMessage())
        .isEqualTo(
            "Element 'Inclusive Gateway' is not supported in Zeebe version '8.0.0'. It is available in version '8.1.0'.");
    ElementCheckResult joinGateway = result.getResult("JoinGateway");
    assertThat(joinGateway.getMessages()).hasSize(1);
    assertThat(joinGateway.getMessages().get(0).getMessage())
        .isEqualTo(
            "Element 'Inclusive Gateway' is not supported in Zeebe version '8.0.0'. It is available in version '8.6.0'.");
  }

  @Test
  void testOrGateways_8_1() {
    String bpmnFile = "or-gateways.bpmn";
    DiagramCheckResult result = loadAndCheckAgainstVersion(bpmnFile, "8.1.0");
    ElementCheckResult joinGateway = result.getResult("JoinGateway");
    assertThat(joinGateway.getMessages()).hasSize(1);
    assertThat(joinGateway.getMessages().get(0).getMessage())
        .isEqualTo(
            "Element 'Inclusive Gateway' is not supported in Zeebe version '8.1.0'. It is available in version '8.6.0'.");
  }

  @Test
  void testCallActivityBefore_8_3() {
    DiagramCheckResult result = loadAndCheckAgainstVersion("call-activity-latest.bpmn", "8.2");
    ElementCheckResult callActivityResult = result.getResult("callLatest");
    assertThat(callActivityResult.getMessages()).hasSize(2);
    assertThat(callActivityResult.getMessages().get(0).getMessage())
        .isEqualTo(
            "Element 'camunda:in' with attribute 'variables=\"all\"' is removed. It is default in Zeebe before 8.3.");
    assertThat(callActivityResult.getMessages().get(0).getSeverity()).isEqualTo(Severity.INFO);
    assertThat(callActivityResult.getMessages().get(1).getMessage())
        .isEqualTo(
            "Element 'camunda:out' with attribute 'variables=\"all\"' is mapped to 'propagateAllChildVariables=\"true\"'.");
    assertThat(callActivityResult.getMessages().get(1).getSeverity()).isEqualTo(Severity.INFO);
  }

  @Test
  void testCallActivityLatest() {
    DiagramCheckResult result = loadAndCheck("call-activity-latest.bpmn");
    ElementCheckResult callActivityResult = result.getResult("callLatest");
    assertThat(callActivityResult.getMessages()).hasSize(2);
    assertThat(callActivityResult.getMessages().get(0).getMessage())
        .isEqualTo(
            "Element 'camunda:in' with attribute 'variables=\"all\"' is mapped to 'propagateAllParentVariables=\"true\"'.");
    assertThat(callActivityResult.getMessages().get(0).getSeverity()).isEqualTo(Severity.INFO);
    assertThat(callActivityResult.getMessages().get(1).getMessage())
        .isEqualTo(
            "Element 'camunda:out' with attribute 'variables=\"all\"' is mapped to 'propagateAllChildVariables=\"true\"'.");
    assertThat(callActivityResult.getMessages().get(1).getSeverity()).isEqualTo(Severity.INFO);
  }

  @Test
  void testCallActivityDeployment() {
    DiagramCheckResult result = loadAndCheckAgainstVersion("call-activity-deployment.bpmn", "8.5");
    ElementCheckResult callActivityResult = result.getResult("callDeployment");
    assertThat(callActivityResult.getMessages()).hasSize(3);
    assertThat(callActivityResult.getMessages().get(0).getMessage())
        .isEqualTo(
            "Attribute 'calledElementBinding' with value 'deployment' on 'callActivity' is not supported.");
    assertThat(callActivityResult.getMessages().get(0).getSeverity()).isEqualTo(Severity.WARNING);
    assertThat(callActivityResult.getMessages().get(1).getMessage())
        .isEqualTo(
            "Element 'camunda:in' with attribute 'variables=\"all\"' is mapped to 'propagateAllParentVariables=\"true\"'.");
    assertThat(callActivityResult.getMessages().get(1).getSeverity()).isEqualTo(Severity.INFO);
    assertThat(callActivityResult.getMessages().get(2).getMessage())
        .isEqualTo(
            "Element 'camunda:out' with attribute 'variables=\"all\"' is mapped to 'propagateAllChildVariables=\"true\"'.");
    assertThat(callActivityResult.getMessages().get(2).getSeverity()).isEqualTo(Severity.INFO);
  }

  @Test
  void testCallActivityDeployment_8_6() {
    DiagramCheckResult result = loadAndCheckAgainstVersion("call-activity-deployment.bpmn", "8.6");
    ElementCheckResult callActivityResult = result.getResult("callDeployment");
    assertThat(callActivityResult.getMessages()).hasSize(3);
    assertThat(callActivityResult.getMessages().get(0).getMessage())
        .isEqualTo("Called element reference binding has been mapped.");
    assertThat(callActivityResult.getMessages().get(0).getSeverity()).isEqualTo(Severity.INFO);
    assertThat(callActivityResult.getMessages().get(1).getMessage())
        .isEqualTo(
            "Element 'camunda:in' with attribute 'variables=\"all\"' is mapped to 'propagateAllParentVariables=\"true\"'.");
    assertThat(callActivityResult.getMessages().get(1).getSeverity()).isEqualTo(Severity.INFO);
    assertThat(callActivityResult.getMessages().get(2).getMessage())
        .isEqualTo(
            "Element 'camunda:out' with attribute 'variables=\"all\"' is mapped to 'propagateAllChildVariables=\"true\"'.");
    assertThat(callActivityResult.getMessages().get(2).getSeverity()).isEqualTo(Severity.INFO);
  }

  @Test
  void testReferences() {
    DiagramCheckResult result = loadAndCheck("message-example.bpmn");
    ElementCheckResult receiveTask = result.getResult("Receive1Task");
    assertThat(receiveTask).isNotNull();
    assertThat(receiveTask.getReferences()).hasSize(1);
    assertThat(receiveTask.getReferences().get(0)).isEqualTo("Receive1Message");
    ElementCheckResult message = result.getResult("Receive1Message");
    assertThat(message).isNotNull();
    assertThat(message.getReferencedBy()).hasSize(1);
    assertThat(message.getReferencedBy().get(0)).isEqualTo("Receive1Task");
  }

  @Test
  void testExecutionListener() {
    DiagramCheckResult result = loadAndCheck("execution-listener.bpmn");
    ElementCheckResult serviceTaskWithListenerTask =
        result.getResult("ServiceTaskWithListenerTask");
    assertThat(serviceTaskWithListenerTask).isNotNull();
    assertThat(serviceTaskWithListenerTask.getMessages()).hasSize(7);
    assertThat(serviceTaskWithListenerTask.getMessages().get(1).getMessage())
        .isEqualTo(
            "Listener at 'end' with implementation '${endListener.execute(something)}' can be transformed to a job worker. Please adjust the job type.");
    assertThat(serviceTaskWithListenerTask.getMessages().get(2).getMessage())
        .isEqualTo(
            "Listener at 'start' with implementation '${anotherStartListener}' can be transformed to a job worker. Please adjust the job type.");
    assertThat(serviceTaskWithListenerTask.getMessages().get(3).getMessage())
        .isEqualTo(
            "Listener at 'end' with implementation 'groovy' can be transformed to a job worker. Please adjust the job type.");
    assertThat(serviceTaskWithListenerTask.getMessages().get(0).getMessage())
        .isEqualTo(
            "Element 'script' cannot be transformed. Script 'print(\"something\");' with format 'groovy' on 'executionListener'.");
    assertThat(serviceTaskWithListenerTask.getMessages().get(4).getMessage())
        .isEqualTo(
            "Listener at 'start' with implementation 'com.example.StartListener' can be transformed to a job worker. Please adjust the job type.");
  }

  @Test
  void testConditionalFlow() {
    DiagramCheckResult result = loadAndCheck("conditional-flow.bpmn");
    ElementCheckResult checkResult = result.getResult("SomethingWorkedSequenceFlow");
    List<ElementCheckMessage> messages = checkResult.getMessages();
    assertThat(messages).hasSize(2);
    assertThat(messages.get(0).getMessage()).isEqualTo("A Conditional flow is not supported.");
  }

  @Test
  void testInternalScript_8_2() {
    DiagramCheckResult result = loadAndCheckAgainstVersion("internal-script.bpmn", "8.2.0");
    assertThat(result)
        .isNotNull()
        .extracting(r -> r.getResult("FeelScriptTask"))
        .isNotNull()
        .extracting(ElementCheckResult::getMessages)
        .asList()
        .hasSize(2);
    assertThat(result.getResult("FeelScriptTask").getMessages().get(0).getMessage())
        .isEqualTo("Result variable is set to Zeebe script result variable.");
    assertThat(result.getResult("FeelScriptTask").getMessages().get(1).getMessage())
        .isEqualTo("Script is transformed to Zeebe script.");
  }

  @Test
  void testInternalScript_8_1() {
    DiagramCheckResult result = loadAndCheckAgainstVersion("internal-script.bpmn", "8.1.0");
    assertThat(result)
        .isNotNull()
        .extracting(r -> r.getResult("FeelScriptTask"))
        .isNotNull()
        .extracting(ElementCheckResult::getMessages)
        .asList()
        .hasSize(4);
    assertThat(result.getResult("FeelScriptTask").getMessages().get(0).getMessage())
        .isEqualTo("Script format 'feel' was set to header 'language'. Please review.");
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
  void testExcutionGetVariable() {
    DiagramCheckResult result = loadAndCheck("expression-get-variable.bpmn");
    List<ElementCheckMessage> equalsYesMessage =
        result.getResult("GetVariableEqualsYesFlow").getMessages();
    assertThat(equalsYesMessage).hasSize(1);
    assertThat(equalsYesMessage.get(0).getSeverity()).isEqualTo(Severity.REVIEW);
    assertThat(equalsYesMessage.get(0).getMessage()).contains("-> '=exampleVar = \"yes\"");
    List<ElementCheckMessage> notEqualsYesMessage =
        result.getResult("GetVariableNotEqualsYesFlow").getMessages();
    assertThat(notEqualsYesMessage).hasSize(1);
    assertThat(notEqualsYesMessage.get(0).getSeverity()).isEqualTo(Severity.REVIEW);
    assertThat(notEqualsYesMessage.get(0).getMessage()).contains("-> '=exampleVar != \"yes\"");
  }

  @Test
  void testExpressionWithMethodInvocation() {
    DiagramCheckResult result = loadAndCheck("expression-method-invocation.bpmn");
    List<ElementCheckMessage> easyExpressionMessage =
        result.getResult("EasyExpressionSequenceFlow").getMessages();
    assertThat(easyExpressionMessage).hasSize(1);
    assertThat(easyExpressionMessage.get(0).getSeverity()).isEqualTo(Severity.REVIEW);

    List<ElementCheckMessage> executionIsUsedMessage =
        result.getResult("ExecutionIsUsedSequenceFlow").getMessages();
    assertThat(executionIsUsedMessage).hasSize(1);
    assertThat(executionIsUsedMessage.get(0).getSeverity()).isEqualTo(Severity.REVIEW);
    assertThat(executionIsUsedMessage.get(0).getMessage())
        .contains("-> '=input != null and input > 5");

    List<ElementCheckMessage> methodInvocationIsUsedMessage =
        result.getResult("MethodInvocationIsUsedSequenceFlow").getMessages();
    assertThat(methodInvocationIsUsedMessage).hasSize(1);
    assertThat(methodInvocationIsUsedMessage.get(0).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(methodInvocationIsUsedMessage.get(0).getMessage())
        .contains("Method invocation is not possible in FEEL");
  }

  @Test
  void testInMappingWithMethodInvocationAndExecution() {
    DiagramCheckResult result = loadAndCheck("expression-method-invocation.bpmn");
    List<ElementCheckMessage> inMappingMessages =
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
    DiagramCheckResult result = loadAndCheck("expression-method-invocation.bpmn");
    List<ElementCheckMessage> outMappingMessages =
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
    DiagramCheckResult result = loadAndCheck("expression-method-invocation.bpmn");
    List<ElementCheckMessage> messages =
        result.getResult("MultiInstanceConfigurationWithExecutionServiceTask").getMessages();
    assertThat(messages).hasSize(4);
    assertThat(messages.get(0).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(messages.get(0).getMessage()).contains("Collecting results");

    assertThat(messages.get(1).getSeverity()).isEqualTo(Severity.REVIEW);
    assertThat(messages.get(1).getMessage()).contains(Arrays.asList("Collection", "-> '=myList'"));

    assertThat(messages.get(2).getSeverity()).isEqualTo(Severity.REVIEW);
    assertThat(messages.get(2).getMessage())
        .contains(Arrays.asList("Completion condition", "-> '=complete = true'"));

    assertThat(messages.get(3).getSeverity()).isEqualTo(Severity.INFO);
  }

  @Test
  void testMultiInstanceConfigurationWithMethodInvocation() {
    DiagramCheckResult result = loadAndCheck("expression-method-invocation.bpmn");
    List<ElementCheckMessage> messages =
        result.getResult("MultiInstanceConfigurationWithMethodInvocationServiceTask").getMessages();
    assertThat(messages).hasSize(4);
    assertThat(messages.get(0).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(messages.get(0).getMessage()).contains("Collecting results");

    assertThat(messages.get(1).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(messages.get(1).getMessage())
        .contains(Arrays.asList("Collection", "Method invocation is not possible in FEEL"));

    assertThat(messages.get(2).getSeverity()).isEqualTo(Severity.TASK);
    assertThat(messages.get(2).getMessage())
        .contains(
            Arrays.asList("Completion condition", "Method invocation is not possible in FEEL"));

    assertThat(messages.get(3).getSeverity()).isEqualTo(Severity.INFO);
  }

  @Test
  void testEmptyInputParameterMapping() {
    DiagramCheckResult result = loadAndCheck("empty-input-parameter.bpmn");
    List<ElementCheckMessage> messages = result.getResult("AUserTaskTask").getMessages();
    assertThat(messages).hasSize(1);
    ElementCheckMessage message = messages.get(0);
    assertThat(message.getMessage())
        .isEqualTo(
            "Input parameter 'inputParameterName': Please review transformed expression: '' -> '=null'.");
  }

  @Test
  void testExecutionListenerWithoutImpl() {
    DiagramCheckResult result = loadAndCheck("execution-listener-no-impl.bpmn");
    List<ElementCheckMessage> messages = result.getResult("ListenerWithoutImplTask").getMessages();
    assertThat(messages).hasSize(1);
    ElementCheckMessage message = messages.get(0);
    assertThat(message.getMessage())
        .isEqualTo(
            "Listener at 'start' with implementation 'null' can be transformed to a job worker. Please adjust the job type.");
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
        "DurationStartEvent,false",
        "DateStartEvent,true",
        // intermediate events
        "FlexibleDurationEvent,true",
        "FlexibleDateEvent,true",
        "FlexibleCycleEvent,false",
        // boundary events
        "DurationBoundaryEvent,true",
        "DateBoundaryEvent,true",
        "CycleBoundaryEvent,false",
        // boundary events non-interrupting
        "DurationNonInterruptingBoundaryEvent,true",
        "DateNonInterruptingBoundaryEvent,true",
        "CycleNonInterruptingBoundaryEvent,true",
        // event sub process
        "DateEventSubprocessStartEvent,true",
        "DurationEventSubprocessStartEvent,true",
        "CycleEventSubprocessStartEvent,false",
        // event sub process non-interrupting
        "DateEventSubprocessNonInterruptingStartEvent,true",
        "DurationEventSubprocessNonInterruptingStartEvent,true",
        "CycleEventSubprocessNonInterruptingStartEvent,true"
      })
  void testTimerEventMessages(String elementId, boolean allowed) {
    DiagramCheckResult result = loadAndCheck("flexible-timer-event.bpmn");
    ElementCheckResult elementResult = result.getResult(elementId);
    assertThat(elementResult).isNotNull();
    List<ElementCheckMessage> warningMessages =
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
    DiagramConverter converter = DiagramConverterFactory.getInstance().get();
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
    DiagramCheckResult result = loadAndCheck("conditional-flow-many-languages.bpmn");
    assertThat(result.getResult("JuelSequenceFlow"))
        .isNotNull()
        .extracting(ElementCheckResult::getMessages)
        .matches(l -> !l.isEmpty())
        .extracting(l -> l.get(0).getMessage())
        .isEqualTo(
            "Condition expression: Please review transformed expression: '${x == 1}' -> '=x = 1'.");
    assertThat(result.getResult("ExternalScriptSequenceFlow"))
        .isNotNull()
        .extracting(ElementCheckResult::getMessages)
        .matches(l -> !l.isEmpty())
        .extracting(l -> l.get(0).getMessage())
        .isEqualTo(
            "Please translate the content from 'some-resource.js' to a valid FEEL expression.");
    assertThat(result.getResult("InternalScriptSequenceFlow"))
        .isNotNull()
        .extracting(ElementCheckResult::getMessages)
        .matches(l -> !l.isEmpty())
        .extracting(l -> l.get(0).getMessage())
        .isEqualTo(
            "Please translate the javascript script from 'return x === 3;' to a valid FEEL expression.");
    assertThat(result.getResult("FeelScriptSequenceFlow"))
        .isNotNull()
        .extracting(ElementCheckResult::getMessages)
        .matches(l -> !l.isEmpty())
        .extracting(l -> l.get(0).getMessage())
        .isEqualTo(
            "FEEL Condition expression: Please review transformed expression: 'x=4' -> '=x=4'. Check for custom FEEL functions as they are not supported by Zeebe.");
  }

  @Test
  void testEscalationCode() {
    BpmnModelInstance modelInstance = loadAndConvert("escalation-code.bpmn");
    DomElement escalation = modelInstance.getDocument().getElementById("Escalation_2ja61hj");
    assertThat(escalation.getAttribute(BPMN, "name")).isEqualTo("EscalationName");
    assertThat(escalation.getAttribute(BPMN, "escalationCode")).isEqualTo("EscalationCode");
  }

  @Test
  void testMessageEventDefinitionOnThrowEvents() {
    BpmnModelInstance modelInstance = loadAndConvert("message-event-definition-handling.bpmn");
    DomElement catchEvent = modelInstance.getDocument().getElementById("CatchEvent");
    DomElement throwEvent = modelInstance.getDocument().getElementById("ThrowEvent");
    DomElement endEvent = modelInstance.getDocument().getElementById("EndEndEvent");
    DomElement message = modelInstance.getDocument().getElementById("Message_1o49fvh");
    assertThat(catchEvent).isNotNull();
    assertThat(throwEvent).isNotNull();
    assertThat(endEvent).isNotNull();
    assertThat(message).isNotNull();
    assertThat(throwEvent.getChildElementsByNameNs(BPMN, "extensionElements")).isEmpty();
    assertThat(endEvent.getChildElementsByNameNs(BPMN, "extensionElements")).isEmpty();
    assertThat(catchEvent.getChildElementsByNameNs(BPMN, "extensionElements")).hasSize(1);
    assertThat(message.getChildElementsByNameNs(BPMN, "extensionElements")).hasSize(1);
    assertThat(throwEvent.getChildElementsByNameNs(BPMN, "messageEventDefinition")).hasSize(1);
    assertThat(endEvent.getChildElementsByNameNs(BPMN, "messageEventDefinition")).hasSize(1);
    assertThat(catchEvent.getChildElementsByNameNs(BPMN, "messageEventDefinition")).hasSize(1);
    assertThat(
            message
                .getChildElementsByNameNs(BPMN, "extensionElements")
                .get(0)
                .getChildElementsByNameNs(CONVERSION, "referencedBy"))
        .hasSize(1);
    assertThat(
            message
                .getChildElementsByNameNs(BPMN, "extensionElements")
                .get(0)
                .getChildElementsByNameNs(CONVERSION, "referencedBy")
                .get(0)
                .getTextContent())
        .isEqualTo("CatchEvent");
  }

  @Test
  void testZeebeUserTask() {
    BpmnModelInstance modelInstance = loadAndConvert("example-c7_2.bpmn");
    DomElement userTask = modelInstance.getDocument().getElementById("Activity_1b9oq8z");
    assertThat(userTask).isNotNull();
    DomElement extensionElements =
        userTask.getChildElementsByNameNs(BPMN, "extensionElements").get(0);
    DomElement zeebeUserTask = extensionElements.getChildElementsByNameNs(ZEEBE, "userTask").get(0);
    assertThat(zeebeUserTask).isNotNull();
  }

  @Test
  void testZeebeUserTask_pre_8_5() {
    BpmnModelInstance modelInstance = loadAndConvert("example-c7_2.bpmn", "8.4");
    DomElement userTask = modelInstance.getDocument().getElementById("Activity_1b9oq8z");
    assertThat(userTask).isNotNull();
    assertThat(userTask.getChildElementsByNameNs(BPMN, "extensionElements")).isEmpty();
  }

  @Test
  void testEscalationEventOnServiceTask() {
    DiagramCheckResult bpmnDiagramCheckResult =
        loadAndCheckAgainstVersion("escalation-on-service-task.bpmn", "8.5");
    ElementCheckResult itEscalatesBoundaryEvent =
        bpmnDiagramCheckResult.getResult("ItEscalatesBoundaryEvent");
    assertThat(itEscalatesBoundaryEvent).isNotNull();
    assertThat(itEscalatesBoundaryEvent.getMessages()).hasSize(1);
    assertThat(itEscalatesBoundaryEvent.getMessages().get(0).getMessage())
        .isEqualTo(
            "Element 'Escalation Boundary Event attached to Service Task' is not supported in Zeebe version '8.5'. Please review.");
  }

  @Test
  void testEscalationEventOnSubProcess() {
    DiagramCheckResult bpmnDiagramCheckResult = loadAndCheck("escalation-on-subprocess.bpmn");
    ElementCheckResult itEscalatesBoundaryEvent =
        bpmnDiagramCheckResult.getResult("ItEscalatesBoundaryEvent");
    assertThat(itEscalatesBoundaryEvent).isNotNull();
    assertThat(itEscalatesBoundaryEvent.getMessages()).isEmpty();
  }

  @Test
  void testVersionTagConversion() {
    BpmnModelInstance modelInstance = loadAndConvert("version-tag.bpmn");
    DomElement process = modelInstance.getDocument().getElementById("process");
    assertThat(process).isNotNull();
    assertThat(process.getChildElementsByNameNs(BPMN, "extensionElements")).hasSize(1);
    assertThat(
            process
                .getChildElementsByNameNs(BPMN, "extensionElements")
                .get(0)
                .getChildElementsByNameNs(ZEEBE, "versionTag"))
        .hasSize(1);
    assertThat(
            process
                .getChildElementsByNameNs(BPMN, "extensionElements")
                .get(0)
                .getChildElementsByNameNs(ZEEBE, "versionTag")
                .get(0)
                .getAttribute(ZEEBE, "value"))
        .isEqualTo("1.0");
  }

  @Test
  void testFormRefDeploymentBindingConversion() {
    BpmnModelInstance modelInstance = loadAndConvert("form-ref-deployment.bpmn");
    DomElement userTask = modelInstance.getDocument().getElementById("userTask");
    assertThat(userTask).isNotNull();
    assertThat(userTask.getChildElementsByNameNs(BPMN, "extensionElements")).hasSize(1);
    assertThat(
            userTask
                .getChildElementsByNameNs(BPMN, "extensionElements")
                .get(0)
                .getChildElementsByNameNs(ZEEBE, "formDefinition"))
        .hasSize(1);
    assertThat(
            userTask
                .getChildElementsByNameNs(BPMN, "extensionElements")
                .get(0)
                .getChildElementsByNameNs(ZEEBE, "formDefinition")
                .get(0)
                .getAttribute(ZEEBE, "bindingType"))
        .isEqualTo("deployment");
  }

  @Test
  void testDecisionRefVersionBindingConversion() {
    BpmnModelInstance modelInstance = loadAndConvert("decision-ref-version.bpmn");
    DomElement userTask = modelInstance.getDocument().getElementById("businessRuleTask");
    assertThat(userTask).isNotNull();
    assertThat(userTask.getChildElementsByNameNs(BPMN, "extensionElements")).hasSize(1);
    assertThat(
            userTask
                .getChildElementsByNameNs(BPMN, "extensionElements")
                .get(0)
                .getChildElementsByNameNs(ZEEBE, "calledDecision"))
        .hasSize(1);
    assertThat(
            userTask
                .getChildElementsByNameNs(BPMN, "extensionElements")
                .get(0)
                .getChildElementsByNameNs(ZEEBE, "calledDecision")
                .get(0)
                .getAttribute(ZEEBE, "versionTag"))
        .isEqualTo("1.0");
    assertThat(
            userTask
                .getChildElementsByNameNs(BPMN, "extensionElements")
                .get(0)
                .getChildElementsByNameNs(ZEEBE, "calledDecision")
                .get(0)
                .getAttribute(ZEEBE, "bindingType"))
        .isEqualTo("versionTag");
  }

  @Test
  void testDecisionRefDeploymentBindingConversion() {
    BpmnModelInstance modelInstance = loadAndConvert("decision-ref-deployment.bpmn");
    DomElement userTask = modelInstance.getDocument().getElementById("businessRuleTask");
    assertThat(userTask).isNotNull();
    assertThat(userTask.getChildElementsByNameNs(BPMN, "extensionElements")).hasSize(1);
    assertThat(
            userTask
                .getChildElementsByNameNs(BPMN, "extensionElements")
                .get(0)
                .getChildElementsByNameNs(ZEEBE, "calledDecision"))
        .hasSize(1);
    assertThat(
            userTask
                .getChildElementsByNameNs(BPMN, "extensionElements")
                .get(0)
                .getChildElementsByNameNs(ZEEBE, "calledDecision")
                .get(0)
                .getAttribute(ZEEBE, "bindingType"))
        .isEqualTo("deployment");
  }

  @Test
  void testCalledElementRefVersionBindingConversion() {
    BpmnModelInstance modelInstance = loadAndConvert("called-element-ref-version.bpmn");
    DomElement userTask = modelInstance.getDocument().getElementById("callActivity");
    assertThat(userTask).isNotNull();
    assertThat(userTask.getChildElementsByNameNs(BPMN, "extensionElements")).hasSize(1);
    assertThat(
            userTask
                .getChildElementsByNameNs(BPMN, "extensionElements")
                .get(0)
                .getChildElementsByNameNs(ZEEBE, "calledElement"))
        .hasSize(1);
    assertThat(
            userTask
                .getChildElementsByNameNs(BPMN, "extensionElements")
                .get(0)
                .getChildElementsByNameNs(ZEEBE, "calledElement")
                .get(0)
                .getAttribute(ZEEBE, "versionTag"))
        .isEqualTo("1.0");
    assertThat(
            userTask
                .getChildElementsByNameNs(BPMN, "extensionElements")
                .get(0)
                .getChildElementsByNameNs(ZEEBE, "calledElement")
                .get(0)
                .getAttribute(ZEEBE, "bindingType"))
        .isEqualTo("versionTag");
  }

  @Test
  void testCalledElementRefDeploymentBindingConversion() {
    BpmnModelInstance modelInstance = loadAndConvert("called-element-ref-deployment.bpmn");
    DomElement userTask = modelInstance.getDocument().getElementById("callActivity");
    assertThat(userTask).isNotNull();
    assertThat(userTask.getChildElementsByNameNs(BPMN, "extensionElements")).hasSize(1);
    assertThat(
            userTask
                .getChildElementsByNameNs(BPMN, "extensionElements")
                .get(0)
                .getChildElementsByNameNs(ZEEBE, "calledElement"))
        .hasSize(1);
    assertThat(
            userTask
                .getChildElementsByNameNs(BPMN, "extensionElements")
                .get(0)
                .getChildElementsByNameNs(ZEEBE, "calledElement")
                .get(0)
                .getAttribute(ZEEBE, "bindingType"))
        .isEqualTo("deployment");
  }

  @Test
  void testFeelScriptInputShouldBeTransformed() {
    BpmnModelInstance modelInstance = loadAndConvert("feel_expr_not_tranformed.bpmn");
    DomElement serviceTask = modelInstance.getDocument().getElementById("Activity_1s02kf9");
    assertThat(serviceTask).isNotNull();
    DomElement extensionElements =
        serviceTask.getChildElementsByNameNs(BPMN, "extensionElements").get(0);
    assertThat(extensionElements).isNotNull();
    DomElement ioMapping = extensionElements.getChildElementsByNameNs(ZEEBE, "ioMapping").get(0);
    assertThat(ioMapping).isNotNull();
    DomElement input =
        ioMapping.getChildElementsByNameNs(ZEEBE, "input").stream()
            .filter(e -> e.getAttribute(ZEEBE, "target").equals("HinweisText"))
            .findFirst()
            .get();
    assertThat(input).isNotNull();
    assertThat(input.getAttribute(ZEEBE, "target")).isEqualTo("HinweisText");
    assertThat(input.getAttribute(ZEEBE, "source"))
        .isEqualTo("=\"Vorgang automatisiert durchgeführt und abgeschlossen\"");
  }

  @Test
  void testNonFeelScriptInputShouldNotBeTransformed() {
    BpmnModelInstance modelInstance = loadAndConvert("feel_expr_not_tranformed.bpmn");
    DomElement serviceTask = modelInstance.getDocument().getElementById("Activity_1s02kf9");
    assertThat(serviceTask).isNotNull();
    DomElement extensionElements =
        serviceTask.getChildElementsByNameNs(BPMN, "extensionElements").get(0);
    assertThat(extensionElements).isNotNull();
    DomElement ioMapping = extensionElements.getChildElementsByNameNs(ZEEBE, "ioMapping").get(0);
    assertThat(ioMapping).isNotNull();
    Optional<DomElement> input =
        ioMapping.getChildElementsByNameNs(ZEEBE, "input").stream()
            .filter(e -> e.getAttribute(ZEEBE, "target").equals("anotherReference"))
            .findFirst();
    assertThat(input).isEmpty();
  }

  @Test
  void testDefaultResultVariable() {
    BpmnModelInstance modelInstance = loadAndConvert("default-result-variable.bpmn");
    DomElement businessRuleTask = modelInstance.getDocument().getElementById("businessRuleTask");
    assertThat(businessRuleTask).isNotNull();
    assertThat(businessRuleTask.getChildElementsByNameNs(BPMN, "extensionElements")).hasSize(1);
    DomElement extensionElements =
        businessRuleTask.getChildElementsByNameNs(BPMN, "extensionElements").get(0);
    assertThat(extensionElements).isNotNull();
    DomElement calledDecision =
        extensionElements.getChildElementsByNameNs(ZEEBE, "calledDecision").get(0);
    assertThat(calledDecision).isNotNull();
    String resultVariable = calledDecision.getAttribute(ZEEBE, "resultVariable");
    assertThat(resultVariable).isNotNull().isEqualTo("decisionResult");
  }

  @Test
  void testGatewayExecutionListenerShouldBeTransformed() {
    BpmnModelInstance modelInstance = loadAndConvert("gateway-with-el.bpmn");
    DomElement gateway = modelInstance.getDocument().getElementById("GatewayWithEL");
    assertThat(gateway).isNotNull();
    DomElement extensionElements =
        gateway.getChildElementsByNameNs(BPMN, "extensionElements").get(0);
    assertThat(extensionElements).isNotNull();
    DomElement executionListeners =
        extensionElements.getChildElementsByNameNs(ZEEBE, "executionListeners").get(0);
    assertThat(executionListeners).isNotNull();
    DomElement executionListener =
        executionListeners.getChildElementsByNameNs(ZEEBE, "executionListener").get(0);
    assertThat(executionListener).isNotNull();
    assertThat(executionListener.getAttribute("type")).contains("hellYeah");
  }

  @Test
  void testShouldConvertDelegateExpressionListener() {
    BpmnModelInstance modelInstance = loadAndConvert("delegate-expression-listener.bpmn");
    DomElement serviceTask = modelInstance.getDocument().getElementById("serviceTask");
    assertThat(serviceTask).isNotNull();
    DomElement extensionElements =
        serviceTask.getChildElementsByNameNs(BPMN, "extensionElements").get(0);
    assertThat(extensionElements).isNotNull();
    DomElement executionListeners =
        extensionElements.getChildElementsByNameNs(ZEEBE, "executionListeners").get(0);
    assertThat(executionListeners).isNotNull();
    DomElement executionListener =
        executionListeners.getChildElementsByNameNs(ZEEBE, "executionListener").get(0);
    assertThat(executionListener).isNotNull();
    String type = executionListener.getAttribute("type");
    assertThat(type).isEqualTo("myNewJobType");
  }

  @Test
  void testShouldNotAppendElements() {
    DefaultConverterProperties properties = new DefaultConverterProperties();
    properties.setAppendElements(false);
    ConverterProperties converterProperties =
        ConverterPropertiesFactory.getInstance().merge(properties);
    BpmnModelInstance bpmnModelInstance =
        loadAndConvert("history-time-to-live.bpmn", converterProperties);
    DomElement historyTimeToLive =
        bpmnModelInstance.getDocument().getElementById("HistoryTimeToLive");
    assertThat(historyTimeToLive).isNotNull();
    assertThat(historyTimeToLive.getChildElements()).isEmpty();
  }

  @Test
  void shouldTransformDelegateExpressionAsJobType() {
    DefaultConverterProperties converterProperties = new DefaultConverterProperties();
    converterProperties.setUseDelegateExpressionAsJobType(true);
    BpmnModelInstance modelInstance =
        loadAndConvert(
            "delegate.bpmn", ConverterPropertiesFactory.getInstance().merge(converterProperties));
    printModel(modelInstance);
    DomElement serviceTask = modelInstance.getDocument().getElementById("serviceTask");
    assertThat(serviceTask).isNotNull();
    assertThat(serviceTask.getChildElementsByNameNs(BPMN, "extensionElements")).hasSize(1);
    DomElement extensionProperties =
        serviceTask.getChildElementsByNameNs(BPMN, "extensionElements").get(0);
    assertThat(extensionProperties.getChildElementsByNameNs(ZEEBE, "taskDefinition")).hasSize(1);
    DomElement taskDefinition =
        extensionProperties.getChildElementsByNameNs(ZEEBE, "taskDefinition").get(0);
    assertThat(taskDefinition.hasAttribute("type")).isTrue();
    assertThat(taskDefinition.getAttribute("type")).isEqualTo("myDelegate");
  }

  @Test
  void shouldNotTransformTakeListener() {
    DiagramCheckResult diagramCheckResult = loadAndCheck("take-listener.bpmn");
    ElementCheckResult takeListenerFlow = diagramCheckResult.getResult("takeListenerFlow");
    assertThat(takeListenerFlow).isNotNull();
    assertThat(takeListenerFlow.getMessages()).hasSize(1);
    ElementCheckMessage message = takeListenerFlow.getMessages().get(0);
    assertThat(message).isNotNull();
    assertThat(message.getMessage())
        .isEqualTo(
            "Listener at 'take' with implementation 'class' 'abc.def' cannot be transformed.");
  }
}
