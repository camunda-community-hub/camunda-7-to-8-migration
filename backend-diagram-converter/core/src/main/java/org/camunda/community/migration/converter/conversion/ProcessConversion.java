package org.camunda.community.migration.converter.conversion;

import static org.camunda.community.migration.converter.BpmnElementFactory.*;
import static org.camunda.community.migration.converter.NamespaceUri.*;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.convertible.ProcessConvertible;

public class ProcessConversion extends AbstractTypedConversion<ProcessConvertible> {
  @Override
  protected Class<ProcessConvertible> type() {
    return ProcessConvertible.class;
  }

  @Override
  protected void convertTyped(DomElement element, ProcessConvertible convertible) {
    DomElement extensionElements = getExtensionElements(element);
    if (StringUtils.isNotBlank(convertible.getZeebeVersionTag())) {
      appendZeebeVersionTag(extensionElements, convertible.getZeebeVersionTag());
    }
  }

  private void appendZeebeVersionTag(DomElement extensionElements, String zeebeVersionTag) {
    DomElement versionTag = extensionElements.getDocument().createElement(ZEEBE, "versionTag");
    versionTag.setAttribute(ZEEBE, "value", zeebeVersionTag);
    extensionElements.appendChild(versionTag);
  }
}
