package org.camunda.community.converter;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.impl.util.IoUtil;
import org.camunda.community.converter.BpmnDiagramCheckResult.BpmnElementCheckMessage;
import org.camunda.community.converter.BpmnDiagramCheckResult.BpmnElementCheckResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class BpmnConverterTest {

  @ParameterizedTest
  @CsvSource(value = {"example-c7.bpmn", "example-c7_2.bpmn", "java-delegate-class-c7.bpmn"})
  public void shouldConvert(String bpmnFile) {
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    BpmnModelInstance modelInstance =
        Bpmn.readModelFromStream(this.getClass().getClassLoader().getResourceAsStream(bpmnFile));
    BpmnDiagramCheckResult result = converter.check(bpmnFile, modelInstance, true);
    String processModel = IoUtil.convertXmlDocumentToString(modelInstance.getDocument());
    ByteArrayInputStream stream = new ByteArrayInputStream(processModel.getBytes());
    io.camunda.zeebe.model.bpmn.Bpmn.readModelFromStream(stream);
  }

  @Test
  public void shouldNotConvertC8() {
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    BpmnModelInstance modelInstance =
        Bpmn.readModelFromStream(
            this.getClass().getClassLoader().getResourceAsStream("c8_simple.bpmn"));
    Assertions.assertThrows(RuntimeException.class, () -> converter.convert(modelInstance, false));
  }

  @Test
  public void testDelegateHint() {
    String bpmnFile = "java-delegate-class-c7.bpmn";
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    BpmnModelInstance modelInstance =
        Bpmn.readModelFromStream(this.getClass().getClassLoader().getResourceAsStream(bpmnFile));
    BpmnDiagramCheckResult result =
        converter.check("java-delegate-class-c7.bpmn", modelInstance, false);
    BpmnElementCheckResult delegateClassServiceTask = result.getResult("DelegateClassServiceTask");
    assertNotNull(delegateClassServiceTask);
    assertThat(delegateClassServiceTask.getMessages()).hasSize(1);
    BpmnElementCheckMessage message = delegateClassServiceTask.getMessages().get(0);
    assertEquals(
        "Attribute 'class' on 'serviceTask' was mapped. Delegate call to 'com.camunda.consulting.MyDelegate' was transformed to job type 'camunda-7-adapter'. Please review your implementation.",
        message.getMessage());
  }
}
