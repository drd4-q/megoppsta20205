package ru.minced.client.util.other.animation.impl;


import ru.minced.client.util.other.animation.Animation;

public class AlphaAnim extends Animation {
    @Override
    public double calculation(double value) {
        double x = value / ms;

        if (x < 0.5) {
            return 4 * x * x;
        } else {
            double bounce = x - 1;
            return -4 * bounce * bounce + 1;
        }
    }
}
