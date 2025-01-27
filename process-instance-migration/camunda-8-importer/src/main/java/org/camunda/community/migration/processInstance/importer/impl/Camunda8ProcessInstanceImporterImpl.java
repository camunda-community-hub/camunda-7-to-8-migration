package org.camunda.community.migration.processInstance.importer.impl;

import static java.util.Optional.*;
import static org.camunda.community.migration.processInstance.importer.InitialVariableContext.*;

import com.fasterxml.jackson.databind.JsonNode;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep3;
import io.camunda.zeebe.client.api.command.ModifyProcessInstanceCommandStep1;
import io.camunda.zeebe.client.api.command.ModifyProcessInstanceCommandStep1.ModifyProcessInstanceCommandStep2;
import io.camunda.zeebe.client.api.command.ModifyProcessInstanceCommandStep1.ModifyProcessInstanceCommandStep3;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityContainerData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.VariableScopeData;
import org.camunda.community.migration.processInstance.importer.Camunda8ProcessInstanceImporter;
import org.camunda.community.migration.processInstance.importer.ProcessDefinitionMappingRules;
import org.camunda.community.migration.processInstance.importer.impl.Camunda8ProcessInstanceImporterImpl.ActivateInstructions.VariableInstructions;

public class Camunda8ProcessInstanceImporterImpl implements Camunda8ProcessInstanceImporter {
  private final ZeebeClient zeebeClient;
  private final ProcessDefinitionMappingRules mappingRules;

  public Camunda8ProcessInstanceImporterImpl(
      ZeebeClient zeebeClient, ProcessDefinitionMappingRules mappingRules) {
    this.zeebeClient = zeebeClient;
    this.mappingRules = mappingRules;
  }

  @Override
  public Camunda8ProcessInstance createProcessInstance(ProcessInstanceData processInstanceData) {
    String processDefinitionKey = processInstanceData.getProcessDefinitionKey();
    CreateProcessInstanceCommandStep3 createProcessInstanceCommandStep3 =
        mappingRules
            .checkProcessDefinition(processDefinitionKey)
            .map(
                mappedProcessDefinition ->
                    zeebeClient
                        .newCreateInstanceCommand()
                        .processDefinitionKey(mappedProcessDefinition.processDefinitionKey()))
            .orElseGet(
                () ->
                    zeebeClient
                        .newCreateInstanceCommand()
                        .bpmnProcessId(processInstanceData.getBpmnProcessId())
                        .latestVersion());
    Map<String, Object> variables = new HashMap<>();
    if (processInstanceData.getVariables() != null) {
      variables.putAll(processInstanceData.getVariables());
    }
    variables.put(CAMUNDA_7_PROCESS_INSTANCE_ID_VARIABLE, processInstanceData.getId());
    ProcessInstanceEvent processInstanceEvent =
        createProcessInstanceCommandStep3.variables(variables).send().join();

    return new Camunda8ProcessInstance(processInstanceEvent.getProcessInstanceKey());
  }

  @Override
  public ProcessInstanceDataForPostProcessing updateProcessInstanceToStatus(
      Camunda8ProcessInstance camunda8ProcessInstance,
      ProcessInstanceData data,
      Camunda8ProcessInstanceState stateToTerminate) {
    List<ActivityNodeData> nodesToPostProcess = new ArrayList<>();
    ModifyProcessInstanceCommandStep1 command =
        zeebeClient.newModifyProcessInstanceCommand(camunda8ProcessInstance.processInstanceKey());
    stateToTerminate.elementInstanceKeys().forEach(key -> command.terminateElement(key).and());
    ModifyProcessInstanceCommandStep2 modifyProcessInstanceCommandStep2 =
        decorateWithActivateInstructions(command, data, nodesToPostProcess);
    modifyProcessInstanceCommandStep2.send().join();
    return new ProcessInstanceDataForPostProcessing(nodesToPostProcess);
  }

  private ModifyProcessInstanceCommandStep2 decorateWithActivateInstructions(
      ModifyProcessInstanceCommandStep1 command,
      ActivityContainerData data,
      List<ActivityNodeData> nodesToPostProcess) {
    List<ActivateInstructions> instructions = extractActivateInstructions(data, new ArrayList<>());
    instructions.forEach(
        activateInstructions -> {
          ModifyProcessInstanceCommandStep3 innerCommand =
              command.activateElement(
                  activateInstructions.elementId(),
                  ofNullable(activateInstructions.ancestorElementInstanceKey()).orElse(-1L));
          activateInstructions
              .variableInstructions()
              .forEach(
                  variableInstruction -> {
                    innerCommand.withVariables(
                        variableInstruction.variables(), variableInstruction.scopeId());
                  });
          innerCommand.and();
        });
    return (ModifyProcessInstanceCommandStep2) command;
  }

  private List<ActivateInstructions> extractActivateInstructions(
      ActivityContainerData data, List<VariableInstructions> ancestorVariableInstructions) {
    List<ActivateInstructions> activateInstructions = new ArrayList<>();
    data.getActivities()
        .forEach(
            (id, activity) -> {
              List<VariableInstructions> variableInstructions =
                  new ArrayList<>(ancestorVariableInstructions);
              if (activity instanceof VariableScopeData variableScopeData
                  && variableScopeData.getVariables() != null) {
                variableInstructions.add(
                    new VariableInstructions(variableScopeData.getVariables(), id));
              }
              if (activity instanceof ActivityContainerData activityContainerData) {
                activateInstructions.addAll(
                    extractActivateInstructions(activityContainerData, variableInstructions));
              } else {
                activateInstructions.add(new ActivateInstructions(id, null, variableInstructions));
              }
            });
    return activateInstructions;
  }

  record ActivateInstructions(
      String elementId,
      Long ancestorElementInstanceKey,
      List<VariableInstructions> variableInstructions) {
    record VariableInstructions(Map<String, JsonNode> variables, String scopeId) {}
  }
}
