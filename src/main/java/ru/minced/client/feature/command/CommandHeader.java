package ru.minced.client.feature.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandHeader {
    String name();
    String shortDesc() default "У данной комманды отсутствует вспомогательная информация";
}
