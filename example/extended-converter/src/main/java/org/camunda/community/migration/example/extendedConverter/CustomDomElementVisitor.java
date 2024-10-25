package org.camunda.community.migration.example.extendedConverter;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.ExclusiveGatewayConvertible;
import org.camunda.community.migration.converter.message.ComposedMessage;
import org.camunda.community.migration.converter.visitor.DomElementVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.camunda.community.migration.converter.NamespaceUri.*;

public class CustomDomElementVisitor implements DomElementVisitor {
  private static final Logger LOG = LoggerFactory.getLogger(CustomDomElementVisitor.class);

  @Override
  public void visit(DomElementVisitorContext context) {
    DomElement element = context.getElement();
    if (element
        .getNamespaceURI()
        .equals(BPMN) && element
        .getLocalName()
        .equals("exclusiveGateway")) {
      // this is only applied to exclusive gateways
      List<String> outgoingSequenceFlowIds = findOutgoingSequenceFlows(element);
      if (outgoingSequenceFlowIds.size() > 1) {
        // only when they are forking
        List<ConditionExpression> expressions = outgoingSequenceFlowIds
            .stream()
            .map(id -> element
                .getDocument()
                .getElementById(id))
            .filter(Objects::nonNull)
            .map(this::extractConditionExpression)
            .filter(Objects::nonNull)
            .toList();
        String property = expressions
            .stream()
            .map(e -> e.id() + ": " + e.language() + ": " + e.expression())
            .collect(Collectors.joining(", "));
        context.addConversion(ExclusiveGatewayConvertible.class,
            gateway -> gateway.addZeebeProperty("originalExpressions", property)
        );
        ComposedMessage composedMessage = new ComposedMessage();
        composedMessage.setMessage("Original expressions are: " + property);
        composedMessage.setSeverity(Severity.INFO);
        context.addMessage(composedMessage);
      }
    }
  }

  private List<String> findOutgoingSequenceFlows(DomElement element) {
    return element
        .getChildElementsByNameNs(BPMN, "outgoing")
        .stream()
        .map(DomElement::getTextContent)
        .toList();
  }

  private ConditionExpression extractConditionExpression(DomElement sequenceFlow) {
    return sequenceFlow
        .getChildElementsByNameNs(BPMN, "conditionExpression")
        .stream()
        .map(dom -> {
          String language = dom.getAttribute("language");
          if (language == null) {
            language = "juel";
          }
          return new ConditionExpression(sequenceFlow.getAttribute("id"),language, dom.getTextContent());
        })
        .findFirst()
        .orElse(null);
  }

  private record ConditionExpression(String id, String language, String expression) {}
}
