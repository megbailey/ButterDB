package com.github.megbailey.butter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Auto-manage created_at / updated_at on save. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Timestamps {
    String createdAt() default "created_at";
    String updatedAt() default "updated_at";
}
