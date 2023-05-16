package org.camunda.community.migration.converter;

import java.util.HashMap;
import java.util.Map;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

public class TestUtil {
  public static Map<String, Object> mockVariables(int count) {
    Map<String, Object> variables = new HashMap<>();
    for (int i = 0; i < count; i++) {
      variables.put(RandomStringUtils.random(5), RandomStringUtils.random(20));
    }
    return variables;
  }

  public static BpmnModelInstance loadAndConvert(String bpmnFile) {
    BpmnModelInstance modelInstance = loadModelInstance(bpmnFile);
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();
    converter.convert(modelInstance, false, properties);
    return modelInstance;
  }

  public static BpmnDiagramCheckResult loadAndCheck(String bpmnFile) {
    ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();
    return loadAndCheckAgainstVersion(bpmnFile, properties.getPlatformVersion());
  }

  public static BpmnDiagramCheckResult loadAndCheckAgainstVersion(
      String bpmnFile, String targetVersion) {
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    BpmnModelInstance modelInstance = loadModelInstance(bpmnFile);
    DefaultConverterProperties properties = new DefaultConverterProperties();
    properties.setPlatformVersion(targetVersion);
    BpmnDiagramCheckResult result =
        converter.check(
            bpmnFile,
            modelInstance,
            false,
            ConverterPropertiesFactory.getInstance().merge(properties));
    return result;
  }

  private static BpmnModelInstance loadModelInstance(String bpmnFile) {
    return Bpmn.readModelFromStream(TestUtil.class.getClassLoader().getResourceAsStream(bpmnFile));
  }
}
