# Spring Boot Adapter to re-use Java Delegates or expressions from Camunda Platform in Camunda Cloud

This library allows to reuse Java delegates or Spring expressions from process solutions developed for Camunda Platform within Camunda Cloud. 

The adapter requires to use Spring Boot.

Details on how service tasks are adapted are described in this [migration guide](https://docs.camunda.io/docs/guides/migrating-from-Camunda-Platform/#migration-tooling).

**Important note:*** This adapter does not aim to cover every possible situation, but it might work out-of-the-box for some cases or give you jump-start to extend it to suite your needs.

# How to use

Add the dependency to the adapter library (double check the latest version):
```
<dependency>
    <groupId>org.camunda.community.cloud.migration</groupId>
    <artifactId>camunda-platform-to-cloud-adapter</artifactId>
    <version>0.0.3</version>
</dependency>
```

Import the adapter into your Spring Boot application as shown in the [example application](../example/process-solution-migrated/src/main/java/io/berndruecker/converter/example/Application.java):

```
@SpringBootApplication
@EnableZeebeClient
@Import(CamundaPlatformToCloudAdapterConfig.class)
@ZeebeDeployment(resources = "classpath:*.bpmn")
public class Application {
```

Now you will have a job worker that subscribes to `camunda-platform-to-cloud-migration` and accept task headers for a java delegate class or expression, e.g.:

```
    <bpmn:serviceTask id="task1" name="Java Delegate">
      <bpmn:extensionElements>
        <zeebe:taskDefinition type="camunda-platform-to-cloud-migration" />
        <zeebe:taskHeaders>
          <zeebe:header key="class" value="io.berndruecker.converter.example.SampleJavaDelegate" />
        </zeebe:taskHeaders>
      </bpmn:extensionElements>
    </bpmn:serviceTask>
```

Check out [the full example](../eample/process-solution-migrated/src/main/resources/process.bpmn) for more details.
