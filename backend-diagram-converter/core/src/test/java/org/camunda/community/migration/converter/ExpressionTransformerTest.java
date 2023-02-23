package org.camunda.community.migration.converter;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.camunda.community.migration.converter.expression.ExpressionTransformer;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public class ExpressionTransformerTest {

  private static ExpressionTestBuilder expression(String expression) {
    return new ExpressionTestBuilder(expression);
  }

  @TestFactory
  public Stream<DynamicContainer> shouldResolveExpression() {
    return Stream.of(
            expression("${someVariable}").hasResult("=someVariable"),
            expression("someStaticValue").hasResult("someStaticValue"),
            expression("${var.innerField}").hasResult("=var.innerField"),
            expression("hello-${World}").hasResult("=\"hello-\" + World"),
            expression("#{x}").hasResult("=x"),
            expression("${x}").hasResult("=x"),
            expression("#{x>5}").hasResult("=x>5"),
            expression("#{x gt 5}").hasResult("=x > 5"),
            expression("#{x < 5}").hasResult("=x < 5"),
            expression("#{x lt 5}").hasResult("=x < 5"),
            expression("#{x==5}").hasResult("=x=5"),
            expression("#{x!=5}").hasResult("=x!=5"),
            expression("#{x eq 5}").hasResult("=x = 5"),
            expression("#{x ne 5}").hasResult("=x != 5"),
            expression("#{x == \"test\"}").hasResult("=x = \"test\""),
            expression("#{true}").hasResult("=true"),
            expression("${false}").hasResult("=false"),
            expression("#{!x}").hasResult("=not(x)"),
            expression("${!x and y}").hasResult("=not(x) and y"),
            expression("${!(x and y)}").hasResult("=not(x and y)"),
            expression("#{!true}").hasResult("=not(true)"),
            expression("#{not(x>5)}").hasResult("=not(x>5)"),
            expression("${not x}").hasResult("=not(x)"),
            expression("#{x && y}").hasResult("=x and y"),
            expression("#{x and y}").hasResult("=x and y"),
            expression("#{x || y}").hasResult("=x or y"),
            expression("#{x or y}").hasResult("=x or y"),
            expression("#{customer.name}").hasResult("=customer.name"),
            expression("#{customer.address[\"street\"]}").hasResult("=customer.address.street"),
            expression("#{customer.orders[1]}").hasResult("=customer.orders[2]"),
            expression("${not empty x}").hasResult("=not(x=null)"),
            expression("${empty donut}").hasResult("=donut=null"),
            expression("${!empty donut}").hasResult("=not(donut=null)"),
            expression("${empty donut || coffee}").hasResult("=donut=null or coffee"),
            expression("${not empty donut || coffee}").hasResult("=not(donut=null) or coffee"),
            expression("${not(empty donut || coffee)}").hasResult("=not(donut=null or coffee)"),
            expression("${execution.getVariable(\"a\")}").hasUsedExecution(true),
            expression("${myexecutionContext.isSpecial()}").hasUsedExecution(false),
            expression("${var.getSomething()}").hasMethodInvocation(true),
            expression("${!dauerbuchungVoat21Ids.isEmpty()}").hasMethodInvocation(true),
            expression("${!dauerbuchungVoat21Ids.contains(\"someText\")}")
                .hasMethodInvocation(true),
            expression("${input > 5.5}").hasMethodInvocation(false))
        .map(
            data ->
                DynamicContainer.dynamicContainer(
                    data.getResult().getOldExpression(), data.getTests()));
  }

  private static class ExpressionTestBuilder {
    private final ExpressionTransformationResult result;
    private final List<DynamicTest> tests = new ArrayList<>();

    public ExpressionTestBuilder(String expression) {
      this.result = ExpressionTransformer.transform(expression);
    }

    public ExpressionTransformationResult getResult() {
      return result;
    }

    public List<DynamicTest> getTests() {
      return tests;
    }

    public ExpressionTestBuilder hasResult(String expectedResult) {
      tests.add(
          DynamicTest.dynamicTest(
              "Expect Result: '" + expectedResult + "'",
              () -> assertThat(result.getNewExpression()).isEqualTo(expectedResult)));
      return this;
    }

    public ExpressionTestBuilder hasMethodInvocation(boolean expected) {
      tests.add(
          DynamicTest.dynamicTest(
              String.format("Expect %s method invocation", expected ? "a" : "no"),
              () -> assertThat(result.hasMethodInvocation()).isEqualTo(expected)));
      return this;
    }

    public ExpressionTestBuilder hasUsedExecution(boolean expected) {
      tests.add(
          DynamicTest.dynamicTest(
              String.format("Expect %s execution used", expected ? "a" : "no"),
              () -> assertThat(result.hasExecution()).isEqualTo(expected)));
      return this;
    }
  }
}
