package org.camunda.community.migration.detector.example;

import org.camunda.bpm.engine.*;
import org.springframework.stereotype.Component;

@Component
public class SimpleJavaApiCallingService {

  private final RuntimeService runtimeService;
  private final CaseService caseService;
  private final DecisionService decisionService;
  private final ManagementService managementService;
  private final IdentityService identityService;
  private final FormService formService;
  private final ExternalTaskService externalTaskService;
  private final TaskService taskService;
  private final RepositoryService repositoryService;

  private final FilterService filterService;

  public SimpleJavaApiCallingService(
      RuntimeService runtimeService,
      CaseService caseService,
      DecisionService decisionService,
      ManagementService managementService,
      IdentityService identityService,
      FormService formService,
      ExternalTaskService externalTaskService,
      TaskService taskService,
      RepositoryService repositoryService,
      FilterService filterService) {
    this.runtimeService = runtimeService;
    this.caseService = caseService;
    this.decisionService = decisionService;
    this.managementService = managementService;
    this.identityService = identityService;
    this.formService = formService;
    this.externalTaskService = externalTaskService;
    this.taskService = taskService;
    this.repositoryService = repositoryService;
    this.filterService = filterService;
  }

  public void doExecutionQuery() {
    this.runtimeService.createExecutionQuery().list();
  }

  public void doTaskQuery() {
    this.taskService.createTaskQuery().list();
  }

  public void doJobQuery() {
    this.managementService.createJobQuery().list();
  }

  public void doFormQuery() {
    this.formService.getTaskFormKey("1", "task-def");
  }

  public void closeCase() {
    this.caseService.closeCaseInstance("12");
  }

  public void checkPassword(String password) {
    this.identityService.checkPassword("1212", password);
  }

  public void getExtTasks() {
    this.externalTaskService.createExternalTaskQuery().list();
  }

  public void decide() {
    this.decisionService.evaluateDecisionById("");
  }

  public void getFilter() {
    this.filterService.getFilter("1231");
  }

  public void getDeployments() {
    this.repositoryService.createDeploymentQuery().list();
  }
}
