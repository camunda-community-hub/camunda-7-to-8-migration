# Migrating from Camunda Platform to Camunda Cloud

# ---
id: migrating-from-camunda-platform-to-camunda-cloud
title: Migrating from Camunda Platform to Camunda Cloud
---

This guide describeds how-to migrate Camunda Platform solutions to Camunda Cloud, also describing the limitations of such a migration.

Depending on the migrations scenario you need to migrate

// https://page.camunda.com/wp-how-to-migrate-to-camunda

* **BPMN models:** While Camunda Cloud also uses BPMN files (like Camunda Platform), it requires *different extension atrributes* to configure details for execution. BPMN models can be semi-automatically converted using [this Camunda Modeler Plugin](https://github.com/berndruecker/camunda-platform-to-cloud-migration/tree/main/camunda-modeler-plugin-platform-to-cloud-converter). Note that some elements or attributes are either not supported or behave slightly different in Camunda Cloud, so the convertion will require manual supervision.

* **Application code:** The application code needs to leverage a *different API* and/or a *different client library*. This will lead to *code changes* you have to implement. This applies to clients talking to the workflow engine (e.g. to start process instances), but also to glue code (e.g. connected to service tasks) as external task workers from Camunda Platform need to be changed to job workers in Camunda Cloud. If you are in Java and used the Camunda Platform Java Client, you can leverage [this adapter library](https://github.com/berndruecker/camunda-platform-to-cloud-migration/tree/main/zeebe-camunda-platform-adapter), which removes some manual work for code changed. Note, that this will not completly automate this step.

* **Runtime data:** Running process instances of Camunda Platform are stored in the Camunda Platform database. *This data cannot be migrated to Camunda Cloud*. A possible workaround is to create a process model on Camunda Cloud that is purely used for migration to bring process instances to their respective wait state as described in the whitepaper [How to migrate to Camunda](https://page.camunda.com/wp-how-to-migrate-to-camunda).

* **History data:** *Historic data cannot be migrated*.



# Converting BPMN models





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
