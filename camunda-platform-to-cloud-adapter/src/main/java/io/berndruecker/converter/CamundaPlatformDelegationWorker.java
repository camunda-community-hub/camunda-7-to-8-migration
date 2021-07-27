package io.berndruecker.converter;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

public class CamundaPlatformDelegationWorker {

    @ZeebeWorker(type="camunda-platform-to-cloud-migration")
    public void delegateToOldCode(final JobClient client, final ActivatedJob job) {

        job.getCustomHeaders().get("class");
        job.getCustomHeaders().get("delegateExpression");
        job.getCustomHeaders().get("expression");
    }

}