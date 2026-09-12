package com.github.megbailey.butter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Eloquent-style collection wrapper around a list of models.
 */
public class ModelCollection<T extends Model> implements Iterable<T> {
    private final List<T> models;

    public ModelCollection() {
        this.models = new ArrayList<>();
    }

    public ModelCollection(Collection<T> models) {
        this.models = new ArrayList<>(models);
    }

    public void add(T model) {
        models.add(model);
    }

    public T first() {
        return models.isEmpty() ? null : models.get(0);
    }

    public T last() {
        return models.isEmpty() ? null : models.get(models.size() - 1);
    }

    public int count() {
        return models.size();
    }

    public boolean isEmpty() {
        return models.isEmpty();
    }

    public List<T> all() {
        return new ArrayList<>(models);
    }

    public List<Object> pluck(String field) {
        return models.stream()
                .map(m -> m.getFieldValue(field))
                .collect(Collectors.toList());
    }

    public <R> List<R> map(Function<T, R> mapper) {
        return models.stream().map(mapper).collect(Collectors.toList());
    }

    public ModelCollection<T> filter(Function<T, Boolean> predicate) {
        return new ModelCollection<>(models.stream()
                .filter(m -> Boolean.TRUE.equals(predicate.apply(m)))
                .collect(Collectors.toList()));
    }

    public T get(int index) {
        return models.get(index);
    }

    @Override
    public Iterator<T> iterator() {
        return models.iterator();
    }

    @Override
    public String toString() {
        return "ModelCollection{count=" + count() + ", models=" + models + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelCollection)) return false;
        ModelCollection<?> that = (ModelCollection<?>) o;
        return Objects.equals(models, that.models);
    }

    @Override
    public int hashCode() {
        return Objects.hash(models);
    }
}
