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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DiagramCheckResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ConverterControllerTest {
  private static final Logger LOG = LoggerFactory.getLogger(ConverterControllerTest.class);
  @LocalServerPort int port;

  @BeforeEach
  void setup() {
    RestAssured.port = port;
  }

  @Test
  void singleBpmnCheckWithJsonResult() throws URISyntaxException {
    List<DiagramCheckResult> checkResult =
        RestAssured.given()
            .contentType(ContentType.MULTIPART)
            .multiPart(
                "file", new File(getClass().getClassLoader().getResource("example.bpmn").toURI()))
            .accept(ContentType.JSON)
            .post("/check")
            .getBody()
            .as(new TypeRef<List<DiagramCheckResult>>() {});

    assertThat(checkResult)
        .hasSize(1)
        .first()
        .matches(result -> result.getFilename().equals("example.bpmn"), "Filename is set correctly")
        .matches(result -> result.getResults().size() > 0, "Found results");
  }

  @Test
  void multipleFilesCheck() throws URISyntaxException {
    List<DiagramCheckResult> checkResult =
        RestAssured.given()
            .contentType(ContentType.MULTIPART)
            .multiPart(
                "file", new File(getClass().getClassLoader().getResource("example.bpmn").toURI()))
            .multiPart(
                "file", new File(getClass().getClassLoader().getResource("example2.bpmn").toURI()))
            .accept(ContentType.JSON)
            .post("/check")
            .getBody()
            .as(new TypeRef<List<DiagramCheckResult>>() {});

    assertThat(checkResult)
        .anySatisfy(
            singleCheckResult -> {
              assertThat(singleCheckResult.getFilename()).isEqualTo("example.bpmn");
              assertThat(singleCheckResult.getResults())
                  .isNotEmpty()
                  .anySatisfy(
                      result -> assertThat(result.getElementId()).isEqualTo("Activity_Example1"));
            })
        .anySatisfy(
            singleCheckResult -> {
              assertThat(singleCheckResult.getFilename()).isEqualTo("example2.bpmn");
              assertThat(singleCheckResult.getResults())
                  .isNotEmpty()
                  .anySatisfy(
                      result -> assertThat(result.getElementId()).isEqualTo("Activity_Example2"));
            });
  }

  @Test
  void singleBpmnCheckWithCsvResult() throws URISyntaxException, IOException {
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
  void singleBpmnCheckWithExcelResult() throws Exception {
    byte[] response =
        RestAssured.given()
            .contentType(ContentType.MULTIPART)
            .multiPart(
                "file", new File(getClass().getClassLoader().getResource("example.bpmn").toURI()))
            .accept("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            //	      .accept("application/vnd.ms-excel")
            //	      .accept("application/excel")
            .post("/check")
            .getBody()
            .asByteArray();

    // Validate Excel using Apache POI
    try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(response))) {
      assertThat(workbook.getNumberOfSheets()).isGreaterThan(0);
    }
  }

  @Test
  void multipleBpmnCheckWithExcelResult() throws Exception {
    byte[] response =
        RestAssured.given()
            .contentType(ContentType.MULTIPART)
            .multiPart(
                "file", new File(getClass().getClassLoader().getResource("example.bpmn").toURI()))
            .multiPart(
                "file", new File(getClass().getClassLoader().getResource("example2.bpmn").toURI()))
            .accept("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            //	      .accept("application/vnd.ms-excel")
            //	      .accept("application/excel")
            .post("/check")
            .getBody()
            .asByteArray();

    // Validate Excel using Apache POI
    try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(response))) {
      XSSFSheet sheet = workbook.getSheet("AnalysisResults");
      assertThat(sheet).as("Sheet 'AnalysisResults' should exist").isNotNull();

      // Collect values from rows 1 and 2, column 0
      String filename1 = sheet.getRow(1).getCell(0).getStringCellValue();
      String filename2 = sheet.getRow(2).getCell(0).getStringCellValue();

      // Assert both expected filenames are present, order-independent
      assertThat(List.of(filename1, filename2))
          .containsExactlyInAnyOrder("example.bpmn", "example2.bpmn");
    }
  }

  @Test
  void convertBpmn() throws URISyntaxException {
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
    LOG.info("{}", new String(bpmn));
    BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromStream(in);
    DomElement process = bpmnModelInstance.getDocument().getElementById("Process_11j5dku");
    assertThat(process).isNotNull();
  }

  @Test
  void convertDmn() throws URISyntaxException {
    byte[] bpmn =
        RestAssured.given()
            .contentType(ContentType.MULTIPART)
            .multiPart(
                "file", new File(getClass().getClassLoader().getResource("first.dmn").toURI()))
            .formParam("appendDocumentation", true)
            .accept("application/dmn+xml")
            .post("/convert")
            .getBody()
            .asByteArray();
    ByteArrayInputStream in = new ByteArrayInputStream(bpmn);
    LOG.info("{}", new String(bpmn));
    DmnModelInstance bpmnModelInstance = Dmn.readModelFromStream(in);
    DomElement decision = bpmnModelInstance.getDocument().getElementById("Decision_0kjih6z");
    assertThat(decision).isNotNull();
  }

  @Test
  void convertBpmnBatch() throws URISyntaxException, IOException {
    byte[] zip =
        RestAssured.given()
            .contentType(ContentType.MULTIPART)
            .multiPart(
                "file", new File(getClass().getClassLoader().getResource("example.bpmn").toURI()))
            .multiPart(
                "file", new File(getClass().getClassLoader().getResource("example2.bpmn").toURI()))
            .formParam("appendDocumentation", true)
            .accept("application/zip")
            .post("/convertBatch")
            .getBody()
            .asByteArray();

    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zip))) {
      int entryCount = 0;
      ZipEntry zipEntry;
      while ((zipEntry = zis.getNextEntry()) != null) {
        entryCount++;

        if (entryCount == 1) {
          assertThat(zipEntry.getName()).isEqualTo("converted-c8-example.bpmn");

          ByteArrayInputStream in = new ByteArrayInputStream(zis.readAllBytes());
          BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromStream(in);

          DomElement process = bpmnModelInstance.getDocument().getElementById("Process_11j5dku");
          assertThat(process).isNotNull();
        } else if (entryCount == 2) {
          assertThat(zipEntry.getName()).isEqualTo("converted-c8-example2.bpmn");

          ByteArrayInputStream in = new ByteArrayInputStream(zis.readAllBytes());
          BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromStream(in);

          DomElement process = bpmnModelInstance.getDocument().getElementById("Process_Example2");
          assertThat(process).isNotNull();
        }

        zis.closeEntry();
      }

      // Final assertions
      assertThat(entryCount).as("There should be exactly 2 entries in the zip").isEqualTo(2);
    }
  }
}
