package org.camunda.community.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.ConverterProperties;
import org.camunda.community.converter.convertible.AbstractActivityConvertible;
import org.camunda.community.converter.convertible.AbstractActivityConvertible.BpmnMultiInstanceLoopCharacteristics;
import org.camunda.community.converter.convertible.AbstractActivityConvertible.ZeebeLoopCharacteristics;

public class ActivityConversion extends AbstractTypedConversion<AbstractActivityConvertible> {

  @Override
  protected Class<AbstractActivityConvertible> type() {
    return AbstractActivityConvertible.class;
  }

  @Override
  public final void convertTyped(
      DomElement element, AbstractActivityConvertible convertible, ConverterProperties properties) {
    if (convertible.wasLoopCharacteristicsInitialized()) {
      createMultiInstance(
          element, convertible.getBpmnMultiInstanceLoopCharacteristics(), properties);
    }
  }

  private void createMultiInstance(
      DomElement element,
      BpmnMultiInstanceLoopCharacteristics bpmnMultiInstanceLoopCharacteristics,
      ConverterProperties properties) {
    DomElement multiInstanceLoopCharacteristics =
        element.getChildElements().stream()
            .filter(e -> e.getLocalName().equals("multiInstanceLoopCharacteristics"))
            .findFirst()
            .orElseGet(
                () -> {
                  DomElement mil =
                      element
                          .getDocument()
                          .createElement(
                              properties.getBpmnNamespace().getUri(),
                              "bpmn:multiInstanceLoopCharacteristics");
                  element.appendChild(mil);
                  return mil;
                });
    if (bpmnMultiInstanceLoopCharacteristics.isSequential()) {
      multiInstanceLoopCharacteristics.setAttribute("isSequential", Boolean.toString(true));
    }

    DomElement extensionElements =
        getExtensionElements(multiInstanceLoopCharacteristics, properties);
    extensionElements.appendChild(
        createLoopCharacteristics(
            element.getDocument(), bpmnMultiInstanceLoopCharacteristics, properties));
    if (bpmnMultiInstanceLoopCharacteristics.getCompletionCondition() != null) {
      getCompletionCondition(
          multiInstanceLoopCharacteristics, bpmnMultiInstanceLoopCharacteristics, properties);
    }
  }

  private DomElement createLoopCharacteristics(
      DomDocument document,
      BpmnMultiInstanceLoopCharacteristics bpmnMultiInstanceLoopCharacteristics,
      ConverterProperties properties) {
    DomElement loopCharacteristics =
        document.createElement(properties.getZeebeNamespace().getUri(), "loopCharacteristics");
    ZeebeLoopCharacteristics zbLoopCharacteristics =
        bpmnMultiInstanceLoopCharacteristics.getZeebeLoopCharacteristics();
    if (zbLoopCharacteristics.getInputCollection() != null) {
      loopCharacteristics.setAttribute(
          "inputCollection", zbLoopCharacteristics.getInputCollection());
    }
    if (zbLoopCharacteristics.getInputElement() != null) {
      loopCharacteristics.setAttribute("inputElement", zbLoopCharacteristics.getInputElement());
    }
    if (zbLoopCharacteristics.getOutputCollection() != null) {
      loopCharacteristics.setAttribute(
          "outputCollection", zbLoopCharacteristics.getOutputCollection());
    }
    if (zbLoopCharacteristics.getOutputElement() != null) {
      loopCharacteristics.setAttribute("outputElement", zbLoopCharacteristics.getOutputElement());
    }
    return loopCharacteristics;
  }

  private void getCompletionCondition(
      DomElement element,
      BpmnMultiInstanceLoopCharacteristics bpmnMultiInstanceLoopCharacteristics,
      ConverterProperties properties) {
    DomElement completionCondition =
        element.getChildElements().stream()
            .filter(e -> e.getLocalName().equals("completionCondition"))
            .findFirst()
            .orElseGet(
                () -> {
                  DomElement mil =
                      element
                          .getDocument()
                          .createElement(
                              properties.getBpmnNamespace().getUri(), "bpmn:completionCondition");
                  mil.setAttribute(
                      properties.getXsiNamespace().getUri(), "type", "bpmn:tFormalExpression");
                  element.appendChild(mil);
                  return mil;
                });
    completionCondition.setTextContent(
        bpmnMultiInstanceLoopCharacteristics.getCompletionCondition());
  }
}
