package org.camunda.community.migration.processInstance.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("camunda7.client")
@Configuration
public class Camunda7ClientProperties {
  private String baseUrl;

  private Camunda7ClientAuthentication authentication = new Camunda7ClientAuthentication();

  public Camunda7ClientAuthentication getAuthentication() {
    return authentication;
  }

  public void setAuthentication(Camunda7ClientAuthentication authentication) {
    this.authentication = authentication;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public static class Camunda7ClientAuthentication {
    private Camunda7BasicAuthAuthentication basicAuth = new Camunda7BasicAuthAuthentication();
    private String custom;

    public Camunda7BasicAuthAuthentication getBasicAuth() {
      return basicAuth;
    }

    public void setBasicAuth(Camunda7BasicAuthAuthentication basicAuth) {
      this.basicAuth = basicAuth;
    }

    public String getCustom() {
      return custom;
    }

    public void setCustom(String custom) {
      this.custom = custom;
    }
  }

  public static class Camunda7BasicAuthAuthentication {
    private String username;
    private String password;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }
}
