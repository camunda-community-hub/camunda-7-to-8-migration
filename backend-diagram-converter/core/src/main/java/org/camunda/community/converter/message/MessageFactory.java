package org.camunda.community.converter.message;

import java.util.Collections;
import java.util.Map;
import org.camunda.community.converter.expression.ExpressionTransformationResult;

public class MessageFactory {
  private static final MessageFactory INSTANCE = new MessageFactory();

  private final MessageTemplateProvider messageTemplateProvider = new MessageTemplateProvider();
  private final MessageTemplateProcessor messageTemplateProcessor = new MessageTemplateProcessor();
  private final MessageLinkProvider messageLinkProvider = new MessageLinkProvider();

  private MessageFactory() {}

  public static Message collectionHint() {
    return INSTANCE.staticMessage("collection-hint");
  }

  public static Message callActivityNoCalledElementHint() {
    return INSTANCE.staticMessage("call-activity-no-called-element-hint");
  }

  public static Message elementNotSupportedHint(String elementLocalName) {
    return INSTANCE.composeMessage(
        "element-not-supported-hint",
        ContextBuilder.builder().context(elementNotSupportedPrefix(elementLocalName)).build());
  }

  public static Message completionCondition(ExpressionTransformationResult transformationResult) {
    return INSTANCE.composeMessage(
        "completion-condition",
        ContextBuilder.builder()
            .context(expressionTransformationResult(transformationResult))
            .build());
  }

  public static Message candidateGroups(
      String attributeLocalName,
      String elementLocalName,
      ExpressionTransformationResult transformationResult) {
    return INSTANCE.composeMessage(
        "candidate-groups",
        ContextBuilder.builder()
            .context(
                supportedAttributeExpression(
                    attributeLocalName, elementLocalName, transformationResult))
            .build());
  }

  public static Message calledElement(
      String attributeLocalName,
      String elementLocalName,
      ExpressionTransformationResult transformationResult) {
    return INSTANCE.composeMessage(
        "called-element",
        ContextBuilder.builder()
            .context(
                supportedAttributeExpression(
                    attributeLocalName, elementLocalName, transformationResult))
            .build());
  }

  public static Message decisionRef(
      String attributeLocalName,
      String elementLocalName,
      ExpressionTransformationResult transformationResult) {
    return INSTANCE.composeMessage(
        "decision-ref",
        ContextBuilder.builder()
            .context(
                supportedAttributeExpression(
                    attributeLocalName, elementLocalName, transformationResult))
            .build());
  }

  public static Message formRef(
      String attributeLocalName,
      String elementLocalName,
      ExpressionTransformationResult transformationResult) {
    return INSTANCE.composeMessage(
        "form-ref",
        ContextBuilder.builder()
            .context(
                supportedAttributeExpression(
                    attributeLocalName, elementLocalName, transformationResult))
            .build());
  }

  public static Message collection(
      String attributeLocalName,
      String elementLocalName,
      ExpressionTransformationResult transformationResult) {
    return INSTANCE.composeMessage(
        "collection",
        ContextBuilder.builder()
            .context(
                supportedAttributeExpression(
                    attributeLocalName, elementLocalName, transformationResult))
            .build());
  }

  public static Message assignee(
      String attributeLocalName,
      String elementLocalName,
      ExpressionTransformationResult transformationResult) {
    return INSTANCE.composeMessage(
        "assignee",
        ContextBuilder.builder()
            .context(
                supportedAttributeExpression(
                    attributeLocalName, elementLocalName, transformationResult))
            .build());
  }

  public static Message conditionExpression(ExpressionTransformationResult transformationResult) {
    return INSTANCE.composeMessage(
        "condition-expression",
        ContextBuilder.builder()
            .context(expressionTransformationResult(transformationResult))
            .build());
  }

  public static Message inputOutputParameterIsNoExpression(
      String elementLocalName, String parameterName) {
    return INSTANCE.composeMessage(
        "input-output-parameter-is-no-expression",
        ContextBuilder.builder()
            .context(elementNotTransformablePrefix(elementLocalName))
            .entry("parameterName", parameterName)
            .build());
  }

  public static Message inputOutputParameter(
      String elementLocalName,
      String parameterName,
      ExpressionTransformationResult transformationResult) {
    return INSTANCE.composeMessage(
        "input-output-parameter",
        ContextBuilder.builder()
            .entry("parameterName", parameterName)
            .context(elementTransformedPrefix(elementLocalName))
            .context(expressionTransformationResult(transformationResult))
            .build());
  }

  public static Message localVariablePropagationNotSupported() {
    return INSTANCE.staticMessage("local-variable-propagation-not-supported-hint");
  }

  public static Message inAllNotRecommendedHint() {
    return INSTANCE.staticMessage("in-all-not-recommended-hint");
  }

  public static Message outAllNotRecommendedHint() {
    return INSTANCE.staticMessage("out-all-not-recommended-hint");
  }

  public static Message inOutBusinessKeyNotSupported(String elementLocalName) {
    return INSTANCE.composeMessage(
        "in-out-business-key-not-supported",
        ContextBuilder.builder()
            .context(businessKeyNotSupported())
            .context(elementNotTransformablePrefix(elementLocalName))
            .build());
  }

  public static Message elementCanBeUsed(String elementLocalName) {
    return INSTANCE.composeMessage(
        "element-can-be-used",
        ContextBuilder.builder().context(elementCanBeUsedPrefix(elementLocalName)).build());
  }

  public static Message elementNotSupported(String elementLocalName) {
    return INSTANCE.composeMessage(
        "element-not-supported",
        ContextBuilder.builder().context(elementNotSupportedPrefix(elementLocalName)).build());
  }

  public static Message script() {
    return INSTANCE.staticMessage("script");
  }

  public static Message loopCardinality() {
    return INSTANCE.staticMessage("loop-cardinality");
  }

  public static Message scriptFormat(String headerName, String scriptFormat) {
    return INSTANCE.composeMessage(
        "script-format",
        ContextBuilder.builder()
            .entry("headerName", headerName)
            .entry("scriptFormat", scriptFormat)
            .build());
  }

  public static Message scriptJobType(String elementLocalName, String jobType) {
    return INSTANCE.composeMessage(
        "script-job-type",
        ContextBuilder.builder()
            .context(elementTransformedPrefix(elementLocalName))
            .entry("jobType", jobType)
            .build());
  }

  public static Message scriptFormatMissing() {
    return INSTANCE.staticMessage("script-format-missing");
  }

  public static Message attributeNotSupported(String attributeLocalName, String elementLocalName) {
    return INSTANCE.composeMessage(
        "attribute-not-supported",
        ContextBuilder.builder()
            .context(attributeNotSupportedPrefix(attributeLocalName, elementLocalName))
            .build());
  }

  public static Message attributeRemoved(String attributeLocalName, String elementLocalName) {
    return INSTANCE.composeMessage(
        "attribute-removed",
        ContextBuilder.builder()
            .entry("attributeLocalName", attributeLocalName)
            .entry("elementLocalName", elementLocalName)
            .build());
  }

  public static Message correlationKeyHint() {
    return INSTANCE.staticMessage("correlation-key-hint");
  }

  public static Message connectorId(String elementLocalName) {
    return INSTANCE.composeMessage(
        "connector-id",
        ContextBuilder.builder().context(elementTransformedPrefix(elementLocalName)).build());
  }

  public static Message property(String elementLocalName, String propertyName) {
    return INSTANCE.composeMessage(
        "property",
        ContextBuilder.builder()
            .entry("propertyName", propertyName)
            .context(elementTransformedPrefix(elementLocalName))
            .build());
  }

  public static Message executionListener(String elementLocalName) {
    return INSTANCE.composeMessage(
        "execution-listener",
        ContextBuilder.builder().context(elementNotTransformablePrefix(elementLocalName)).build());
  }

  public static Message resultVariableBusinessRule(
      String attributeLocalName, String elementLocalName) {
    return INSTANCE.composeMessage(
        "result-variable-business-rule",
        ContextBuilder.builder()
            .context(supportedAttributePrefix(attributeLocalName, elementLocalName))
            .build());
  }

  public static Message resultVariableRest(
      String attributeLocalName, String elementLocalName, String headerName) {
    return INSTANCE.composeMessage(
        "result-variable-rest",
        ContextBuilder.builder()
            .context(supportedAttributePrefix(attributeLocalName, elementLocalName))
            .entry("headerName", headerName)
            .build());
  }

  public static Message elementVariable(String attributeLocalName, String elementLocalName) {
    return INSTANCE.composeMessage(
        "element-variable",
        ContextBuilder.builder()
            .context(supportedAttributePrefix(attributeLocalName, elementLocalName))
            .build());
  }

  public static Message resource(
      String attributeLocalName, String elementLocalName, String headerName) {
    return INSTANCE.composeMessage(
        "resource",
        ContextBuilder.builder()
            .context(supportedAttributePrefix(attributeLocalName, elementLocalName))
            .entry("headerName", headerName)
            .build());
  }

  public static Message failedJobRetryTimeCycle(String elementLocalName, String timecycle) {
    return INSTANCE.composeMessage(
        "failed-job-retry-time-cycle",
        ContextBuilder.builder()
            .context(elementNotTransformablePrefix(elementLocalName))
            .entry("timecycle", timecycle)
            .build());
  }

  public static Message errorEventDefinition(String elementLocalName) {
    return INSTANCE.composeMessage(
        "error-event-definition",
        ContextBuilder.builder().context(elementNotTransformablePrefix(elementLocalName)).build());
  }

  public static Message taskListener(String elementLocalName) {
    return INSTANCE.composeMessage(
        "task-listener",
        ContextBuilder.builder().context(elementNotTransformablePrefix(elementLocalName)).build());
  }

  public static Message formData(String elementLocalName) {
    return INSTANCE.composeMessage(
        "form-data",
        ContextBuilder.builder().context(elementNotTransformablePrefix(elementLocalName)).build());
  }

  public static Message topic(String attributeLocalName, String elementLocalName) {
    return INSTANCE.composeMessage(
        "topic",
        ContextBuilder.builder()
            .context(supportedAttributePrefix(attributeLocalName, elementLocalName))
            .build());
  }

  public static Message potentialStarter(String elementLocalName) {
    return INSTANCE.composeMessage(
        "potential-starter",
        ContextBuilder.builder().context(elementNotTransformablePrefix(elementLocalName)).build());
  }

  public static Message formKey(String attributeLocalName, String elementLocalName) {
    return INSTANCE.composeMessage(
        "form-key",
        ContextBuilder.builder()
            .context(supportedAttributePrefix(attributeLocalName, elementLocalName))
            .build());
  }

  public static Message delegateImplementation(
      String attributeLocalName, String elementLocalName, String binding, String jobType) {
    return INSTANCE.composeMessage(
        "delegate-implementation",
        ContextBuilder.builder()
            .context(supportedAttributePrefix(attributeLocalName, elementLocalName))
            .entry("binding", binding)
            .entry("jobType", jobType)
            .build());
  }

  public static Message fieldContent(String elementLocalName) {
    return INSTANCE.composeMessage(
        "field-content",
        ContextBuilder.builder().context(elementNotTransformablePrefix(elementLocalName)).build());
  }

  public static Message inputOutput() {
    return INSTANCE.staticMessage("input-output");
  }

  public static Message camundaScript(String elementLocalName, String script, String scriptFormat) {
    return INSTANCE.composeMessage(
        "camunda.script",
        ContextBuilder.builder()
            .context(elementNotTransformablePrefix(elementLocalName))
            .entry("script", script)
            .entry("scriptFormat", scriptFormat)
            .build());
  }

  public static Message connectorHint() {
    return INSTANCE.staticMessage("connector-hint");
  }

  private static Map<String, String> attributeNotSupportedPrefix(
      String attributeLocalName, String elementLocalName) {
    return ContextBuilder.builder()
        .entry("attributeLocalName", attributeLocalName)
        .entry("elementLocalName", elementLocalName)
        .build();
  }

  private static Map<String, String> elementCanBeUsedPrefix(String elementLocalName) {
    return ContextBuilder.builder().entry("elementLocalName", elementLocalName).build();
  }

  private static Map<String, String> businessKeyNotSupported() {
    return ContextBuilder.builder().build();
  }

  private static Map<String, String> supportedAttributeExpression(
      String attributeLocalName,
      String elementLocalName,
      ExpressionTransformationResult transformationResult) {
    return ContextBuilder.builder()
        .context(supportedAttributePrefix(attributeLocalName, elementLocalName))
        .context(expressionTransformationResult(transformationResult))
        .build();
  }

  private static Map<String, String> expressionTransformationResult(
      ExpressionTransformationResult transformationResult) {
    return ContextBuilder.builder()
        .entry("oldExpression", transformationResult.getOldExpression())
        .entry("newExpression", transformationResult.getNewExpression())
        .build();
  }

  private static Map<String, String> supportedAttributePrefix(
      String attributeLocalName, String elementLocalName) {
    return ContextBuilder.builder()
        .entry("attributeLocalName", attributeLocalName)
        .entry("elementLocalName", elementLocalName)
        .build();
  }

  private static Map<String, String> elementNotSupportedPrefix(String elementLocalName) {
    return ContextBuilder.builder().entry("elementLocalName", elementLocalName).build();
  }

  private static Map<String, String> elementTransformedPrefix(String elementLocalName) {
    return ContextBuilder.builder().entry("elementLocalName", elementLocalName).build();
  }

  private static Map<String, String> elementNotTransformablePrefix(String elementLocalName) {
    return ContextBuilder.builder().entry("elementLocalName", elementLocalName).build();
  }

  public static Message list() {
    return INSTANCE.emptyMessage();
  }

  public static Message generatedFormData() {
    return INSTANCE.emptyMessage();
  }

  public static Message value() {
    return INSTANCE.emptyMessage();
  }

  public static Message properties() {
    return INSTANCE.emptyMessage();
  }

  public static Message entry() {
    return INSTANCE.emptyMessage();
  }

  public static Message map() {
    return INSTANCE.emptyMessage();
  }

  private Message composeMessage(String templateName, Map<String, String> context) {
    ComposedMessage message = new ComposedMessage();
    MessageTemplate template = messageTemplateProvider.getMessageTemplate(templateName);
    message.setMessage(messageTemplateProcessor.decorate(template, context));
    message.setLink(messageLinkProvider.getMessageLink(templateName));
    return message;
  }

  private Message staticMessage(String templateName) {
    return composeMessage(templateName, Collections.emptyMap());
  }

  private Message emptyMessage() {
    return new EmptyMessage();
  }
}
