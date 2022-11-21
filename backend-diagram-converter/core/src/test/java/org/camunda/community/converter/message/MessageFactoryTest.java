package org.camunda.community.converter.message;

import static org.junit.jupiter.api.Assertions.*;

import org.camunda.community.converter.expression.ExpressionTransformationResult;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

public class MessageFactoryTest {

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
  void shouldBuildScriptJobType() {
    Message message = MessageFactory.scriptJobType(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildMap() {
    Message message = MessageFactory.map();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildScript() {
    Message message = MessageFactory.script();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCollection() {
    Message message = MessageFactory.collection(random(), random(), result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildProperty() {
    Message message = MessageFactory.property(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildResource() {
    Message message = MessageFactory.resource(random(), random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCallActivityNoCalledElementHint() {
    Message message = MessageFactory.callActivityNoCalledElementHint();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildInputOutputParameterIsNoExpression() {
    Message message = MessageFactory.inputOutputParameterIsNoExpression(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildLocalVariablePropagationNotSupported() {
    Message message = MessageFactory.localVariablePropagationNotSupported();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildInOutBusinessKeyNotSupported() {
    Message message = MessageFactory.inOutBusinessKeyNotSupported(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildAssignee() {
    Message message = MessageFactory.assignee(random(), random(), result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildTopic() {
    Message message = MessageFactory.topic(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildFormRef() {
    Message message = MessageFactory.formRef(random(), random(), result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildFormData() {
    Message message = MessageFactory.formData(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildFormKey() {
    Message message = MessageFactory.formKey(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildScriptFormat() {
    Message message = MessageFactory.scriptFormat(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildConditionExpression() {
    Message message = MessageFactory.conditionExpression(result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildInputOutputParameter() {
    Message message = MessageFactory.inputOutputParameter(random(), random(), result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildInAllNotRecommendedHint() {
    Message message = MessageFactory.inAllNotRecommendedHint();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildOutAllNotRecommendedHint() {
    Message message = MessageFactory.outAllNotRecommendedHint();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildElementCanBeUsed() {
    Message message = MessageFactory.elementCanBeUsed(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildElementNotSupported() {
    Message message = MessageFactory.elementNotSupported(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildLoopCardinality() {
    Message message = MessageFactory.loopCardinality();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildScriptFormatMissing() {
    Message message = MessageFactory.scriptFormatMissing();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildAttributeNotSupported() {
    Message message = MessageFactory.attributeNotSupported(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildAttributeRemoved() {
    Message message = MessageFactory.attributeRemoved(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCorrelationKeyHint() {
    Message message = MessageFactory.correlationKeyHint();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildConnectorId() {
    Message message = MessageFactory.connectorId(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildResultVariableBusinessRule() {
    Message message = MessageFactory.resultVariableBusinessRule(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildResultVariableRest() {
    Message message = MessageFactory.resultVariableRest(random(), random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildElementVariable() {
    Message message = MessageFactory.elementVariable(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildFailedJobRetryTimeCycle() {
    Message message = MessageFactory.failedJobRetryTimeCycle(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildErrorEventDefinition() {
    Message message = MessageFactory.errorEventDefinition(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildTaskListener() {
    Message message = MessageFactory.taskListener(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildPotentialStarter() {
    Message message = MessageFactory.potentialStarter(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildDelegateImplementation() {
    Message message = MessageFactory.delegateImplementation(random(), random(), random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildFieldContent() {
    Message message = MessageFactory.fieldContent(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildInputOutput() {
    Message message = MessageFactory.inputOutput();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCamundaScript() {
    Message message = MessageFactory.camundaScript(random(), random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildConnectorHint() {
    Message message = MessageFactory.connectorHint();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCandidateGroups() {
    Message message = MessageFactory.candidateGroups(random(), random(), result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCollectionHint() {
    Message message = MessageFactory.collectionHint();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCalledElement() {
    Message message = MessageFactory.calledElement(random(), random(), result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildDecisionRef() {
    Message message = MessageFactory.decisionRef(random(), random(), result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildExecutionListener() {
    Message message = MessageFactory.executionListener(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildElementNotSupportedHint() {
    Message message = MessageFactory.elementNotSupportedHint(random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCompletionCondition() {
    Message message = MessageFactory.completionCondition(result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }
}
