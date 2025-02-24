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
            expression("").isMappedTo("=null"),
            expression("${someVariable}").isMappedTo("=someVariable"),
            expression("someStaticValue").isMappedTo("someStaticValue"),
            expression("${var.innerField}").isMappedTo("=var.innerField"),
            expression("hello-${World}").isMappedTo("=\"hello-\" + World"),
            expression("#{x}").isMappedTo("=x"),
            expression("${x}").isMappedTo("=x"),
            expression("#{x>5}").isMappedTo("=x>5"),
            expression("#{x gt 5}").isMappedTo("=x > 5"),
            expression("#{x < 5}").isMappedTo("=x < 5"),
            expression("#{x lt 5}").isMappedTo("=x < 5"),
            expression("#{x==5}").isMappedTo("=x=5"),
            expression("#{x!=5}").isMappedTo("=x!=5"),
            expression("#{x eq 5}").isMappedTo("=x = 5"),
            expression("#{x ne 5}").isMappedTo("=x != 5"),
            expression("#{x == \"test\"}").isMappedTo("=x = \"test\""),
            expression("#{true}").isMappedTo("=true"),
            expression("${false}").isMappedTo("=false"),
            expression("#{!x}").isMappedTo("=not(x)"),
            expression("${!x and y}").isMappedTo("=not(x) and y"),
            expression("${!(x and y)}").isMappedTo("=not(x and y)"),
            expression("#{!true}").isMappedTo("=not(true)"),
            expression("#{not(x>5)}").isMappedTo("=not(x>5)"),
            expression("${not x}").isMappedTo("=not(x)"),
            expression("#{x && y}").isMappedTo("=x and y"),
            expression("#{x and y}").isMappedTo("=x and y"),
            expression("#{x || y}").isMappedTo("=x or y"),
            expression("#{x or y}").isMappedTo("=x or y"),
            expression("#{customer.name}").isMappedTo("=customer.name"),
            expression("#{customer.address[\"street\"]}").isMappedTo("=customer.address.street"),
            expression("#{customer.orders[1]}").isMappedTo("=customer.orders[2]"),
            expression("${not empty x}").isMappedTo("=not(x=null)"),
            expression("${empty donut}").isMappedTo("=donut=null"),
            expression("${!empty donut}").isMappedTo("=not(donut=null)"),
            expression("${empty donut || coffee}").isMappedTo("=donut=null or coffee"),
            expression("${not empty donut || coffee}").isMappedTo("=not(donut=null) or coffee"),
            expression("${not(empty donut || coffee)}").isMappedTo("=not(donut=null or coffee)"),
            expression("${execution.getVariable(\"a\")}").isMappedTo("=a"),
            expression("${execution.getProcessInstanceId()}").hasUsedExecution(true),
            expression("${myexecutionContext.isSpecial()}").hasUsedExecution(false),
            expression("${var.getSomething()}").hasMethodInvocation(true),
            expression("${!dauerbuchungVoat21Ids.isEmpty()}").hasMethodInvocation(true),
            expression("${!dauerbuchungVoat21Ids.contains(\"someText\")}")
                .hasMethodInvocation(true),
            expression("${input > 5.5}").hasMethodInvocation(false),
            expression("${input != ''}").isMappedTo("=input != \"\""),
            expression("${input != 'what the F***'}").isMappedTo("=input != \"what the F***\""))
        .map(
            data ->
                DynamicContainer.dynamicContainer(
                    "Expression: " + data.getResult().getJuelExpression(), data.getTests()));
  }

  private static class ExpressionTestBuilder {
    private final ExpressionTransformationResult result;
    private final List<DynamicTest> tests = new ArrayList<>();

    public ExpressionTestBuilder(String expression) {
      this.result = ExpressionTransformer.transform("Test", expression);
    }

    public ExpressionTransformationResult getResult() {
      return result;
    }

    public List<DynamicTest> getTests() {
      return tests;
    }

    public ExpressionTestBuilder isMappedTo(String expectedResult) {
      tests.add(
          DynamicTest.dynamicTest(
              "Expect Result: '" + expectedResult + "'",
              () -> assertThat(result.getFeelExpression()).isEqualTo(expectedResult)));
      return this;
    }

    public ExpressionTestBuilder hasMethodInvocation(boolean expected) {
      tests.add(
          DynamicTest.dynamicTest(
              String.format("Expect %s method invocation", expected ? "a" : "no"),
              () -> assertThat(result.getHasMethodInvocation()).isEqualTo(expected)));
      return this;
    }

    public ExpressionTestBuilder hasUsedExecution(boolean expected) {
      tests.add(
          DynamicTest.dynamicTest(
              String.format("Expect %s execution used", expected ? "a" : "no"),
              () -> assertThat(result.getHasExecutionOnly()).isEqualTo(expected)));
      return this;
    }
  }
}
