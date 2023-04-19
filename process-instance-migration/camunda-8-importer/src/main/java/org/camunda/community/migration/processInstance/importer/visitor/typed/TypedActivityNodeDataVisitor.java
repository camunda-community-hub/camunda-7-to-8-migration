package org.camunda.community.migration.processInstance.importer.visitor.typed;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.importer.visitor.ActivityNodeDataVisitor;
import org.camunda.community.migration.processInstance.importer.visitor.ActivityNodeDataVisitorContext;

public abstract class TypedActivityNodeDataVisitor<T extends ActivityNodeData>
    implements ActivityNodeDataVisitor {

  @Override
  public final void handle(ActivityNodeDataVisitorContext context) {
    if (getType().isAssignableFrom(context.getActivityNodeData().getClass())) {
      handle(TypedActivityNodeDataVisitorContext.from(context, getType()));
    }
  }

  protected abstract void handle(TypedActivityNodeDataVisitorContext<T> context);

  protected abstract Class<T> getType();
}
