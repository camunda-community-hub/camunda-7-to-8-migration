package org.camunda.community.migration.example.extendedConverter;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.community.migration.converter.BpmnConverter;
import org.camunda.community.migration.converter.BpmnConverterFactory;
import org.camunda.community.migration.converter.ConverterPropertiesFactory;
import org.camunda.community.migration.converter.DomElementVisitorFactory;
import org.camunda.community.migration.converter.visitor.DomElementVisitor;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

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
    converter.convert(modelInstance,
        ConverterPropertiesFactory
            .getInstance()
            .get()
    );
    StringWriter writer = new StringWriter();
    converter.printXml(modelInstance.getDocument(),true,writer);
    System.out.println(writer);
  }
}
