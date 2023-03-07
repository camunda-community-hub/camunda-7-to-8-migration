package org.camunda.community.migration.processInstance.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class DataSerializationTest {

  @Test
  void shouldSerializeSimpleProcessInstanceData() throws JsonProcessingException {
    ProcessInstanceData processInstanceData =
        Builder.processInstanceData()
            .bpmnProcessId("invoice")
            .id("1e88a1fa-b9a0-11ed-8966-3ce1a1c19785")
            .name("Invoice Receipt")
            .activity("approveInvoice", Builder.userTaskData().name("Approve invoice").build())
            .variable("amount", Builder.number(300.0D))
            .variable(
                "approverGroups", Builder.array(Builder.text("accounting"), Builder.text("sales")))
            .variable("creditor", Builder.text("Moby Ferries"))
            .variable("invoiceCategory", Builder.text("Travel expenses"))
            .variable("invoiceDocument", Builder.text("Some file reference"))
            .variable("invoiceNumber", Builder.text("837hfz6e-hneze"))
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
            .bpmnProcessId("invoice")
            .id("1e88a1fa-b9a0-11ed-8966-3ce1a1c19785")
            .name("Invoice Receipt")
            .activity(
                "reviewInvoice",
                Builder.callActivityData()
                    .name("Review Invoice")
                    .processInstance(
                        Builder.processInstanceData()
                            .bpmnProcessId("ReviewInvoice")
                            .name("Review Invoice")
                            .activity(
                                "reviewInvoice",
                                Builder.userTaskData().name("Review Invoice").build())
                            .build())
                    .variable("amount", Builder.number(300.0D))
                    .variable(
                        "approverGroups",
                        Builder.array(Builder.text("accounting"), Builder.text("sales")))
                    .variable("creditor", Builder.text("Moby Ferries"))
                    .variable("invoiceCategory", Builder.text("Travel expenses"))
                    .variable("invoiceDocument", Builder.text("Some file reference"))
                    .variable("invoiceNumber", Builder.text("837hfz6e-hneze"))
                    .build())
            .variable("amount", Builder.number(300.0D))
            .variable(
                "approverGroups", Builder.array(Builder.text("accounting"), Builder.text("sales")))
            .variable("creditor", Builder.text("Moby Ferries"))
            .variable("invoiceCategory", Builder.text("Travel expenses"))
            .variable("invoiceDocument", Builder.text("Some file reference"))
            .variable("invoiceNumber", Builder.text("837hfz6e-hneze"))
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
