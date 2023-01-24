package org.camunda.community.migration.converter.cli;

import static java.nio.file.StandardCopyOption.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ConvertLocalCommandTest {
  private void setupDir(String filename, File tempDir) {
    try (InputStream in = getClass().getClassLoader().getResourceAsStream(filename)) {
      Files.copy(
          Objects.requireNonNull(in), new File(tempDir, filename).toPath(), REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void shouldConvert(@TempDir File tempDir) {
    setupDir("c7.bpmn", tempDir);
    ConvertLocalCommand command = new ConvertLocalCommand();
    command.file = tempDir;
    Integer call = command.call();
    assertEquals(0, call);
  }

  @Test
  public void shouldConvertLegacy(@TempDir File tempDir) {
    setupDir("c7.bpmn20.xml", tempDir);
    ConvertLocalCommand command = new ConvertLocalCommand();
    command.file = tempDir;
    Integer call = command.call();
    assertEquals(0, call);
  }

  @Test
  public void shouldNotConvert(@TempDir File tempDir) {
    setupDir("c8.bpmn", tempDir);
    ConvertLocalCommand command = new ConvertLocalCommand();
    command.file = tempDir;
    Integer call = command.call();
    assertEquals(1, call);
  }

  @Test
  void shouldCreateCsv(@TempDir File tempDir) {
    setupDir("c7.bpmn", tempDir);
    ConvertLocalCommand command = new ConvertLocalCommand();
    command.csv = true;
    command.file = tempDir;
    Integer call = command.call();
    assertEquals(0, call);
    assertThat(tempDir.listFiles())
        .hasSize(3)
        .anyMatch(file -> file.getName().equals("c7.bpmn"))
        .anyMatch(file -> file.getName().equals("converted-c8-c7.bpmn"))
        .anyMatch(file -> file.getName().equals("conversion-results.csv"));
  }

  @Test
  void shouldNotCreateCsv(@TempDir File tempDir) {
    setupDir("c7.bpmn", tempDir);
    ConvertLocalCommand command = new ConvertLocalCommand();
    command.file = tempDir;
    Integer call = command.call();
    assertEquals(0, call);
    assertThat(tempDir.listFiles())
        .hasSize(2)
        .anyMatch(file -> file.getName().equals("c7.bpmn"))
        .anyMatch(file -> file.getName().equals("converted-c8-c7.bpmn"));
  }

  @Test
  void shouldNotCreateConvertedDiagrams(@TempDir File tempDir) {
    setupDir("c7.bpmn", tempDir);
    ConvertLocalCommand command = new ConvertLocalCommand();
    command.file = tempDir;
    command.check = true;
    Integer call = command.call();
    assertEquals(0, call);
    assertThat(tempDir.listFiles()).hasSize(1).anyMatch(file -> file.getName().equals("c7.bpmn"));
  }
}
