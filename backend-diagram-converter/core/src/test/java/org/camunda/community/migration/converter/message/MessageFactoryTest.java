package org.camunda.community.migration.converter.message;

import static org.assertj.core.api.Assertions.*;
import static org.camunda.community.migration.converter.message.MessageFactory.*;
import static org.junit.jupiter.api.Assertions.*;

import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

// TODO create more fine-grained tests
public class MessageFactoryTest {

  private static String random() {
    return RandomStringUtils.randomAlphabetic(10);
  }

  private static ExpressionTransformationResult result() {
    return new ExpressionTransformationResult("${test}", "=test");
  }

  @Test
  void shouldBuildScriptJobType() {
    Message message = MessageFactory.scriptJobType(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildInclusiveGatewayJoin() {
    Message message = MessageFactory.inclusiveGatewayJoin();
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
    Message message = MessageFactory.inAllHint();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildOutAllNotRecommendedHint() {
    Message message = MessageFactory.outAllHint();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildElementNotSupported() {
    Message message = MessageFactory.elementNotSupported(random(), random());
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
    Message message = MessageFactory.attributeNotSupported("attributeName", "element", "value");
    assertNotNull(message);
    assertThat(message.getMessage())
        .isEqualTo("Attribute 'attributeName' with value 'value' on 'element' is not supported.");
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
    Message message = MessageFactory.failedJobRetryTimeCycle(random(), random(), 3, random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildFailedJobRetryTimeCycleRemoved() {
    Message message = MessageFactory.failedJobRetryTimeCycleRemoved(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildFailedJobRetryTimeCycleError() {
    Message message = MessageFactory.failedJobRetryTimeCycleError(random(), random());
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
    Message message = MessageFactory.taskListener("create", "my tasklistener implementation");
    assertNotNull(message);
    assertThat(message.getMessage())
        .isEqualTo(
            "Listener at 'create' with implementation 'my tasklistener implementation' cannot be transformed. Task Listeners do not exist in Zeebe.");
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
    Message message =
        MessageFactory.camundaScript("var message = \"hello world\"", "javascript", "taskListener");
    assertNotNull(message);
    assertThat(message.getMessage())
        .isEqualTo(
            "Element 'script' cannot be transformed. Script 'var message = \"hello world\"' with format 'javascript' on 'taskListener'.");
  }

  @Test
  void shouldBuildConnectorHint() {
    Message message = MessageFactory.connectorHint();
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCandidateGroups() {
    Message message = MessageFactory.candidateGroups(result());
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
    Message message = MessageFactory.executionListener("start", "${myExecutionListener}");
    assertNotNull(message);
    assertNotNull(message.getMessage());
    assertThat(message.getMessage())
        .isEqualTo(
            "Listener at 'start' with implementation '${myExecutionListener}' cannot be transformed. Execution Listeners do not exist in Zeebe.");
  }

  @Test
  void shouldBuildElementNotSupportedHint() {
    Message message = MessageFactory.elementNotSupportedHint(random(), random());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildCompletionCondition() {
    Message message = MessageFactory.completionCondition(result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
  }

  @Test
  void shouldBuildElementAvailableInFutureVersion() {
    Message message =
        MessageFactory.elementAvailableInFutureVersion("inclusiveGateway", "8.0.0", "8.1.0");
    assertNotNull(message);
    assertNotNull(message.getMessage());
    assertThat(message.getMessage())
        .isEqualTo(
            "Element 'inclusiveGateway' is not supported in Zeebe version '8.0.0'. It is available in version '8.1.0'.");
  }

  @Test
  void shouldBuildEscalationCode() {
    Message message = MessageFactory.escalationCode("old", "new");
    assertNotNull(message);
    assertNotNull(message.getMessage());
    assertNotNull(message.getSeverity());
    assertThat(message.getMessage())
        .isEqualTo("Escalation code is transformed from 'old' to 'new'. Please review.");
  }

  @Test
  void shouldBuildErrorCode() {
    Message message = MessageFactory.errorCode("old", "new");
    assertNotNull(message);
    assertNotNull(message.getMessage());
    assertNotNull(message.getSeverity());
    assertThat(message.getMessage())
        .isEqualTo("Error code is transformed from 'old' to 'new'. Please review.");
  }

  @Test
  void shouldBuildInternalScript() {
    Message message = MessageFactory.internalScript();
    assertNotNull(message);
    assertNotNull(message.getMessage());
    assertNotNull(message.getSeverity());
    assertThat(message.getMessage()).isEqualTo("Script is transformed to Zeebe script.");
  }

  @Test
  void shouldBuildResultVariableInternalScript() {
    Message message = MessageFactory.resultVariableInternalScript();
    assertNotNull(message);
    assertNotNull(message.getMessage());
    assertNotNull(message.getSeverity());
    assertThat(message.getMessage())
        .isEqualTo("Result variable is set to Zeebe script result variable.");
  }

  @Test
  void shouldBuildCandidateUsers() {
    Message message = MessageFactory.candidateUsers(result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
    assertNotNull(message.getSeverity());
    assertThat(message.getMessage())
        .isEqualTo(
            "Attribute 'candidateUsers' on 'userTask' was mapped. Please review transformed expression: '${test}' -> '=test'.");
  }

  @Test
  void shouldBuildDueDate() {
    Message message = MessageFactory.dueDate(result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
    assertNotNull(message.getSeverity());
    assertThat(message.getMessage())
        .isEqualTo(
            "Attribute 'dueDate' on 'userTask' was mapped. Please review transformed expression: '${test}' -> '=test'.");
  }

  @Test
  void shouldBuildFollowUpDate() {
    Message message = MessageFactory.followUpDate(result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
    assertNotNull(message.getSeverity());
    assertThat(message.getMessage())
        .isEqualTo(
            "Attribute 'followUpDate' on 'userTask' was mapped. Please review transformed expression: '${test}' -> '=test'.");
  }

  @Test
  void shouldBuildErrorCodeNoExpression() {
    Message message = errorCodeNoExpression();
    assertNotNull(message);
    assertNotNull(message.getMessage());
    assertNotNull(message.getSeverity());
    assertThat(message.getMessage()).isEqualTo("Error code cannot be an expression.");
  }

  @Test
  void shouldBuildEscalationCodeNoExpression() {
    Message message = escalationCodeNoExpression();
    assertNotNull(message);
    assertNotNull(message.getMessage());
    assertNotNull(message.getSeverity());
    assertThat(message.getMessage()).isEqualTo("Escalation code cannot be an expression.");
  }

  @Test
  void shouldBuildTimerExpressionMappedMessage() {
    Message message = timerExpressionMapped(result());
    assertNotNull(message);
    assertNotNull(message.getMessage());
    assertNotNull(message.getSeverity());
    assertThat(message.getMessage())
        .isEqualTo(
            "Timer expression was transformed: Please review transformed expression: '${test}' -> '=test'.");
  }

  @Test
  void shouldBuildTimerExpressionNotSupported() {
    String timerType = random();
    String timerValue = random();
    String eventType = random();
    String semanticVersion = random();
    Message message =
        timerExpressionNotSupported(timerType, timerValue, eventType, semanticVersion);
    assertNotNull(message);
    assertNotNull(message.getMessage());
    assertNotNull(message.getSeverity());
    assertThat(message.getMessage())
        .isEqualTo(
            "Timer of type '"
                + timerType
                + "' with value '"
                + timerValue
                + "' is not supported for event type '"
                + eventType
                + "' in Zeebe version '"
                + semanticVersion
                + "'.");
  }
}
