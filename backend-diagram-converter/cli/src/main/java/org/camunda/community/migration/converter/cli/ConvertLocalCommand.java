package org.camunda.community.migration.converter.cli;

import static org.camunda.community.migration.converter.cli.ConvertCommand.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.file.PathVisitor;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "local",
    description = {
      "Converts the diagram from the given directory or file",
      "%nExecute as:",
      "%njava -Dfile.encoding=UTF-8 -jar backend-diagram-converter-cli.jar local%n"
    },
    mixinStandardHelpOptions = true,
    optionListHeading = "Options:%n",
    parameterListHeading = "Parameter:%n",
    showDefaultValues = true)
public class ConvertLocalCommand extends AbstractConvertCommand {

  private static final String[] FILE_ENDINGS = new String[] {"bpmn", "bpmn20.xml"};

  @Parameters(index = "0", description = "The file to convert or directory to search in")
  File file;

  @Option(
      names = {"-nr", "--not-recursive"},
      description = "If enabled, recursive search in subfolders will be omitted")
  boolean notRecursive;

  @Override
  protected File targetDirectory() {
    if (file.isDirectory()) {
      return file;
    }
    return file.getParentFile();
  }

  @Override
  protected Map<File, BpmnModelInstance> modelInstances() {
    if (!file.exists()) {
      LOG_CLI.error("File {} does not exist", file.getAbsolutePath());
      returnCode = 1;
      return new HashMap<>();
    }
    Collection<File> files = new ArrayList<>();
    if (file.isDirectory()) {
      files.addAll(findFiles(file));
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
    return files.stream()
        .filter(this::canHandleFile)
        .collect(Collectors.toMap(f -> f, this::handleFile));
  }

  private boolean canHandleFile(File file) {
    try {
      handleFile(file);
      return true;
    } catch (Exception e) {
      LOG_CLI.error(
          "Unable to parse file {}, reason is: {}", file.getAbsolutePath(), createMessage(e));
      return false;
    }
  }

  private BpmnModelInstance handleFile(File file) {
    return Bpmn.readModelFromFile(file);
  }

  private boolean isBpmnFile(File file) {
    return Arrays.stream(FILE_ENDINGS).anyMatch(ending -> file.getName().endsWith(ending));
  }

  private List<File> findFiles(File directory) {
    List<File> files = new ArrayList<>();
    try {
      Files.walkFileTree(
          directory.toPath(), new FindFileBySuffixPathVisitor(files, Arrays.asList(FILE_ENDINGS)));
    } catch (Exception e) {
      LOG_CLI.error("Error while finding files: {}", createMessage(e));
      returnCode = 1;
    }
    return files;
  }

  public static class FindFileBySuffixPathVisitor implements PathVisitor {
    private static final Logger LOG = LoggerFactory.getLogger(FindFileBySuffixPathVisitor.class);
    private final List<File> files;
    private final List<String> suffixes;

    public FindFileBySuffixPathVisitor(List<File> files, List<String> suffixes) {
      this.files = files;
      this.suffixes = suffixes;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
        throws IOException {
      LOG.debug("Start visiting directory '{}'", dir.toFile().getAbsolutePath());
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      if (suffixes.stream().anyMatch(suffix -> file.toFile().getName().endsWith(suffix))) {
        files.add(file.toFile());
        LOG.debug("Visited file, added '{}'", file.toFile().getAbsolutePath());
      } else {
        LOG.debug("Visited file, not added '{}'", file.toFile().getAbsolutePath());
      }
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
      LOG.debug("Visiting file failed '{}'", file.toFile().getAbsolutePath());
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
      LOG.debug("Done visiting directory '{}'", dir.toFile().getAbsolutePath());
      return FileVisitResult.CONTINUE;
    }
  }
}
