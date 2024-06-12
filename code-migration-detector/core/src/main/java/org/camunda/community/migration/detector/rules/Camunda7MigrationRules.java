package org.camunda.community.migration.detector.rules;

import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.*;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.*;
import static org.camunda.community.migration.detector.rules.Camunda7MigrationConditions.*;
import static org.camunda.community.migration.detector.rules.Camunda7MigrationPredicates.*;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

/** Specific rules for checks of Camunda 7. */
public class Camunda7MigrationRules {

  public static ArchRule ensureNoImplementationOfCamunda7Interfaces() {
    return ArchRuleDefinition.noClasses().should(implementCamunda7Interface());
  }

  public static ArchRule ensureNoSpringBootEvents() {
    return ArchRuleDefinition.noMethods()
        .that(isSpringEventListener())
        .should(haveCamunda7ParameterTypes());
  }

  public static ArchRule ensureNoInvocationOfCamunda7Api() {
    return ArchRuleDefinition.noClasses()
        .should()
        .callMethodWhere(target(owner(assignableTo(camunda7Api()))));
  }
}
