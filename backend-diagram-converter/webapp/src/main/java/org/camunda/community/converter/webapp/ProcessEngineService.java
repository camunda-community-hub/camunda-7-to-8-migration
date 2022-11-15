package org.camunda.community.converter.webapp;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.camunda.community.converter.webapp.dto.ProcessDefinitionDiagramDto;
import org.camunda.community.converter.webapp.dto.ProcessDefinitionDto;
import org.camunda.community.converter.webapp.properties.ProcessEngineClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ProcessEngineService {
  private final RestTemplate restTemplate;
  private final ProcessEngineClientProperties properties;

  @Autowired
  public ProcessEngineService(
      @Qualifier("camunda-engine-client") RestTemplate restTemplate,
      ProcessEngineClientProperties properties) {
    this.restTemplate = restTemplate;
    this.properties = properties;
  }

  public Map<String, String> getAllLatestBpmnXml() {
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
            UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
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
        UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
            .pathSegment("process-definition")
            .pathSegment(processDefinitionDto.getId())
            .pathSegment("xml")
            .build()
            .toUri(),
        ProcessDefinitionDiagramDto.class);
  }
}
