package org.camunda.community.converter;

public abstract class AbstractFactory<T> {
  private T instance;

  protected abstract T createInstance();

  public T get() {
    if (instance == null) {
      instance = createInstance();
    }
    return instance;
  }
}
