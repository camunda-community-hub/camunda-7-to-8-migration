package io.berndruecker.converter.delegate.juel;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.el.ProcessEngineElContext;
import org.camunda.bpm.engine.impl.javax.el.ExpressionFactory;
import org.camunda.bpm.engine.impl.javax.el.FunctionMapper;
import org.camunda.bpm.engine.impl.javax.el.ValueExpression;
import org.camunda.bpm.engine.impl.juel.ExpressionFactoryImpl;
import org.camunda.bpm.engine.spring.SpringExpressionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JuelExpressionResolver {

    @Autowired
    private ApplicationContext springApplicationContext;

    private ExpressionFactory expressionFactory = new ExpressionFactoryImpl();
    protected List<FunctionMapper> functionMappers = new ArrayList<FunctionMapper>();
    private ProcessEngineElContext elContext = new ProcessEngineElContext(functionMappers);

    public Object evaluate(String expressionString, DelegateExecution execution) {
        ExpressionManager expressionManager = new SpringExpressionManager(springApplicationContext, null);
        ValueExpression valueExpression = expressionFactory.createValueExpression(elContext, expressionString, Object.class);
        return new EnginelessJuelExpression(valueExpression, expressionManager, expressionString).getValue(execution);
    }
}
