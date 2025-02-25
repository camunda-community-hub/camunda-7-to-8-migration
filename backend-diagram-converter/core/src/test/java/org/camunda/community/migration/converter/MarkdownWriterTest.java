package org.camunda.community.migration.converter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.camunda.community.migration.converter.DiagramCheckResult.ElementCheckMessage;
import org.camunda.community.migration.converter.DiagramCheckResult.ElementCheckResult;
import org.camunda.community.migration.converter.DiagramCheckResult.Severity;
import org.junit.jupiter.api.Test;

public class MarkdownWriterTest {
  private static final String FILENAME = "mock-process.bpmn";
  private static final String LINK = "https://www.example.com";
  private static final String MESSAGE = "Test message";
  private static final String ELEMENT_ID = "abc.123";
  private static final String ELEMENT_NAME = "Example;Name";
  private static final String ELEMENT_TYPE = "userTask";
  private static final Severity SEVERITY = Severity.TASK;
  private static final DiagramConverter SERVICE = DiagramConverterFactory.getInstance().get();

  @Test
  public void shouldCreateValidCsv() {
    StringWriter writer = new StringWriter();
    List<DiagramCheckResult> results = mockResults();
    SERVICE.writeMarkdownFile(results, writer);
    System.out.println(writer.toString());
  }

  private List<DiagramCheckResult> mockResults() {
    List<DiagramCheckResult> results = new ArrayList<>();
    results.add(mockDiagramResult());
    return results;
  }

  private DiagramCheckResult mockDiagramResult() {
    DiagramCheckResult result = new DiagramCheckResult();
    result.setFilename(FILENAME);
    result.getResults().add(mockElementResult());
    return result;
  }

  private ElementCheckResult mockElementResult() {
    ElementCheckResult result = new ElementCheckResult();
    result.setElementId(ELEMENT_ID);
    result.setElementName(ELEMENT_NAME);
    result.setElementType(ELEMENT_TYPE);
    result.getMessages().add(mockMessage(SEVERITY, MESSAGE, LINK));
    result.getMessages().add(mockMessage(SEVERITY, MESSAGE, null));
    return result;
  }

  private ElementCheckMessage mockMessage() {
    ElementCheckMessage message = new ElementCheckMessage();
    message.setSeverity(SEVERITY);
    message.setMessage(MESSAGE);
    message.setLink(LINK);
    return message;
  }

  private ElementCheckMessage mockMessage(Severity severity, String message, String link) {
    ElementCheckMessage msg = new ElementCheckMessage();
    msg.setSeverity(severity);
    msg.setMessage(message);
    msg.setLink(link);
    return msg;
  }
}
