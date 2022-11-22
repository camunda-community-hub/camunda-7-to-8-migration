package org.camunda.community.converter.cli;

import static org.camunda.community.converter.cli.ConvertCommand.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "local",
    description = "Converts the diagrams from the given directory or file",
    mixinStandardHelpOptions = true)
public class ConvertLocalCommand extends AbstractConvertCommand {

  private static final String[] FILE_ENDINGS = new String[] {"bpmn", "bpmn20.xml"};

  @Parameters(index = "0", description = "The file or directory to search for")
  File file;

  @Option(
      names = {"-nr", "--not-recursive"},
      description = "If enabled, recursive search will not be performed")
  boolean notRecursive;

  @Override
  protected Map<File, BpmnModelInstance> modelInstances() throws Exception {
    if (!file.exists()) {
      LOG_CLI.error("File {} does not exist", file.getAbsolutePath());
      throw new FileNotFoundException();
    }
    Collection<File> files = new ArrayList<>();
    if (file.isDirectory()) {
      files.addAll(FileUtils.listFiles(file, FILE_ENDINGS, !notRecursive));
    } else {
      if (isBpmnFile(file)) {
        files.add(file);
      } else {
        LOG_CLI.error("The selected file is no bpmn file");
        throw new IllegalArgumentException("The selected file is no bpmn file");
      }
    }
    return handleFiles(files);
  }

  private Map<File, BpmnModelInstance> handleFiles(Collection<File> files) {
    return files.stream().collect(Collectors.toMap(f -> f, this::handleFile));
  }

  private BpmnModelInstance handleFile(File file) {

    return Bpmn.readModelFromFile(file);
  }

  private boolean isBpmnFile(File file) {
    return Arrays.stream(FILE_ENDINGS).anyMatch(ending -> file.getName().endsWith(ending));
  }
}
