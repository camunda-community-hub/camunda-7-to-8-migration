package org.camunda.community.migration.execution;

import io.camunda.zeebe.client.api.response.ActivatedJob;

/**
 * DelegateExecution implementation that can be initialized with an {@link ActivatedJob} and
 * provides all methods required for executing a JavaDelegate as part of an job worker.
 *
 * @author Falko Menge (Camunda)
 */
public class ZeebeJobDelegateExecution extends AbstractDelegateExecution {

  private static final long serialVersionUID = 1L;

  private ActivatedJob job;

  public ZeebeJobDelegateExecution(ActivatedJob job) {
    super(job.getVariablesAsMap());
    this.job = job;
  }

  @Override
  public String getProcessInstanceId() {
    return String.valueOf(job.getProcessInstanceKey());
  }

  @Override
  public String getProcessDefinitionId() {
    return String.valueOf(job.getProcessDefinitionKey());
  }

  @Override
  public String getCurrentActivityId() {
    return job.getElementId();
  }

  @Override
  public String getActivityInstanceId() {
    return String.valueOf(job.getElementInstanceKey());
  }

  @Override
  public String getTenantId() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getId() {
    return getProcessInstanceId();
  }

}
