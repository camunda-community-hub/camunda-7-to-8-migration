package org.camunda.community.converter.webapp;

import java.io.IOException;
import java.io.InputStream;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  public ConverterController(BpmnConverterService bpmnConverter) {
    this.bpmnConverter = bpmnConverter;
  }

  @PostMapping("/check")
  public ResponseEntity<?> check(@RequestParam("file") MultipartFile bpmnFile) {
    try (InputStream in = bpmnFile.getInputStream()) {
      return ResponseEntity.ok(bpmnConverter.check(Bpmn.readModelFromStream(in), false));
    } catch (IOException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  @PostMapping("/convert")
  public ResponseEntity<?> getFile(
      @RequestParam("file") MultipartFile bpmnFile,
      @RequestParam("appendDocumentation") boolean appendDocumentation) {
    try (InputStream in = bpmnFile.getInputStream()) {
      BpmnModelInstance modelInstance = Bpmn.readModelFromStream(in);
      bpmnConverter.convert(modelInstance, appendDocumentation);
      String bpmnXml = bpmnConverter.printXml(modelInstance.getDocument(), true);
      Resource file = new ByteArrayResource(bpmnXml.getBytes());
      return ResponseEntity.ok()
          .header(
              HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"converted-c8-" + bpmnFile.getOriginalFilename() + "\"")
          .contentType(MediaType.TEXT_XML)
          .body(file);
    } catch (IOException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }
}
