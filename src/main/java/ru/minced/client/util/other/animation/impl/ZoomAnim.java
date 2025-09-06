package ru.minced.client.util.other.animation.impl;


import ru.minced.client.util.other.animation.Animation;

public class ZoomAnim extends Animation {
    @Override
    public double calculation(double value) {
        return 1 - Math.abs((value / ms) - 1);
    }
}