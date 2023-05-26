# Curated list of community migration approaches

List of interesting approaches which should help to migrate. It might be an
[architectural approach](#architectural-approaches) or just tips and tricks /
experiences to keep in mind.

## Architectural approaches

- [**Leveraging Clean Architecture for Migration**](https://github.com/lwluc/camunda-ddd-and-clean-architecture/pull/1)
  shows how you can achieve local isolated changes in a predefined specific part
  of the application during your migration process without having to make
  changes everywhere. By using a clean architecture, an approach with less
  invasive changes is presented that keeps your business domain stable during
  migration.
- [**Vanilla BP**](https://www.vanillabp.io) shows a Camunda
  Platform-independent approach for your process automation project. Use an
  abstraction layer in your implementation and switch between Camunda Platform 7
  and Camunda Platform 8. You can watch the recording from the Camunda Community
  Summit 2023 here:
  https://page.camunda.com/camunda-community-summit-2023-migrating-from-camunda-platform-7-to-8
