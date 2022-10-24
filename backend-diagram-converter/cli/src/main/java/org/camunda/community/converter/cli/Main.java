package org.camunda.community.converter.cli;

import picocli.CommandLine;

public class Main {

  public static void main(String[] args) {
    int exitCode = new CommandLine(new ConvertCommand()).execute(args);
    System.exit(exitCode);
  }
}
