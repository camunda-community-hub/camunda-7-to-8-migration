package org.camunda.community.converter.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

public class Main {
  public static final Logger LOG_CLI = LoggerFactory.getLogger("cli");

  public static void main(String[] args) {
    int exitCode = new CommandLine(new ConvertCommand()).execute(args);
    System.exit(exitCode);
  }
}
