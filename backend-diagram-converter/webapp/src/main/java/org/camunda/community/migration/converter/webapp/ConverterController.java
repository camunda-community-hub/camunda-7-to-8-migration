package org.camunda.community.migration.converter.webapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.community.migration.converter.DiagramCheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ConverterController {
  private static final Logger LOG = LoggerFactory.getLogger(ConverterController.class);
  private final BpmnConverterService bpmnConverter;
  private final BuildProperties buildProperties;

  @Autowired
  public ConverterController(BpmnConverterService bpmnConverter, BuildProperties buildProperties) {
    this.bpmnConverter = bpmnConverter;
    this.buildProperties = buildProperties;
  }

  @PostMapping(
      value = "/check",
      produces = {MediaType.APPLICATION_JSON_VALUE, "text/csv"},
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<?> check(
      @RequestParam("file") MultipartFile bpmnFile,
      @RequestParam(value = "defaultJobType", required = false) String defaultJobType,
      @RequestParam(value = "platformVersion", required = false) String platformVersion,
      @RequestParam(value = "defaultJobTypeEnabled", required = false, defaultValue = "true")
          Boolean defaultJobTypeEnabled,
      @RequestHeader(HttpHeaders.ACCEPT) String[] contentType) {
    try (InputStream in = bpmnFile.getInputStream()) {
      DiagramCheckResult diagramCheckResult =
          bpmnConverter.check(
              bpmnFile.getOriginalFilename(),
              Bpmn.readModelFromStream(in),
              defaultJobType,
              platformVersion,
              defaultJobTypeEnabled);
      if (contentType == null
          || contentType.length == 0
          || Arrays.asList(contentType).contains(MediaType.APPLICATION_JSON_VALUE)) {
        return ResponseEntity.ok(diagramCheckResult);
      }
      if (Arrays.asList(contentType).contains("text/csv")) {
        StringWriter sw = new StringWriter();
        bpmnConverter.writeCsvFile(Collections.singletonList(diagramCheckResult), sw);
        Resource file = new ByteArrayResource(sw.toString().getBytes());
        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"conversionResult.csv\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(file);
      } else {
        String errorMessage = "Invalid content type '" + String.join("', '", contentType) + "'";
        LOG.error("{}", errorMessage);
        return ResponseEntity.badRequest().body(errorMessage);
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
      @RequestParam(value = "appendDocumentation", required = false, defaultValue = "false")
          Boolean appendDocumentation,
      @RequestParam(value = "defaultJobType", required = false) String defaultJobType,
      @RequestParam(value = "platformVersion", required = false) String platformVersion,
      @RequestParam(value = "defaultJobTypeEnabled", required = false, defaultValue = "true")
          Boolean defaultJobTypeEnabled) {
    try (InputStream in = bpmnFile.getInputStream()) {
      BpmnModelInstance modelInstance = Bpmn.readModelFromStream(in);
      bpmnConverter.convert(
          modelInstance,
          appendDocumentation,
          defaultJobType,
          platformVersion,
          defaultJobTypeEnabled);
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

  @GetMapping(value = "/version", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> getVersion() {
    String implementationVersion = buildProperties.getVersion();
    LOG.debug("Version: {}", implementationVersion);
    return ResponseEntity.ok().body(implementationVersion);
  }
}
