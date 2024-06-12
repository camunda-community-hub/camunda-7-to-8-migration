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
