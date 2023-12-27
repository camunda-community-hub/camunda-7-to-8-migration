package org.camunda.community.migration_example;

import java.io.File;
import org.camunda.community.rest.client.api.DeploymentApi;
import org.camunda.community.rest.client.dto.DeploymentWithDefinitionsDto;
import org.camunda.community.rest.client.invoker.ApiClient;
import org.camunda.community.rest.client.invoker.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExternalTaskWorkerApplication {
  
  private static final Logger LOG = LoggerFactory.getLogger(ExternalTaskWorkerApplication.class);

  public static void main(String[] args) throws ApiException {
    SpringApplication.run(ExternalTaskWorkerApplication.class, args);
    
    deploy("payment_process.bpmn");
    deploy("forms/check-payment-data.form");
  }
  
  public static void deploy(String resource) throws ApiException {
    ApiClient client = new ApiClient();
    client.setBasePath("http://localhost:8080/engine-rest");

    DeploymentWithDefinitionsDto deployment = new DeploymentApi(client).createDeployment(
            null,
            "Process Application",
            true,
            true,
            "paymentProcessApplication",
            null,
            new File(ExternalTaskWorkerApplication.class.getClassLoader().getResource(resource).getFile())
    );
    
    LOG.info("Deployment done: {}", deployment.getId());
  }
}
