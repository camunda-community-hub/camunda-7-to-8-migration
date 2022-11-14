package org.camunda.community.converter.message;

import java.util.Map;

public class ComposedMessage implements Message {
  private static final MessageTemplateProvider MESSAGE_TEMPLATE_PROVIDER =
      new MessageTemplateProvider();
  private static final MessageTemplateProcessor MESSAGE_TEMPLATE_DECORATOR =
      new MessageTemplateProcessor();
  private static final MessageLinkProvider MESSAGE_LINK_PROVIDER = new MessageLinkProvider();
  private final String message;
  private final String link;

  public ComposedMessage(String templateName, Map<String, String> context) {
    MessageTemplate template = MESSAGE_TEMPLATE_PROVIDER.getMessageTemplate(templateName);
    this.message = MESSAGE_TEMPLATE_DECORATOR.decorate(template, context);
    this.link = MESSAGE_LINK_PROVIDER.getMessageLink(templateName);
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public String getLink() {
    return link;
  }
}
