package org.camunda.community.converter;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
public class ConverterController {
  private final BpmnConverter bpmnConverter;

  @Autowired
  public ConverterController(BpmnConverter bpmnConverter) {
    this.bpmnConverter = bpmnConverter;
  }

  @PostMapping("/check")
  public ResponseEntity<?> check(@RequestParam("file") MultipartFile bpmnFile) {
    try (InputStream in = bpmnFile.getInputStream()) {
      return ResponseEntity.ok(bpmnConverter.check(Bpmn.readModelFromStream(in)));
    } catch (IOException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/convert")
  public ResponseEntity<Resource> getFile(@RequestBody BpmnDiagramCheckResult result) {
    String bpmnXml = bpmnConverter.printXml(bpmnConverter.convert(result).getDocument(),true);
    Resource file = new ByteArrayResource(bpmnXml.getBytes());
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"converted.bpmn\"")
        .body(file);
  }
}
