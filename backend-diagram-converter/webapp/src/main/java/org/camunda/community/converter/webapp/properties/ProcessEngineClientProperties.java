package org.camunda.community.converter.webapp.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("camunda.bpm.client")
@Configuration
public class ProcessEngineClientProperties {
  private String baseUrl;
  private boolean enabled;
  private ProcessEngineClientSecurityProperties security =
      new ProcessEngineClientSecurityProperties();

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public ProcessEngineClientSecurityProperties getSecurity() {
    return security;
  }

  public void setSecurity(ProcessEngineClientSecurityProperties security) {
    this.security = security;
  }

  public static class ProcessEngineClientSecurityProperties {
    private ProcessEngineClientBasicAuthSecurity basicAuth =
        new ProcessEngineClientBasicAuthSecurity();

    public ProcessEngineClientBasicAuthSecurity getBasicAuth() {
      return basicAuth;
    }

    public void setBasicAuth(ProcessEngineClientBasicAuthSecurity basicAuth) {
      this.basicAuth = basicAuth;
    }
  }

  public static class ProcessEngineClientBasicAuthSecurity {
    private String username;
    private String password;
    private boolean enabled;

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

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }
}
