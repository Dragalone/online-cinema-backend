package com.example.onlinecinemabackend.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Accessible {
    AccessCheckType checkBy();

    boolean availableForModerator() default false;
}
