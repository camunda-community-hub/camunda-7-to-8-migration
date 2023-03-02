# Backend Diagram Converter

This project can be used to perform a diagram migration and give you advice on
how you can transform your diagram.

## Setup

This project contains some modules:

- [Core](./core): The core component of diagram migration. Here, all migration
  logic is implemented
- [Webapp](./webapp): The webapp that can be run to have an interactive
  migration
- [CLI](./cli): The CLI implementation of the migration tool. Can be used to
  perform migration on the local machine

Build all modules by running on the root of this repo:

```shell
mvn clean package
```
