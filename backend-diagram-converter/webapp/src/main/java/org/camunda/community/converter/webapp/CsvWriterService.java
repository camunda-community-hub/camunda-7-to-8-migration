package org.camunda.community.converter.webapp;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import org.camunda.community.converter.BpmnDiagramCheckResult;
import org.springframework.stereotype.Service;

@Service
public class CsvWriterService {
  public void writeCsvFile(List<BpmnDiagramCheckResult> results, Writer writer) {
    try (ICSVWriter csvWriter = new CSVWriterBuilder(writer).withSeparator(';').build()) {
      csvWriter.writeNext(createHeaders());
      csvWriter.writeAll(createLines(results));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String[] createHeaders() {
    return new String[] {
      "filename", "elementName", "elementId", "elementType", "severity", "message", "link"
    };
  }

  private List<String[]> createLines(List<BpmnDiagramCheckResult> results) {
    return results.stream()
        .flatMap(
            diagramCheckResult ->
                diagramCheckResult.getResults().stream()
                    .flatMap(
                        elementCheckResult ->
                            elementCheckResult.getMessages().stream()
                                .map(
                                    message ->
                                        new String[] {
                                          diagramCheckResult.getFilename(),
                                          elementCheckResult.getElementName(),
                                          elementCheckResult.getElementId(),
                                          elementCheckResult.getElementType(),
                                          message.getSeverity().name(),
                                          message.getMessage(),
                                          message.getLink()
                                        })))
        .collect(Collectors.toList());
  }
}
