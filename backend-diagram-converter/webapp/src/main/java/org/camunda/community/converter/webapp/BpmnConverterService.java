package org.camunda.community.converter.webapp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.community.converter.BpmnConverter;
import org.camunda.community.converter.BpmnDiagramCheckResult;
import org.camunda.community.converter.ConverterPropertiesFactory;
import org.camunda.community.converter.DefaultConverterProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BpmnConverterService {
  private final BpmnConverter bpmnConverter;
  private final ProcessEngineService processEngineService;

  @Autowired
  public BpmnConverterService(
      BpmnConverter bpmnConverter, ProcessEngineService processEngineService) {
    this.bpmnConverter = bpmnConverter;
    this.processEngineService = processEngineService;
  }

  public void convert(
      BpmnModelInstance modelInstance, boolean appendDocumentation, String adapterJobType) {
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
      String adapterJobType) {
    DefaultConverterProperties adaptedProperties = new DefaultConverterProperties();
    adaptedProperties.setAdapterJobType(adapterJobType);
    return bpmnConverter.check(
        filename,
        modelInstance,
        appendDocumentation,
        ConverterPropertiesFactory.getInstance().merge(adaptedProperties));
  }

  public Map<String, BpmnModelInstance> convertFromEngine(boolean appendDocumentation) {
    return processEngineService.getAllLatestBpmnXml().entrySet().stream()
        .collect(
            Collectors.toMap(
                Entry::getKey,
                e -> {
                  BpmnModelInstance modelInstance =
                      Bpmn.readModelFromStream(streamForString(e.getValue()));
                  convert(modelInstance, appendDocumentation);
                  return modelInstance;
                }));
  }

  public List<BpmnDiagramCheckResult> checkFromEngine(boolean appendDocumentation) {
    return processEngineService.getAllLatestBpmnXml().entrySet().stream()
        .map(
            e ->
                check(
                    e.getKey(),
                    Bpmn.readModelFromStream(streamForString(e.getValue())),
                    appendDocumentation))
        .collect(Collectors.toList());
  }

  private ByteArrayInputStream streamForString(String bpmnXml) {
    return new ByteArrayInputStream(bpmnXml.getBytes());
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
