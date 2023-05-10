package org.camunda.community.migration.detector.rules;

import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.*;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.*;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.springframework.context.event.EventListener;

public class Camunda7MigrationRules {

  public static ArchRule ensureNoTaskListener() {
    return ArchRuleDefinition.classes()
        .that()
        .implement(TaskListener.class)
        .should(new ClassNotExistsCondition());
  }

  public static ArchRule ensureNoExecutionListener() {
    return ArchRuleDefinition.classes()
        .that()
        .implement(ExecutionListener.class)
        .should(new ClassNotExistsCondition());
  }

  public static ArchRule ensureNoJavaDelegate() {
    return ArchRuleDefinition.classes()
        .that()
        .implement(JavaDelegate.class)
        .should(new ClassNotExistsCondition());
  }

  public static ArchRule ensureNoSpringEventTaskListeners() {
    return ArchRuleDefinition.classes()
        .that(new IsSpringListener<>(DelegateTask.class))
        .should(new ClassNotExistsCondition());
  }

  public static ArchRule ensureNoSpringEventExecutionListeners() {
    return ArchRuleDefinition.classes()
        .that(new IsSpringListener<>(DelegateExecution.class))
        .should(new ClassNotExistsCondition());
  }

  public static ArchRule ensureNoSpringEventHistoryEventListeners() {
    return ArchRuleDefinition.classes()
        .that(new IsSpringListener<>(HistoryEvent.class))
        .should(new ClassNotExistsCondition());
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

  static class ClassNotExistsCondition extends ArchCondition<JavaClass> {

    public ClassNotExistsCondition() {
      super("not exist");
    }

    @Override
    public void check(JavaClass javaClass, ConditionEvents conditionEvents) {
      conditionEvents.add(new SimpleConditionEvent(javaClass, false, javaClass.getFullName()));
    }
  }

  static class IsSpringListener<T> extends DescribedPredicate<JavaClass> {

    private final Class<T> eventType;

    public IsSpringListener(Class<T> eventType) {
      super("is a Spring Event Listener for " + eventType.getName());
      this.eventType = eventType;
    }

    @Override
    public boolean test(JavaClass javaClass) {
      return javaClass.getMethods().stream()
          .anyMatch(
              (method) ->
                  method.isAnnotatedWith(EventListener.class)
                      && method.getRawParameterTypes().stream()
                          .anyMatch((clazz) -> eventType.isAssignableFrom(clazz.reflect())));
    }
  }
}
