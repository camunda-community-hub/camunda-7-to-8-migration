package org.camunda.community.migration.execution;

import java.util.Map;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowElement;

/**
 * Abstract DelegateExecution that can be used for as base for DelegateExecution
 * implementations for executing a JavaDelegate.
 * 
 * It contains all methods that have to be implemented with additional REST API
 * calls or wrapper classes that emulate the Camunda Java API. 
 *
 * @author Falko Menge (Camunda)
 */
public abstract class AbstractDelegateExecution extends SimpleVariableScope implements DelegateExecution {

  private static final long serialVersionUID = 1L;
  public static final String VARIABLE_NAME_BUSINESS_KEY = "businessKey";

  protected RepositoryService repositoryService;

  public AbstractDelegateExecution() {
    super();
  }

  public AbstractDelegateExecution(Map<String, ? extends Object> variables) {
    super(variables);
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
    throw new UnsupportedOperationException("This DelegateExecution implementation is not meant to be used for ExecutionListeners");
  }

  @Override
  public String getCurrentTransitionId() {
    throw new UnsupportedOperationException("This DelegateExecution implementation is not meant to be used for ExecutionListeners");
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

}