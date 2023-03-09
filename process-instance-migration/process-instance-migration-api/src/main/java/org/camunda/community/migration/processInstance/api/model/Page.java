package org.camunda.community.migration.processInstance.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.camunda.community.migration.processInstance.api.model.FinalBuildStep.FinalBuildStepImpl;
import org.camunda.community.migration.processInstance.api.model.Page.PageImpl;

@JsonDeserialize(as = PageImpl.class)
public interface Page<T> {
  static <T> PageBuilder<T> builder() {
    return new PageBuilderImpl<>();
  }

  long getStart();

  long getLimit();

  long getTotal();

  List<T> getResults();

  interface PageBuilder<T> extends FinalBuildStep<Page<T>> {
    PageBuilder<T> withStart(long start);

    PageBuilder<T> withLimit(long limit);

    PageBuilder<T> withTotal(long total);

    PageBuilder<T> withResults(List<T> results);
  }

  class PageImpl<T> implements Page<T> {
    private long start;
    private long limit;
    private long total;
    private List<T> results;

    @Override
    public long getStart() {
      return start;
    }

    public void setStart(long start) {
      this.start = start;
    }

    @Override
    public long getLimit() {
      return limit;
    }

    public void setLimit(long limit) {
      this.limit = limit;
    }

    @Override
    public long getTotal() {
      return total;
    }

    public void setTotal(long total) {
      this.total = total;
    }

    @Override
    public List<T> getResults() {
      return results;
    }

    public void setResults(List<T> results) {
      this.results = results;
    }
  }

  class PageBuilderImpl<T> extends FinalBuildStepImpl<Page<T>, PageImpl<T>>
      implements PageBuilder<T> {
    @Override
    protected PageImpl<T> createData() {
      return new PageImpl<>();
    }

    @Override
    public PageBuilder<T> withStart(long start) {
      data.setStart(start);
      return this;
    }

    @Override
    public PageBuilder<T> withLimit(long limit) {
      data.setLimit(limit);
      return this;
    }

    @Override
    public PageBuilder<T> withTotal(long total) {
      data.setTotal(total);
      return this;
    }

    @Override
    public PageBuilder<T> withResults(List<T> results) {
      data.setResults(results);
      return this;
    }
  }
}
