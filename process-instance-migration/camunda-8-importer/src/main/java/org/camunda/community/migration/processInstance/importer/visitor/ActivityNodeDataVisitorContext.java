package org.camunda.community.migration.processInstance.importer.visitor;

import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep3;
import java.util.function.Consumer;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface ActivityNodeDataVisitorContext {

  ActivityNodeData getActivityNodeData();

  String getActivityId();

  void addStartInstruction(Consumer<CreateProcessInstanceCommandStep3> instruction);

  void addSpecialInstruction(Runnable instruction);

  void addChildData(String name, ActivityNodeData data);

  void addManualStep(String instruction);
}
