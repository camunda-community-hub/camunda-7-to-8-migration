package org.camunda.community.migration.converter.cli;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.community.migration.converter.cli.mock.App;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(classes = App.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ConvertEngineCommandTest {

  @LocalServerPort int randomServerPort;
  @Autowired ProcessEngine processEngine;

  @AfterEach
  void cleanUp() {
    processEngine
        .getRepositoryService()
        .createDeploymentQuery()
        .list()
        .forEach(
            deployment ->
                processEngine
                    .getRepositoryService()
                    .deleteDeployment(deployment.getId(), true, true, true));
  }

  @Test
  void shouldConvertSingleFileWithMultipleProcesses(@TempDir File tempDir) throws Exception {
    processEngine
        .getRepositoryService()
        .createDeployment()
        .name("test")
        .addClasspathResource("multiple-processes.bpmn")
        .deploy();
    ConvertEngineCommand command = new ConvertEngineCommand();
    command.targetDirectory = tempDir;
    command.url = "http://localhost:" + randomServerPort + "/engine-rest";
    command.call();
    assertThat(tempDir.listFiles()).hasSize(1);
  }

  @Test
  void shouldConvertManyFilesWithSameName(@TempDir File tempDir) throws Exception {
    BpmnModelInstance test1 =
        Bpmn.createProcess("test1")
            .executable()
            .camundaHistoryTimeToLive(180)
            .startEvent("x")
            .endEvent("y")
            .done();
    BpmnModelInstance test2 =
        Bpmn.createProcess("test2")
            .executable()
            .camundaHistoryTimeToLive(180)
            .startEvent("a")
            .endEvent("b")
            .done();
    processEngine
        .getRepositoryService()
        .createDeployment()
        .name("test")
        .addModelInstance("test.bpmn", test2)
        .deploy();
    processEngine
        .getRepositoryService()
        .createDeployment()
        .name("test")
        .addModelInstance("test.bpmn", test1)
        .deploy();
    ConvertEngineCommand command = new ConvertEngineCommand();
    command.targetDirectory = tempDir;
    command.url = "http://localhost:" + randomServerPort + "/engine-rest";
    command.call();
    assertThat(tempDir.listFiles()).hasSize(2);
  }
}
