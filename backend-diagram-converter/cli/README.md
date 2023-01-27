# Conversion CLI tool

The command line interface tool can convert a Camunda Platform 7 diagram or all diagrams inside a folder (plus sub directories).

All diagrams that should be converted need to have the `.bpmn` or `.bpmn20.xml` file ending.

The tool logs all results (either location of the new file or the exception the migration tool faced during migration).
Diagrams that are already in C8 format will be logged as problem so that you know the converter got past them.

In case you run the CLI tool on your Windows computer and your process diagram contains Umlaute,
add the java option `-Dfile.encoding=UTF-8` between the java and -jar parameters:

```
java -Dfile.encoding=UTF-8 -jar backend-diagram-converter-cli-version.jar
```

## Commands

### Convert diagrams from the local file systems

```
Usage: java -Dfile.encoding=UTF-8 -jar backend-diagram-converter-cli.jar local
       [-dhoV] [--check] [--csv] [-nr] [--adapter-job-type=<adapterJobType>]
       [--platform-version=<platformVersion>] [--prefix=<prefix>] <file>
Converts the diagrams from the given directory or file
      <file>                 The file to convert or directory to search in
      --adapter-job-type=<adapterJobType>
                             If set, the default value for the adapter job is
                               overridden
      --check                If enabled, no converted diagrams are exported
      --csv                  If enabled, a CSV file will be created containing
                               the results for all conversions
  -d, --documentation        If enabled, messages are also appended to
                               documentation
  -h, --help                 Show this help message and exit.
      -nr, --not-recursive   If enabled, recursive search in subfolders will be
                               omitted
  -o, --override             If enabled, existing files are overridden
      --platform-version=<platformVersion>
                             Semantic version of the target platform, defaults
                               to latest version
      --prefix=<prefix>      Prefix for the name of the generated file
  -V, --version              Print version information and exit.
```

**Example:**

```
java -Dfile.encoding=UTF-8 -jar backend-diagram-converter-cli-v.v.v.jar local c:\myDirectory
```

### Convert diagrams from a running process engine

```
Usage: java -Dfile.encoding=UTF-8 -jar backend-diagram-converter-cli.jar engine
       [-dhoV] [--check] [--csv] [--adapter-job-type=<adapterJobType>]
       [-p=<password>] [--platform-version=<platformVersion>]
       [--prefix=<prefix>] [-t=<targetDirectory>] [-u=<username>] <url>
Converts the diagrams from the given process engine
      <url>               Fully qualified http(s) address to the process engine
                            REST API
      --adapter-job-type=<adapterJobType>
                          If set, the default value for the adapter job is
                            overridden
      --check             If enabled, no converted diagrams are exported
      --csv               If enabled, a CSV file will be created containing the
                            results for all conversions
  -d, --documentation     If enabled, messages are also appended to
                            documentation
  -h, --help              Show this help message and exit.
  -o, --override          If enabled, existing files are overridden
  -p, --password=<password>
                          Password for basic auth
      --platform-version=<platformVersion>
                          Semantic version of the target platform, defaults to
                            latest version
      --prefix=<prefix>   Prefix for the name of the generated file
  -t, --target-directory=<targetDirectory>
                          The directory to save the .bpmn files
  -u, --username=<username>
                          Username for basic auth
  -V, --version           Print version information and exit.
```

**Example:**

```
java -Dfile.encoding=UTF-8 -jar backend-diagram-converter-cli-v.v.v.jar engine http://localhost:8080/engine-rest
```

## Download

There is a prepackaged jar file available in the Camunda Artifactory at
[https://artifacts.camunda.com/artifactory/camunda-bpm-community-extensions/org/camunda/community/migration/backend-diagram-converter-cli/](https://artifacts.camunda.com/artifactory/camunda-bpm-community-extensions/org/camunda/community/migration/backend-diagram-converter-cli/)

Enter the folder with the latest version and download `backend-diagram-converter-cli-v.v.v.jar`.

You can start the jar file from the command line with

```shell
java -jar backend-diagram-converter-cli-0.4.3.jar --help
```

## Compile from source

To get a functioning `.jar` file, run

```shell
mvn clean package
```

Afterwards, the file can be found in `target` with the name `backend-diagram-converter-cli.jar` as shown before.
