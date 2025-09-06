package ru.minced.client.util.other.animation.util;

@FunctionalInterface
public interface Easing {
    double ease(double value);
}