# Diagram Converter Webapp

This webapp uses the diagram converter to check and convert diagrams. It allows to use a frontend as well as a REST API.

## Features

### Frontend

The frontend is self-explanatory. You upload .bpmn Files and then click either **Check** or **Convert and download**.

**Check** will allow you to review all infos, tasks and warnings directly in the browser.

**Convert and download** will download you the converted .bpmn Files that consists of all the infos, tasks and warnings you have seen in the browser as well.

### Rest API

The API offers 2 methods:

`POST /check`: Requires the usage of `FormData`. The formdata needs to consist of fields `files` which are the .bpmn Files. In the `Accept` header, you can either set `application/json` or `text/csv`. It will return:

`200`: Everything fine. The body contains a list of [check results](./../core/src/main/java/org/camunda/community/converter/BpmnDiagramCheckResult.java), either in `application/json` format or flattened as `text/csv`.

`POST /convert`: Requires the usage of `FormData`. The formdata needs to consist of fields `files` which are the .bpmn Files plus a field `appendDocumentation` which is a boolean and controls whether the messages are also appended to the documentation section of each BPMN element. It will return:

`200`: Everything fine. The body contains a zipped `Blob` which can be saved as a file and is a zip archive containing your converted BPMN diagrams.

These error can occur on both endpoints:

`4xx`: The file you provided could not be parsed
`5xx`: There was an exception during parsing or transforming your process.

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
