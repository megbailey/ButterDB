package com.github.megbailey.butter.event;

import com.github.megbailey.butter.Model;

@FunctionalInterface
public interface ModelObserver {
    void handle(Model model, ModelEvent event);
}
