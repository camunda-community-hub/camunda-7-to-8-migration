package org.camunda.community.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.impl.util.IoUtil;
import org.camunda.community.converter.conversion.Conversion;
import org.camunda.community.converter.visitor.DomElementVisitor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class BpmnConverterTest {

  static Stream<Arguments> argProvider() {
    return Stream.of("example-c7.bpmn", "example-c7_2.bpmn")
        .flatMap(fileName -> IntStream.range(0, 100).mapToObj(i -> Arguments.of(fileName)));
  }

  @ParameterizedTest
  @MethodSource("argProvider")
  public void shouldConvert(String bpmnFile) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    Set<DomElementVisitor> visitors = DomElementVisitorFactory.getInstance().get();
    Set<Conversion> conversions = ConversionFactory.getInstance().get();
    BpmnConverter converter = new BpmnConverter(visitors, conversions);
    BpmnModelInstance modelInstance =
        Bpmn.readModelFromStream(this.getClass().getClassLoader().getResourceAsStream(bpmnFile));
    BpmnDiagramCheckResult result = converter.check(modelInstance, true);
    result =
        objectMapper.readValue(
            objectMapper.writeValueAsString(result), BpmnDiagramCheckResult.class);
    System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
    String processModel = IoUtil.convertXmlDocumentToString(modelInstance.getDocument());
    System.out.println(processModel);
    ByteArrayInputStream stream = new ByteArrayInputStream(processModel.getBytes());
    io.camunda.zeebe.model.bpmn.Bpmn.readModelFromStream(stream);
  }
}
