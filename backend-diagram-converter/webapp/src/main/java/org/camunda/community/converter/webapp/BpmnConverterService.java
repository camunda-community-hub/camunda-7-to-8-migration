package org.camunda.community.converter.webapp;

import java.io.IOException;
import java.io.StringWriter;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.community.converter.BpmnConverter;
import org.camunda.community.converter.BpmnDiagramCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BpmnConverterService {
  private final BpmnConverter bpmnConverter;

  @Autowired
  public BpmnConverterService(BpmnConverter bpmnConverter) {
    this.bpmnConverter = bpmnConverter;
  }

  public void convert(BpmnModelInstance modelInstance, boolean appendDocumentation) {
    bpmnConverter.convert(modelInstance, appendDocumentation);
  }

  public BpmnDiagramCheckResult check(
      String filename, BpmnModelInstance modelInstance, boolean appendDocumentation) {
    return bpmnConverter.check(filename, modelInstance, appendDocumentation);
  }

  public String printXml(DomDocument document, boolean b) {
    try (StringWriter sw = new StringWriter()) {
      bpmnConverter.printXml(document, b, sw);
      return sw.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
