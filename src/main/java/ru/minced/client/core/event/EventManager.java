package ru.minced.client.core.event;

import ru.minced.client.core.event.api.Priority;
import ru.minced.client.core.event.api.StoppableEvent;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {
    private static final Map<Class<?>, List<HandlerMethod>> handlers = new HashMap<>();

    public void subscribe(Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandler.class) && method.getParameterCount() == 1) {
                Class<?> eventType = method.getParameterTypes()[0];
                method.setAccessible(true);
                handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                        .add(new HandlerMethod(listener, method, method.getAnnotation(EventHandler.class).value()));
                handlers.get(eventType).sort(Comparator.comparing(h -> h.priority));
            }
        }
    }

    public void unsubscribe(Object listener) {
        for (List<HandlerMethod> list : handlers.values()) {
            list.removeIf(h -> h.listener == listener);
        }
    }

    public static <T> T post(T event) {
        List<HandlerMethod> list = handlers.get(event.getClass());
        if (list == null) return event;
        for (HandlerMethod h : list) {
            try {
                h.method.invoke(h.listener, event);
                if (event instanceof StoppableEvent && ((StoppableEvent) event).isStopped()) break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return event;
    }

    private static class HandlerMethod {
        final Object listener;
        final Method method;
        final Priority priority;
        HandlerMethod(Object listener, Method method, Priority priority) {
            this.listener = listener;
            this.method = method;
            this.priority = priority;
        }
    }
} 