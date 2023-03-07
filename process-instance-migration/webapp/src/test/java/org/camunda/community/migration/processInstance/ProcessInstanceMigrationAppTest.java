package org.camunda.community.migration.processInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.camunda.community.migration.processInstance.TestUtil.*;
import static org.mockito.Mockito.*;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.auth.Authentication;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.model.ProcessDefinition;
import io.camunda.process.test.api.CamundaSpringProcessTest;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData;
import org.camunda.community.migration.processInstance.dto.Camunda8ProcessDefinitionData;
import org.camunda.community.migration.processInstance.service.Camunda7Service;
import org.camunda.community.migration.processInstance.service.Camunda8Service;
import org.camunda.community.migration.processInstance.service.ProcessDefinitionMigrationHintService;
import org.camunda.community.migration.processInstance.service.ProcessInstanceMigrationHintService;
import org.camunda.community.migration.processInstance.variables.ProcessInstanceMigrationVariables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@CamundaSpringProcessTest
@ExtendWith(ProcessEngineExtension.class)
public class ProcessInstanceMigrationAppTest {
  @Autowired Camunda8Service camunda8Service;
  @MockBean Authentication authentication;
  @MockBean CamundaOperateClient operateClient;
  @Autowired Camunda7Service camunda7Service;
  @Autowired JsonMapper jsonMapper;
  @Autowired ProcessDefinitionMigrationHintService processDefinitionMigrationHintService;
  @Autowired ProcessInstanceMigrationHintService processInstanceMigrationHintService;
  @Autowired ZeebeClient zeebeClient;

  @BeforeEach
  void setup() throws OperateException {
    TestUtil.zeebeClient = zeebeClient;
    when(operateClient.searchProcessDefinitions(any()))
        .thenReturn(Collections.singletonList(new ProcessDefinition()));
  }

  @Test
  void shouldEvaluateProcessDefinitionRules() throws OperateException {
    BpmnModelInstance modelInstance =
        Bpmn.createExecutableProcess()
            .startEvent()
            .messageEventDefinition()
            .message("start-test")
            .messageEventDefinitionDone()
            .endEvent()
            .done();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Bpmn.writeModelToStream(out, modelInstance);
    when(operateClient.getProcessDefinitionXml(any())).thenReturn(out.toString());
    long processDefinitionKey = deployProcessToZeebe(modelInstance).getProcessDefinitionKey();
    Camunda8ProcessDefinitionData data =
        camunda8Service.getProcessDefinitionData(processDefinitionKey);
    List<String> migrationHints = processDefinitionMigrationHintService.getMigrationHints(data);
    assertThat(migrationHints).contains("A process definition must contain a None Start Event");
  }

  @Test
  void shouldEvaluateProcessInstanceRules() {
    List<String> expectedHints =
        Arrays.asList(
            "Process instance contains a multi-instance. This cannot be migrated",
            "The process instance contains variables that are not in process scope");
    org.camunda.bpm.model.bpmn.BpmnModelInstance modelInstance =
        org.camunda.bpm.model.bpmn.Bpmn.createExecutableProcess("test")
            .camundaHistoryTimeToLive(180)
            .startEvent()
            .userTask()
            .multiInstance()
            .parallel()
            .cardinality("5")
            .multiInstanceDone()
            .endEvent()
            .done();
    repositoryService().createDeployment().addModelInstance("test.bpmn", modelInstance).deploy();
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("test");
    Camunda7ProcessInstanceData data = camunda7Service.getProcessData(processInstance.getId());
    List<String> migrationHints = processInstanceMigrationHintService.getMigrationHints(data);
    assertThat(migrationHints).containsAll(expectedHints);
  }

  @Test
  void shouldMapOnlyNonNullFields() {
    ProcessInstanceMigrationVariables variables = new ProcessInstanceMigrationVariables();
    String json = jsonMapper.toJson(variables);
    Map<String, Object> parsed = jsonMapper.fromJsonAsMap(json);
    assertThat(parsed).isEmpty();
  }
}
