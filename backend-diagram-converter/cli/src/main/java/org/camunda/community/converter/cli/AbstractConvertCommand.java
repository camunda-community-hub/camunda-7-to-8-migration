package org.camunda.community.converter.cli;

import static org.camunda.community.converter.cli.ConvertCommand.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.commons.io.FilenameUtils;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.community.converter.BpmnConverter;
import org.camunda.community.converter.BpmnConverterFactory;
import org.camunda.community.converter.ConverterPropertiesFactory;
import org.camunda.community.converter.DefaultConverterProperties;
import picocli.CommandLine.Option;

public abstract class AbstractConvertCommand implements Callable<Integer> {
  private static final String DEFAULT_PREFIX = "converted-c8-";

  protected final BpmnConverter converter;

  @Option(
      names = {"-d", "--documentation"},
      description = "If enabled, messages are also appended to documentation")
  boolean documentation;

  @Option(
      names = {"--adapter-job-type"},
      description = "If set, the default value for the adapter job is overridden")
  String adapterJobType;

  @Option(
      names = {"--prefix"},
      description = "Prefix for the name of the generated file",
      defaultValue = DEFAULT_PREFIX)
  String prefix = DEFAULT_PREFIX;

  @Option(
      names = {"-o", "--override"},
      description = "If enabled, existing files are overridden")
  boolean override;

  public AbstractConvertCommand() {
    BpmnConverterFactory factory = BpmnConverterFactory.getInstance();
    factory.getNotificationServiceFactory().setInstance(new PrintNotificationServiceImpl());
    converter = factory.get();
  }

  @Override
  public final Integer call() throws Exception {
    return modelInstances().entrySet().stream()
        .mapToInt(e -> handleModel(determineFileName(e.getKey()), e.getValue()))
        .max()
        .orElse(0);
  }

  private int handleModel(File file, BpmnModelInstance modelInstance) {
    try {
      converter.convert(
          modelInstance,
          documentation,
          ConverterPropertiesFactory.getInstance().merge(converterProperties()));
    } catch (Exception e) {
      LOG_CLI.error("Problem while converting: {}", e.getMessage());
      return 1;
    }
    if (!override && file.exists()) {
      LOG_CLI.error("File does already exist: {}", file);
      return 1;
    }
    LOG_CLI.info("Created {}", file);
    try (FileWriter fw = new FileWriter(file)) {
      converter.printXml(modelInstance.getDocument(), true, fw);
      fw.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return 0;
  }

  protected abstract Map<File, BpmnModelInstance> modelInstances() throws Exception;

  protected DefaultConverterProperties converterProperties() {
    DefaultConverterProperties properties = new DefaultConverterProperties();
    properties.setAdapterJobType(adapterJobType);
    return properties;
  }

  private File determineFileName(File file) {
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
    return newFile;
  }
}
