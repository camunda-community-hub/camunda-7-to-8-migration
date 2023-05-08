package org.camunda.community.migration.processInstance.api.model.data;

import static java.util.Collections.*;
import static org.camunda.community.migration.processInstance.api.model.data.Builder.*;
import static org.camunda.community.migration.processInstance.api.model.data.Builder.Json.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

public class DataSerializationTest {

  private Stream<ProcessInstanceData> testData() {
    return Stream.of(
        processInstanceData()
            .withBpmnProcessId("invoice")
            .withId("1e88a1fa-b9a0-11ed-8966-3ce1a1c19785")
            .withName("Invoice Receipt")
            .withActivity(
                "approveInvoice", Builder.userTaskData().withName("Approve invoice").build())
            .withVariable("amount", number(300.0D))
            .withVariable("approverGroups", array(text("accounting"), text("sales")))
            .withVariable("creditor", text("Moby Ferries"))
            .withVariable("invoiceCategory", text("Travel expenses"))
            .withVariable("invoiceDocument", text("Some file reference"))
            .withVariable("invoiceNumber", text("837hfz6e-hneze"))
            .build(),
        processInstanceData()
            .withBpmnProcessId("invoice")
            .withId("1e88a1f5-b9a0-11ed-8966-3ce1a1c19785")
            .withName("Invoice Receipt")
            .withActivity(
                "reviewInvoice",
                callActivityData()
                    .withName("Review Invoice")
                    .withProcessInstance(
                        processInstanceData()
                            .withBpmnProcessId("ReviewInvoice")
                            .withName("Review Invoice")
                            .withActivity(
                                "reviewInvoice", userTaskData().withName("Review Invoice").build())
                            .build())
                    .withVariable("amount", number(300.0D))
                    .withVariable("approverGroups", array(text("accounting"), text("sales")))
                    .withVariable("creditor", text("Moby Ferries"))
                    .withVariable("invoiceCategory", text("Travel expenses"))
                    .withVariable("invoiceDocument", text("Some file reference"))
                    .withVariable("invoiceNumber", text("837hfz6e-hneze"))
                    .build())
            .withVariable("amount", number(300.0D))
            .withVariable("approverGroups", array(text("accounting"), text("sales")))
            .withVariable("creditor", text("Moby Ferries"))
            .withVariable("invoiceCategory", text("Travel expenses"))
            .withVariable("invoiceDocument", text("Some file reference"))
            .withVariable("invoiceNumber", text("837hfz6e-hneze"))
            .build());
  }

  @TestFactory
  Stream<DynamicTest> shouldSerializeAndDeserialize() {
    return testData()
        .map(
            data ->
                DynamicTest.dynamicTest(
                    String.join(" - ", data.getBpmnProcessId(), data.getId()),
                    () -> testSerializeAndDeserialize(data)));
  }

  private void testSerializeAndDeserialize(ProcessInstanceData processInstanceData)
      throws JsonProcessingException {
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
  void shouldSerializeSimpleProcessInstanceData() throws JsonProcessingException {
    ProcessInstanceData processInstanceData =
        processInstanceData()
            .withBpmnProcessId("invoice")
            .withId("1e88a1fa-b9a0-11ed-8966-3ce1a1c19785")
            .withName("Invoice Receipt")
            .withActivity("approveInvoice", userTaskData().withName("Approve invoice").build())
            .withVariable("amount", number(300.0D))
            .withVariable("approverGroups", array(text("accounting"), text("sales")))
            .withVariable("creditor", text("Moby Ferries"))
            .withVariable("invoiceCategory", text("Travel expenses"))
            .withVariable("invoiceDocument", text("Some file reference"))
            .withVariable("invoiceNumber", text("837hfz6e-hneze"))
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
        processInstanceData()
            .withBpmnProcessId("invoice")
            .withId("1e88a1fa-b9a0-11ed-8966-3ce1a1c19785")
            .withName("Invoice Receipt")
            .withActivity(
                "reviewInvoice",
                callActivityData()
                    .withName("Review Invoice")
                    .withProcessInstance(
                        processInstanceData()
                            .withBpmnProcessId("ReviewInvoice")
                            .withName("Review Invoice")
                            .withActivity(
                                "reviewInvoice", userTaskData().withName("Review Invoice").build())
                            .build())
                    .withVariable("amount", number(300.0D))
                    .withVariable("approverGroups", array(text("accounting"), text("sales")))
                    .withVariable("creditor", text("Moby Ferries"))
                    .withVariable("invoiceCategory", text("Travel expenses"))
                    .withVariable("invoiceDocument", text("Some file reference"))
                    .withVariable("invoiceNumber", text("837hfz6e-hneze"))
                    .build())
            .withVariable("amount", number(300.0D))
            .withVariable("approverGroups", array(text("accounting"), text("sales")))
            .withVariable("creditor", text("Moby Ferries"))
            .withVariable("invoiceCategory", text("Travel expenses"))
            .withVariable("invoiceDocument", text("Some file reference"))
            .withVariable("invoiceNumber", text("837hfz6e-hneze"))
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
