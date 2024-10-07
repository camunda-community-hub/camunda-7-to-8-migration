package org.camunda.community.migration.detector.example;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchIgnore;
import com.tngtech.archunit.junit.ArchTest;
import org.camunda.community.migration.detector.rules.Camunda7MigrationRules;

// remove the @ArchIgnore if you want to check if this test is really working
@ArchIgnore
@AnalyzeClasses(packages = "org.camunda.community.migration.detector.example")
class SimpleDetectionTest {

  @ArchTest
  public void testNoTaskListeners(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoImplementationOfCamunda7Interfaces().check(classes);
  }

  @ArchTest
  public void testNoExecutionListeners(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoImplementationOfCamunda7Interfaces().check(classes);
  }

  @ArchTest
  public void testNoJavaDelegates(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoImplementationOfCamunda7Interfaces().check(classes);
  }

  @ArchTest
  public void testNoSpringEventTaskListeners(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoSpringBootEvents().check(classes);
  }

  @ArchTest
  public void testNoSpringEventExecutionListeners(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoSpringBootEvents().check(classes);
  }

  @ArchTest
  public void testNoSpringEventHistoryListeners(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoSpringBootEvents().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfRuntimeService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfCamunda7Api().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfRepositoryService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfCamunda7Api().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfTaskService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfCamunda7Api().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfIdentityService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfCamunda7Api().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfFilterService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfCamunda7Api().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfDecisionService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfCamunda7Api().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfExternalTaskService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfCamunda7Api().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfManagementService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfCamunda7Api().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfCaseService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfCamunda7Api().check(classes);
  }
}
