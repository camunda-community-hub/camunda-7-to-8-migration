package org.camunda.community.converter.message;

import static org.junit.jupiter.api.Assertions.*;

import org.camunda.community.converter.expression.ExpressionTransformationResult;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

public class MessageTest {

  private static String random() {
    return RandomStringUtils.randomAlphabetic(10);
  }

  private static ExpressionTransformationResult result() {
    ExpressionTransformationResult result = new ExpressionTransformationResult();
    result.setNewExpression("=test");
    result.setOldExpression("${test}");
    return result;
  }

  @Test
  void shouldBuildEmpty() {
    Message message = Message.empty();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildScript() {
    Message message = Message.script();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCollection() {
    Message message = Message.collection(random(), random(), result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildProperty() {
    Message message = Message.property(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildResource() {
    Message message = Message.resource(random(), random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCallActivityNoCalledElementHint() {
    Message message = Message.callActivityNoCalledElementHint();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildInputOutputParameterIsNoExpression() {
    Message message = Message.inputOutputParameterIsNoExpression(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildLocalVariablePropagationNotSupported() {
    Message message = Message.localVariablePropagationNotSupported();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildInOutBusinessKeyNotSupported() {
    Message message = Message.inOutBusinessKeyNotSupported(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildAssignee() {
    Message message = Message.assignee(random(), random(), result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildTopic() {
    Message message = Message.topic(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildFormRef() {
    Message message = Message.formRef(random(), random(), result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildFormData() {
    Message message = Message.formData(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildFormKey() {
    Message message = Message.formKey(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildScriptFormat() {
    Message message = Message.scriptFormat(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildConditionExpression() {
    Message message = Message.conditionExpression(result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildInputOutputParameter() {
    Message message = Message.inputOutputParameter(random(), random(), result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildInAllNotRecommendedHint() {
    Message message = Message.inAllNotRecommendedHint();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildOutAllNotRecommendedHint() {
    Message message = Message.outAllNotRecommendedHint();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildElementCanBeUsed() {
    Message message = Message.elementCanBeUsed(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildElementNotSupported() {
    Message message = Message.elementNotSupported(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildLoopCardinality() {
    Message message = Message.loopCardinality();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildScriptFormatMissing() {
    Message message = Message.scriptFormatMissing();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildAttributeNotSupported() {
    Message message = Message.attributeNotSupported(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildAttributeRemoved() {
    Message message = Message.attributeRemoved(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCorrelationKeyHint() {
    Message message = Message.correlationKeyHint();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildConnectorId() {
    Message message = Message.connectorId(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildResultVariableBusinessRule() {
    Message message = Message.resultVariableBusinessRule(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildResultVariableRest() {
    Message message = Message.resultVariableRest(random(), random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildElementVariable() {
    Message message = Message.elementVariable(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildFailedJobRetryTimeCycle() {
    Message message = Message.failedJobRetryTimeCycle(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildErrorEventDefinition() {
    Message message = Message.errorEventDefinition(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildTaskListener() {
    Message message = Message.taskListener(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildPotentialStarter() {
    Message message = Message.potentialStarter(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildDelegateImplementation() {
    Message message = Message.delegateImplementation(random(), random(), random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildFieldContent() {
    Message message = Message.fieldContent(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildInputOutput() {
    Message message = Message.inputOutput();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCamundaScript() {
    Message message = Message.camundaScript(random(), random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildConnectorHint() {
    Message message = Message.connectorHint();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCandidateGroups() {
    Message message = Message.candidateGroups(random(), random(), result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCollectionHint() {
    Message message = Message.collectionHint();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCalledElement() {
    Message message = Message.calledElement(random(), random(), result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildDecisionRef() {
    Message message = Message.decisionRef(random(), random(), result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildExecutionListener() {
    Message message = Message.executionListener(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildElementNotSupportedHint() {
    Message message = Message.elementNotSupportedHint(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCompletionCondition() {
    Message message = Message.completionCondition(result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }
}
