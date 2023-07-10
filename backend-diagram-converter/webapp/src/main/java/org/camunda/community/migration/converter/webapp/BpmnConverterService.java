package org.camunda.community.migration.converter.webapp;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
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
      String defaultJobType,
      String platformVersion,
      Boolean defaultJobTypeEnabled) {
    DefaultConverterProperties adaptedProperties = new DefaultConverterProperties();
    adaptedProperties.setDefaultJobType(defaultJobType);
    adaptedProperties.setPlatformVersion(platformVersion);
    adaptedProperties.setDefaultJobTypeEnabled(defaultJobTypeEnabled);
    adaptedProperties.setAppendDocumentation(appendDocumentation);
    bpmnConverter.convert(
        modelInstance, ConverterPropertiesFactory.getInstance().merge(adaptedProperties));
  }

  public BpmnDiagramCheckResult check(
      String filename,
      BpmnModelInstance modelInstance,
      String defaultJobType,
      String platformVersion,
      Boolean defaultJobTypeEnabled) {
    DefaultConverterProperties adaptedProperties = new DefaultConverterProperties();
    adaptedProperties.setDefaultJobType(defaultJobType);
    adaptedProperties.setPlatformVersion(platformVersion);
    adaptedProperties.setDefaultJobTypeEnabled(defaultJobTypeEnabled);
    return bpmnConverter.check(
        filename, modelInstance, ConverterPropertiesFactory.getInstance().merge(adaptedProperties));
  }

  public String printXml(DomDocument document, boolean prettyPrint) {
    try (StringWriter sw = new StringWriter()) {
      bpmnConverter.printXml(document, prettyPrint, sw);
      return sw.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void writeCsvFile(List<BpmnDiagramCheckResult> results, Writer writer) {
    bpmnConverter.writeCsvFile(results, writer);
  }
}
