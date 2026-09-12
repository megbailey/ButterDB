package com.github.megbailey.butter.relation;

import com.github.megbailey.butter.Model;
import com.github.megbailey.butter.ModelCollection;

import java.util.Map;

/**
 * Base type for Eloquent-style relationships.
 */
public abstract class Relation<T extends Model> {
    protected final Model parent;
    protected final Class<T> relatedClass;
    protected final String foreignKey;
    protected final String localKey;
    protected ModelCollection<T> loaded = new ModelCollection<>();
    protected boolean loadedFlag;

    protected Relation(Model parent, Class<T> relatedClass, String foreignKey, String localKey) {
        this.parent = parent;
        this.relatedClass = relatedClass;
        this.foreignKey = foreignKey;
        this.localKey = localKey;
    }

    public abstract ModelCollection<T> getResults() throws Exception;

    public ModelCollection<T> get() throws Exception {
        if (loadedFlag) {
            return loaded;
        }
        ModelCollection<T> results = getResults();
        markLoaded(results);
        return results;
    }

    public T first() throws Exception {
        ModelCollection<T> results = get();
        return results.isEmpty() ? null : results.first();
    }

    public void markLoaded(ModelCollection<T> results) {
        this.loaded = results == null ? new ModelCollection<>() : results;
        this.loadedFlag = true;
    }

    public boolean isLoaded() {
        return loadedFlag;
    }

    public ModelCollection<T> getLoaded() {
        return loaded;
    }

    public String getForeignKey() {
        return foreignKey;
    }

    public String getLocalKey() {
        return localKey;
    }

    public Class<T> getRelatedClass() {
        return relatedClass;
    }

    public Model getParent() {
        return parent;
    }

    public Object parentKey() {
        return parent.getFieldValue(localKey);
    }

    /** Attach eager-loaded rows for this parent from a FK-keyed map. */
    public void match(Map<Object, ModelCollection<T>> grouped) {
        Object key = parentKey();
        ModelCollection<T> matched = key == null
                ? new ModelCollection<>()
                : grouped.getOrDefault(key, grouped.getOrDefault(String.valueOf(key), new ModelCollection<>()));
        markLoaded(matched);
    }
}
