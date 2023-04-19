package org.camunda.community.migration.processInstance.importer.visitor.typed;

import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep3;
import java.util.function.Consumer;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.importer.visitor.ActivityNodeDataVisitorContext;

public interface TypedActivityNodeDataVisitorContext<T extends ActivityNodeData>
    extends ActivityNodeDataVisitorContext {
  static <T extends ActivityNodeData> TypedActivityNodeDataVisitorContext<T> from(
      ActivityNodeDataVisitorContext context, Class<T> type) {
    return new TypedActivityNodeDataVisitorContext<T>() {

      @Override
      public T getActivityNodeData() {
        return context.getActivityNodeData().as(type);
      }

      @Override
      public String getActivityId() {
        return context.getActivityId();
      }

      @Override
      public void addStartInstruction(Consumer<CreateProcessInstanceCommandStep3> instruction) {
        context.addStartInstruction(instruction);
      }

      @Override
      public void addSpecialInstruction(Runnable instruction) {
        context.addSpecialInstruction(instruction);
      }

      @Override
      public void addChildData(String name, ActivityNodeData data) {
        context.addChildData(name, data);
      }

      @Override
      public void addManualStep(String instruction) {
        context.addManualStep(instruction);
      }
    };
  }

  @Override
  T getActivityNodeData();
}
