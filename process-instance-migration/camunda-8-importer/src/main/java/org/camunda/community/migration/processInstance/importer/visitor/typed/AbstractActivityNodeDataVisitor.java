package org.camunda.community.migration.processInstance.importer.visitor.typed;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.importer.visitor.typed.TypedActivityNodeDataVisitor;
import org.camunda.community.migration.processInstance.importer.visitor.typed.TypedActivityNodeDataVisitorContext;

public abstract class AbstractActivityNodeDataVisitor<T extends ActivityNodeData>
    extends TypedActivityNodeDataVisitor<T> {
  @Override
  protected final void handle(TypedActivityNodeDataVisitorContext<T> context) {
    context.addStartInstruction(command -> command.startBeforeElement(context.getActivityId()));
    // TODO set variables contained to correct scope
  }

  protected abstract void doHandle(TypedActivityNodeDataVisitorContext<T> context);
}
