package org.camunda.community.migration.converter.webapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ConverterController {
  private static final Logger LOG = LoggerFactory.getLogger(ConverterController.class);
  private final BpmnConverterService bpmnConverter;
  private final CsvWriterService csvWriterService;

  @Autowired
  public ConverterController(
      BpmnConverterService bpmnConverter, CsvWriterService csvWriterService) {
    this.bpmnConverter = bpmnConverter;
    this.csvWriterService = csvWriterService;
  }

  @PostMapping(
      value = "/check",
      produces = {MediaType.APPLICATION_JSON_VALUE, "text/csv"},
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<?> check(
      @RequestParam("file") MultipartFile bpmnFile,
      @RequestParam(value = "adapterJobType", required = false) String adapterJobType,
      @RequestParam(value = "platformVersion", required = false) String platformVersion,
      @RequestHeader(HttpHeaders.ACCEPT) String[] contentType) {

    try (InputStream in = bpmnFile.getInputStream()) {
      BpmnDiagramCheckResult diagramCheckResult =
          bpmnConverter.check(
              bpmnFile.getOriginalFilename(),
              Bpmn.readModelFromStream(in),
              false,
              adapterJobType,
              platformVersion);
      if (contentType == null
          || contentType.length == 0
          || Arrays.asList(contentType).contains(MediaType.APPLICATION_JSON_VALUE)) {
        return ResponseEntity.ok(diagramCheckResult);
      }
      if (Arrays.asList(contentType).contains("text/csv")) {
        StringWriter sw = new StringWriter();
        csvWriterService.writeCsvFile(Collections.singletonList(diagramCheckResult), sw);
        Resource file = new ByteArrayResource(sw.toString().getBytes());
        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"conversionResult.csv\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(file);
      } else {
        return ResponseEntity.badRequest().build();
      }
    } catch (IOException e) {
      LOG.error("Error while reading input stream of BPMN file", e);
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping(
      value = "/convert",
      produces = {"application/bpmn+xml"},
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<?> getFile(
      @RequestParam("file") MultipartFile bpmnFile,
      @RequestParam("appendDocumentation") boolean appendDocumentation,
      @RequestParam(value = "adapterJobType", required = false) String adapterJobType,
      @RequestParam(value = "platformVersion", required = false) String platformVersion) {
    try (InputStream in = bpmnFile.getInputStream()) {
      BpmnModelInstance modelInstance = Bpmn.readModelFromStream(in);
      bpmnConverter.convert(modelInstance, appendDocumentation, adapterJobType, platformVersion);
      String bpmnXml = bpmnConverter.printXml(modelInstance.getDocument(), true);
      Resource file = new ByteArrayResource(bpmnXml.getBytes());
      return ResponseEntity.ok()
          .header(
              HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"converted-c8-" + bpmnFile.getOriginalFilename() + "\"")
          .header(HttpHeaders.CONTENT_TYPE, "application/bpmn+xml")
          .body(file);
    } catch (IOException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }
}
