package org.camunda.community.converter.webapp;

import org.camunda.community.converter.webapp.dto.ConverterManifestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManifestController {
  private final boolean engineEnabled;

  @Autowired
  public ManifestController(@Value("${camunda.bpm.client.enabled}") boolean engineEnabled) {
    this.engineEnabled = engineEnabled;
  }

  @GetMapping("/manifest")
  public ResponseEntity<ConverterManifestDto> getManifest() {
    ConverterManifestDto manifest = new ConverterManifestDto();
    manifest.setEngineEnabled(engineEnabled);
    return ResponseEntity.ok(manifest);
  }
}
