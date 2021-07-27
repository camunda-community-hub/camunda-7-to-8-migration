package com.camunda.consulting;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowElement;

import io.camunda.zeebe.client.api.response.ActivatedJob;

/**
 * @author Daniel Meyer (Camunda)
 * @author Falko Menge (Camunda)
 */
public class JobDelegateExecutionAdapter implements DelegateExecution {

	private ActivatedJob job;
	private Map<String, Object> payloadMap;

	public JobDelegateExecutionAdapter(ActivatedJob job) {
		this.job = job;
		payloadMap = job.getVariablesAsMap();
	}
	
	public Map<String, Object> getPayloadMap() {
		return payloadMap;
	}

	@UnsupportedOperation
	@Override
	public String getId() {
		throw new UnsupportedOperationException();
	}

    @UnsupportedOperation
	@Override
	public String getEventName() {
		throw new UnsupportedOperationException();
	}

    @UnsupportedOperation
	@Override
	public String getBusinessKey() {
		throw new UnsupportedOperationException();
	}

    @UnsupportedOperation
	@Override
	public String getVariableScopeKey() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getVariables() {
		return new HashMap<>(payloadMap);
	}

	@Override
	public VariableMap getVariablesTyped() {
		return Variables.fromMap(payloadMap);
	}

	@Override
	public VariableMap getVariablesTyped(boolean deserializeValues) {
		return Variables.fromMap(payloadMap);
	}

	@Override
	public Map<String, Object> getVariablesLocal() {
		return new HashMap<>(payloadMap);
	}

	@Override
	public VariableMap getVariablesLocalTyped() {
		return Variables.fromMap(payloadMap);
	}

	@Override
	public VariableMap getVariablesLocalTyped(boolean deserializeValues) {
		return Variables.fromMap(payloadMap);
	}

	@Override
	public Object getVariable(String variableName) {
		return payloadMap.get(variableName);
	}

	@Override
	public Object getVariableLocal(String variableName) {
		return payloadMap.get(variableName);
	}

	@Override
	public <T extends TypedValue> T getVariableTyped(String variableName) {
		return getVariablesTyped().getValueTyped(variableName);
	}

	@Override
	public <T extends TypedValue> T getVariableTyped(String variableName, boolean deserializeValue) {
		return getVariablesTyped().getValueTyped(variableName);
	}

	@Override
	public <T extends TypedValue> T getVariableLocalTyped(String variableName) {
		return getVariablesTyped().getValueTyped(variableName);
	}

	@Override
	public <T extends TypedValue> T getVariableLocalTyped(String variableName, boolean deserializeValue) {
		return getVariablesTyped().getValueTyped(variableName);
	}

	@Override
	public Set<String> getVariableNames() {
		return payloadMap.keySet();
	}

	@Override
	public Set<String> getVariableNamesLocal() {
		return payloadMap.keySet();
	}

	@Override
	public void setVariable(String variableName, Object value) {
		payloadMap.put(variableName, value);
	}

	@Override
	public void setVariableLocal(String variableName, Object value) {
		payloadMap.put(variableName, value);
	}

	@Override
	public void setVariables(Map<String, ? extends Object> variables) {
		payloadMap.putAll(variables);
	}

	@Override
	public void setVariablesLocal(Map<String, ? extends Object> variables) {
		payloadMap.putAll(variables);
	}

	@Override
	public boolean hasVariables() {
		return !payloadMap.isEmpty();
	}

	@Override
	public boolean hasVariablesLocal() {
		return !payloadMap.isEmpty();
	}

	@Override
	public boolean hasVariable(String variableName) {
		return payloadMap.containsKey(variableName);
	}

	@Override
	public boolean hasVariableLocal(String variableName) {
		return payloadMap.containsKey(variableName);
	}

	@Override
	public void removeVariable(String variableName) {
		payloadMap.remove(variableName);
	}

	@Override
	public void removeVariableLocal(String variableName) {
		payloadMap.remove(variableName);
	}

	@Override
	public void removeVariables(Collection<String> variableNames) {
		variableNames.stream().forEach(payloadMap::remove);
	}

	@Override
	public void removeVariablesLocal(Collection<String> variableNames) {
		variableNames.stream().forEach(payloadMap::remove);
	}

	@Override
	public void removeVariables() {
		payloadMap.clear();
	}

	@Override
	public void removeVariablesLocal() {
		payloadMap.clear();
	}

    @UnsupportedOperation
	@Override
	public BpmnModelInstance getBpmnModelInstance() {
		throw new UnsupportedOperationException();
	}

    @UnsupportedOperation
	@Override
	public FlowElement getBpmnModelElementInstance() {
		throw new UnsupportedOperationException();
	}

    @UnsupportedOperation
	@Override
	public ProcessEngineServices getProcessEngineServices() {
		throw new UnsupportedOperationException();
	}

    @UnsupportedOperation
	@Override
	public String getProcessInstanceId() {
		throw new UnsupportedOperationException();
	}

    @UnsupportedOperation
	@Override
	public String getProcessBusinessKey() {
		throw new UnsupportedOperationException();
	}

    @UnsupportedOperation
	@Override
	public String getProcessDefinitionId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getParentId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCurrentActivityId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCurrentActivityName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getActivityInstanceId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getParentActivityInstanceId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCurrentTransitionId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public DelegateExecution getProcessInstance() {
		throw new UnsupportedOperationException();
	}

	@Override
	public DelegateExecution getSuperExecution() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCanceled() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTenantId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setVariable(String variableName, Object value, String activityId) {
		throw new UnsupportedOperationException();
	}

//	@Override
	public Incident createIncident(String incidentType, String configuration) {
		throw new UnsupportedOperationException();
	}

//	@Override
	public Incident createIncident(String incidentType, String configuration, String message) {
		throw new UnsupportedOperationException();
	}

//	@Override
	public void resolveIncident(String incidentId) {
		throw new UnsupportedOperationException();
	}

//  @Override
  public ProcessEngine getProcessEngine() {
    throw new UnsupportedOperationException();
  }

//  @Override
  public void setProcessBusinessKey(String businessKey) {
    throw new UnsupportedOperationException();
  }
}
