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

## Classification

Each use case is classified based on the following table:

| Level                    | Description                                                                          |
| ------------------------ | ------------------------------------------------------------------------------------ |
| Fully-Supported          | Supported by Camunda 8 API.                                                          |
| Not-supported            | Not supported by Camunda 8 API.                                                      |
| Partially-supported      | Part of the API is supported.                                                        |
| Migration-tool-available | There exists a migration tool. It might be a temporary Adapter library, for example. |
| Alternative-exists       | There exists an alternative tool or approach.                                        |

## Use cases

Following table contains identified use cases:

| Covered by tests | ID   | Use Case                                                                     | Classification           | Note                                                                                                                                                                                                                                                                                                                        |
| ---------------- | ---- | ---------------------------------------------------------------------------- | ------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ✔                | 1    | Direct usage of Java API Services                                            | Partially-supported      | See [Camunda 8 API references](https://docs.camunda.io/docs/apis-tools/working-with-apis-tools/#api-reference) and how to use the [Java Client](https://docs.camunda.io/docs/apis-tools/java-client/).                                                                                                                      |
| ✔                | 1.1  | RepositoryService                                                            | Partially-supported      | Look into [Zeebe API (gRPC)](https://docs.camunda.io/docs/apis-tools/grpc/) or [REST Operate API](https://docs.camunda.io/docs/apis-tools/operate-api/overview/). You can use the [Java Client](https://docs.camunda.io/docs/apis-tools/java-client/).                                                                      |
| ✔                | 1.2  | RuntimeService                                                               | Partially-supported      | Look into [Zeebe API (gRPC)](https://docs.camunda.io/docs/apis-tools/grpc/) for services that control the process. For queries see the other [Public APIs](https://docs.camunda.io/docs/apis-tools/public-api/).                                                                                                            |
| ✔                | 1.3  | TaskService                                                                  | Partially-supported      | See both the [Tasklist API (REST)](https://docs.camunda.io/docs/apis-tools/tasklist-api-rest/tasklist-api-rest-overview/) nad [Tasklist API (GraphQL)](https://docs.camunda.io/docs/apis-tools/tasklist-api/tasklist-api-overview/).                                                                                        |
| ✔                | 1.4  | IdentityService                                                              | Not-supported            | See [Camunda Identity](https://docs.camunda.io/docs/self-managed/identity/what-is-identity/).                                                                                                                                                                                                                               |
| ✔                | 1.5  | FormService                                                                  | Partially-supported      | See both the [Tasklist API (REST)](https://docs.camunda.io/docs/apis-tools/tasklist-api-rest/tasklist-api-rest-overview/) nad [Tasklist API (GraphQL)](https://docs.camunda.io/docs/apis-tools/tasklist-api/tasklist-api-overview/).                                                                                        |
| ✔                | 1.6  | HistoryService                                                               | Partially-supported      | Public API is currently under development. See [History Architecture](https://docs.camunda.io/docs/components/best-practices/operations/reporting-about-processes/#history-architecture)                                                                                                                                    |
| ✔                | 1.7  | ManagementService                                                            | Partially-supported      | See [Zeebe API (gRPC)](https://docs.camunda.io/docs/apis-tools/grpc/).                                                                                                                                                                                                                                                      |
| ✔                | 1.8  | FilterService                                                                | Not-supported            |                                                                                                                                                                                                                                                                                                                             |
| ✔                | 1.9  | ExternalTaskService                                                          | Alternative-exists       | See [Job Workers](https://docs.camunda.io/docs/components/concepts/job-workers/)                                                                                                                                                                                                                                            |
| ✔                | 1.10 | CaseService                                                                  | Not-supported            |                                                                                                                                                                                                                                                                                                                             |
| ✔                | 1.11 | DecisionService                                                              | Alternative-exists       | You can use the [Zeebe API (gRPC)](https://docs.camunda.io/docs/apis-tools/grpc/) specifically the [`EvaluateDecision` method](https://docs.camunda.io/docs/apis-tools/grpc/#evaluatedecision-rpc), or you can use standalone [DMN Rule Engine API](https://github.com/camunda/camunda-bpm-platform/tree/master/engine-dmn) |
| ✔                | 2    | Usage of Java delegates                                                      | Migration-tool-available | You can use [Camunda 7 Adapter](https://github.com/camunda-community-hub/camunda-7-to-8-migration/tree/main/camunda-7-adapter) as temporary solution and refactor to job worker.                                                                                                                                            |
| ✔                | 3    | Spring task listeners                                                        | Not-supported            |                                                                                                                                                                                                                                                                                                                             |
| ✔                | 4    | Spring execution listeners                                                   | Migration-tool-available | Will be available after CCS 2023 ;-)                                                                                                                                                                                                                                                                                        |
|                  | 5    | History Events API (HistoryEventProducer, HistoryEventHistory, EventHandler) | Alternative-exists       | See [Exporters](https://docs.camunda.io/docs/components/zeebe/technical-concepts/architecture/#exporters) as alternative to History Events API.                                                                                                                                                                             |
|                  | 6    | Usage of Java API Services through DelegateExecution                         | Partially-supported      |                                                                                                                                                                                                                                                                                                                             |

---

TBD: (following part contains example of refined use cases)

### RepositoryService

| Identification | Use case          | Camunda 7 Method | Classification | Camunda 8 API    | Description                                                                                            |
| -------------- | ----------------- | ---------------- | -------------- | ---------------- | ------------------------------------------------------------------------------------------------------ |
| 1.1.1          | Create deployment | createDeployment | Supported      | Zeebe API (gRPC) | Use the [DeployResource](https://docs.camunda.io/docs/apis-tools/grpc/#deployresource-rpc) RPC method. |
| ...            |                   |                  |                |                  |                                                                                                        |

