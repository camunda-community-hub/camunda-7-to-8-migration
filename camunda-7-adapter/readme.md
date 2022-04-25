# Spring Boot Adapter to re-use Java Delegates or expressions from Camunda Platform in Camunda Cloud

This library allows to reuse Java delegates or Spring expressions from process solutions developed for Camunda Platform within Camunda Cloud. 

The adapter requires to use Spring Boot.

Details on how service tasks are adapted are described in this [migration guide](https://docs.camunda.io/docs/guides/migrating-from-Camunda-Platform/#migration-tooling).

**Important note:*** This adapter does not aim to cover every possible situation, but it might work out-of-the-box for some cases or give you jump-start to extend it to suite your needs.

# How to use

## Add dependency

Add the dependency to the adapter library (double-check for the latest version):

```xml
<dependency>
    <groupId>org.camunda.community.migration</groupId>
    <artifactId>camunda-7-adapter</artifactId>
    <version>0.1.0</version>
    <exclusions>
      <exclusion>
        <groupId>org.springframework</groupId>
        <artifactId>spring-beans</artifactId>
      </exclusion>
    </exclusions>
</dependency>
```

The exclusion makes sure, your own Spring dependency version is used and no conflicts occur. 

## Import adapter

Import the adapter into your Spring Boot application as shown in the [example application](../example/process-solution-migrated/src/main/java/io/berndruecker/converter/example/Application.java):

```java
@SpringBootApplication
@EnableZeebeClient
@Import(CamundaPlatform7AdapterConfig.class)
@ZeebeDeployment(resources = "classpath:*.bpmn")
public class Application {
```

This will also start a job worker that subscribes to `camunda-7-adapter`.

## Using migration worker

To use that worker, add the `taskType=camunda-7-adapter` to your service task and add task headers for a java delegate class or expression, e.g.:

```xml
<bpmn:serviceTask id="task1" name="Java Delegate">
  <bpmn:extensionElements>
    <zeebe:taskDefinition type="camunda-7-adapter" />
    <zeebe:taskHeaders>
      <zeebe:header key="class" value="io.berndruecker.converter.example.SampleJavaDelegate" />
    </zeebe:taskHeaders>
  </bpmn:extensionElements>
</bpmn:serviceTask>
```

## Example

Check out [the full example](../example/process-solution-migrated/src/main/resources/process.bpmn) for more details.

