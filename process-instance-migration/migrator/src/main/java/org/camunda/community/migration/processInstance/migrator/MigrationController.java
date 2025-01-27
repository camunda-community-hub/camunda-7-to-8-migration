package org.camunda.community.migration.processInstance.migrator;

import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.Camunda7ProcessInstance;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationOrder;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MigrationController {
  private final ProcessInstanceMigrator migrator;

  @Autowired
  public MigrationController(ProcessInstanceMigrator migrator) {
    this.migrator = migrator;
  }

  @PostMapping("/{processInstanceId}")
  public ProcessInstanceMigrationOrder migrate(
      @PathVariable("processInstanceId") String processInstanceId) {
    return migrator.prepareMigration(new Camunda7ProcessInstance(processInstanceId));
  }

  @GetMapping("{orderId}")
  public ProcessInstanceMigrationStatus getStatus(@PathVariable("orderId") String orderId) {
    return migrator.checkMigrationStatus(new ProcessInstanceMigrationOrder(orderId));
  }
}
