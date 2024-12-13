package org.camunda.community.migration.converter;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

public class TestUtil {

  public static Map<String, Object> mockVariables(String... names) {
    Map<String, Object> variables = new HashMap<>();
    for (String name : names) {
      variables.put(name, RandomStringUtils.random(20));
    }
    return variables;
  }

  public static Map<String, Object> mockVariables(int count) {
    String[] names = new String[count];
    for (int i = 0; i < count; i++) {
      names[i] = RandomStringUtils.random(5);
    }
    return mockVariables(names);
  }

  public static BpmnModelInstance loadAndConvert(String bpmnFile) {
    BpmnModelInstance modelInstance = loadModelInstance(bpmnFile);
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();
    converter.convert(modelInstance, properties);
    return modelInstance;
  }

  public static BpmnModelInstance loadAndConvert(String bpmnFile, String targetVersion) {
    BpmnModelInstance modelInstance = loadModelInstance(bpmnFile);
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    DefaultConverterProperties properties = new DefaultConverterProperties();
    properties.setPlatformVersion(targetVersion);
    converter.convert(modelInstance, ConverterPropertiesFactory.getInstance().merge(properties));
    return modelInstance;
  }

  public static BpmnModelInstance loadAndConvert(String bpmnFile, ConverterProperties properties) {
    BpmnModelInstance modelInstance = loadModelInstance(bpmnFile);
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    converter.convert(modelInstance, properties);
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
            bpmnFile, modelInstance, ConverterPropertiesFactory.getInstance().merge(properties));
    return result;
  }

  private static BpmnModelInstance loadModelInstance(String bpmnFile) {
    return Bpmn.readModelFromStream(TestUtil.class.getClassLoader().getResourceAsStream(bpmnFile));
  }
}
