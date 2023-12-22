package org.camunda.community.migration.processInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.camunda.community.migration.processInstance.dto.rest.ProcessInstanceMigrationStartRequestDto;
import org.camunda.community.migration.processInstance.dto.rest.UserTaskDto;
import org.camunda.community.migration.processInstance.dto.task.UserTask;
import org.camunda.community.migration.processInstance.properties.Camunda7ClientProperties;
import org.camunda.community.migration.processInstance.service.Camunda8Service;
import org.camunda.community.migration.processInstance.service.MigrationTaskService;
import org.camunda.community.migration.processInstance.service.TaskMappingService;
import org.camunda.community.migration.processInstance.variables.ProcessInstanceMigrationVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
  private final String operateBaseUrl;
  private final MigrationTaskService selectionService;
  private final Camunda8Service camunda8Service;
  private final TaskMappingService taskMappingService;
  private final Camunda7ClientProperties camunda7ClientProperties;

  @Autowired
  public ProcessInstanceMigrationController(
      @Value("${camunda.operate.base-url:null}") String operateBaseUrl,
      MigrationTaskService selectionService,
      Camunda8Service camunda8Service,
      TaskMappingService taskMappingService,
      Camunda7ClientProperties camunda7ClientProperties) {
    this.operateBaseUrl = operateBaseUrl;
    this.selectionService = selectionService;
    this.camunda8Service = camunda8Service;
    this.taskMappingService = taskMappingService;
    this.camunda7ClientProperties = camunda7ClientProperties;
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

  @GetMapping("/tasks/{id}")
  public UserTaskDto task(@PathVariable("id") Long id) {
    return fromTask(selectionService.getTask(id));
  }

  @PutMapping("/tasks/{id}")
  public UserTaskDto complete(
      @PathVariable("id") Long id, @RequestBody ProcessInstanceMigrationVariables body) {
    return fromTask(selectionService.complete(id, body));
  }

  @GetMapping("/links")
  public Map<String, Object> links() {
    Map<String, Object> links = new HashMap<>();
    links.put("C8", operateBaseUrl);
    links.put("C7", camunda7ClientProperties.getBaseUrl());
    return links;
  }

  private UserTaskDto fromTask(UserTask task) {
    UserTaskDto userTaskDto = new UserTaskDto();
    userTaskDto.setProcessInstanceKey(task.getProcessInstanceKey());
    userTaskDto.setName(task.getName());
    userTaskDto.setKey(task.getKey());
    userTaskDto.setState(task.getState());
    userTaskDto.setType(task.getType());
    userTaskDto.setData(taskMappingService.crateDto(task.getType(), task.getData()));
    return userTaskDto;
  }
}
