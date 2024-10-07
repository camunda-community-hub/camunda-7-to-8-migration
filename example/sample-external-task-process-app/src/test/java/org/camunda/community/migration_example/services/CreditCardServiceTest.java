package org.camunda.community.migration_example.services;

import static org.assertj.core.api.Assertions.*;
import org.camunda.community.migration_example.services.CreditCardService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CreditCardServiceTest {
  
  public CreditCardService service = new CreditCardService();
  
  @ParameterizedTest
  @ValueSource(strings = {"01/30", "09/30"})
  public void testValidExpiryDates(String expiryDate) {
    assertThat(service.validateExpiryDate(expiryDate)).isTrue();
  }
  
  @ParameterizedTest
  @ValueSource(strings = {"00/23", "13/25", "02/22", "123456" })
  public void testInvalidExpiryDates(String expiryDate) {
    assertThat(service.validateExpiryDate(expiryDate)).isFalse();
  }
}
