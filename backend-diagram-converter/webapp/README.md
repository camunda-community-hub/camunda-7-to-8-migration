# Diagram Converter Webapp

This webapp uses the diagram converter to check and convert diagrams. It allows
to use a frontend as well as a REST API.

## Features

### Frontend

The frontend is self-explanatory. You upload .bpmn Files and then click either
**Check** or **Convert and download**.

**Check** will allow you to review all infos, tasks and warnings directly in the
browser.

**Convert and download** will download you the converted .bpmn Files that
consists of all the infos, tasks and warnings you have seen in the browser as
well.

### Rest API

The API offers 2 methods:

`POST /check`:

- Request:
  - Format: `FormData`
  - Fields
    - `file` (`MultipartFile`): BPMN file _(mandatory)_
    - `adapterJobType` (`String`): type of the job all service tasks formerly
      implemented as delegates or expressions should have. _(optional)_
    - `platformVersion` (`String`): version of the target platform _(optional)_
  - Headers
    - `Accept`: Either `application/json` or `text/csv`
- Response:
  - `200`: Everything fine. The body contains a
    [check results](../core/src/main/java/org/camunda/community/migration/converter/BpmnDiagramCheckResult.java),
    either in `application/json` format or flattened as `text/csv`.

`POST /convert`:

- Request:
  - Format: `FormData`
  - Fields
    - `file` (`MultipartFile`): BPMN file _(mandatory)_
    - `appendDocumentation` (`Boolean`): whether the check results should also
      be added to the documentation of each BPMN element _(default: `false`)_
    - `adapterJobType` (`String`): type of the job all service tasks formerly
      implemented as delegates or expressions should have. _(optional)_
    - `platformVersion` (`String`): version of the target platform _(optional)_
- Response:
  - `200`: Everything fine. The body contains a BPMN diagram. The header
    contains a `Content-Disposition` field that declares this as attachment and
    holds a filename. The `Content-Type` is `application/bpmn+xml`.

These error can occur on both endpoints:

`4xx`: The request you provided could not be handled

`5xx`: There was an exception during parsing or transforming your process.

### Notifications

The app can be configured to notify people in the background directly.
Currently, this is possible via Slack.

To use this feature, you need to configure the following:

```yaml
notification:
  slack:
    enabled: true
    token: <YOUR_BOT_TOKEN>
    channel-name: <NAME_OR_ID_OF_THE_CHANNEL>
```

In order to function, the slack app this notification service will be connected
to needs these scopes: `channels:read`, `chat:write`, `chat:write.public` and
`files:write`.

Also, the Bot needs to be added to the channel it should send the notifications
to. Otherwise, no stacktrace files will arrive.

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

If you don't want to use notifications, you can just leave out the env
arguments.

## Further notice

This app does not save any data it provides nor does it require authentication.
