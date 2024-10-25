package org.camunda.community.migration.example.extendedConverter;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.BpmnConverter;
import org.camunda.community.migration.converter.BpmnConverterFactory;
import org.camunda.community.migration.converter.ConverterPropertiesFactory;
import org.camunda.community.migration.converter.DomElementVisitorFactory;
import org.camunda.community.migration.converter.visitor.DomElementVisitor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.camunda.community.migration.converter.NamespaceUri.*;

public class ExtendedConverterTest {
  private static BpmnModelInstance loadModelInstance(String bpmnFile) {
    return Bpmn.readModelFromStream(ExtendedConverterTest.class
        .getClassLoader()
        .getResourceAsStream(bpmnFile));
  }

  @Test
  void shouldLoadCustomDomElementVisitor() {
    List<DomElementVisitor> domElementVisitors = DomElementVisitorFactory
        .getInstance()
        .get();
    assertThat(domElementVisitors).hasAtLeastOneElementOfType(CustomDomElementVisitor.class);
  }

  @Test
  void shouldAddPropertiesToGateway() {
    BpmnConverter converter = BpmnConverterFactory
        .getInstance()
        .get();
    BpmnModelInstance modelInstance = loadModelInstance("example-model.bpmn");
    converter.convert(
        modelInstance,
        ConverterPropertiesFactory
            .getInstance()
            .get()
    );
    StringWriter writer = new StringWriter();
    converter.printXml(modelInstance.getDocument(), true, writer);
    System.out.println(writer);
  }

  @Test
  void shouldSetCustomJobType() {
    BpmnConverter converter = BpmnConverterFactory
        .getInstance()
        .get();
    BpmnModelInstance modelInstance = loadModelInstance("ExternalTaskWorker_Example.bpmn");
    converter.convert(
        modelInstance,
        ConverterPropertiesFactory
            .getInstance()
            .get()
    );
    DomElement extensionElements = modelInstance
        .getDocument()
        .getElementById("Activity_1qqj67q")
        .getChildElementsByNameNs(BPMN, "extensionElements")
        .get(0);
    DomElement header = extensionElements.getChildElementsByNameNs(ZEEBE, "taskHeaders").get(0).getChildElementsByNameNs(ZEEBE,"header").get(0);
    String headerKey = header.getAttribute(ZEEBE,"key");
    String headerValue = header.getAttribute(ZEEBE,"value");
    String jobType = extensionElements
        .getChildElementsByNameNs(ZEEBE, "taskDefinition")
        .get(0)
        .getAttribute(ZEEBE, "type");
    assertThat(jobType).isEqualTo("GenericWorker");
    assertThat(headerKey).isEqualTo("topic");
    assertThat(headerValue).isEqualTo("TestTopic");
    StringWriter writer = new StringWriter();
    converter.printXml(modelInstance.getDocument(), true, writer);
    System.out.println(writer);
  }
}
