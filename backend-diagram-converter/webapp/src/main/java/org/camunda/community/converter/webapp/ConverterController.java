package org.camunda.community.converter.webapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.community.converter.BpmnDiagramCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ConverterController {
  private final BpmnConverterService bpmnConverter;
  private final boolean engineEnabled;

  @Autowired
  public ConverterController(
      BpmnConverterService bpmnConverter,
      @Value("${camunda.bpm.client.enabled}") boolean engineEnabled) {
    this.bpmnConverter = bpmnConverter;
    this.engineEnabled = engineEnabled;
  }

  @PostMapping("/check")
  public ResponseEntity<?> check(
      @RequestParam(value = "files", required = false) MultipartFile[] bpmnFiles) {
    if (bpmnFiles != null && !engineEnabled) {
      List<BpmnDiagramCheckResult> results =
          Arrays.stream(bpmnFiles)
              .map(
                  bpmnFile -> {
                    try (InputStream in = bpmnFile.getInputStream()) {
                      return bpmnConverter.check(
                          bpmnFile.getOriginalFilename(), Bpmn.readModelFromStream(in), false);
                    } catch (IOException e) {
                      BpmnDiagramCheckResult result = new BpmnDiagramCheckResult();
                      result.setFilename(bpmnFile.getOriginalFilename());
                      return result;
                    }
                  })
              .collect(Collectors.toList());
      return ResponseEntity.ok(results);
    }
    if (bpmnFiles == null && engineEnabled) {
      return ResponseEntity.ok(bpmnConverter.checkFromEngine(false));
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/convert")
  public ResponseEntity<?> getFile(
      @RequestParam(value = "files", required = false) MultipartFile[] bpmnFiles,
      @RequestParam("appendDocumentation") boolean appendDocumentation) {
    if (bpmnFiles != null && !engineEnabled) {
      Map<MultipartFile, Exception> exceptions = new HashMap<>();
      ByteArrayOutputStream bo = new ByteArrayOutputStream();
      try (ZipOutputStream out = new ZipOutputStream(bo)) {
        for (MultipartFile bpmnFile : bpmnFiles) {
          try (InputStream in = bpmnFile.getInputStream()) {
            BpmnModelInstance modelInstance = Bpmn.readModelFromStream(in);
            bpmnConverter.convert(modelInstance, appendDocumentation);
            ZipEntry entry = new ZipEntry("converted-c8-" + bpmnFile.getOriginalFilename());
            out.putNextEntry(entry);
            out.write(bpmnConverter.printXml(modelInstance.getDocument(), true).getBytes());
          } catch (IOException e) {
            exceptions.put(bpmnFile, e);
          }
        }
      } catch (IOException e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
      }
      if (exceptions.isEmpty()) {
        Resource file = new ByteArrayResource(bo.toByteArray());
        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"conversionResult.zip\"")
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
    if (bpmnFiles == null && engineEnabled) {
      ByteArrayOutputStream bo = new ByteArrayOutputStream();
      try (ZipOutputStream out = new ZipOutputStream(bo)) {
        for (Entry<String, BpmnModelInstance> bpmnFile :
            bpmnConverter.convertFromEngine(appendDocumentation).entrySet()) {
          ZipEntry entry = new ZipEntry("converted-c8-" + bpmnFile.getKey());
          out.putNextEntry(entry);
          out.write(bpmnConverter.printXml(bpmnFile.getValue().getDocument(), true).getBytes());
        }
      } catch (IOException e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
      }
      Resource file = new ByteArrayResource(bo.toByteArray());
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"conversionResult.zip\"")
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(file);
    }
    return ResponseEntity.badRequest().build();
  }
}
