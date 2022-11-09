package org.camunda.community.converter.cli;

import org.camunda.community.converter.NotificationService;

public class PrintNotificationServiceImpl implements NotificationService {
  @Override
  public void notify(Object object) {
    if (object instanceof RuntimeException) {
      throw (RuntimeException) object;
    } else {
      System.out.println(object);
    }
  }
}
