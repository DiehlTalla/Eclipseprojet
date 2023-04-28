package jfox.context;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.inject.Scope;

@Scope
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Dependent {

}
