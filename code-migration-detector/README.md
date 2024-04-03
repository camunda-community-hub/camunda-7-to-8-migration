# Code Migration Detector

Tool for static analysis of Camunda 7 projects, that runs through the
application and searches for usages of Java API and classifies each usage by how
complicated it would be to migrate to Camunda 8. This tool also provides
hints/suggestions for migration (if it is possible).

This tool uses the [ArchUnit framework](https://www.archunit.org/) for static
code analysis.

## Usage of the library

Add the library to your test class path and include the ArchUnit library too:

```xml

<dependency>
    <groupId>org.camunda.community.migration</groupId>
    <artifactId>code-migration-detector-test</artifactId>
    <version>${project.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.tngtech.archunit</groupId>
    <artifactId>archunit-junit5</artifactId>
    <version>${archunit.version}</version>
    <scope>test</scope>
</dependency>
```

The central class with the Arch Unit rules is `Camunda7MigrationRules`. Static
methods of this class allows testing different aspects.

The test will then look like this:

```java
@AnalyzeClasses(packages = "org.camunda.community.migration.detector.example")
public class SimpleDetectionTest {

  @ArchTest
    public void testNoTaskListeners(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoTaskListener().check(classes);
  }

}
```

Please check the examples project for more details. The `example` folder
contains a small example, how the test can be used. To run it locally, please
make sure to active Maven Profile `examples` (by activating it in the IDE or by
passing `-Pexamples` to your Maven build command). Currently, the examples
contain one ArchUnit test `SimpleDetectionTest` with all tests disabled by
`@ArchIgnore`. Remove or comment out the annotation, and the run will break the
build.

You can copy this test class into your project to check for all crucial points:

```java
@AnalyzeClasses(packages = "your_package")
public class MigrationPreparationTest {

  @ArchTest
  public void testNoExecutionListeners(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoTaskListener().check(classes);
  }

  @ArchTest
  public void testNoTaskListeners(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoExecutionListener().check(classes);
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
```
