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
    Camunda7MigrationRules.ensureNoTaskListener().check(classes);
  }

  @ArchTest
  public void testNoExecutionListeners(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoExecutionListener().check(classes);
  }

  @ArchTest
  public void testNoJavaDelegates(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoJavaDelegate().check(classes);
  }

  @ArchTest
  public void testNoSpringEventTaskListeners(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoSpringEventTaskListeners().check(classes);
  }

  @ArchTest
  public void testNoSpringEventExecutionListeners(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoSpringEventExecutionListeners().check(classes);
  }

  @ArchTest
  public void testNoSpringEventHistoryListeners(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoSpringEventHistoryEventListeners().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfRuntimeService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfRuntimeService().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfRepositoryService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfRepositoryService().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfTaskService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfTaskService().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfIdentityService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfIdentityService().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfFilterService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfFilterService().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfDecisionService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfDecisionService().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfExternalTaskService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfExternalTaskService().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfManagementService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfManagementService().check(classes);
  }

  @ArchTest
  public void testNoInvocationOfCaseService(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfCaseService().check(classes);
  }
}
