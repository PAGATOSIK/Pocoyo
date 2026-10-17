package main.client.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {
    private final List<Object> listeners = new CopyOnWriteArrayList<>();

    public void register(Object listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unregister(Object listener) {
        listeners.remove(listener);
    }

    public void post(Object event) {
        for (Object listener : listeners) {
            for (Method method : listener.getClass().getDeclaredMethods()) {
                // Проверяем, помечен ли метод аннотацией эвента (можно создать свою @EventTarget)
                if (method.getParameterCount() == 1 && method.getParameterTypes()[0].isAssignableFrom(event.getClass())) {
                    try {
                        method.setAccessible(true);
                        method.invoke(listener, event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}