package ru.minced.client.core.event;

import ru.minced.client.core.event.api.Priority;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {
    Priority value() default Priority.NORMAL;
} 