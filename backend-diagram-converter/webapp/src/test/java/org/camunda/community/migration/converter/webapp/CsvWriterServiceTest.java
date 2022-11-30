package org.camunda.community.migration.converter.webapp;

import static org.assertj.core.api.Assertions.*;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.BpmnElementCheckMessage;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.BpmnElementCheckResult;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.Severity;
import org.junit.jupiter.api.Test;

public class CsvWriterServiceTest {
  private static final String FILENAME = "mock-process.bpmn";
  private static final String LINK = "https://www.example.com";
  private static final String MESSAGE = "Test message";
  private static final String ELEMENT_ID = "abc.123";
  private static final String ELEMENT_NAME = "Example;Name";
  private static final String ELEMENT_TYPE = "userTask";
  private static final Severity SEVERITY = Severity.TASK;
  private static final CsvWriterService SERVICE = new CsvWriterService();

  @Test
  public void shouldCreateValidCsv() {
    StringWriter writer = new StringWriter();
    List<BpmnDiagramCheckResult> results = mockResults();
    SERVICE.writeCsvFile(results, writer);

    StringReader reader = new StringReader(writer.toString());
    try (CSVReader csvReader =
        new CSVReaderBuilder(reader)
            .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
            .build()) {
      List<String[]> lines = csvReader.readAll();
      assertThat(lines).hasSize(2);
      assertThat(lines.get(0))
          .containsExactly(
              "filename", "elementName", "elementId", "elementType", "severity", "message", "link");
      assertThat(lines.get(1))
          .containsExactly(
              FILENAME, ELEMENT_NAME, ELEMENT_ID, ELEMENT_TYPE, SEVERITY.name(), MESSAGE, LINK);
    } catch (IOException | CsvException e) {
      throw new RuntimeException(e);
    }
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
    result.getMessages().add(mockMessage());
    return result;
  }

  private BpmnElementCheckMessage mockMessage() {
    BpmnElementCheckMessage message = new BpmnElementCheckMessage();
    message.setSeverity(SEVERITY);
    message.setMessage(MESSAGE);
    message.setLink(LINK);
    return message;
  }
}
