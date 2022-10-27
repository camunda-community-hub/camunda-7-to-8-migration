package org.camunda.community.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.camunda.bpm.model.bpmn.Bpmn;

public class BpmnModelInstanceUtil {

  public static org.camunda.bpm.model.bpmn.BpmnModelInstance fromResource(String resourceName) {
    try (InputStream in =
        BpmnModelInstanceUtil.class.getClassLoader().getResourceAsStream(resourceName)) {
      return Bpmn.readModelFromStream(in);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static io.camunda.zeebe.model.bpmn.BpmnModelInstance transform(
      org.camunda.bpm.model.bpmn.BpmnModelInstance modelInstance) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    org.camunda.bpm.model.bpmn.Bpmn.writeModelToStream(out, modelInstance);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    return io.camunda.zeebe.model.bpmn.Bpmn.readModelFromStream(in);
  }
}
