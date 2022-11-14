package org.camunda.community.converter.message;

import java.util.Map;
import org.camunda.community.converter.expression.ExpressionTransformationResult;

public interface Message {
  static Message collectionHint() {
    return new ComposedMessage("collection-hint", ContextBuilder.builder().build());
  }

  static Message callActivityNoCalledElementHint() {
    return new ComposedMessage(
        "call-activity-no-called-element-hint", ContextBuilder.builder().build());
  }

  static Message elementNotSupportedHint(String elementLocalName) {
    return new ComposedMessage(
        "element-not-supported-hint",
        ContextBuilder.builder().context(elementNotSupportedPrefix(elementLocalName)).build());
  }

  static Message completionCondition(ExpressionTransformationResult transformationResult) {

    return new ComposedMessage(
        "completion-condition",
        ContextBuilder.builder()
            .context(expressionTransformationResult(transformationResult))
            .build());
  }

  static Message candidateGroups(
      String attributeLocalName,
      String elementLocalName,
      ExpressionTransformationResult transformationResult) {
    return new ComposedMessage(
        "candidate-groups",
        ContextBuilder.builder()
            .context(
                supportedAttributeExpression(
                    attributeLocalName, elementLocalName, transformationResult))
            .build());
  }

  static Message calledElement(
      String attributeLocalName,
      String elementLocalName,
      ExpressionTransformationResult transformationResult) {
    return new ComposedMessage(
        "called-element",
        ContextBuilder.builder()
            .context(
                supportedAttributeExpression(
                    attributeLocalName, elementLocalName, transformationResult))
            .build());
  }

  static Message decisionRef(
      String attributeLocalName,
      String elementLocalName,
      ExpressionTransformationResult transformationResult) {
    return new ComposedMessage(
        "decision-ref",
        ContextBuilder.builder()
            .context(
                supportedAttributeExpression(
                    attributeLocalName, elementLocalName, transformationResult))
            .build());
  }

  static Message formRef(
      String attributeLocalName,
      String elementLocalName,
      ExpressionTransformationResult transformationResult) {
    return new ComposedMessage(
        "form-ref",
        ContextBuilder.builder()
            .context(
                supportedAttributeExpression(
                    attributeLocalName, elementLocalName, transformationResult))
            .build());
  }

  static Message collection(
      String attributeLocalName,
      String elementLocalName,
      ExpressionTransformationResult transformationResult) {
    return new ComposedMessage(
        "collection",
        ContextBuilder.builder()
            .context(
                supportedAttributeExpression(
                    attributeLocalName, elementLocalName, transformationResult))
            .build());
  }

  static Message assignee(
      String attributeLocalName,
      String elementLocalName,
      ExpressionTransformationResult transformationResult) {
    return new ComposedMessage(
        "assignee",
        ContextBuilder.builder()
            .context(
                supportedAttributeExpression(
                    attributeLocalName, elementLocalName, transformationResult))
            .build());
  }

  static Message conditionExpression(ExpressionTransformationResult transformationResult) {
    return new ComposedMessage(
        "condition-expression",
        ContextBuilder.builder()
            .context(expressionTransformationResult(transformationResult))
            .build());
  }

  static Message inputOutputParameterIsNoExpression(String elementLocalName, String parameterName) {
    return new ComposedMessage(
        "input-output-parameter-is-no-expression",
        ContextBuilder.builder()
            .context(elementNotTransformablePrefix(elementLocalName))
            .entry("parameterName", parameterName)
            .build());
  }

  static Message inputOutputParameter(
      String elementLocalName,
      String parameterName,
      ExpressionTransformationResult transformationResult) {
    return new ComposedMessage(
        "input-output-parameter",
        ContextBuilder.builder()
            .entry("parameterName", parameterName)
            .context(elementTransformedPrefix(elementLocalName))
            .context(expressionTransformationResult(transformationResult))
            .build());
  }

  static Message localVariablePropagationNotSupported() {
    return new ComposedMessage(
        "local-variable-propagation-not-supported-hint", ContextBuilder.builder().build());
  }

  static Message inAllNotRecommendedHint() {
    return new ComposedMessage("in-all-not-recommended-hint", ContextBuilder.builder().build());
  }

  static Message outAllNotRecommendedHint() {
    return new ComposedMessage("out-all-not-recommended-hint", ContextBuilder.builder().build());
  }

  static Message inOutBusinessKeyNotSupported(String elementLocalName) {
    return new ComposedMessage(
        "in-out-business-key-not-supported",
        ContextBuilder.builder()
            .context(businessKeyNotSupported())
            .context(elementNotTransformablePrefix(elementLocalName))
            .build());
  }

  static Message elementCanBeUsed(String elementLocalName) {
    return new ComposedMessage(
        "element-can-be-used",
        ContextBuilder.builder().context(elementCanBeUsedPrefix(elementLocalName)).build());
  }

  static Message elementNotSupported(String elementLocalName) {
    return new ComposedMessage(
        "element-not-supported",
        ContextBuilder.builder().context(elementNotSupportedPrefix(elementLocalName)).build());
  }

  static Message script() {
    return new ComposedMessage("script", ContextBuilder.builder().build());
  }

  static Message loopCardinality() {
    return new ComposedMessage("loop-cardinality", ContextBuilder.builder().build());
  }

  static Message scriptFormat(String headerName, String scriptFormat) {
    return new ComposedMessage(
        "script-format",
        ContextBuilder.builder()
            .entry("headerName", headerName)
            .entry("scriptFormat", scriptFormat)
            .build());
  }

  static Message scriptFormatMissing() {
    return new ComposedMessage("script-format-missing", ContextBuilder.builder().build());
  }

  static Message attributeNotSupported(String attributeLocalName, String elementLocalName) {
    return new ComposedMessage(
        "attribute-not-supported",
        ContextBuilder.builder()
            .context(attributeNotSupportedPrefix(attributeLocalName, elementLocalName))
            .build());
  }

  static Message attributeRemoved(String attributeLocalName, String elementLocalName) {
    return new ComposedMessage(
        "attribute-removed",
        ContextBuilder.builder()
            .entry("attributeLocalName", attributeLocalName)
            .entry("elementLocalName", elementLocalName)
            .build());
  }

  static Message correlationKeyHint() {
    return new ComposedMessage("correlation-key-hint", ContextBuilder.builder().build());
  }

  static Message connectorId(String elementLocalName) {
    return new ComposedMessage(
        "connector-id",
        ContextBuilder.builder().context(elementTransformedPrefix(elementLocalName)).build());
  }

  static Message property(String elementLocalName, String propertyName) {
    return new ComposedMessage(
        "property",
        ContextBuilder.builder()
            .entry("propertyName", propertyName)
            .context(elementTransformedPrefix(elementLocalName))
            .build());
  }

  static Message executionListener(String elementLocalName) {
    return new ComposedMessage(
        "execution-listener",
        ContextBuilder.builder().context(elementNotTransformablePrefix(elementLocalName)).build());
  }

  static Message empty() {
    return new EmptyMessage();
  }

  static Message resultVariableBusinessRule(String attributeLocalName, String elementLocalName) {
    return new ComposedMessage(
        "result-variable-business-rule",
        ContextBuilder.builder()
            .context(supportedAttributePrefix(attributeLocalName, elementLocalName))
            .build());
  }

  static Message resultVariableRest(
      String attributeLocalName, String elementLocalName, String headerName) {
    return new ComposedMessage(
        "result-variable-rest",
        ContextBuilder.builder()
            .context(supportedAttributePrefix(attributeLocalName, elementLocalName))
            .entry("headerName", headerName)
            .build());
  }

  static Message elementVariable(String attributeLocalName, String elementLocalName) {
    return new ComposedMessage(
        "element-variable",
        ContextBuilder.builder()
            .context(supportedAttributePrefix(attributeLocalName, elementLocalName))
            .build());
  }

  static Message resource(String attributeLocalName, String elementLocalName, String headerName) {
    return new ComposedMessage(
        "resource",
        ContextBuilder.builder()
            .context(supportedAttributePrefix(attributeLocalName, elementLocalName))
            .entry("headerName", headerName)
            .build());
  }

  static Message failedJobRetryTimeCycle(String elementLocalName, String timecycle) {
    return new ComposedMessage(
        "failed-job-retry-time-cycle",
        ContextBuilder.builder()
            .context(elementNotTransformablePrefix(elementLocalName))
            .entry("timecycle", timecycle)
            .build());
  }

  static Message errorEventDefinition(String elementLocalName) {
    return new ComposedMessage(
        "error-event-definition",
        ContextBuilder.builder().context(elementNotTransformablePrefix(elementLocalName)).build());
  }

  static Message taskListener(String elementLocalName) {
    return new ComposedMessage(
        "task-listener",
        ContextBuilder.builder().context(elementNotTransformablePrefix(elementLocalName)).build());
  }

  static Message formData(String elementLocalName) {
    return new ComposedMessage(
        "form-data",
        ContextBuilder.builder().context(elementNotTransformablePrefix(elementLocalName)).build());
  }

  static Message topic(String attributeLocalName, String elementLocalName) {
    return new ComposedMessage(
        "topic",
        ContextBuilder.builder()
            .context(supportedAttributePrefix(attributeLocalName, elementLocalName))
            .build());
  }

  static Message potentialStarter(String elementLocalName) {
    return new ComposedMessage(
        "potential-starter",
        ContextBuilder.builder().context(elementNotTransformablePrefix(elementLocalName)).build());
  }

  static Message formKey(String attributeLocalName, String elementLocalName) {
    return new ComposedMessage(
        "form-key",
        ContextBuilder.builder()
            .context(supportedAttributePrefix(attributeLocalName, elementLocalName))
            .build());
  }

  static Message delegateImplementation(
      String attributeLocalName, String elementLocalName, String binding, String jobType) {
    return new ComposedMessage(
        "delegate-implementation",
        ContextBuilder.builder()
            .context(supportedAttributePrefix(attributeLocalName, elementLocalName))
            .entry("binding", binding)
            .entry("jobType", jobType)
            .build());
  }

  static Message fieldContent(String elementLocalName) {
    return new ComposedMessage(
        "field-content",
        ContextBuilder.builder().context(elementNotTransformablePrefix(elementLocalName)).build());
  }

  static Message inputOutput() {
    return new ComposedMessage("input-output", ContextBuilder.builder().build());
  }

  static Message camundaScript(String elementLocalName, String script, String scriptFormat) {
    return new ComposedMessage(
        "camunda.script",
        ContextBuilder.builder()
            .context(elementNotTransformablePrefix(elementLocalName))
            .entry("script", script)
            .entry("scriptFormat", scriptFormat)
            .build());
  }

  static Message connectorHint() {
    return new ComposedMessage("connector-hint", ContextBuilder.builder().build());
  }

  static Map<String, String> attributeNotSupportedPrefix(
      String attributeLocalName, String elementLocalName) {
    return ContextBuilder.builder()
        .entry("attributeLocalName", attributeLocalName)
        .entry("elementLocalName", elementLocalName)
        .build();
  }

  static Map<String, String> elementCanBeUsedPrefix(String elementLocalName) {
    return ContextBuilder.builder().entry("elementLocalName", elementLocalName).build();
  }

  static Map<String, String> businessKeyNotSupported() {
    return ContextBuilder.builder().build();
  }

  static Map<String, String> supportedAttributeExpression(
      String attributeLocalName,
      String elementLocalName,
      ExpressionTransformationResult transformationResult) {
    return ContextBuilder.builder()
        .context(supportedAttributePrefix(attributeLocalName, elementLocalName))
        .context(expressionTransformationResult(transformationResult))
        .build();
  }

  static Map<String, String> expressionTransformationResult(
      ExpressionTransformationResult transformationResult) {
    return ContextBuilder.builder()
        .entry("oldExpression", transformationResult.getOldExpression())
        .entry("newExpression", transformationResult.getNewExpression())
        .build();
  }

  static Map<String, String> supportedAttributePrefix(
      String attributeLocalName, String elementLocalName) {
    return ContextBuilder.builder()
        .entry("attributeLocalName", attributeLocalName)
        .entry("elementLocalName", elementLocalName)
        .build();
  }

  static Map<String, String> elementNotSupportedPrefix(String elementLocalName) {
    return ContextBuilder.builder().entry("elementLocalName", elementLocalName).build();
  }

  static Map<String, String> elementTransformedPrefix(String elementLocalName) {
    return ContextBuilder.builder().entry("elementLocalName", elementLocalName).build();
  }

  static Map<String, String> elementNotTransformablePrefix(String elementLocalName) {
    return ContextBuilder.builder().entry("elementLocalName", elementLocalName).build();
  }

  String getMessage();

  String getLink();
}
