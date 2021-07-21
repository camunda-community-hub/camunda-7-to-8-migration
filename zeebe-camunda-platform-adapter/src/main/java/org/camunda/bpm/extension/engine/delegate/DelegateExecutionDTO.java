package org.camunda.bpm.extension.engine.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;

/**
 * An implementation of {@link DelegateExecution} that copies all data from a
 * given execution object into local fields and can therefore safely be passed
 * as a Local DTO to another thread.
 * 
 * Variables can be set and modified but will not be saved back into the engine.
 *
 * @author Falko Menge (Camunda)
 */
public class DelegateExecutionDTO extends AbstractDelegateExecution implements DelegateExecution {

  private static final long serialVersionUID = 1L;

  protected final String businessKey;
  protected final String id;
  protected final String processInstanceId;
  protected final String processDefinitionId;
  protected final String currentActivityId;
  protected final String activityInstanceId;
  protected final String tenantId;
  protected final String parentId;
  protected final String parentActivityInstanceId;
  protected final String eventName;
  protected final String currentActivityName;
  protected final String currentTransitionId;
  protected final boolean canceled;

  public DelegateExecutionDTO(final DelegateExecution execution) {
    super(execution.getVariables());
    businessKey = execution.getBusinessKey();
    id = execution.getId();
    processInstanceId = execution.getProcessInstanceId();
    processDefinitionId = execution.getProcessDefinitionId();
    currentActivityId = execution.getCurrentActivityId();
    activityInstanceId = execution.getActivityInstanceId();
    tenantId = execution.getTenantId();
    parentId = execution.getParentId();
    parentActivityInstanceId = getParentActivityInstanceId();
    eventName = execution.getEventName();
    currentActivityName = execution.getCurrentActivityName();
    currentTransitionId = execution.getCurrentTransitionId();
    canceled = execution.isCanceled();
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getProcessBusinessKey() {
    return businessKey;
  }

  @Override
  public String getProcessInstanceId() {
    return processInstanceId;
  }

  @Override
  public String getProcessDefinitionId() {
    return processDefinitionId;
  }

  @Override
  public String getCurrentActivityId() {
    return currentActivityId;
  }

  @Override
  public String getActivityInstanceId() {
    return activityInstanceId;
  }

  @Override
  public String getTenantId() {
    return tenantId;
  }

  @Override
  public String getParentId() {
    return parentId;
  }

  @Override
  public String getParentActivityInstanceId() {
    return parentActivityInstanceId;
  }

  @Override
  public String getEventName() {
    return eventName;
  }

  @Override
  public String getCurrentActivityName() {
    return currentActivityName;
  }

  @Override
  public String getCurrentTransitionId() {
    return currentTransitionId;
  }

  @Override
  public boolean isCanceled() {
    return canceled;
  }

}