package org.camunda.community.migration.processInstance;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.camunda.bpm.engine.runtime.ProcessInstance;

public class MigrationTestProcessInstanceInput {
  private final String scenarioName;
  private final Map<String, Object> variables;
  private final List<Consumer<ProcessInstance>> processSteps;
  private final List<String> routingActivities;

  public MigrationTestProcessInstanceInput(
      String scenarioName,
      Map<String, Object> variables,
      List<Consumer<ProcessInstance>> processSteps,
      List<String> routingActivities) {
    this.scenarioName = scenarioName;
    this.variables = variables;
    this.processSteps = processSteps;
    this.routingActivities = routingActivities;
  }

  public String getScenarioName() {
    return scenarioName;
  }

  public Map<String, Object> getVariables() {
    return variables;
  }

  public List<Consumer<ProcessInstance>> getProcessSteps() {
    return processSteps;
  }

  public List<String> getRoutingActivities() {
    return routingActivities;
  }
}
