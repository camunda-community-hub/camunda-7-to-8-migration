# Diagram Converter Webapp

This webapp uses the diagram converter to check and convert diagrams. It allows to use a frontend as well as a REST API.

## Features

### Frontend

The frontend is self-explanatory. In case no engine connection is configured, you upload .bpmn Files and then click either **Check** or **Convert and download**.

**Check** will allow you to review all infos, tasks and warnings directly in the browser.

**Convert and download** will download you the converted .bpmn Files that consists of all the infos, tasks and warnings you have seen in the browser as well.

### Rest API

The API offers 3 methods:

`GET /manifest`: The request does not require any further modification. It will return:

`200`: Everything fine. The body contains the [`ConverterManifest`](./src/main/java/org/camunda/community/converter/webapp/ConverterManifest.java) which exposes information about the app configuration. This information might be required to configure the calls towards other endpoints.

`POST /check`: Requires the usage of `FormData`. In case no engine is configured, the formdata needs to consist of fields `files` which are the .bpmn Files. It will return:

`200`: Everything fine. The body contains a list of [check results](./../core/src/main/java/org/camunda/community/converter/BpmnDiagramCheckResult.java). In case there is a check result that only consists of a filename, there was an error during conversion.
`400`: You provided BPMN files although there is an engine connection configured or the other way around.

`POST /convert`: Requires the usage of `FormData`. In case no engine is configured, the formdata needs to consist of fields `files` which are the .bpmn Files. In any case, a field `appendDocumentation` has to be present which is a boolean and controls whether the messages are also appended to the documentation section of each BPMN element. It will return:

`200`: Everything fine. The body contains a zipped `Blob` which can be saved as a file and is a zip archive containing your converted BPMN diagrams.
`400`: You provided BPMN files although there is an engine connection configured or the other way around. Or there were errors during conversion. These errors would then be appended to the body.
`500`: There was an exception during parsing the BPMN files.

### Notifications

The app can be configured to notify people in the background directly. Currently, this is possible via Slack.

To use this feature, you need to configure the following:

```yaml
notification:
  slack:
    enabled: true
    token: <YOUR_BOT_TOKEN>
    channel-name: <NAME_OR_ID_OF_THE_CHANNEL>
```

In order to function, the slack app this notification service will be connected to needs these scopes: `channels:read`, `chat:write`, `chat:write.public` and `files:write`.

Also, the Bot needs to be added to the channel it should send the notifications to. Otherwise, no stacktrace files will arrive.

## Installation

### Docker

This app is built as docker image by default. To pull the latest version, run:

```shell
docker pull ghcr.io/camunda-community-hub/camunda-7-to-8-migration/diagram-converter:latest
```

To run it, use:

```shell
docker run --name diagram-converter -p 8080:8080 -e NOTIFICATION_SLACK_ENABLED=true -e NOTIFICATION_SLACK_CHANNEL-NAME=<NAME_OR_ID_OF_THE_CHANNEL> -e NOTIFICATION_SLACK_TOKEN=<YOUR_BOT_TOKEN>  ghcr.io/camunda-community-hub/camunda-7-to-8-migration/diagram-converter:latest
```

If you don't want to use notifications, you can just leave out the env arguments.

## Further notice

This app does not save any data it provides nor does it require authentication.
