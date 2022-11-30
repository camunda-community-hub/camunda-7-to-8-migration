package org.camunda.community.migration.converter.conversion;

import static org.camunda.community.migration.converter.BpmnElementFactory.*;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.convertible.AbstractActivityConvertible;
import org.camunda.community.migration.converter.convertible.AbstractActivityConvertible.BpmnMultiInstanceLoopCharacteristics;
import org.camunda.community.migration.converter.convertible.AbstractActivityConvertible.ZeebeLoopCharacteristics;

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
    DomElement multiInstanceLoopCharacteristics = getMultiInstanceLoopCharacteristics(element);
    if (bpmnMultiInstanceLoopCharacteristics.isSequential()) {
      multiInstanceLoopCharacteristics.setAttribute("isSequential", Boolean.toString(true));
    }
    DomElement extensionElements = getExtensionElements(multiInstanceLoopCharacteristics);
    extensionElements.appendChild(
        createLoopCharacteristics(element.getDocument(), bpmnMultiInstanceLoopCharacteristics));
    if (bpmnMultiInstanceLoopCharacteristics.getCompletionCondition() != null) {
      createCompletionCondition(
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

  private void createCompletionCondition(
      DomElement element,
      BpmnMultiInstanceLoopCharacteristics bpmnMultiInstanceLoopCharacteristics) {
    DomElement completionCondition = getCompletionCondition(element);
    completionCondition.setTextContent(
        bpmnMultiInstanceLoopCharacteristics.getCompletionCondition());
  }
}
