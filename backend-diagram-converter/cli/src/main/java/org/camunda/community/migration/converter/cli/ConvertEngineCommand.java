package org.camunda.community.migration.converter.cli;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "engine",
    description = "Converts the diagrams from the given process engine",
    mixinStandardHelpOptions = true)
public class ConvertEngineCommand extends AbstractConvertCommand {
  private static final String DEFAULT_URL = "http://localhost:8080/engine-rest";

  @Parameters(
      index = "0",
      description = "Fully qualified http(s) address to the process engine REST API",
      defaultValue = DEFAULT_URL)
  String url = DEFAULT_URL;

  @Option(
      names = {"-u", "--username"},
      description = "Username for basic auth")
  String username;

  @Option(
      names = {"-p", "--password"},
      description = "Password for basic auth")
  String password;

  @Option(
      names = {"-t", "--target-directory"},
      description = "The directory to save the .bpmn files",
      defaultValue = ".")
  File targetDirectory = new File(".");

  @Override
  protected File targetDirectory() {
    return targetDirectory;
  }

  @Override
  protected Map<File, BpmnModelInstance> modelInstances() {
    Map<String, Map<String, Set<String>>> allLatestBpmnXml = getAllLatestBpmnXml();
    Map<File, BpmnModelInstance> result = new HashMap<>();
    allLatestBpmnXml.forEach(
        (resourceName, models) ->
            models.forEach(
                (model, processDefinitionKeys) -> {
                  String filename =
                      models.size() == 1
                          ? resourceName
                          : FilenameUtils.getBaseName(resourceName)
                              + " ("
                              + String.join(", ", processDefinitionKeys)
                              + ")."
                              + FilenameUtils.getExtension(resourceName);
                  result.put(
                      new File(targetDirectory, filename),
                      Bpmn.readModelFromStream(new ByteArrayInputStream(model.getBytes())));
                }));
    return result;
  }

  private Map<String, Map<String, Set<String>>> getAllLatestBpmnXml() {
    ProcessEngineClient client = ProcessEngineClient.withEngine(url, username, password);
    return client.getAllLatestProcessDefinitions().stream()
        .collect(
            Collectors.groupingBy(
                ProcessDefinitionDto::getResource,
                Collectors.groupingBy(
                    pd -> client.getBpmnXml(pd.getId()).getBpmn20Xml(),
                    Collectors.mapping(ProcessDefinitionDto::getKey, Collectors.toSet()))));
  }
}
