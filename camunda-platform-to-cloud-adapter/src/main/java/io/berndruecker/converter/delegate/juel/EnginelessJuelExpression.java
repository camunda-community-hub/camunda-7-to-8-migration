package io.berndruecker.converter.delegate.juel;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.delegate.BaseDelegateExecution;
import org.camunda.bpm.engine.delegate.VariableScope;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.el.JuelExpression;
import org.camunda.bpm.engine.impl.javax.el.*;

public class EnginelessJuelExpression extends JuelExpression {

    public EnginelessJuelExpression(ValueExpression valueExpression, ExpressionManager expressionManager, String expressionText) {
        super(valueExpression, expressionManager, expressionText);
    }

    @Override
    public Object getValue(VariableScope variableScope, BaseDelegateExecution contextExecution) {
        ELContext elContext = expressionManager.getElContext(variableScope);
        try {
            return valueExpression.getValue(elContext);
//            ExpressionGetInvocation invocation = new ExpressionGetInvocation(valueExpression, elContext, contextExecution);
//            new DefaultDelegateInterceptor().handleInvocation(invocation);
//            return invocation.getInvocationResult();
        } catch (PropertyNotFoundException pnfe) {
            throw new ProcessEngineException("Unknown property used in expression: " + expressionText+". Cause: "+pnfe.getMessage(), pnfe);
        } catch (MethodNotFoundException mnfe) {
            throw new ProcessEngineException("Unknown method used in expression: " + expressionText+". Cause: "+mnfe.getMessage(), mnfe);
        } catch(ELException ele) {
            throw new ProcessEngineException("Error while evaluating expression: " + expressionText+". Cause: "+ele.getMessage(), ele);
        } catch (Exception e) {
            throw new ProcessEngineException("Error while evaluating expression: " + expressionText+". Cause: "+e.getMessage(), e);
        }
    }
}
