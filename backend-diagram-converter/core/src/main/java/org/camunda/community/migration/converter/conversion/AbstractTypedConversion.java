package org.camunda.community.migration.converter.conversion;

import static org.camunda.community.migration.converter.BpmnElementFactory.*;

import java.util.List;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DiagramCheckResult.ElementCheckMessage;
import org.camunda.community.migration.converter.convertible.Convertible;

public abstract class AbstractTypedConversion<T extends Convertible> implements Conversion {

  @Override
  public final void convert(
      DomElement element, Convertible convertible, List<ElementCheckMessage> messages) {
    if (type().isAssignableFrom(convertible.getClass())) {
      convertTyped(element, type().cast(convertible));
    }
    removeIfEmpty(getExtensionElements(element));
    removeIfEmpty(getDocumentation(element));
  }

  private void removeIfEmpty(DomElement extensionElements) {
    if (extensionElements.getChildElements().isEmpty()
        && (extensionElements.getTextContent() == null
            || extensionElements.getTextContent().trim().equals(""))) {
      extensionElements.getParentElement().removeChild(extensionElements);
    }
  }

  protected abstract Class<T> type();

  protected abstract void convertTyped(DomElement element, T convertible);
}
