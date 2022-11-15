package org.camunda.community.converter.webapp;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.camunda.community.rest.client.api.ProcessDefinitionApi;
import org.camunda.community.rest.client.dto.ProcessDefinitionDiagramDto;
import org.camunda.community.rest.client.dto.ProcessDefinitionDto;
import org.camunda.community.rest.client.invoker.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessEngineService {
  private final ProcessDefinitionApi processDefinitionApi;

  @Autowired
  public ProcessEngineService(ProcessDefinitionApi processDefinitionApi) {
    this.processDefinitionApi = processDefinitionApi;
  }

  public Map<String, String> getAllLatestBpmnXml() {
    return getAllLatestProcessDefinitions().stream()
        .collect(
            Collectors.toMap(
                ProcessDefinitionDto::getResource,
                pd -> getProcessDefinitionXml(pd).getBpmn20Xml()));
  }

  private List<ProcessDefinitionDto> getAllLatestProcessDefinitions() {
    try {

      return processDefinitionApi.getProcessDefinitions(
          null, null, null, null, null, null, null, null, null, null, null, null, null, true, null,
          null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
          null, null, null, null, null, null);
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  private ProcessDefinitionDiagramDto getProcessDefinitionXml(
      ProcessDefinitionDto processDefinitionDto) {
    try {
      return processDefinitionApi.getProcessDefinitionBpmn20Xml(processDefinitionDto.getId());
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }
}
