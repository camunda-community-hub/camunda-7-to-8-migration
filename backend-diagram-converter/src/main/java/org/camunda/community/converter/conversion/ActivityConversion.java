package org.camunda.community.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.convertible.AbstractActivityConvertible;
import org.camunda.community.converter.convertible.AbstractActivityConvertible.BpmnMultiInstanceLoopCharacteristics;
import org.camunda.community.converter.convertible.AbstractActivityConvertible.ZeebeLoopCharacteristics;

public class ActivityConversion extends AbstractTypedConversion<AbstractActivityConvertible> {

  @Override
  protected Class<AbstractActivityConvertible> type() {
    return AbstractActivityConvertible.class;
  }

  @Override
  public final void convertTyped(DomElement element, AbstractActivityConvertible convertible) {
    if (convertible.wasLoopCharacteristicsInitialized()) {
      createMultiInstance(element, convertible.getBpmnMultiInstanceLoopCharacteristics());
    }
  }

  private void createMultiInstance(
      DomElement element,
      BpmnMultiInstanceLoopCharacteristics bpmnMultiInstanceLoopCharacteristics) {
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
                              NamespaceUri.BPMN, "bpmn:multiInstanceLoopCharacteristics");
                  element.appendChild(mil);
                  return mil;
                });
    if (bpmnMultiInstanceLoopCharacteristics.isSequential()) {
      multiInstanceLoopCharacteristics.setAttribute("isSequential", Boolean.toString(true));
    }

    DomElement extensionElements = getExtensionElements(multiInstanceLoopCharacteristics);
    extensionElements.appendChild(
        createLoopCharacteristics(element.getDocument(), bpmnMultiInstanceLoopCharacteristics));
    if (bpmnMultiInstanceLoopCharacteristics.getCompletionCondition() != null) {
      getCompletionCondition(
          multiInstanceLoopCharacteristics, bpmnMultiInstanceLoopCharacteristics);
    }
  }

  private DomElement createLoopCharacteristics(
      DomDocument document,
      BpmnMultiInstanceLoopCharacteristics bpmnMultiInstanceLoopCharacteristics) {
    DomElement loopCharacteristics =
        document.createElement(NamespaceUri.ZEEBE, "loopCharacteristics");
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
      BpmnMultiInstanceLoopCharacteristics bpmnMultiInstanceLoopCharacteristics) {
    DomElement completionCondition =
        element.getChildElements().stream()
            .filter(e -> e.getLocalName().equals("completionCondition"))
            .findFirst()
            .orElseGet(
                () -> {
                  DomElement mil =
                      element
                          .getDocument()
                          .createElement(NamespaceUri.BPMN, "bpmn:completionCondition");
                  mil.setAttribute(NamespaceUri.XSI, "type", "bpmn:tFormalExpression");
                  element.appendChild(mil);
                  return mil;
                });
    completionCondition.setTextContent(
        bpmnMultiInstanceLoopCharacteristics.getCompletionCondition());
  }
}
