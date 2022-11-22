# Camunda Platform 7 To Camunda Platform 8 Migration Tooling

[![Community Extension](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)

This tool helps with migration of process solutions from Camunda Platform 7 to Camunda Platform 8. This is described in the [migration guide](https://docs.camunda.io/docs/guides/migrating-from-Camunda-Platform/)

This projects contains:

* [A Camunda Dektop Modeler Plugin](./modeler-plugin-7-to-8-converter) to convert models (BPMN/DMN) from Camunda Platform 7 to Camunda Platform 8
* [An adapter library](./camunda-7-adapter) to re-use Java Delegates or expressions from Camunda Platform 7 when running Camunda Platform 8
* [An example](./example) containing a sample process solution for Camunda Platform 7 and the migrated version that runs on Camunda Platform 8
* [A process diagram converter](./backend-diagram-converter) that is implemented in Java and performs checks and migration

The following video walks you through the migration process:

<a href="http://www.youtube.com/watch?feature=player_embedded&v=yS0wAO0KgBc" target="_blank"><img src="http://img.youtube.com/vi/yS0wAO0KgBc/0.jpg" alt="Walkthrough" width="240" height="180" border="10" /></a>

## Use Snapshot Versions

Some features you see in the code might not yet be available in a released version. You can find the `SNAPSHOT` versions [here](https://artifacts.camunda.com/ui/repos/tree/General/camunda-bpm-community-extensions-snapshots/org/camunda/community/migration/camunda-7-adapter).
