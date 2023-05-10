package org.camunda.community.migration.examples.worker.legacy;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import static org.camunda.community.migration.examples.worker.C7WorkerWithListeners.logger;

@Component("legacyStartListener")
public class OnStartExecutionListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) throws Exception {
        logger.info("running: OnStartExecutionListener");

        execution.setVariable("value", 1);
        execution.setVariable("a1", "A 1");
        execution.setVariable("a2", "some stuff");

    }
}
