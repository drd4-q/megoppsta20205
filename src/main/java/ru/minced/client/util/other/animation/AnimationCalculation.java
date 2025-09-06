package ru.minced.client.util.other.animation;

public interface AnimationCalculation {
    default double calculation(double value) {
        return 0;
    }
}