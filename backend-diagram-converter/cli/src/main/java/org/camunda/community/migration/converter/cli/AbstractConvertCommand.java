package org.camunda.community.migration.converter.cli;

import static org.camunda.community.migration.converter.cli.ConvertCommand.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.community.migration.converter.BpmnConverter;
import org.camunda.community.migration.converter.BpmnConverterFactory;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult;
import org.camunda.community.migration.converter.ConverterPropertiesFactory;
import org.camunda.community.migration.converter.DefaultConverterProperties;
import picocli.CommandLine.Option;

public abstract class AbstractConvertCommand implements Callable<Integer> {
  private static final String DEFAULT_PREFIX = "converted-c8-";

  protected final BpmnConverter converter;
  protected int returnCode = 0;

  @Option(
      names = {"-d", "--documentation"},
      description = "If enabled, messages are also appended to documentation")
  boolean documentation;

  @Option(
      names = {"--default-job-type"},
      description =
          "If set, the default value from the 'converter-properties.properties' for the job type is overridden")
  String defaultJobType;

  @Option(
      names = {"--prefix"},
      description = "Prefix for the name of the generated file",
      defaultValue = DEFAULT_PREFIX)
  String prefix = DEFAULT_PREFIX;

  @Option(
      names = {"-o", "--override"},
      description = "If enabled, existing files are overridden")
  boolean override;

  @Option(
      names = {"--platform-version"},
      description = "Semantic version of the target platform, defaults to latest version")
  String platformVersion;

  @Option(
      names = {"--csv"},
      description =
          "If enabled, a CSV file will be created containing the results for all conversions")
  boolean csv;

  @Option(
      names = {"--md", "--markdown"},
      description =
          "If enabled, a markdown file will be created containing the results for all conversions")
  boolean markdown;

  @Option(
      names = {"--delegate-execution-as-job-type", "--delegate-expression-as-job-type"},
      description = "If enabled, sets the delegate expression as the job type")
  boolean delegateExecutionAsJobType;

  @Option(names = "--check", description = "If enabled, no converted diagrams are exported")
  boolean check;

  @Option(names = "--disable-default-job-type", description = "Disables the default job type")
  boolean defaultJobTypeDisabled;

  @Option(
      names = "--disable-append-elements",
      description = "Disables adding conversion messages to the bpmn xml")
  boolean disableAppendElements;

  public AbstractConvertCommand() {
    BpmnConverterFactory factory = BpmnConverterFactory.getInstance();
    factory.getNotificationServiceFactory().setInstance(new PrintNotificationServiceImpl());
    converter = factory.get();
  }

  @Override
  public final Integer call() {
    returnCode = 0;
    Map<File, BpmnModelInstance> modelInstances = modelInstances();
    List<BpmnDiagramCheckResult> results = checkModels(modelInstances);
    writeResults(modelInstances, results);
    return returnCode;
  }

  private void writeResults(
      Map<File, BpmnModelInstance> modelInstances, List<BpmnDiagramCheckResult> results) {
    if (!check) {
      for (Entry<File, BpmnModelInstance> modelInstance : modelInstances.entrySet()) {
        File file = determineFileName(prefixFileName(modelInstance.getKey()));
        if (!override && file.exists()) {
          LOG_CLI.error("File does already exist: {}", file);
          returnCode = 1;
        }
        LOG_CLI.info("Created {}", file);
        try (FileWriter fw = new FileWriter(file)) {
          converter.printXml(modelInstance.getValue().getDocument(), true, fw);
          fw.flush();
        } catch (IOException e) {
          LOG_CLI.error("Error while creating BPMN file: {}", createMessage(e));
          returnCode = 1;
        }
      }
    }
    if (csv) {
      File csvFile = determineFileName(new File(targetDirectory(), "conversion-results.csv"));
      try (FileWriter fw = new FileWriter(csvFile)) {
        converter.writeCsvFile(results, fw);
        LOG_CLI.info("Created {}", csvFile);
      } catch (IOException e) {
        LOG_CLI.error("Error while creating csv results: {}", createMessage(e));
        returnCode = 1;
      }
    }
    if (markdown) {
      File markdownFile = determineFileName(new File(targetDirectory(), "conversion-results.md"));
      try (FileWriter fw = new FileWriter(markdownFile)) {
        converter.writeMarkdownFile(results, fw);
        LOG_CLI.info("Created {}", markdownFile);
      } catch (IOException e) {
        LOG_CLI.error("Error while creating markdown results: {}", createMessage(e));
        returnCode = 1;
      }
    }
  }

  protected abstract File targetDirectory();

  private List<BpmnDiagramCheckResult> checkModels(Map<File, BpmnModelInstance> modelInstances) {
    return modelInstances.entrySet().stream()
        .map(this::checkModel)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private BpmnDiagramCheckResult checkModel(Entry<File, BpmnModelInstance> modelInstance) {
    try {
      return converter.check(
          modelInstance.getKey().getPath(),
          modelInstance.getValue(),
          ConverterPropertiesFactory.getInstance().merge(converterProperties()));
    } catch (Exception e) {
      LOG_CLI.error("Problem while converting: {}", createMessage(e));
      returnCode = 1;
      return null;
    }
  }

  protected abstract Map<File, BpmnModelInstance> modelInstances();

  protected DefaultConverterProperties converterProperties() {
    DefaultConverterProperties properties = new DefaultConverterProperties();
    properties.setDefaultJobType(defaultJobType);
    properties.setPlatformVersion(platformVersion);
    properties.setDefaultJobTypeEnabled(!defaultJobTypeDisabled);
    properties.setAppendDocumentation(documentation);
    properties.setAppendElements(!disableAppendElements);
    properties.setUseDelegateExpressionAsJobType(delegateExecutionAsJobType);
    return properties;
  }

  private File prefixFileName(File file) {
    return new File(file.getParentFile(), prefix + file.getName());
  }

  private File determineFileName(File file) {
    File newFile = file;
    int counter = 0;
    while (!override && newFile.exists()) {
      counter++;
      newFile =
          new File(
              file.getParentFile(),
              FilenameUtils.getBaseName(file.getName())
                  + " ("
                  + counter
                  + ")."
                  + FilenameUtils.getExtension(file.getName()));
    }
    return newFile;
  }

  protected String createMessage(Exception e) {
    StringBuilder message = new StringBuilder(e.getMessage());
    Throwable ex = e.getCause();
    while (ex != null) {
      message.append(",").append("\n").append("caused by: ").append(ex.getMessage());
      ex = ex.getCause();
    }
    return message.toString();
  }
}
