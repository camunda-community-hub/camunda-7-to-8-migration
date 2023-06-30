# Camunda Platform 7 To Camunda Platform 8 Migration Tooling

[![Community Extension](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.camunda.community.migration/camunda-7-to-8-migration/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.camunda.community.migration/camunda-7-to-8-migration)

This tool helps with migration of process solutions from Camunda Platform 7 to
Camunda Platform 8. This is described in the
[migration guide](https://docs.camunda.io/docs/guides/migrating-from-Camunda-Platform/)

This projects contains:

- [An adapter library](./camunda-7-adapter) to re-use Java Delegates or
  expressions from Camunda Platform 7 when running Camunda Platform 8
- [An example](./example) containing a sample process solution for Camunda
  Platform 7 and the migrated version that runs on Camunda Platform 8
- [A process diagram converter](./backend-diagram-converter) that is implemented
  in Java and performs checks and migration
  - [A web frontend](./backend-diagram-converter/webapp) with graphical output
    of the conversion result
  - [A command line tool](./backend-diagram-converter/cli) to support mass
    conversion
- [A Process Instance Migration Tool](./process-instance-migration) to migrate
  instances of a converted process definition to Camunda Platform 8
- [A curated list of community migration approaches](./migration-approaches/README.md)

## Use Snapshot Versions

Some features you see in the code might not yet be available in a released
version. You can find the `SNAPSHOT` versions
[here](https://artifacts.camunda.com/ui/repos/tree/General/camunda-bpm-community-extensions-snapshots/org/camunda/community/migration/camunda-7-adapter).
