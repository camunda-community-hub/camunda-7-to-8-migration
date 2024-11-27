package org.camunda.community.migration.converter;

import static org.assertj.core.api.Assertions.*;
import static org.camunda.community.migration.converter.visitor.AbstractDelegateImplementationVisitor.*;

import java.util.regex.Matcher;
import org.junit.jupiter.api.Test;

public class DelegateImplementationVisitorTest {
  @Test
  void shouldReturnDelegateName() {
    String delegateExpression = "${someReallyCoolDelegate}";
    Matcher matcher = DELEGATE_NAME_EXTRACT.matcher(delegateExpression);
    String delegateName = matcher.find() ? matcher.group(1) : null;
    assertThat(delegateName).isNotNull().isEqualTo("someReallyCoolDelegate");
  }
}
