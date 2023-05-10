package org.camunda.community.migration.adapter.execution;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.VariableScope;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowElement;

/**
 * Abstract DelegateExecution that can be used for as base for DelegateExecution implementations for
 * executing a JavaDelegate.
 *
 * <p>It contains all methods that have to be implemented with additional REST API calls or wrapper
 * classes that emulate the Camunda Java API.
 *
 * @author Falko Menge (Camunda)
 */
public abstract class AbstractDelegateExecution implements DelegateExecution {

  private static final long serialVersionUID = 1L;
  public static final String VARIABLE_NAME_BUSINESS_KEY = "businessKey";

  private final VariableScope variableScope = new SimpleVariableScope();

  protected RepositoryService repositoryService;

  public AbstractDelegateExecution() {
    this(Collections.EMPTY_MAP);
  }

  public AbstractDelegateExecution(Map<String, Object> variables) {
    variableScope.setVariables(variables);
  }

  @Override
  public String getCurrentActivityName() {
    return getBpmnModelElementInstance().getName();
  }

  @Override
  public FlowElement getBpmnModelElementInstance() {
    return getBpmnModelInstance().getModelElementById(getCurrentActivityId());
  }

  @Override
  public BpmnModelInstance getBpmnModelInstance() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ProcessEngineServices getProcessEngineServices() {
    throw new UnsupportedOperationException();
  }

  public ProcessEngine getProcessEngine() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getParentId() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getParentActivityInstanceId() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getEventName() {
    throw new UnsupportedOperationException(
        "This DelegateExecution implementation is not meant to be used for ExecutionListeners");
  }

  @Override
  public String getCurrentTransitionId() {
    throw new UnsupportedOperationException(
        "This DelegateExecution implementation is not meant to be used for ExecutionListeners");
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

  public Incident createIncident(String incidentType, String configuration) {
    throw new UnsupportedOperationException();
  }

  public Incident createIncident(String incidentType, String configuration, String message) {
    throw new UnsupportedOperationException();
  }

  public void resolveIncident(String incidentId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getBusinessKey() {
    return getProcessBusinessKey();
  }

  @Override
  public String getProcessBusinessKey() {
    return (String) getVariable(VARIABLE_NAME_BUSINESS_KEY);
  }

  public void setProcessBusinessKey(String businessKey) {
    setVariable(VARIABLE_NAME_BUSINESS_KEY, businessKey);
  }

  @Override
  public void setVariable(String variableName, Object value, String activityId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getVariableScopeKey() {
    return variableScope.getVariableScopeKey();
  }

  @Override
  public Map<String, Object> getVariables() {
    return variableScope.getVariables();
  }

  @Override
  public VariableMap getVariablesTyped() {
    return variableScope.getVariablesTyped();
  }

  @Override
  public VariableMap getVariablesTyped(boolean b) {
    return variableScope.getVariablesTyped(b);
  }

  @Override
  public Map<String, Object> getVariablesLocal() {
    return variableScope.getVariablesLocal();
  }

  @Override
  public VariableMap getVariablesLocalTyped() {
    return variableScope.getVariablesLocalTyped();
  }

  @Override
  public VariableMap getVariablesLocalTyped(boolean b) {
    return variableScope.getVariablesLocalTyped(b);
  }

  @Override
  public Object getVariable(String s) {
    return variableScope.getVariable(s);
  }

  @Override
  public Object getVariableLocal(String s) {
    return variableScope.getVariableLocal(s);
  }

  @Override
  public <T extends TypedValue> T getVariableTyped(String s) {
    return variableScope.getVariableTyped(s);
  }

  @Override
  public <T extends TypedValue> T getVariableTyped(String s, boolean b) {
    return variableScope.getVariableTyped(s, b);
  }

  @Override
  public <T extends TypedValue> T getVariableLocalTyped(String s) {
    return variableScope.getVariableLocalTyped(s);
  }

  @Override
  public <T extends TypedValue> T getVariableLocalTyped(String s, boolean b) {
    return variableScope.getVariableLocalTyped(s, b);
  }

  @Override
  public Set<String> getVariableNames() {
    return variableScope.getVariableNames();
  }

  @Override
  public Set<String> getVariableNamesLocal() {
    return variableScope.getVariableNamesLocal();
  }

  @Override
  public void setVariable(String s, Object o) {
    variableScope.setVariable(s, o);
  }

  @Override
  public void setVariableLocal(String s, Object o) {
    variableScope.setVariableLocal(s, o);
  }

  @Override
  public void setVariables(Map<String, ?> map) {
    variableScope.setVariables(map);
  }

  @Override
  public void setVariablesLocal(Map<String, ?> map) {
    variableScope.setVariablesLocal(map);
  }

  @Override
  public boolean hasVariables() {
    return variableScope.hasVariables();
  }

  @Override
  public boolean hasVariablesLocal() {
    return variableScope.hasVariablesLocal();
  }

  @Override
  public boolean hasVariable(String s) {
    return variableScope.hasVariable(s);
  }

  @Override
  public boolean hasVariableLocal(String s) {
    return variableScope.hasVariableLocal(s);
  }

  @Override
  public void removeVariable(String s) {
    variableScope.removeVariable(s);
  }

  @Override
  public void removeVariableLocal(String s) {
    variableScope.removeVariableLocal(s);
  }

  @Override
  public void removeVariables(Collection<String> collection) {
    variableScope.removeVariables(collection);
  }

  @Override
  public void removeVariablesLocal(Collection<String> collection) {
    variableScope.removeVariablesLocal(collection);
  }

  @Override
  public void removeVariables() {
    variableScope.removeVariables();
  }

  @Override
  public void removeVariablesLocal() {
    variableScope.removeVariablesLocal();
  }
}
