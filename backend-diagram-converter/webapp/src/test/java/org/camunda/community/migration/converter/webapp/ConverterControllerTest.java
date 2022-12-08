package org.camunda.community.migration.converter.webapp;

import static org.assertj.core.api.Assertions.*;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.List;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ConverterControllerTest {
  @LocalServerPort int port;

  @BeforeEach
  void setup() {
    RestAssured.port = port;
  }

  @Test
  void shouldReturnCheckResult() throws URISyntaxException {
    BpmnDiagramCheckResult checkResult =
        RestAssured.given()
            .contentType(ContentType.MULTIPART)
            .multiPart(
                "file", new File(getClass().getClassLoader().getResource("example.bpmn").toURI()))
            .accept(ContentType.JSON)
            .post("/check")
            .getBody()
            .as(BpmnDiagramCheckResult.class);
    assertThat(checkResult)
        .matches(
            result -> result.getFilename().equals("example.bpmn"), "Filename is set correctly");
    assertThat(checkResult.getResults()).isNotEmpty();
  }

  @Test
  void shouldReturnCsv() throws URISyntaxException, IOException {
    String body =
        RestAssured.given()
            .contentType(ContentType.MULTIPART)
            .multiPart(
                "file", new File(getClass().getClassLoader().getResource("example.bpmn").toURI()))
            .accept("text/csv")
            .post("/check")
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
  void shouldReturnBpmn() throws URISyntaxException {
    byte[] bpmn =
        RestAssured.given()
            .contentType(ContentType.MULTIPART)
            .multiPart(
                "file", new File(getClass().getClassLoader().getResource("example.bpmn").toURI()))
            .formParam("appendDocumentation", true)
            .accept("application/bpmn+xml")
            .post("/convert")
            .getBody()
            .asByteArray();
    ByteArrayInputStream in = new ByteArrayInputStream(bpmn);
    BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromStream(in);
    DomElement process = bpmnModelInstance.getDocument().getElementById("Process_11j5dku");
    assertThat(process).isNotNull();
  }
}
