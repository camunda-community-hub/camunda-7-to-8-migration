package org.camunda.community.converter;

import static org.assertj.core.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.camunda.community.converter.visitor.AbstractActivityVisitor;
import org.camunda.community.converter.visitor.AbstractAttributeVisitor;
import org.camunda.community.converter.visitor.AbstractElementVisitor;
import org.camunda.community.converter.visitor.AbstractEventDefinitionVisitor;
import org.camunda.community.converter.visitor.AbstractEventReferenceVisitor;
import org.camunda.community.converter.visitor.AbstractEventVisitor;
import org.camunda.community.converter.visitor.AbstractFlownodeVisitor;
import org.camunda.community.converter.visitor.AbstractGatewayVisitor;
import org.camunda.community.converter.visitor.DomElementVisitor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.junit.jupiter.api.Test;

public class CoverageTest {
  private static final Set<String> ACTIVITY_TYPES =
      Stream.of(
              "task",
              "serviceTask",
              "userTask",
              "sendTask",
              "businessRuleTask",
              "scriptTask",
              "receiveTask",
              "manualTask",
              "subProcess",
              "callActivity",
              "transaction")
          .collect(Collectors.toSet());
  private static final Set<String> EVENT_REFERENCE_TYPES =
      Stream.of("error", "escalation", "message", "signal").collect(Collectors.toSet());
  private static final Set<String> EVENT_TYPES =
      Stream.of(
              "startEvent",
              "intermediateCatchEvent",
              "intermediateThrowEvent",
              "boundaryEvent",
              "endEvent")
          .collect(Collectors.toSet());
  private static final Set<String> OTHER_ELEMENTS =
      Stream.of("definitions", "completionCondition", "loopCardinality", "conditionExpression")
          .collect(Collectors.toSet());

  private static final Set<String> GATEWAY_TYPES =
      Stream.of(
              "exclusiveGateway",
              "parallelGateway",
              "inclusiveGateway",
              "complexGateway",
              "eventBasedGateway")
          .collect(Collectors.toSet());

  private static final Set<String> EVENT_DEFINITION_TYPES =
      Stream.of(
              "messageEventDefinition",
              "timerEventDefinition",
              "errorEventDefinition",
              "signalEventDefinition",
              "conditionalEventDefinition",
              "escalationEventDefinition",
              "compensateEventDefinition",
              "cancelEventDefinition",
              "terminateEventDefinition",
              "linkEventDefinition",
              "multipleEventDefinition",
              "multipleParallelEventDefinition")
          .collect(Collectors.toSet());

  @Test
  public void shouldCoverAllExtensionElements() {
    assertThat(getCoveredElements()).containsAll(getElementsToCover());
  }

  @Test
  public void shouldCoverAllExtensionAttributes() {
    assertThat(getCoveredAttributes()).containsAll(getAttributesToCover());
  }

  @Test
  public void shouldCoverAllActivityTypes() {
    assertThat(getCoveredActivityTypes()).containsAll(ACTIVITY_TYPES);
  }

  @Test
  public void shouldCoverAllEventTypes() {
    assertThat(getCoveredEventTypes()).containsAll(EVENT_TYPES);
  }

  @Test
  public void shouldCoverOtherElements() {
    assertThat(getCoveredElements()).containsAll(OTHER_ELEMENTS);
  }

  @Test
  public void shouldCoverAllEventReferenceTypes() {
    assertThat(getCoveredEventReferenceTypes()).containsAll(EVENT_REFERENCE_TYPES);
  }

  @Test
  public void shouldCoverAllGatewayTypes() {
    assertThat(getCoveredGatewayTypes()).containsAll(GATEWAY_TYPES);
  }

  @Test
  public void shouldCoverAllEventDefinitionTypes() {
    assertThat(getCoveredEventDefinitionTypes()).containsAll(EVENT_DEFINITION_TYPES);
  }

  @Test
  public void shouldCoverAllCollaborationTypes() {
    assertThat(getCoveredElements())
        .containsAll(Stream.of("collaboration", "participant").collect(Collectors.toSet()));
  }

  @Test
  void shouldCoverLanes() {
    assertThat(getCoveredElements())
        .containsAll(Stream.of("lane", "laneSet", "flowNodeRef").collect(Collectors.toSet()));
  }

  private Set<String> getCoveredEventDefinitionTypes() {
    return getElementsFilteredByClass(AbstractEventDefinitionVisitor.class)
        .map(AbstractElementVisitor::localName)
        .collect(Collectors.toSet());
  }

  private Set<String> getCoveredGatewayTypes() {
    return getElementsFilteredByClass(AbstractGatewayVisitor.class)
        .map(AbstractElementVisitor::localName)
        .collect(Collectors.toSet());
  }

  private Set<String> getCoveredEventReferenceTypes() {
    return getElementsFilteredByClass(AbstractEventReferenceVisitor.class)
        .map(AbstractEventReferenceVisitor::localName)
        .collect(Collectors.toSet());
  }

  private Set<String> getCoveredActivityTypes() {
    return getElementsFilteredByClass(AbstractActivityVisitor.class)
        .map(AbstractFlownodeVisitor::localName)
        .collect(Collectors.toSet());
  }

  private Set<String> getCoveredEventTypes() {
    return getElementsFilteredByClass(AbstractEventVisitor.class)
        .map(AbstractEventVisitor::localName)
        .collect(Collectors.toSet());
  }

  private Set<String> getCoveredAttributes() {
    return getElementsFilteredByClass(AbstractAttributeVisitor.class)
        .map(AbstractAttributeVisitor::attributeLocalName)
        .collect(Collectors.toSet());
  }

  private Set<String> getCoveredElements() {
    return getElementsFilteredByClass(AbstractElementVisitor.class)
        .map(AbstractElementVisitor::localName)
        .collect(Collectors.toSet());
  }

  private <T extends DomElementVisitor> Stream<T> getElementsFilteredByClass(Class<T> type) {
    return DomElementVisitorFactory.getInstance().get().stream()
        .filter(domElementVisitor -> type.isAssignableFrom(domElementVisitor.getClass()))
        .map(type::cast);
  }

  private Set<String> getElementsToCover() {
    return readHtmlList("extensionElements.html");
  }

  private Set<String> getAttributesToCover() {
    return readHtmlList("extensionAttributes.html");
  }

  private Set<String> readHtmlList(String resourceName) {
    try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName)) {
      return Jsoup.parseBodyFragment(
              new BufferedReader(new InputStreamReader(in))
                  .lines()
                  .collect(Collectors.joining("\n")))
          .body()
          .childNodes()
          .stream()
          .flatMap(node -> node.childNodes().stream())
          .flatMap(node -> node.childNodes().stream())
          .flatMap(node -> node.childNodes().stream())
          .map(Node::toString)
          .collect(Collectors.toSet());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
