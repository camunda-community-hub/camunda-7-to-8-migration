package org.camunda.community.converter.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.Callable;
import org.apache.commons.io.FileUtils;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.community.converter.BpmnConverter;
import org.camunda.community.converter.ConversionFactory;
import org.camunda.community.converter.DomElementVisitorFactory;
import org.camunda.community.converter.conversion.Conversion;
import org.camunda.community.converter.visitor.DomElementVisitor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "camunda-convert",
    mixinStandardHelpOptions = true,
    description = "Converts the diagrams from the given directory or file")
public class ConvertCommand implements Callable<Integer> {
  private final Set<DomElementVisitor> visitors = DomElementVisitorFactory.getInstance().get();
  private final Set<Conversion> conversions = ConversionFactory.getInstance().get();
  private final BpmnConverter converter = new BpmnConverter(visitors, conversions);

  @Parameters(index = "0", description = "The file or directory to search for")
  File file;

  @Option(
      names = {"-d", "--documentation"},
      description = "If enabled, messages are also appended to documentation")
  boolean documentation;

  @Override
  public Integer call() throws Exception {
    if (!file.exists()) {
      System.err.println("File " + file.getAbsolutePath() + " does not exist");
      return 1;
    }
    Collection<File> files = new ArrayList<>();
    if (file.isDirectory()) {
      files.addAll(FileUtils.listFiles(file, new String[] {"bpmn"}, true));
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
    File newFile = new File(file.getParentFile(), "converted-c8-" + file.getName());
    BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
    try {
      converter.convert(modelInstance, documentation);
    } catch (Exception e) {
      System.err.println("Problem while converting: " + e.getMessage());
      return 1;
    }
    System.out.println("Created " + newFile);
    Bpmn.writeModelToFile(newFile, modelInstance);
    return 0;
  }

  private boolean isBpmnFile(File file) {
    return file.getName().endsWith(".bpmn");
  }
}
