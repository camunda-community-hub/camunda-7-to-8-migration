# Camunda 7 To 8 Migration Artefacts

[![Community Extension](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.camunda.community.migration/camunda-7-to-8-migration/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.camunda.community.migration/camunda-7-to-8-migration)

Collection of tools and material around migration of process solutions from Camunda 7 to Camunda 8.

Refer to the [migration guide](https://docs.camunda.io/docs/guides/migrating-from-camunda-7/) for details.

This repository contains:
- [The Code Migration Detector](./code-migration-detector) to check your implementation for Camunda 7 API usage
- [The Camunda 7 Adapter](./camunda-7-adapter) to re-use Java Delegates or  expressions from Camunda 7 when running Camunda 8
- [An example](./example) containing a sample process solution for Camunda 7 and the migrated version that runs on Camunda 8
- [The Cawemo migration script](./cawemo-to-web-modeler-migration) to migrate Cawemo projects to Web Modeler
- [A curated list of community migration approaches](./migration-approaches/README.md)

You might also want to check out the [Migration Analyzer](https://github.com/camunda-community-hub/camunda-7-to-8-migration-analyzer) or other official [migration tooling](https://docs.camunda.io/docs/guides/migrating-from-camunda-7/migration-tooling/).

## Use Snapshot Versions

Some features you see in the code might not yet be available in a released
version. You can find the `SNAPSHOT` versions
[here](https://artifacts.camunda.com/ui/repos/tree/General/camunda-bpm-community-extensions-snapshots/org/camunda/community/migration/camunda-7-adapter).
