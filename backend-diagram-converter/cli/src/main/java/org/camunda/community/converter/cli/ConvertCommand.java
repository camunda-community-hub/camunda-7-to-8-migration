package org.camunda.community.converter.cli;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.OptionalInt;
import java.util.concurrent.Callable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.community.converter.BpmnConverter;
import org.camunda.community.converter.BpmnConverterFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "camunda-convert",
    mixinStandardHelpOptions = true,
    description = "Converts the diagrams from the given directory or file")
public class ConvertCommand implements Callable<Integer> {
  private static final String DEFAULT_PREFIX = "converted-c8-";
  private final BpmnConverter converter;

  @Parameters(index = "0", description = "The file or directory to search for")
  File file;

  @Option(
      names = {"-d", "--documentation"},
      description = "If enabled, messages are also appended to documentation")
  boolean documentation;

  @Option(
      names = {"-p", "--prefix"},
      description = "Prefix for the name of the generated file",
      defaultValue = DEFAULT_PREFIX)
  String prefix = DEFAULT_PREFIX;

  @Option(
      names = {"-o", "--override"},
      description = "If enabled, existing files are overridden")
  boolean override;

  @Option(
      names = {"-nr", "--not-recursive"},
      description = "If enabled, recursive search will not be performed")
  boolean notRecursive;

  public ConvertCommand() {
    BpmnConverterFactory factory = BpmnConverterFactory.getInstance();
    factory.getNotificationServiceFactory().setInstance(new PrintNotificationServiceImpl());
    converter = factory.get();
  }

  @Override
  public Integer call() {
    if (!file.exists()) {
      System.err.println("File " + file.getAbsolutePath() + " does not exist");
      return 1;
    }
    Collection<File> files = new ArrayList<>();
    if (file.isDirectory()) {
      files.addAll(FileUtils.listFiles(file, new String[] {"bpmn"}, !notRecursive));
    } else {
      if (isBpmnFile(file)) {
        files.add(file);
      } else {
        System.err.println("The selected file is no .bpmn file");
        return 1;
      }
    }
    return handleFiles(files).orElse(0);
  }

  private OptionalInt handleFiles(Collection<File> files) {
    return files.stream().mapToInt(this::handleFile).max();
  }

  private int handleFile(File file) {
    File newFile = new File(file.getParentFile(), prefix + file.getName());
    int counter = 0;
    while (!override && newFile.exists()) {
      counter++;
      newFile =
          new File(
              file.getParentFile(),
              prefix
                  + FilenameUtils.getBaseName(file.getName())
                  + " ("
                  + counter
                  + ")."
                  + FilenameUtils.getExtension(file.getName()));
    }
    BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
    try {
      converter.convert(modelInstance, documentation);
    } catch (Exception e) {
      System.err.println("Problem while converting: " + e.getMessage());
      return 1;
    }
    System.out.println("Created " + newFile);
    try (FileWriter fw = new FileWriter(newFile)) {
      converter.printXml(modelInstance.getDocument(), true, fw);
      fw.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return 0;
  }

  private boolean isBpmnFile(File file) {
    return file.getName().endsWith(".bpmn");
  }
}
