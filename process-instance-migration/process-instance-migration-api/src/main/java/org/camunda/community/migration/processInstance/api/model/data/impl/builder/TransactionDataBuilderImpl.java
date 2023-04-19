package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import static org.camunda.community.migration.processInstance.api.model.data.impl.builder.BuilderUtil.*;

import java.util.List;
import java.util.Map;
import org.camunda.community.migration.processInstance.api.model.data.TransactionData;
import org.camunda.community.migration.processInstance.api.model.data.TransactionData.TransactionDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.impl.TransactionDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

public class TransactionDataBuilderImpl
    extends ActivityNodeDataBuilderImpl<
        TransactionDataBuilder, TransactionData, TransactionDataImpl>
    implements TransactionDataBuilder {
  @Override
  protected TransactionDataImpl createData() {
    return new TransactionDataImpl();
  }

  @Override
  public TransactionDataBuilder withActivity(String name, ActivityNodeData activity) {
    addMapEntry(data::getActivities, data::setActivities, name, activity);
    return this;
  }

  @Override
  public TransactionDataBuilder withActivities(Map<String, ActivityNodeData> activities) {
    activities.forEach(this::withActivity);
    return this;
  }

  @Override
  protected TransactionDataBuilder builder() {
    return this;
  }

  @Override
  protected ActivityNodeDataImpl data() {
    return data;
  }
}
