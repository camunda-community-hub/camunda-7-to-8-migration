package org.camunda.community.migration.detector.rules;

import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.*;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.*;
import static org.camunda.community.migration.detector.rules.Camunda7MigrationConditions.*;
import static org.camunda.community.migration.detector.rules.Camunda7MigrationPredicates.*;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;

/** Specific rules for checks of Camunda 7. */
public class Camunda7MigrationRules {

  public static ArchRule ensureNoTaskListener() {
    return ArchRuleDefinition.noClasses().should().implement(TaskListener.class);
  }

  public static ArchRule ensureNoExecutionListener() {
    return ArchRuleDefinition.noClasses().should().implement(ExecutionListener.class);
  }

  public static ArchRule ensureNoProcessEnginePlugin() {
    return ArchRuleDefinition.noClasses().should().implement(ProcessEnginePlugin.class);
  }

  public static ArchRule ensureNoInvocationOfProcessEngine() {
    return ArchRuleDefinition.noClasses()
        .should()
        .callMethodWhere(target(owner(assignableTo(ProcessEngine.class))));
  }

  public static ArchRule ensureNoSpringBootEvents() {
    return ArchRuleDefinition.noMethods()
        .that(isSpringEventListener())
        .should(haveCamundaBpmParameterTypes());
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
