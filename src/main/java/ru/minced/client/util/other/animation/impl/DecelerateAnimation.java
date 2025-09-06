package ru.minced.client.util.other.animation.impl;


import ru.minced.client.util.other.animation.Animation;

public class DecelerateAnimation extends Animation {

    @Override
    public double calculation(double value) {
        double x = value / ms;
        return 1 - (x - 1) * (x - 1);
    }
}