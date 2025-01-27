package org.camunda.community.migration.processInstance.migrator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus.Completed;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus.Created;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus.Extracted;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus.Failed;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus.Started;

public interface ProcessInstanceMigrator {
  ProcessInstanceMigrationOrder prepareMigration(Camunda7ProcessInstance processInstance);

  ProcessInstanceMigrationStatus checkMigrationStatus(ProcessInstanceMigrationOrder order);

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "status")
  @JsonSubTypes({
    @Type(value = Created.class, name = "created"),
    @Type(value = Failed.class, name = "failed"),
    @Type(value = Extracted.class, name = "extracted"),
    @Type(value = Started.class, name = "started"),
    @Type(value = Completed.class, name = "completed")
  })
  sealed interface ProcessInstanceMigrationStatus {
    /** The process instance migration is created, just an empty order */
    record Created() implements ProcessInstanceMigrationStatus {}

    /**
     * The process instance migration has failed
     *
     * @param messages the messages to detect the issue
     */
    record Failed(List<String> messages) implements ProcessInstanceMigrationStatus {}

    /**
     * The process instance migration has extracted the data and therefore interrupted the process
     * instance execution
     */
    record Extracted() implements ProcessInstanceMigrationStatus {}

    /**
     * The process instance migration is started, the instance has been created in camunda 8 but is
     * not yet in the correct state
     *
     * @param processInstanceKey the key of the process instance in camunda 8
     */
    record Started(Long processInstanceKey) implements ProcessInstanceMigrationStatus {}

    /**
     * The process instance migration is complete, the instance has the correct state in camunda 8
     *
     * @param processInstanceKey the key of the process instance in camunda 8
     */
    record Completed(Long processInstanceKey) implements ProcessInstanceMigrationStatus {}
  }

  record Camunda7ProcessInstance(String processInstanceId) {}

  record Camunda8ProcessInstance(Long processInstanceId) {}

  record ProcessInstanceMigrationOrder(String reference) {}
}
