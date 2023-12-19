package org.camunda.community.migration_example.payment;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.community.migration_example.services.CreditCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription("creditCardCharging")
public class CreditCardHandler implements ExternalTaskHandler {

  private static final Logger LOG = LoggerFactory.getLogger(CreditCardHandler.class);
  
  @Autowired
  public CreditCardService service;

  @Override
  public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
    LOG.info("handle topic {} for task id {}", externalTask.getTopicName(), externalTask.getId());
    // TODO Add your business logic here
    String cardNumber = externalTask.getVariable("cardNumber");
    String cvc = externalTask.getVariable("CVC");
    String expiryDate = externalTask.getVariable("expiryDate");
    Double openAmount = externalTask.getVariable("openAmount");
    
    try {
      service.chargeAmount(cardNumber, cvc, expiryDate, openAmount);
      externalTaskService.complete(externalTask);
    } catch (IllegalArgumentException e) {
      externalTaskService.handleBpmnError(externalTask, "creditCardChargeError", e.getLocalizedMessage());
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      externalTaskService.handleFailure(externalTask, "credit card expired", sw.toString(), 0, 0);
    }
  }

}
