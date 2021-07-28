package org.camunda.bpm.extension.engine.delegate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.core.variable.CoreVariableInstance;
import org.camunda.bpm.engine.impl.core.variable.event.VariableEvent;
import org.camunda.bpm.engine.impl.core.variable.scope.AbstractVariableScope;
import org.camunda.bpm.engine.impl.javax.el.ELContext;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowElement;

/**
 * Abstract base class for implementing a {@link DelegateExecution} for
 * executing a {@link JavaDelegate} without being forced to implement all
 * methods provided, which makes the implementation more robust to future
 * changes.
 * 
 * Variables can be set and modified but will not be saved back into the engine.
 * 
 * @author Falko Menge (Camunda)
 */
public class AbstractDelegateExecution extends SimpleVariableScope implements DelegateExecution {

  private static final long serialVersionUID = 1L;

  public AbstractDelegateExecution() {
    super();
  }

  public AbstractDelegateExecution(Map<String, ? extends Object> variables) {
    super(variables);
  }

  @Override
  public String getId() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getProcessInstanceId() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getProcessDefinitionId() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getCurrentActivityId() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getActivityInstanceId() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getTenantId() {
    throw new UnsupportedOperationException();
  }
  @Override
  public String getCurrentActivityName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public FlowElement getBpmnModelElementInstance() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BpmnModelInstance getBpmnModelInstance() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ProcessEngineServices getProcessEngineServices() {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 7.10.0
   */
//  @Override
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

  /**
   * @since 7.8.0
   */
//  @Override
  public Incident createIncident(String incidentType, String configuration) {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 7.8.0
   */
//  @Override
  public Incident createIncident(String incidentType, String configuration, String message) {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 7.8.0
   */
//  @Override
  public void resolveIncident(String incidentId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getBusinessKey() {
    return getProcessBusinessKey();
  }

  @Override
  public String getProcessBusinessKey() {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 7.10.0
   */
//  @Override
  public void setProcessBusinessKey(String businessKey) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setVariable(String variableName, Object value, String activityId) {
    throw new UnsupportedOperationException();
  }

  // disable public methods inherited from AbstractVariableScope
  // that don't belong to DelegateExecution

  /**
   * @throws UnsupportedOperationException
   */
  @Deprecated
  @Override
  public void collectVariables(VariableMapImpl resultVariables, Collection<String> variableNames, boolean isLocal,
      boolean deserializeValues) {
    super.collectVariables(resultVariables, variableNames, isLocal, deserializeValues);
  }
  
  /**
   * @throws UnsupportedOperationException
   */
  @Deprecated
  @Override
  public void dispatchEvent(VariableEvent variableEvent) {
    super.dispatchEvent(variableEvent);
  }
  
  /**
   * @throws UnsupportedOperationException
   */
  @Deprecated
  @Override
  public ELContext getCachedElContext() {
    return super.getCachedElContext();
  }
  
  /**
   * @throws UnsupportedOperationException
   */
  @Deprecated
  @Override
  public AbstractVariableScope getParentVariableScope() {
    return super.getParentVariableScope();
  }
  
  /**
   * @throws UnsupportedOperationException
   */
  @Deprecated
  @Override
  public Object getVariable(String variableName, boolean deserializeObjectValue) {
    return super.getVariable(variableName, deserializeObjectValue);
  }
  
  /**
   * @throws UnsupportedOperationException
   */
  @Deprecated
  @Override
  public CoreVariableInstance getVariableInstance(String variableName) {
    return super.getVariableInstance(variableName);
  }
  
  /**
   * @throws UnsupportedOperationException
   */
  @Deprecated
  @Override
  public CoreVariableInstance getVariableInstanceLocal(String name) {
    return super.getVariableInstanceLocal(name);
  }
  
  /**
   * @throws UnsupportedOperationException
   */
  @Deprecated
  @Override
  public List<CoreVariableInstance> getVariableInstancesLocal() {
    return super.getVariableInstancesLocal();
  }
  
  /**
   * @throws UnsupportedOperationException
   */
  @Deprecated
  @Override
  public Object getVariableLocal(String variableName, boolean deserializeObjectValue) {
    return super.getVariableLocal(variableName, deserializeObjectValue);
  }
  
  /**
   * @throws UnsupportedOperationException
   */
  @Deprecated
  @Override
  public void setCachedElContext(ELContext cachedElContext) {
    super.setCachedElContext(cachedElContext);
  }
  
  /**
   * @throws UnsupportedOperationException
   */
  @Deprecated
  @Override
  public void setVariableLocal(String variableName, TypedValue value, AbstractVariableScope sourceActivityExecution) {
    super.setVariableLocal(variableName, value, sourceActivityExecution);
  }
  
}