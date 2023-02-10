package org.camunda.community.migration.processInstance.core;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.search.ProcessDefinitionFilter;
import io.camunda.operate.search.SearchQuery;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.camunda.community.migration.processInstance.core.dto.ProcessInstanceMigrationStartRequestDto;
import org.camunda.community.migration.processInstance.core.dto.ProcessInstanceMigrationTaskDto;
import org.camunda.community.migration.processInstance.core.dto.ProcessInstanceMigrationTaskDto.ProcessInstanceDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/migration")
public class ProcessInstanceMigrationController {

  private final ProcessInstanceMigrationService starter;
  private final ProcessInstanceSelectionService selectionService;
  private final Camunda7Service camunda7Service;
  private final CamundaOperateClient operateClient;

  @Autowired
  public ProcessInstanceMigrationController(
      ProcessInstanceMigrationService starter,
      ProcessInstanceSelectionService selectionService,
      Camunda7Service camunda7Service,
      CamundaOperateClient operateClient) {
    this.starter = starter;
    this.selectionService = selectionService;
    this.camunda7Service = camunda7Service;
    this.operateClient = operateClient;
  }

  @PostMapping("/start")
  public void start(@RequestBody ProcessInstanceMigrationStartRequestDto dto) {
    starter.startProcessInstanceMigration(dto.getBpmnProcessId());
  }

  @GetMapping("/tasks")
  public List<ProcessInstanceMigrationTaskDto> tasks(
      @RequestParam(value = "includeCompleted", defaultValue = "false") boolean includeCompleted) {
    return selectionService.getTasks(includeCompleted).stream()
        .map(this::fromTask)
        .collect(Collectors.toList());
  }

  @PutMapping("/tasks/{id}")
  public ProcessInstanceMigrationTaskDto complete(
      @PathVariable("id") Long id, @RequestBody List<String> processInstanceIds) {
    return fromTask(selectionService.complete(id, processInstanceIds));
  }

  private ProcessInstanceMigrationTaskDto fromTask(ProcessInstanceSelectionTask task) {
    ProcessInstanceMigrationTaskDto dto = new ProcessInstanceMigrationTaskDto();
    dto.setState(task.getState());
    dto.setId(task.getJobKey());
    dto.setProcessDefinitionId(task.getProcessDefinitionId());
    dto.setBpmnProcessId(task.getBpmnProcessId());
    dto.setAvailableProcessInstances(
        camunda7Service
            .getProcessInstancesByProcessDefinitionId(task.getProcessDefinitionId())
            .stream()
            .map(
                pi -> {
                  ProcessInstanceDataDto data = new ProcessInstanceDataDto();
                  data.setId(pi.getId());
                  data.setBusinessKey(pi.getBusinessKey());
                  return data;
                })
            .collect(Collectors.toList()));
    ProcessDefinitionFilter filter = new ProcessDefinitionFilter();
    filter.setBpmnProcessId(task.getBpmnProcessId());
    SearchQuery query = new SearchQuery();
    query.setFilter(filter);
    try {
      operateClient.searchProcessDefinitions(query).stream()
          .max(Comparator.comparingLong(ProcessDefinition::getVersion))
          .ifPresent(pd -> dto.setProcessDefinitionKey(pd.getKey()));
    } catch (OperateException e) {
      throw new RuntimeException(e);
    }
    return dto;
  }
}
