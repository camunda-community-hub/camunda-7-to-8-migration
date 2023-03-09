package org.camunda.community.migration.processInstance.api.model.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class DataSerializationTest {

  @Test
  void shouldSerializeSimpleProcessInstanceData() throws JsonProcessingException {
    ProcessInstanceData processInstanceData =
        Builder.processInstanceData()
            .withBpmnProcessId("invoice")
            .withId("1e88a1fa-b9a0-11ed-8966-3ce1a1c19785")
            .withName("Invoice Receipt")
            .withActivity(
                "approveInvoice", Builder.userTaskData().withName("Approve invoice").build())
            .withVariable("amount", Builder.number(300.0D))
            .withVariable(
                "approverGroups", Builder.array(Builder.text("accounting"), Builder.text("sales")))
            .withVariable("creditor", Builder.text("Moby Ferries"))
            .withVariable("invoiceCategory", Builder.text("Travel expenses"))
            .withVariable("invoiceDocument", Builder.text("Some file reference"))
            .withVariable("invoiceNumber", Builder.text("837hfz6e-hneze"))
            .build();

    ObjectMapper objectMapper = new ObjectMapper();
    String data =
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(processInstanceData);
    System.out.println(data);
    System.out.println("-----");
    ProcessInstanceData deserialized = objectMapper.readValue(data, ProcessInstanceData.class);
    System.out.println(
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(deserialized));
  }

  @Test
  void shouldSerializeCallActivityData() throws JsonProcessingException {
    ProcessInstanceData processInstanceData =
        Builder.processInstanceData()
            .withBpmnProcessId("invoice")
            .withId("1e88a1fa-b9a0-11ed-8966-3ce1a1c19785")
            .withName("Invoice Receipt")
            .withActivity(
                "reviewInvoice",
                Builder.callActivityData()
                    .withName("Review Invoice")
                    .withProcessInstance(
                        Builder.processInstanceData()
                            .withBpmnProcessId("ReviewInvoice")
                            .withName("Review Invoice")
                            .withActivity(
                                "reviewInvoice",
                                Builder.userTaskData().withName("Review Invoice").build())
                            .build())
                    .withVariable("amount", Builder.number(300.0D))
                    .withVariable(
                        "approverGroups",
                        Builder.array(Builder.text("accounting"), Builder.text("sales")))
                    .withVariable("creditor", Builder.text("Moby Ferries"))
                    .withVariable("invoiceCategory", Builder.text("Travel expenses"))
                    .withVariable("invoiceDocument", Builder.text("Some file reference"))
                    .withVariable("invoiceNumber", Builder.text("837hfz6e-hneze"))
                    .build())
            .withVariable("amount", Builder.number(300.0D))
            .withVariable(
                "approverGroups", Builder.array(Builder.text("accounting"), Builder.text("sales")))
            .withVariable("creditor", Builder.text("Moby Ferries"))
            .withVariable("invoiceCategory", Builder.text("Travel expenses"))
            .withVariable("invoiceDocument", Builder.text("Some file reference"))
            .withVariable("invoiceNumber", Builder.text("837hfz6e-hneze"))
            .build();

    ObjectMapper objectMapper = new ObjectMapper();
    String data =
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(processInstanceData);
    System.out.println(data);
    System.out.println("-----");
    ProcessInstanceData deserialized = objectMapper.readValue(data, ProcessInstanceData.class);
    System.out.println(
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(deserialized));
  }
}
