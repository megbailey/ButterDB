package com.github.megbailey.butter.event;

import com.github.megbailey.butter.Model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Simple global observer registry for model lifecycle events.
 */
public final class ModelEventDispatcher {
    private static final Map<ModelEvent, List<ModelObserver>> OBSERVERS = new EnumMap<>(ModelEvent.class);

    private ModelEventDispatcher() {}

    public static synchronized void listen(ModelEvent event, ModelObserver observer) {
        OBSERVERS.computeIfAbsent(event, e -> new ArrayList<>()).add(observer);
    }

    public static synchronized void clear() {
        OBSERVERS.clear();
    }

    public static void dispatch(Model model, ModelEvent event) {
        List<ModelObserver> list;
        synchronized (ModelEventDispatcher.class) {
            list = OBSERVERS.get(event);
            if (list == null || list.isEmpty()) {
                return;
            }
            list = new ArrayList<>(list);
        }
        for (ModelObserver observer : list) {
            observer.handle(model, event);
        }
    }
}
