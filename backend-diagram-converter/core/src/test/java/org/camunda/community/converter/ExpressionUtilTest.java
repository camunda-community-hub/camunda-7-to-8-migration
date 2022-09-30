package org.camunda.community.converter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public class ExpressionUtilTest {

  @TestFactory
  public Stream<DynamicTest> shouldResolveExpression() {
    return DynamicTest.stream(
        Stream.of("${someVariable}", "someStaticValue", "${var.innerField}", "hello-${World}"),
        String::toString,
        this::testExpression);
  }

  private void testExpression(String expression) {
    Optional<String> result = ExpressionUtil.transform(expression, true);
    assertTrue(result.isPresent());
  }
}
