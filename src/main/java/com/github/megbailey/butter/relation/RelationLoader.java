package com.github.megbailey.butter.relation;

import com.github.megbailey.butter.Model;
import com.github.megbailey.butter.ModelCollection;
import com.github.megbailey.butter.query.QueryBuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Eager-loads named relations onto a collection of parents using batched queries.
 */
public final class RelationLoader {
    private RelationLoader() {}

    public static <T extends Model> void load(ModelCollection<T> parents, List<String> relationNames)
            throws Exception {
        if (parents == null || parents.isEmpty() || relationNames == null) {
            return;
        }
        for (String name : relationNames) {
            loadOne(parents, name);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Model> void loadOne(ModelCollection<T> parents, String relationName)
            throws Exception {
        T firstParent = parents.first();
        Method method = findRelationMethod(firstParent.getClass(), relationName);
        Relation sample = (Relation) method.invoke(firstParent);

        if (sample instanceof BelongsToMany) {
            loadBelongsToMany(parents, method, (BelongsToMany) sample);
        } else if (sample instanceof BelongsTo) {
            loadBelongsTo(parents, method, (BelongsTo) sample);
        } else {
            loadHasRelation(parents, method, sample);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Model> void loadHasRelation(
            ModelCollection<T> parents, Method method, Relation sample) throws Exception {
        List<Object> parentKeys = new ArrayList<>();
        for (T parent : parents) {
            Object key = parent.getFieldValue(sample.getLocalKey());
            if (key != null) {
                parentKeys.add(key);
            }
        }
        ModelCollection related = parentKeys.isEmpty()
                ? new ModelCollection<>()
                : new QueryBuilder<>(sample.getRelatedClass()).whereIn(sample.getForeignKey(), parentKeys).get();

        Map<Object, ModelCollection> grouped = new HashMap<>();
        for (Object obj : related) {
            Model relatedModel = (Model) obj;
            Object fk = relatedModel.getFieldValue(sample.getForeignKey());
            grouped.computeIfAbsent(fk, k -> new ModelCollection<>()).add(relatedModel);
            grouped.putIfAbsent(String.valueOf(fk), grouped.get(fk));
        }

        for (T parent : parents) {
            Relation relation = (Relation) method.invoke(parent);
            Object key = parent.getFieldValue(sample.getLocalKey());
            ModelCollection matched = key == null
                    ? new ModelCollection<>()
                    : (ModelCollection) grouped.getOrDefault(
                            key, grouped.getOrDefault(String.valueOf(key), new ModelCollection<>()));
            relation.markLoaded(matched);
            parent.setRelation(method.getName(), relation);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Model> void loadBelongsTo(
            ModelCollection<T> parents, Method method, BelongsTo sample) throws Exception {
        List<Object> fks = new ArrayList<>();
        for (T parent : parents) {
            Object fk = parent.getFieldValue(sample.getForeignKey());
            if (fk != null) {
                fks.add(fk);
            }
        }
        ModelCollection related = fks.isEmpty()
                ? new ModelCollection<>()
                : new QueryBuilder<>(sample.getRelatedClass()).whereIn(sample.getLocalKey(), fks).get();

        Map<Object, ModelCollection> grouped = new HashMap<>();
        for (Object obj : related) {
            Model relatedModel = (Model) obj;
            Object key = relatedModel.getFieldValue(sample.getLocalKey());
            ModelCollection bucket = new ModelCollection<>();
            bucket.add(relatedModel);
            grouped.put(key, bucket);
            grouped.put(String.valueOf(key), bucket);
        }

        for (T parent : parents) {
            BelongsTo relation = (BelongsTo) method.invoke(parent);
            Object fk = parent.getFieldValue(sample.getForeignKey());
            ModelCollection matched = fk == null
                    ? new ModelCollection<>()
                    : (ModelCollection) grouped.getOrDefault(
                            fk, grouped.getOrDefault(String.valueOf(fk), new ModelCollection<>()));
            relation.markLoaded(matched);
            parent.setRelation(method.getName(), relation);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Model> void loadBelongsToMany(
            ModelCollection<T> parents, Method method, BelongsToMany sample) throws Exception {
        List<Object> parentKeys = new ArrayList<>();
        for (T parent : parents) {
            Object key = parent.getFieldValue(sample.getLocalKey());
            if (key != null) {
                parentKeys.add(key);
            }
        }
        List<Object> relatedKeys = sample.relatedKeysForParents(parentKeys);
        String relatedPk = Model.newBlank(sample.getRelatedClass()).primaryKeyName();
        ModelCollection related = relatedKeys.isEmpty()
                ? new ModelCollection<>()
                : new QueryBuilder<>(sample.getRelatedClass()).whereIn(relatedPk, relatedKeys).get();

        Map grouped = sample.groupByParent(parentKeys, related);
        for (T parent : parents) {
            BelongsToMany relation = (BelongsToMany) method.invoke(parent);
            Object key = parent.getFieldValue(sample.getLocalKey());
            ModelCollection matched = key == null
                    ? new ModelCollection<>()
                    : (ModelCollection) grouped.getOrDefault(
                            key, grouped.getOrDefault(String.valueOf(key), new ModelCollection<>()));
            relation.markLoaded(matched);
            parent.setRelation(method.getName(), relation);
        }
    }

    private static Method findRelationMethod(Class<?> clazz, String name) throws NoSuchMethodException {
        try {
            return clazz.getMethod(name);
        } catch (NoSuchMethodException e) {
            String getter = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
            return clazz.getMethod(getter);
        }
    }
}
