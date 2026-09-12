package com.github.megbailey.butter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Mass-assignment deny-list (ignored when @Fillable is present). */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Guarded {
    String[] value() default {"*"};
}
