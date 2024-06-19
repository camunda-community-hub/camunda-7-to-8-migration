package org.camunda.community.migration.automatictest.service;

import org.camunda.automator.bpmnengine.BpmnEngine;
import org.camunda.automator.bpmnengine.camunda7.BpmnEngineCamunda7;
import org.camunda.automator.bpmnengine.camunda8.BpmnEngineCamunda8;
import org.camunda.automator.engine.AutomatorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

/** Configuration */
@org.springframework.context.annotation.Configuration
@PropertySource("classpath:application.yaml")
public class Configuration {

  @Value("#{'${camunda7.client.base-url}'}")
  public String camunda7BaseUrl;

  @Value("#{'${camunda7.client.rest-api-context}'}")
  public String camunda7RestApiContext;

  @Value("#{'${zeebe.client.broker.gateway-address}'}")
  public String zeebeSelfGatewayAddress;

  @Value("#{'${zeebe.client.security.plaintext}'}")
  public String zeebeSelfSecurityPlainText;

  @Value("#{'${zeebe.client.saas.register}'}")
  public String zeebeSaasCloudRegister;

  @Value("#{'${zeebe.client.saas.region}'}")
  public String zeebeSaasCloudRegion;

  @Value("#{'${zeebe.client.saas.clusterid}'}")
  public String zeebeSaasCloudClusterId;

  @Value("#{'${zeebe.client.saas.clientid}'}")
  public String zeebeSaasCloudClientId;

  @Value("#{'${zeebe.client.saas.clientsecret}'}")
  public String zeebeSaasClientSecret;

  @Value("#{'${operate.client.base-url}'}")
  public String operateUrl;

  @Value("#{'${operate.client.authentication.simple.username}'}")
  public String operateUserName;

  @Value("#{'${operate.client.authentication.simple.password}'}")
  public String operateUserPassword;

  @Value("#{'${tasklist.client.base-url}'}")
  public String tasklistUrl;

  public BpmnEngine getCamunda7BpmnEngine() throws AutomatorException {
    String serverUrl = camunda7BaseUrl + "/" + camunda7RestApiContext;
    return new BpmnEngineCamunda7(serverUrl, false);
  }

  /**
   * CAMUNDA_8,127.0.0.1:26500,http://localhost:8081,demo,demo,http://localhost:8082
   *
   * @return
   */
  public BpmnEngine getCamunda8BpmnEngine() throws AutomatorException {
    BpmnEngineCamunda8 bpmnEngineCamunda8 =
        new BpmnEngineCamunda8(
            zeebeSelfGatewayAddress,
            zeebeSelfSecurityPlainText,
            zeebeSaasCloudRegister,
            zeebeSaasCloudRegion,
            zeebeSaasCloudClusterId,
            zeebeSaasCloudClientId,
            zeebeSaasClientSecret,
            operateUrl,
            operateUserName,
            operateUserPassword,
            tasklistUrl);
    bpmnEngineCamunda8.init();
    return bpmnEngineCamunda8;
  }
}
