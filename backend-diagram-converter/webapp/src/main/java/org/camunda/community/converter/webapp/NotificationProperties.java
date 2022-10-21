package org.camunda.community.converter.webapp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("notification")
@Configuration
public class NotificationProperties {
  @NestedConfigurationProperty private SlackProperties slack;

  public SlackProperties getSlack() {
    return slack;
  }

  public void setSlack(SlackProperties slack) {
    this.slack = slack;
  }
}
