package org.camunda.community.migration.converter.cli;

import java.util.Collections;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

public class ProcessEngineClient {
  private static final String LATEST_PROCESS_DEFINITIONS = "/process-definition?latestVersion=true";
  private static final String LATEST_DECISION_DEFINITIONS =
      "/decision-definition?latestVersion=true";

  private static final String PROCESS_DEFINITION_XML = "/process-definition/{id}/xml";
  private static final String DECISION_DEFINITION_XML = "/decision-definition/{id}/xml";
  private final RestTemplate restTemplate;

  private ProcessEngineClient(String url) {
    this.restTemplate = new RestTemplate();
    restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(url));
  }

  private ProcessEngineClient(String url, String username, String password) {
    this(url);
    restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
  }

  public static ProcessEngineClient withEngine(
      String url, @Nullable String username, @Nullable String password) {
    if (username == null || password == null) {
      return new ProcessEngineClient(url);
    }
    return new ProcessEngineClient(url, username, password);
  }

  public List<ProcessDefinitionDto> getAllLatestProcessDefinitions() {
    return restTemplate
        .exchange(
            LATEST_PROCESS_DEFINITIONS,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ProcessDefinitionDto>>() {})
        .getBody();
  }

  public ProcessDefinitionDiagramDto getBpmnXml(String processDefinitionId) {
    return restTemplate.getForObject(
        PROCESS_DEFINITION_XML,
        ProcessDefinitionDiagramDto.class,
        Collections.singletonMap("id", processDefinitionId));
  }

  public List<DecisionDefinitionDto> getAllLatestDecisionDefinitions() {
    return restTemplate
        .exchange(
            LATEST_DECISION_DEFINITIONS,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<DecisionDefinitionDto>>() {})
        .getBody();
  }

  public DecisionDefinitionDiagramDto getDmnXml(String decisionDefinitionId) {
    return restTemplate.getForObject(
        DECISION_DEFINITION_XML,
        DecisionDefinitionDiagramDto.class,
        Collections.singletonMap("id", decisionDefinitionId));
  }
}
