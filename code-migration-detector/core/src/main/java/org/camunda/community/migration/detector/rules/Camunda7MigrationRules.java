package org.camunda.community.migration.detector.rules;

import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.*;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.*;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;

/** Specific rules for checks of Camunda 7. */
public class Camunda7MigrationRules {

  public static ArchRule ensureNoTaskListener() {
    return ArchRuleDefinition.noClasses().should().implement(TaskListener.class);
  }

  public static ArchRule ensureNoExecutionListener() {
    return ArchRuleDefinition.noClasses().should().implement(ExecutionListener.class);
  }

  public static ArchRule ensureNoJavaDelegate() {
    return ArchRuleDefinition.noClasses().should().implement(JavaDelegate.class);
  }

  public static ArchRule ensureNoSpringEventTaskListeners() {
    return ArchRuleDefinition.noClasses()
        .should(new HasMethod(new IsSpringListener<>(DelegateTask.class)));
  }

  public static ArchRule ensureNoSpringEventExecutionListeners() {
    return ArchRuleDefinition.noClasses()
        .should(new HasMethod(new IsSpringListener<>(DelegateExecution.class)));
  }

  public static ArchRule ensureNoSpringEventHistoryEventListeners() {
    return ArchRuleDefinition.noClasses()
        .should(new HasMethod(new IsSpringListener<>(HistoryEvent.class)));
  }

  public static ArchRule ensureNoInvocationOfRepositoryService() {
    return ArchRuleDefinition.noClasses()
        .should()
        .callMethodWhere(target(owner(assignableTo(RepositoryService.class))));
  }

  public static ArchRule ensureNoInvocationOfRuntimeService() {
    return ArchRuleDefinition.noClasses()
        .should()
        .callMethodWhere(target(owner(assignableTo(RuntimeService.class))));
  }

  public static ArchRule ensureNoInvocationOfTaskService() {
    return ArchRuleDefinition.noClasses()
        .should()
        .callMethodWhere(target(owner(assignableTo(TaskService.class))));
  }

  public static ArchRule ensureNoInvocationOfIdentityService() {
    return ArchRuleDefinition.noClasses()
        .should()
        .callMethodWhere(target(owner(assignableTo(IdentityService.class))));
  }

  public static ArchRule ensureNoInvocationOfFormService() {
    return ArchRuleDefinition.noClasses()
        .should()
        .callMethodWhere(target(owner(assignableTo(FormService.class))));
  }

  public static ArchRule ensureNoInvocationOfHistoryService() {
    return ArchRuleDefinition.noClasses()
        .should()
        .callMethodWhere(target(owner(assignableTo(HistoryService.class))));
  }

  public static ArchRule ensureNoInvocationOfManagementService() {
    return ArchRuleDefinition.noClasses()
        .should()
        .callMethodWhere(target(owner(assignableTo(ManagementService.class))));
  }

  public static ArchRule ensureNoInvocationOfFilterService() {
    return ArchRuleDefinition.noClasses()
        .should()
        .callMethodWhere(target(owner(assignableTo(FilterService.class))));
  }

  public static ArchRule ensureNoInvocationOfExternalTaskService() {
    return ArchRuleDefinition.noClasses()
        .should()
        .callMethodWhere(target(owner(assignableTo(ExternalTaskService.class))));
  }

  public static ArchRule ensureNoInvocationOfDecisionService() {
    return ArchRuleDefinition.noClasses()
        .should()
        .callMethodWhere(target(owner(assignableTo(DecisionService.class))));
  }

  public static ArchRule ensureNoInvocationOfCaseService() {
    return ArchRuleDefinition.noClasses()
        .should()
        .callMethodWhere(target(owner(assignableTo(CaseService.class))));
  }
}
