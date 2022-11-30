package org.camunda.community.migration.converter.cli;

import static org.camunda.community.migration.converter.cli.ConvertCommand.*;

import org.camunda.community.migration.converter.NotificationService;

public class PrintNotificationServiceImpl implements NotificationService {
  @Override
  public void notify(Object object) {
    if (object instanceof RuntimeException) {
      throw (RuntimeException) object;
    } else {
      LOG_CLI.info("{}", object);
    }
  }
}
