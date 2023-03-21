package org.camunda.community.migration.automatictest.service;

import org.camunda.automator.bpmnengine.BpmnEngine;
import org.camunda.automator.bpmnengine.camunda7.BpmnEngineCamunda7;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;


/**
 * Configuration
 */
@org.springframework.context.annotation.Configuration
@PropertySource("classpath:application.yaml")

public class Configuration {

  @Value("#{'${camunda7.client.base-url}'}")
  public String camunda7BaseUrl;


  @Value("#{'${camunda7.client.rest-api-context}'}")
  public String camunda7RestApiContext;

/*
  zeebe:
  client:
  security:
  plaintext: true
  broker:
  gateway-address: localhost:26500
  request-timeout: PT60S

  operate:
  client:
  base-url: http://localhost:8081
  authentication:
  simple:
  username: demo
  password: demo
*/


  public BpmnEngine getCamunda7BpmnEngine() {
    String serverUrl = camunda7BaseUrl+"/"+camunda7RestApiContext;
    return new BpmnEngineCamunda7( serverUrl, false );
  }
}
