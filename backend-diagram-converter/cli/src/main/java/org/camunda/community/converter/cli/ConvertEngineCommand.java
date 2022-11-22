package org.camunda.community.converter.cli;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "engine",
    description = "Converts the diagrams from the given process engine",
    mixinStandardHelpOptions = true)
public class ConvertEngineCommand extends AbstractConvertCommand {
  private static final String DEFAULT_URL = "http://localhost:8080/engine-rest";
  private final RestTemplate restTemplate = new RestTemplate();

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
  protected Map<File, BpmnModelInstance> modelInstances() throws Exception {
    if (username != null && password != null) {
      restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
    }
    return getAllLatestBpmnXml().entrySet().stream()
        .collect(
            Collectors.toMap(
                e -> new File(targetDirectory, e.getKey()),
                e -> Bpmn.readModelFromStream(new ByteArrayInputStream(e.getValue().getBytes()))));
  }

  private Map<String, String> getAllLatestBpmnXml() {
    return getAllLatestProcessDefinitions().stream()
        .collect(
            Collectors.toMap(
                ProcessDefinitionDto::getResource,
                pd -> getProcessDefinitionXml(pd).getBpmn20Xml()));
  }

  private List<ProcessDefinitionDto> getAllLatestProcessDefinitions() {
    RequestEntity<?> entity =
        new RequestEntity<>(
            HttpMethod.GET,
            UriComponentsBuilder.fromHttpUrl(url)
                .pathSegment("process-definition")
                .queryParam("latestVersion", true)
                .build()
                .toUri());
    return restTemplate
        .exchange(entity, new ParameterizedTypeReference<List<ProcessDefinitionDto>>() {})
        .getBody();
  }

  private ProcessDefinitionDiagramDto getProcessDefinitionXml(
      ProcessDefinitionDto processDefinitionDto) {
    return restTemplate.getForObject(
        UriComponentsBuilder.fromHttpUrl(url)
            .pathSegment("process-definition")
            .pathSegment(processDefinitionDto.getId())
            .pathSegment("xml")
            .build()
            .toUri(),
        ProcessDefinitionDiagramDto.class);
  }
}
