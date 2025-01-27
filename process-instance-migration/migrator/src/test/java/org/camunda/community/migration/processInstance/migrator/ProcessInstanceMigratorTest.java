package org.camunda.community.migration.processInstance.migrator;

import static org.assertj.core.api.Assertions.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.process.test.api.CamundaProcessTest;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivateJobsResponse;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import java.util.Map;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.community.migration.processInstance.api.ProcessInstanceDataExporter;
import org.camunda.community.migration.processInstance.exporter.Camunda7Exporter;
import org.camunda.community.migration.processInstance.exporter.EmbeddedProcessEngineService;
import org.camunda.community.migration.processInstance.importer.Camunda8ProcessInstanceImporter;
import org.camunda.community.migration.processInstance.importer.Camunda8ProcessInstanceImporter.Camunda8ProcessInstance;
import org.camunda.community.migration.processInstance.importer.impl.Camunda8ProcessInstanceImporterImpl;
import org.camunda.community.migration.processInstance.importer.impl.ProcessDefinitionMappingRulesImpl;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.Camunda7ProcessInstance;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationOrder;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus.Completed;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus.Created;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus.Extracted;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus.Started;
import org.camunda.community.migration.processInstance.migrator.impl.CompleteMigrationWorker;
import org.camunda.community.migration.processInstance.migrator.impl.InMemoryProcessInstanceRepository;
import org.camunda.community.migration.processInstance.migrator.impl.ProcessInstanceManager;
import org.camunda.community.migration.processInstance.migrator.impl.ProcessInstanceMigratorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ProcessEngineExtension.class)
@CamundaProcessTest
public class ProcessInstanceMigratorTest {
  ZeebeClient zeebeClient;

  @Test
  @Deployment(resources = "firstTest.bpmn")
  void shouldFetchCreatedOrder() throws Exception {
    zeebeClient
        .newDeployResourceCommand()
        .addResourceFromClasspath("firstTest-c8.bpmn")
        .send()
        .join();
    ProcessInstance firstTest = runtimeService().startProcessInstanceByKey("firstTest");
    ProcessInstanceDataExporter exporter =
        new Camunda7Exporter(new EmbeddedProcessEngineService(processEngine(), new ObjectMapper()));
    Camunda8ProcessInstanceImporter importer =
        new Camunda8ProcessInstanceImporterImpl(
            zeebeClient, new ProcessDefinitionMappingRulesImpl(Map.of()));
    ProcessInstanceRepository repository = new InMemoryProcessInstanceRepository();
    ProcessInstanceMigratorImpl migrator =
        new ProcessInstanceMigratorImpl(exporter, importer, new ProcessInstanceManager(repository));
    ProcessInstanceMigrationOrder processInstanceMigrationOrder =
        migrator.prepareMigration(new Camunda7ProcessInstance(firstTest.getProcessInstanceId()));
    assertThat(migrator.checkMigrationStatus(processInstanceMigrationOrder))
        .isEqualTo(new Created());
    migrator.updateToExtracted();
    assertThat(migrator.checkMigrationStatus(processInstanceMigrationOrder))
        .isEqualTo(new Extracted());
    migrator.updateToStarted();
    Camunda8ProcessInstance migratedInstance =
        repository.getMigratedInstance(processInstanceMigrationOrder);
    assertThat(migrator.checkMigrationStatus(processInstanceMigrationOrder))
        .isEqualTo(new Started(migratedInstance.processInstanceKey()));
    ActivateJobsResponse jobs =
        zeebeClient
            .newActivateJobsCommand()
            .jobType("completeMigration")
            .maxJobsToActivate(1)
            .send()
            .join();
    assertThat(jobs.getJobs()).hasSize(1);
    ActivatedJob activatedJob = jobs.getJobs().get(0);
    CompleteMigrationWorker completeMigrationWorker = new CompleteMigrationWorker(migrator);
    completeMigrationWorker.handle(zeebeClient, activatedJob);
    assertThat(migrator.checkMigrationStatus(processInstanceMigrationOrder))
        .isEqualTo(new Completed(migratedInstance.processInstanceKey()));
  }
}
