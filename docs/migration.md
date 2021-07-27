# Migrating from Camunda Platform to Camunda Cloud

# ---
id: migrating-from-camunda-platform-to-camunda-cloud
title: Migrating from Camunda Platform to Camunda Cloud
---

This guide describeds how-to migrate Camunda Platform solutions to Camunda Cloud, also describing the limitations of such a migration.

Depending on the migrations scenario you need to migrate

// https://page.camunda.com/wp-how-to-migrate-to-camunda

* **BPMN models:** While Camunda Cloud also uses BPMN files (like Camunda Platform), it requires *different extension atrributes* to configure details for execution. BPMN models can be semi-automatically converted using [the Camunda Platform To Cloud Converter Modeler Plugin](https://github.com/berndruecker/camunda-platform-to-cloud-migration/tree/main/camunda-modeler-plugin-platform-to-cloud-converter). Note that some elements or attributes are either not supported or behave slightly different in Camunda Cloud, so the convertion will require manual supervision.

* **Application code:** The application code needs to leverage a *different API* and/or a *different client library*. This will lead to *code changes* you have to implement. This applies to clients talking to the workflow engine (e.g. to start process instances), but also to glue code (e.g. connected to service tasks) as external task workers from Camunda Platform need to be changed to job workers in Camunda Cloud. If you are in Java and used the Camunda Platform Java Client, you can leverage [the Camunda Platform to Cloud Adapter](https://github.com/berndruecker/camunda-platform-to-cloud-migration/tree/main/camunda-platform-to-cloud-adapter), which removes some manual work for code changed. Note, that this will not completly automate this step.

* **Runtime data:** Running process instances of Camunda Platform are stored in the Camunda Platform database. *This data cannot be migrated to Camunda Cloud*. A possible workaround is to create a process model on Camunda Cloud that is purely used for migration to bring process instances to their respective wait state as described in the whitepaper [How to migrate to Camunda](https://page.camunda.com/wp-how-to-migrate-to-camunda).

* **History data:** *Historic data cannot be migrated*.





# Converting BPMN models

Camunda Cloud supports the BPMN process language - just like Camunda Platform. Hence, the basic structure of a process model can be simply reused. However, there are also important differences:

1. **Coverage**: Camunda Cloud being a young product still lacks some elements Camunda Platform already supports. You can see [the current coverage of BPMN in the Camunda Cloud docs](https://docs.camunda.io/docs/reference/bpmn-processes/bpmn-coverage). Missing elements will be added to Camunda Cloud step-by-step. If your model uses an unsupported element, you have to adjust your model.

2. **Technical configuration**: Some configuration attributes used for execution differ between Camunda Cloud and Camunda Platform, at least by using a different namespace. The following list gives you a rough overview
Also the basis [this Camunda Modeler Plugin](https://github.com/berndruecker/camunda-platform-to-cloud-migration/tree/main/camunda-modeler-plugin-platform-to-cloud-converter)


## Service Tasks 

![Service Task](/reference/bpmn-processes/assets/bpmn-symbols/service-task.svg)

[Service Tasks used with Camunda Platform](https://docs.camunda.org/manual/7.15/reference/bpmn20/tasks/service-task/) can

* Attach Java glue code that is called by the engine. 
* Use External Tasks, where workers subscribe to the engine.

In Camunda Cloud, you always have external workers that subscribe to the engine, which is why migration code that using External Tasks is generally easier to do.


### Attached Java Code

There are three ways to implement this marked by different attributes in the BPMN model:

* Specifying a class that implements a JavaDelegate or ActivityBehavior: ```camunda:class```
* Evaluating an expression that resolves to a delegation object: ```camunda:delegateExpression```
* Invoking a method or value expression: ```camunda:expression```

Camunda Cloud can not directly execute custom Java code. Instead, there must be a worker, subscribing to the service task, that can execute your existing code.

The [Camunda Platform to Cloud Adapter](https://github.com/berndruecker/camunda-platform-to-cloud-migration/tree/main/camunda-platform-to-cloud-adapter) implements such a worker based on [Spring Zeebe](https://github.com/camunda-community-hub/spring-zeebe). This can either be used directly, but more often might be used as a starting point or for inspiration. It subscribes to the topic ```camunda-platform-to-cloud-migration``` and then uses some [task headers](https://docs.camunda.io/docs/reference/bpmn-processes/service-tasks/service-tasks#task-headers) to call the right delegation class or expression from within this worker. 

The [Camunda Platform To Cloud Converter Modeler Plugin](https://github.com/berndruecker/camunda-platform-to-cloud-migration/tree/main/camunda-modeler-plugin-platform-to-cloud-converter) will adjust your service tasks automatically for this adapter.

The following attributes/elements are migrated:
* ```camunda:class```
* ```camunda:delegateExpression```
* ```camunda:expression```


Note that some attributes cannot be migrated:
* ```camunda:asyncBefore```: every task in Zeebe is always asyncBefore and asyncAfter
* ```camunda:asyncAfter```: every task in Zeebe is always asyncBefore and asyncAfter
* ```camunda:exclusive```
* ```camunda:jobPriority```
* ```camunda:resultVariable```
* ```camunda:taskPriority```
* ```camunda:failedJobRetryTimeCycle```


Todo / To Check:
* ```camunda:inputOutput```
* ```camunda:type```
* ```camunda:errorEventDefinition```


### External Tasks

External Tasks in Camunda Platform also used a worker, like in Camunda Cloud. So the ```external task topic``` is directly translated in a ```task type name``` in Camunda Cloud. This means, you have to migrate your existing external task worker to a Zeebe worker.

If you developed your Camunda Platform external task worker using [the Java client](https://github.com/camunda/camunda-bpm-platform/tree/master/clients/java), you can use [Camunda Platform to Cloud Adapter](https://github.com/berndruecker/camunda-platform-to-cloud-migration/tree/main/camunda-platform-to-cloud-adapter) to adapt your worker code easily. 


The following attributes/elements are migrated:
* ```camunda:topic```


### Connectors

Connectors cannot be migrated at this moment. You need to look at what connectors you are using and decide for your migration strategy.

The following attributes/elements are not migrated:

* ```camunda:connector```


### Field Injection


* ```camunda:field```


TODO: Check https://docs.camunda.org/manual/7.15/user-guide/process-engine/delegation-code/#field-injection - especially value setting & Expression Language


## Send Tasks

![Send Task](/reference/bpmn-processes/assets/bpmn-symbols/send-task.svg)

In both engines, a send task has the same behavior as a service task - so please refer to the details above. A send task is migrated exactly like a service task.


## Human Tasks


* ```xxxx```




## Call Activities



## Message Receive Events And Receive Tasks

## Script Task

## Gateways & Expression Language


## Multiple Instance Markers


## Unsupported Attributes 

### Asymnchronous Continouations

Explain difference



# Converting Your Application

Zeebe also has a Spring Boot integration. We look at delegation code later and focus on the architecture first.
The embedded engine mode itself is not supported within Zeebe. This means, the broker cannot be started within the same JVM as the client application in Spring Boot. The configuration of the engine is also moved out of the Spring Boot application into the broker configuration.


PIC

## Spring Boot

### Maven Dependencies
* Remove Spring Boot Starter and all other Camunda dependencies
* Add [Spring Zeebe Starter](https://github.com/zeebe-io/spring-zeebe)

### Application

* Make sure to set [Camunda Cloud credentials](https://github.com/zeebe-io/spring-zeebe#configuring-camunda-cloud-connection), for example in `src/main/resources/application.properties`
* Probably remove existing Camunda setting
* Replace `@EnableProcessApplication` with `@EnableZeebeClient` in your main Spring Boot application class
* Add `@ZeebeDeployment` to automatically deploy BPMN models


## Container-Managed Engine (Tomcat, WildFly, Websphere & co)

Zeebe doesn't provide a comparable integration into application servers like Camunda Platform did. So applications need to add the Zeebe client library (typically via Spring Boot as described above).


The implications are comparable to the Spring Boot Embedded Engine.


## Polyglot (C#, NodeJS, ...)

In this scenario you exchange one remote engine with another. As Zeebe comes with a different API, you need to adjust all api call towards the workflow engine. Depending on how you did communicate with the workflow engine this might differ:

* REST: No REST API in Zeebe (at least not yet)
* Client library
* ...
