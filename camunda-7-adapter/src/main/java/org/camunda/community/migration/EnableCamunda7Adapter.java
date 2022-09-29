package org.camunda.community.migration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Import(CamundaPlatform7AdapterConfig.class)
public @interface EnableCamunda7Adapter {}
