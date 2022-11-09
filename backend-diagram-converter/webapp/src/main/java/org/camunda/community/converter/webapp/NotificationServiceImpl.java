package org.camunda.community.converter.webapp;

import com.slack.api.Slack;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.files.FilesUploadRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import org.camunda.community.converter.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {
  private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);
  private final NotificationProperties notificationProperties;

  @Autowired
  public NotificationServiceImpl(NotificationProperties notificationProperties) {
    this.notificationProperties = notificationProperties;
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

  @Override
  public void notify(Object object) {
    if (object instanceof Exception) {
      notifySlack((Exception) object);
    }
  }
}
