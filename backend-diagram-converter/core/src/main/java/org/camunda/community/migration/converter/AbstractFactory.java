package org.camunda.community.migration.converter;

public abstract class AbstractFactory<T> {
  private T instance;

  public void setInstance(T instance) {
    this.instance = instance;
  }

  protected abstract T createInstance();

  public T get() {
    if (instance == null) {
      instance = createInstance();
    }
    return instance;
  }
}
