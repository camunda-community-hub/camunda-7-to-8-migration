package org.camunda.community.migration.examples.worker.legacy;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.community.migration.examples.worker.C7WorkerWithListeners;
import org.springframework.stereotype.Component;

import static org.camunda.community.migration.examples.worker.C7WorkerWithListeners.logger;

@Component("legacyEndListener")
public class OnEndExecutionListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) throws Exception {

        logger.info("running: OnEndExecutionListener");
        execution.setVariable("foo", 25);
        execution.setVariable("bar", "some stuff");

    }
}
