package com.camunda.consulting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Falko Menge (Camunda)
 * @see https://stackoverflow.com/questions/4033457/is-there-an-unsupported-operation-annotation-in-java
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UnsupportedOperation {

}
