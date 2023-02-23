package org.camunda.community.migration.processInstance;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.stream.Collectors;
import org.camunda.community.migration.processInstance.dto.rest.ProcessInstanceMigrationStartRequestDto;
import org.camunda.community.migration.processInstance.dto.rest.UserTaskDto;
import org.camunda.community.migration.processInstance.dto.task.UserTask;
import org.camunda.community.migration.processInstance.service.Camunda7Service;
import org.camunda.community.migration.processInstance.service.Camunda8Service;
import org.camunda.community.migration.processInstance.service.MigrationTaskService;
import org.camunda.community.migration.processInstance.service.ProcessInstanceMigrationHintService;
import org.camunda.community.migration.processInstance.service.TaskMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
  private final MigrationTaskService selectionService;
  private final Camunda7Service camunda7Service;
  private final Camunda8Service camunda8Service;
  private final ProcessInstanceMigrationHintService processInstanceMigrationHintService;
  private final TaskMappingService taskMappingService;

  @Autowired
  public ProcessInstanceMigrationController(
      MigrationTaskService selectionService,
      Camunda7Service camunda7Service,
      Camunda8Service camunda8Service,
      ProcessInstanceMigrationHintService processInstanceMigrationHintService,
      TaskMappingService taskMappingService) {
    this.selectionService = selectionService;
    this.camunda7Service = camunda7Service;
    this.camunda8Service = camunda8Service;
    this.processInstanceMigrationHintService = processInstanceMigrationHintService;
    this.taskMappingService = taskMappingService;
  }

  @PostMapping("/start")
  public ResponseEntity<?> start(@RequestBody ProcessInstanceMigrationStartRequestDto dto) {
    if (dto.getMigrationType() == null) {
      return ResponseEntity.badRequest().body("Please provide a migration type: 'simple','router'");
    }
    if (dto.getMigrationType().equals("simple")) {
      camunda8Service.startProcessInstanceMigration(dto.getBpmnProcessId());
      return ResponseEntity.ok().build();
    }
    if (dto.getMigrationType().equals("router")) {
      camunda8Service.startProcessInstanceMigrationRouter(dto.getBpmnProcessId());
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest()
        .body(
            "Invalid migration type '"
                + dto.getMigrationType()
                + "'.Please provide a migration type: 'simple','router'");
  }

  @GetMapping("/tasks")
  public List<UserTaskDto> tasks(
      @RequestParam(value = "includeCompleted", defaultValue = "false") boolean includeCompleted) {
    return selectionService.getTasks(includeCompleted).stream()
        .map(this::fromTask)
        .collect(Collectors.toList());
  }

  @PutMapping("/tasks/{id}")
  public UserTaskDto complete(@PathVariable("id") Long id, @RequestBody JsonNode body) {
    return fromTask(selectionService.complete(id, body));
  }

  private UserTaskDto fromTask(UserTask task) {
    UserTaskDto userTaskDto = new UserTaskDto();
    userTaskDto.setKey(task.getKey());
    userTaskDto.setState(task.getState());
    userTaskDto.setType(task.getType());
    userTaskDto.setData(taskMappingService.crateDto(task.getType(), task.getData()));
    return userTaskDto;
  }
}
