package org.camunda.community.converter.cli;

import static java.nio.file.StandardCopyOption.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ConvertCommandTest {
  private void setupDir(String filename, File tempDir) {
    try (InputStream in = getClass().getClassLoader().getResourceAsStream(filename)) {
      Files.copy(in, new File(tempDir, filename).toPath(), REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void shouldConvert(@TempDir File tempDir) {
    setupDir("c7.bpmn", tempDir);
    ConvertCommand command = new ConvertCommand();
    command.file = tempDir;
    Integer call = command.call();
    assertEquals(0, call);
  }

  @Test
  public void shouldConvertLegacy(@TempDir File tempDir) {
    setupDir("c7.bpmn20.xml", tempDir);
    ConvertCommand command = new ConvertCommand();
    command.file = tempDir;
    Integer call = command.call();
    assertEquals(0, call);
  }

  @Test
  public void shouldNotConvert(@TempDir File tempDir) {
    setupDir("c8.bpmn", tempDir);
    ConvertCommand command = new ConvertCommand();
    command.file = tempDir;
    Integer call = command.call();
    assertEquals(1, call);
  }
}
