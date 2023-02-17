package org.camunda.community.migration.processInstance;

import io.camunda.operate.dto.ProcessDefinition;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData;
import org.camunda.community.migration.processInstance.dto.ProcessInstanceMigrationStartRequestDto;
import org.camunda.community.migration.processInstance.dto.ProcessInstanceMigrationTaskDto;
import org.camunda.community.migration.processInstance.dto.ProcessInstanceMigrationTaskDto.ProcessInstanceDataDto;
import org.camunda.community.migration.processInstance.dto.ProcessInstanceSelectionTask;
import org.camunda.community.migration.processInstance.service.Camunda7Service;
import org.camunda.community.migration.processInstance.service.Camunda8Service;
import org.camunda.community.migration.processInstance.service.ProcessInstanceMigrationHintService;
import org.camunda.community.migration.processInstance.service.ProcessInstanceSelectionService;
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
  private final ProcessInstanceSelectionService selectionService;
  private final Camunda7Service camunda7Service;
  private final Camunda8Service camunda8Service;
  private final ProcessInstanceMigrationHintService processInstanceMigrationHintService;

  @Autowired
  public ProcessInstanceMigrationController(
      ProcessInstanceSelectionService selectionService,
      Camunda7Service camunda7Service,
      Camunda8Service camunda8Service,
      ProcessInstanceMigrationHintService processInstanceMigrationHintService) {
    this.selectionService = selectionService;
    this.camunda7Service = camunda7Service;
    this.camunda8Service = camunda8Service;
    this.processInstanceMigrationHintService = processInstanceMigrationHintService;
  }

  @PostMapping("/start")
  public void start(@RequestBody ProcessInstanceMigrationStartRequestDto dto) {
    camunda8Service.startProcessInstanceMigration(dto.getBpmnProcessId());
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
    ProcessDefinition processDefinition =
        camunda8Service.queryProcessDefinitions(task.getBpmnProcessId()).stream()
            .max(Comparator.comparingLong(ProcessDefinition::getVersion))
            .get();
    ProcessInstanceMigrationTaskDto dto = new ProcessInstanceMigrationTaskDto();
    dto.setProcessDefinitionKey(processDefinition.getKey());
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
                  data.setMigrationHints(new ArrayList<>());
                  Camunda7ProcessInstanceData processData =
                      camunda7Service.getProcessData(pi.getId());
                  data.setMigrationHints(
                      processInstanceMigrationHintService.getMigrationHints(processData));
                  return data;
                })
            .collect(Collectors.toList()));
    camunda8Service.queryProcessDefinitions(task.getBpmnProcessId()).stream()
        .max(Comparator.comparingLong(ProcessDefinition::getVersion))
        .ifPresent(pd -> dto.setProcessDefinitionKey(pd.getKey()));
    return dto;
  }
}
