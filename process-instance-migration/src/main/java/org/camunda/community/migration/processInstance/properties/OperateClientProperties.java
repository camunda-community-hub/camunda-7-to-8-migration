package org.camunda.community.migration.processInstance.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("operate.client")
public class OperateClientProperties {
  private final AuthenticationProperties authentication = new AuthenticationProperties();
  private String baseUrl;

  public AuthenticationProperties getAuthentication() {
    return authentication;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public static class AuthenticationProperties {
    private SimpleAuthenticationProperties simple;
    private SaasAuthenticationProperties saas;
    private SelfManagedAuthenticationProperties selfManaged;

    public SimpleAuthenticationProperties getSimple() {
      return simple;
    }

    public void setSimple(SimpleAuthenticationProperties simple) {
      this.simple = simple;
    }

    public SaasAuthenticationProperties getSaas() {
      return saas;
    }

    public void setSaas(SaasAuthenticationProperties saas) {
      this.saas = saas;
    }

    public SelfManagedAuthenticationProperties getSelfManaged() {
      return selfManaged;
    }

    public void setSelfManaged(SelfManagedAuthenticationProperties selfManaged) {
      this.selfManaged = selfManaged;
    }
  }

  public static class SimpleAuthenticationProperties {
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

  public static class SaasAuthenticationProperties {
    private String clientId;
    private String clientSecret;

    public String getClientId() {
      return clientId;
    }

    public void setClientId(String clientId) {
      this.clientId = clientId;
    }

    public String getClientSecret() {
      return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
      this.clientSecret = clientSecret;
    }
  }

  public static class SelfManagedAuthenticationProperties extends SaasAuthenticationProperties {

    private String keycloakUrl;
    private String keycloakRealm;

    public String getKeycloakUrl() {
      return keycloakUrl;
    }

    public void setKeycloakUrl(String keycloakUrl) {
      this.keycloakUrl = keycloakUrl;
    }

    public String getKeycloakRealm() {
      return keycloakRealm;
    }

    public void setKeycloakRealm(String keycloakRealm) {
      this.keycloakRealm = keycloakRealm;
    }
  }
}
