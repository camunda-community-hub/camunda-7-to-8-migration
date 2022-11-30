package org.camunda.community.migration.converter.webapp;

import static org.assertj.core.api.Assertions.*;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.List;
import java.util.zip.ZipInputStream;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ConverterControllerTest {

  @Test
  void shouldReturnCheckResult() throws URISyntaxException {
    List<BpmnDiagramCheckResult> checkResult =
        RestAssured.given()
            .contentType(ContentType.MULTIPART)
            .multiPart(
                "files", new File(getClass().getClassLoader().getResource("example.bpmn").toURI()))
            .accept(ContentType.JSON)
            .post("http://localhost:8080/check")
            .getBody()
            .as(new TypeRef<List<BpmnDiagramCheckResult>>() {});
    assertThat(checkResult).hasSize(1);
    assertThat(checkResult.get(0))
        .matches(
            result -> result.getFilename().equals("example.bpmn"), "Filename is set correctly");
    assertThat(checkResult.get(0).getResults()).isNotEmpty();
  }

  @Test
  void shouldReturnCsv() throws URISyntaxException, IOException {
    String body =
        RestAssured.given()
            .contentType(ContentType.MULTIPART)
            .multiPart(
                "files", new File(getClass().getClassLoader().getResource("example.bpmn").toURI()))
            .accept("text/csv")
            .post("http://localhost:8080/check")
            .getBody()
            .print();
    try (CSVReader reader =
        new CSVReaderBuilder(new StringReader(body))
            .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
            .build()) {
      List<String[]> all = reader.readAll();
      assertThat(all).hasSize(2);
    } catch (CsvException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void shouldReturnZip() throws IOException, URISyntaxException {
    byte[] zip =
        RestAssured.given()
            .contentType(ContentType.MULTIPART)
            .multiPart(
                "files", new File(getClass().getClassLoader().getResource("example.bpmn").toURI()))
            .accept("text/csv")
            .post("http://localhost:8080/convert")
            .getBody()
            .asByteArray();
    try (ZipInputStream inputStream = new ZipInputStream(new ByteArrayInputStream(zip))) {
      while (inputStream.getNextEntry() != null) {
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromStream(inputStream);
        DomElement process = bpmnModelInstance.getDocument().getElementById("Process_11j5dku");
        assertThat(process).isNotNull();
      }
    }
  }
}
