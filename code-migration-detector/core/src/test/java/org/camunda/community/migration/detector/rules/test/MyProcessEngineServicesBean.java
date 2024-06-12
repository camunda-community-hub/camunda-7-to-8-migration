package org.camunda.community.migration.detector.rules.test;

import org.camunda.bpm.engine.RepositoryService;

public class MyProcessEngineServicesBean {
  RepositoryService repositoryService;

  public void doSomethingWithTheServices() {
    repositoryService.createDeployment();
  }
}
