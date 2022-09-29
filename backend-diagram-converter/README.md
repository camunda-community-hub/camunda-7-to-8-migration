# Backend Diagram Converter

This project can be used to perform a 2-step diagram migration.

## Setup

The project is built using spring-boot. To build the project, run:

```shell
mvn clean package
```

Then, you can run the .jar-File in the target directory:

```shell
java -jar backend-diagram-converter/target/backend-diagram-converter.jar
```

## Usage

### API interaction

The app will now run on port 8080 and expose 2 endpoints:

1. `/check` will accept a `POST` request with a multipart body containing one field named `file` which holds the bpmn xml file. It will return a JSON document containing information about each process element.
2. `/convert` will accept a `POST` request with a JSON body containing the (adjusted) response from the `/check`. It will return the Camunda 8 bpmn xml file.

### The check result

The check result is of Java type [`org.camunda.community.converter.BpmnDiagramCheckResult`](./src/main/java/org/camunda/community/converter/BpmnDiagramCheckResult.java).

#### the bpmn xml

The bpmn xml is contained as text in the `bpmnXml` field. This bpmn xml is prepared for the conversion as all Camunda 7 namespace attributes and elements are already removed.

#### Results list

Each process element (including collaboration elements) will be part of the `results` list.

They are identified with the `elementId` and have a list containing messages mapped to their severity.

There are 3 severity levels:

- INFO: Just an information, nice to know, but action required
- TASK: Something has already happened, but this needs to be reviewed or adjusted or some information is missing
- ERROR: A detail from the Camunda 7 process cannot be used in Camunda 8 in the same way and needs to be reviewed. These messages could contain information about possible migration blocker.

The information itself should give useful information with a proper context on how to handle it.