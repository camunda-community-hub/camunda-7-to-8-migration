package org.camunda.community.converter.webapp;

import com.slack.api.Slack;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.files.FilesUploadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Service
public class NotificationService {
  private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);
  private final NotificationProperties notificationProperties;

  @Autowired
  public NotificationService(NotificationProperties notificationProperties) {
    this.notificationProperties = notificationProperties;
  }

  public void notify(Exception e) {
    notifySlack(e);
  }

  private void notifySlack(Exception e) {
    SlackProperties slackProperties = notificationProperties.getSlack();
    if (!slackProperties.isEnabled()) {
      return;
    }
    Slack slack = Slack.getInstance();
    try {
      slack
          .methodsAsync(slackProperties.getToken())
          .chatPostMessage(
              ChatPostMessageRequest.builder()
                  .channel(slackProperties.getChannelName())
                  .text(
                      "An exception happened. Please review in the attached stacktrace\n```"
                          + e.getMessage()
                          + "```")
                  .build())
          .join();
      slack
          .methodsAsync(slackProperties.getToken())
          .filesUpload(
              FilesUploadRequest.builder()
                  .channels(Collections.singletonList(slackProperties.getChannelName()))
                  .fileData(buildText(e).getBytes())
                  .filename(buildFilename())
                  .build())
          .join();
    } catch (Exception ex) {
      LOG.error("Exception while sending notification", ex);
    }
  }

  private String buildFilename() {
    String filename =
        "stacktrace_" + ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + ".txt";
    LOG.debug("Filename: {}", filename);
    return filename;
  }

  private String buildText(Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    String stacktrace = sw.toString();
    return "```\n" + stacktrace + "\n```";
  }
}
