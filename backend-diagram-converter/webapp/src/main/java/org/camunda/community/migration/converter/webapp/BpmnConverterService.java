package org.camunda.community.migration.converter.webapp;

import java.io.IOException;
import java.io.StringWriter;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.community.migration.converter.BpmnConverter;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult;
import org.camunda.community.migration.converter.ConverterPropertiesFactory;
import org.camunda.community.migration.converter.DefaultConverterProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BpmnConverterService {
  private final BpmnConverter bpmnConverter;

  @Autowired
  public BpmnConverterService(BpmnConverter bpmnConverter) {
    this.bpmnConverter = bpmnConverter;
  }

  public void convert(
      BpmnModelInstance modelInstance,
      boolean appendDocumentation,
      String adapterJobType,
      String platformVersion) {
    DefaultConverterProperties adaptedProperties = new DefaultConverterProperties();
    adaptedProperties.setAdapterJobType(adapterJobType);
    bpmnConverter.convert(
        modelInstance,
        appendDocumentation,
        ConverterPropertiesFactory.getInstance().merge(adaptedProperties));
  }

  public BpmnDiagramCheckResult check(
      String filename,
      BpmnModelInstance modelInstance,
      boolean appendDocumentation,
      String adapterJobType,
      String platformVersion) {
    DefaultConverterProperties adaptedProperties = new DefaultConverterProperties();
    adaptedProperties.setAdapterJobType(adapterJobType);
    return bpmnConverter.check(
        filename,
        modelInstance,
        appendDocumentation,
        ConverterPropertiesFactory.getInstance().merge(adaptedProperties));
  }

  public String printXml(DomDocument document, boolean prettyPrint) {
    try (StringWriter sw = new StringWriter()) {
      bpmnConverter.printXml(document, prettyPrint, sw);
      return sw.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
