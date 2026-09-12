package com.github.megbailey.butter.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Global query scope applied to every new query for a model type.
 */
@FunctionalInterface
public interface GlobalScope {
    void apply(QueryBuilder<?> query);
}

/**
 * Registry of global scopes keyed by model class name.
 */
final class GlobalScopeRegistry {
    private static final List<ScopedRegistration> SCOPES = new ArrayList<>();

    private GlobalScopeRegistry() {}

    static synchronized void add(Class<?> modelClass, String name, GlobalScope scope) {
        SCOPES.add(new ScopedRegistration(modelClass, name, scope));
    }

    static synchronized void remove(Class<?> modelClass, String name) {
        SCOPES.removeIf(s -> s.modelClass.equals(modelClass) && s.name.equals(name));
    }

    static synchronized List<GlobalScope> forModel(Class<?> modelClass) {
        List<GlobalScope> result = new ArrayList<>();
        for (ScopedRegistration s : SCOPES) {
            if (s.modelClass.isAssignableFrom(modelClass)) {
                result.add(s.scope);
            }
        }
        return result;
    }

    private static final class ScopedRegistration {
        private final Class<?> modelClass;
        private final String name;
        private final GlobalScope scope;

        private ScopedRegistration(Class<?> modelClass, String name, GlobalScope scope) {
            this.modelClass = modelClass;
            this.name = name;
            this.scope = scope;
        }
    }
}
