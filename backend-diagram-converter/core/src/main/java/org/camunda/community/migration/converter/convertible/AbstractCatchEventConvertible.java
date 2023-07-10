package org.camunda.community.migration.converter.convertible;

public abstract class AbstractCatchEventConvertible extends AbstractDataMapperConvertible {
  private String timeDurationExpression;
  private String timeCycleExpression;
  private String timeDateExpression;

  public String getTimeDurationExpression() {
    return timeDurationExpression;
  }

  public void setTimeDurationExpression(String timeDurationExpression) {
    this.timeDurationExpression = timeDurationExpression;
  }

  public String getTimeCycleExpression() {
    return timeCycleExpression;
  }

  public void setTimeCycleExpression(String timeCycleExpression) {
    this.timeCycleExpression = timeCycleExpression;
  }

  public String getTimeDateExpression() {
    return timeDateExpression;
  }

  public void setTimeDateExpression(String timeDateExpression) {
    this.timeDateExpression = timeDateExpression;
  }
}
