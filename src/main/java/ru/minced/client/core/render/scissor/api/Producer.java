package ru.minced.client.core.render.scissor.api;

public interface Producer<T> {
    T create();
}