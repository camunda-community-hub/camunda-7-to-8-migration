package org.camunda.community.migration.juel;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.VariableScope;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.javax.el.ELContext;
import org.camunda.bpm.engine.impl.javax.el.ExpressionFactory;
import org.camunda.bpm.engine.impl.javax.el.ValueExpression;
import org.springframework.stereotype.Component;

@Component
public class JuelExpressionResolver {

  private final ExpressionManager expressionManager;
  private final ExpressionFactory expressionFactory;
  private final ELContext elContext;

  public JuelExpressionResolver(
      ExpressionManager expressionManager, ExpressionFactory expressionFactory, ELContext elContext
  ) {
    this.expressionManager = expressionManager;
    this.elContext = elContext;
    this.expressionFactory = expressionFactory;
  }

  public Object evaluate(String expressionString, VariableScope variableScope, DelegateExecution execution) {
    ValueExpression valueExpression = expressionFactory.createValueExpression(elContext,
        expressionString,
        Object.class
    );
    return new EnginelessJuelExpression(valueExpression, expressionManager, expressionString).getValue(variableScope,
        execution
    );
  }
}
