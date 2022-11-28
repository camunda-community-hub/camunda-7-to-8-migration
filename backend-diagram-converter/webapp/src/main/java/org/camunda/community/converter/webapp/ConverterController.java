package org.camunda.community.converter.webapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.community.converter.BpmnDiagramCheckResult;
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
      @RequestParam("files") MultipartFile[] bpmnFiles,
      @RequestParam(value = "adapterJobType", required = false) String adapterJobType,
      @RequestParam(value = "platformVersion", required = false) String platformVersion,
      @RequestHeader(HttpHeaders.ACCEPT) String[] contentType) {
    List<BpmnDiagramCheckResult> results =
        Arrays.stream(bpmnFiles)
            .map(
                bpmnFile -> {
                  try (InputStream in = bpmnFile.getInputStream()) {
                    return bpmnConverter.check(
                        bpmnFile.getOriginalFilename(),
                        Bpmn.readModelFromStream(in),
                        false,
                        adapterJobType,
                        platformVersion);
                  } catch (IOException e) {
                    BpmnDiagramCheckResult result = new BpmnDiagramCheckResult();
                    result.setFilename(bpmnFile.getOriginalFilename());
                    return result;
                  }
                })
            .collect(Collectors.toList());
    if (contentType == null
        || contentType.length == 0
        || Arrays.asList(contentType).contains(MediaType.APPLICATION_JSON_VALUE)) {
      return ResponseEntity.ok(results);
    }
    if (Arrays.asList(contentType).contains("text/csv")) {
      StringWriter sw = new StringWriter();
      csvWriterService.writeCsvFile(results, sw);
      Resource file = new ByteArrayResource(sw.toString().getBytes());
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"conversionResult.csv\"")
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(file);
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping(
      value = "/convert",
      produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE},
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<?> getFile(
      @RequestParam("files") MultipartFile[] bpmnFiles,
      @RequestParam("appendDocumentation") boolean appendDocumentation,
      @RequestParam(value = "adapterJobType", required = false) String adapterJobType,
      @RequestParam(value = "platformVersion", required = false) String platformVersion) {
    Map<MultipartFile, Exception> exceptions = new HashMap<>();
    ByteArrayOutputStream bo = new ByteArrayOutputStream();
    try (ZipOutputStream out = new ZipOutputStream(bo)) {
      for (MultipartFile bpmnFile : bpmnFiles) {
        try (InputStream in = bpmnFile.getInputStream()) {
          BpmnModelInstance modelInstance = Bpmn.readModelFromStream(in);
          bpmnConverter.convert(
              modelInstance, appendDocumentation, adapterJobType, platformVersion);
          ZipEntry entry = new ZipEntry("converted-c8-" + bpmnFile.getOriginalFilename());
          out.putNextEntry(entry);
          out.write(bpmnConverter.printXml(modelInstance.getDocument(), true).getBytes());
        } catch (Exception e) {
          exceptions.put(bpmnFile, e);
        }
      }
    } catch (IOException e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
    if (exceptions.isEmpty()) {
      Resource file = new ByteArrayResource(bo.toByteArray());
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"conversionResult.zip\"")
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(file);
    } else {
      return ResponseEntity.badRequest()
          .body(
              exceptions.entrySet().stream()
                  .map(e -> new SimpleEntry<>(e.getKey().getOriginalFilename(), e.getValue()))
                  .collect(Collectors.toList()));
    }
  }
}
