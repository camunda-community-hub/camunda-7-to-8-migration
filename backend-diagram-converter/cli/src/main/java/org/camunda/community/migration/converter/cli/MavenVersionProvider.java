package org.camunda.community.migration.converter.cli;

import picocli.CommandLine.IVersionProvider;

public class MavenVersionProvider implements IVersionProvider {
  @Override
  public String[] getVersion() throws Exception {
    return new String[] {this.getClass().getPackage().getImplementationVersion()};
  }
}
