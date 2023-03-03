# Camunda 8 Process Instance Importer

This library should import process instances into the Zeebe engine.

The process instances were started in some other process engine like Camunda
Platform 7.

They were exported into a json or any other compareable, structured file format
like [exampleProcessInstances.json](exampleProcessInstances.json)

The libary should define an API to accept one or more process instances and
start them at the given activities and with given variables.

In case of call activities, the started called process need additional process
instance modification to reach the desired activity.
