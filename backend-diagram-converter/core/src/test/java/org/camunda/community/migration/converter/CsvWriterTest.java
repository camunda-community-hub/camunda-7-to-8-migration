package org.camunda.community.migration.converter;

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
import org.camunda.community.migration.converter.DiagramCheckResult.ElementCheckMessage;
import org.camunda.community.migration.converter.DiagramCheckResult.ElementCheckResult;
import org.camunda.community.migration.converter.DiagramCheckResult.Severity;
import org.junit.jupiter.api.Test;

public class CsvWriterTest {
  private static final String FILENAME = "mock-process.bpmn";
  private static final String LINK = "https://www.example.com";
  private static final String MESSAGE_ID = "test-message";
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
              "filename",
              "elementName",
              "elementId",
              "elementType",
              "severity",
              "messageId",
              "message",
              "link");
      assertThat(lines.get(1))
          .containsExactly(
              FILENAME,
              ELEMENT_NAME,
              ELEMENT_ID,
              ELEMENT_TYPE,
              SEVERITY.name(),
              MESSAGE_ID,
              MESSAGE,
              LINK);
    } catch (IOException | CsvException e) {
      throw new RuntimeException(e);
    }
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
    result.getMessages().add(mockMessage());
    return result;
  }

  private ElementCheckMessage mockMessage() {
    ElementCheckMessage message = new ElementCheckMessage();
    message.setSeverity(SEVERITY);
    message.setMessage(MESSAGE);
    message.setLink(LINK);
    message.setId(MESSAGE_ID);
    return message;
  }
}
