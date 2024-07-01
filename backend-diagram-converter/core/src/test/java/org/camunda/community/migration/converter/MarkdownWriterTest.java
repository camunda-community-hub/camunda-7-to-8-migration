package org.camunda.community.migration.converter;

import static org.assertj.core.api.Assertions.*;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.BpmnElementCheckMessage;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.BpmnElementCheckResult;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.Severity;
import org.junit.jupiter.api.Test;

public class MarkdownWriterTest {
  private static final String FILENAME = "mock-process.bpmn";
  private static final String LINK = "https://www.example.com";
  private static final String MESSAGE = "Test message";
  private static final String ELEMENT_ID = "abc.123";
  private static final String ELEMENT_NAME = "Example;Name";
  private static final String ELEMENT_TYPE = "userTask";
  private static final Severity SEVERITY = Severity.TASK;
  private static final BpmnConverter SERVICE = BpmnConverterFactory.getInstance().get();

  @Test
  public void shouldCreateValidCsv() {
    StringWriter writer = new StringWriter();
    List<BpmnDiagramCheckResult> results = mockResults();
    SERVICE.writeMarkdownFile(results, writer);
    System.out.println(writer.toString());
  }

  private List<BpmnDiagramCheckResult> mockResults() {
    List<BpmnDiagramCheckResult> results = new ArrayList<>();
    results.add(mockDiagramResult());
    return results;
  }

  private BpmnDiagramCheckResult mockDiagramResult() {
    BpmnDiagramCheckResult result = new BpmnDiagramCheckResult();
    result.setFilename(FILENAME);
    result.getResults().add(mockElementResult());
    return result;
  }

  private BpmnElementCheckResult mockElementResult() {
    BpmnElementCheckResult result = new BpmnElementCheckResult();
    result.setElementId(ELEMENT_ID);
    result.setElementName(ELEMENT_NAME);
    result.setElementType(ELEMENT_TYPE);
    result.getMessages().add(mockMessage(SEVERITY, MESSAGE, LINK));
    result.getMessages().add(mockMessage(SEVERITY, MESSAGE, null));
    return result;
  }

  private BpmnElementCheckMessage mockMessage() {
    BpmnElementCheckMessage message = new BpmnElementCheckMessage();
    message.setSeverity(SEVERITY);
    message.setMessage(MESSAGE);
    message.setLink(LINK);
    return message;
  }

  private BpmnElementCheckMessage mockMessage(Severity severity, String message, String link) {
    BpmnElementCheckMessage msg = new BpmnElementCheckMessage();
    msg.setSeverity(severity);
    msg.setMessage(message);
    msg.setLink(link);
    return msg;
  }
}
