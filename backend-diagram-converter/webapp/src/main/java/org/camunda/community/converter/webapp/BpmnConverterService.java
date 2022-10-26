package org.camunda.community.converter.webapp;

import java.io.IOException;
import java.io.StringWriter;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.community.converter.BpmnConverter;
import org.camunda.community.converter.BpmnDiagramCheckResult;
import org.camunda.community.converter.exception.VisitorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BpmnConverterService {
  private final BpmnConverter bpmnConverter;
  private final NotificationService notificationService;

  @Autowired
  public BpmnConverterService(
      BpmnConverter bpmnConverter, NotificationService notificationService) {
    this.bpmnConverter = bpmnConverter;
    this.notificationService = notificationService;
  }

  public void convert(BpmnModelInstance modelInstance, boolean appendDocumentation) {
    check(modelInstance, appendDocumentation);
  }

  public BpmnDiagramCheckResult check(
      BpmnModelInstance modelInstance, boolean appendDocumentation) {
    try {
      return bpmnConverter.check(modelInstance, appendDocumentation);
    } catch (VisitorException e) {
      notificationService.notify(e);
      throw e;
    }
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
