# Conversion CLI tool

## Features

### Migration of existing diagrams

The CLI tool can migrate a given diagram or all content inside a folder (plus subdirectories).

All diagrams that should be converted need to have the `.bpmn` or `.bpmn20.xml` file ending.

The tool logs all results (either location of the new file or the exception the migration tool faced during migration). Diagrams that are already in C8 format will be logged as problem so that you know the converter got past them.

## Setup

To get a functioning `.jar` file, run

```shell
mvn clean package
```

The file can then be found in `target` with the name `camunda-convert.jar`.

Then you can run this `.jar` file with the location of the files to convert in the args. For help, please use `-h` or `--help`.
