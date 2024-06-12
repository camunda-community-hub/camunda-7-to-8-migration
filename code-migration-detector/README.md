# Code Migration Detector

Tool for static analysis of Camunda 7 projects, that runs through the
application and searches for usages of Java API and classifies each usage by how
complicated it would be to migrate to Camunda 8. This tool also provides
hints/suggestions for migration (if it is possible).

This tool uses the [ArchUnit framework](https://www.archunit.org/) for static
code analysis.

## Usage of the library

### Unit Tests

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
    public void testCamundaBpmInterfacesImplemented(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoCamundaBpmInterfacesImplemented().check(classes);
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
import org.camunda.community.migration.detector.rules.Camunda7MigrationRules;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

@AnalyzeClasses(packages = "your_package", importOptions = ImportOption.DoNotIncludeTests.class)
public class MigrationPreparationTest {

  @ArchTest
  public void testNoImplementationOfCamunda7Interfaces(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoImplementationOfCamunda7Interfaces().check(classes);
  }

  @ArchTest
  public void testNoSpringBootEvents(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoSpringBootEvents().check(classes);
  }
  @ArchTest
  public void testNoInvocationOfCamunda7Api(JavaClasses classes) {
    Camunda7MigrationRules.ensureNoInvocationOfCamunda7Api().check(classes);
  }
}
```

### Reporting

On top of the unit testing, you can also generate report data that will map all violations to its containing classes and the matched rule.

To do, you can generate a report like that:

```java
CodeMigrationReport report =
    new CodeMigrationReportBuilder(classes)
        .withArchRule(Camunda7MigrationRules.ensureNoImplementationOfCamunda7Interfaces())
        .withArchRule(Camunda7MigrationRules.ensureNoInvocationOfCamunda7Api())
        .withArchRule(Camunda7MigrationRules.ensureNoSpringBootEvents())
        .build();
```

Here, you can add more ArchRules by more invocations of `withArchRule`.

To get a nicely formatted markdown file, you can use the `org.camunda.community.migration.detector.rules.CodeMigrationReportPrinter`:

```java
    StringWriter writer = new StringWriter();
    CodeMigrationReportPrinter.print(writer, report);
```

This will write the report to the supplied `Writer` instance which will look like this:

```markdown
# Migration Report

## Class: `org.camunda.community.migration.detector.rules.test.MyProcessEnginePlugin`
### Rule: no classes should implement camunda 7 interface
* Class &lt;org.camunda.community.migration.detector.rules.test.MyProcessEnginePlugin&gt; does implement &lt;org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin&gt; in (MyProcessEnginePlugin.java:0)
## Class: `org.camunda.community.migration.detector.rules.test.MyProcessEngineServicesBean`
### Rule: no classes should call method where target owner assignable to camunda 7 api
* Method &lt;org.camunda.community.migration.detector.rules.test.MyProcessEngineServicesBean.doSomethingWithTheServices()&gt; calls method &lt;org.camunda.bpm.engine.RepositoryService.createDeployment()&gt; in (MyProcessEngineServicesBean.java:9)
## Class: `org.camunda.community.migration.detector.rules.test.MyExecutionListener`
### Rule: no classes should implement camunda 7 interface
* Class &lt;org.camunda.community.migration.detector.rules.test.MyExecutionListener&gt; does implement &lt;org.camunda.bpm.engine.delegate.ExecutionListener&gt; in (MyExecutionListener.java:0)
* Class &lt;org.camunda.community.migration.detector.rules.test.MyExecutionListener&gt; does implement &lt;org.camunda.bpm.engine.delegate.DelegateListener&gt; in (MyExecutionListener.java:0)
### Rule: no classes should call method where target owner assignable to camunda 7 api
* Method &lt;org.camunda.community.migration.detector.rules.test.MyExecutionListener.notify(org.camunda.bpm.engine.delegate.BaseDelegateExecution)&gt; calls method &lt;org.camunda.community.migration.detector.rules.test.MyExecutionListener.notify(org.camunda.bpm.engine.delegate.DelegateExecution)&gt; in (MyExecutionListener.java:6)
## Class: `org.camunda.community.migration.detector.rules.test.MySpringEventSubscriber`
### Rule: no methods that is spring event listener should have camunda 7 parameter types
* Parameter type org.camunda.bpm.spring.boot.starter.event.TaskEvent comes from camunda 7
## Class: `org.camunda.community.migration.detector.rules.test.MyTaskListener`
### Rule: no classes should implement camunda 7 interface
* Class &lt;org.camunda.community.migration.detector.rules.test.MyTaskListener&gt; does implement &lt;org.camunda.bpm.engine.delegate.TaskListener&gt; in (MyTaskListener.java:0)
## Class: `org.camunda.community.migration.detector.rules.test.MyDelegate`
### Rule: no classes should implement camunda 7 interface
* Class &lt;org.camunda.community.migration.detector.rules.test.MyDelegate&gt; does implement &lt;org.camunda.bpm.engine.delegate.JavaDelegate&gt; in (MyDelegate.java:0)
### Rule: no classes should call method where target owner assignable to camunda 7 api
* Method &lt;org.camunda.community.migration.detector.rules.test.MyDelegate.execute(org.camunda.bpm.engine.delegate.DelegateExecution)&gt; calls method &lt;org.camunda.bpm.engine.delegate.DelegateExecution.getProcessEngine()&gt; in (MyDelegate.java:9)
```
