package org.camunda.community.migration.processInstance.core;

import static io.camunda.zeebe.protocol.Protocol.*;
import static org.assertj.core.api.Assertions.*;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivateJobsResponse;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.community.migration.converter.BpmnConverter;
import org.camunda.community.migration.converter.BpmnConverterFactory;
import org.camunda.community.migration.converter.ConverterPropertiesFactory;

public class TestUtil {
  static ZeebeClient zeebeClient;

  public static void completeUserTask(Object variables) {
    ActivateJobsResponse response =
        zeebeClient
            .newActivateJobsCommand()
            .jobType(USER_TASK_JOB_TYPE)
            .maxJobsToActivate(1)
            .send()
            .join();
    assertThat(response.getJobs()).isNotEmpty().hasSize(1);
    ActivatedJob userTask = response.getJobs().get(0);
    zeebeClient.newCompleteCommand(userTask).variables(variables).send().join();
  }

  public static void deployCamunda7ProcessToZeebe(String resourceName) {
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    try (InputStream in =
        TestUtil.class.getClassLoader().getResourceAsStream("user-tasks-linear.bpmn")) {
      BpmnModelInstance model = Bpmn.readModelFromStream(in);
      converter.convert(model, false, ConverterPropertiesFactory.getInstance().get());
      StringWriter writer = new StringWriter();
      converter.printXml(model.getDocument(), true, writer);
      ByteArrayInputStream stream = new ByteArrayInputStream(writer.toString().getBytes());
      io.camunda.zeebe.model.bpmn.BpmnModelInstance zeebeModel =
          io.camunda.zeebe.model.bpmn.Bpmn.readModelFromStream(stream);
      zeebeClient
          .newDeployResourceCommand()
          .addProcessModel(zeebeModel, "user-tasks-linear.bpmn")
          .send()
          .join();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
