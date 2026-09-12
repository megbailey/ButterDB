package com.github.megbailey.butter.relation;

import com.github.megbailey.butter.Model;
import com.github.megbailey.butter.ModelCollection;
import com.github.megbailey.butter.query.QueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BelongsToMany<T extends Model> extends Relation<T> {
    private final PivotTable pivot;
    private final String relatedKeyName;
    private final Map<Object, Map<String, Object>> pivotDataByRelatedKey = new HashMap<>();

    public BelongsToMany(
            Model parent,
            Class<T> relatedClass,
            String table,
            String foreignPivotKey,
            String relatedPivotKey,
            String parentKey,
            String relatedKey
    ) {
        super(parent, relatedClass, foreignPivotKey, parentKey);
        this.pivot = new PivotTable(table, foreignPivotKey, relatedPivotKey);
        this.relatedKeyName = relatedKey;
    }

    public BelongsToMany<T> withPivot(String... columns) {
        pivot.withPivot(columns);
        return this;
    }

    public BelongsToMany<T> wherePivot(String column, Object value) {
        pivot.wherePivot(column, value);
        return this;
    }

    public PivotTable getPivot() {
        return pivot;
    }

    public Map<String, Object> getPivotAttributes(Object relatedKey) {
        return pivotDataByRelatedKey.getOrDefault(relatedKey, Map.of());
    }

    @Override
    public ModelCollection<T> getResults() throws Exception {
        Object parentKey = parentKey();
        if (parentKey == null) {
            return new ModelCollection<>();
        }
        List<Map<String, Object>> pivotRows = pivot.rowsForParent(parentKey);
        List<Object> relatedKeys = new ArrayList<>();
        for (Map<String, Object> row : pivotRows) {
            Object relatedKey = row.get(pivot.getRelatedPivotKey());
            relatedKeys.add(relatedKey);
            pivotDataByRelatedKey.put(relatedKey, row);
        }
        if (relatedKeys.isEmpty()) {
            return new ModelCollection<>();
        }
        return new QueryBuilder<>(relatedClass).whereIn(relatedKeyName, relatedKeys).get();
    }

    public void attach(Object relatedKey) throws Exception {
        attach(relatedKey, Map.of());
    }

    public void attach(Object relatedKey, Map<String, Object> attributes) throws Exception {
        pivot.attach(parentKey(), relatedKey, attributes);
        loadedFlag = false;
    }

    public void detach(Object relatedKey) throws Exception {
        pivot.detach(parentKey(), relatedKey);
        loadedFlag = false;
    }

    public void sync(List<Object> relatedKeys) throws Exception {
        pivot.sync(parentKey(), relatedKeys, null);
        loadedFlag = false;
    }

    public void sync(List<Object> relatedKeys, Map<Object, Map<String, Object>> pivotPayload) throws Exception {
        pivot.sync(parentKey(), relatedKeys, pivotPayload);
        loadedFlag = false;
    }

    public List<Object> relatedKeysForParents(List<Object> parentKeys) throws Exception {
        return pivot.rowsForParents(parentKeys).stream()
                .map(r -> r.get(pivot.getRelatedPivotKey()))
                .collect(Collectors.toList());
    }

    public Map<Object, ModelCollection<T>> groupByParent(List<Object> parentKeys, ModelCollection<T> related)
            throws Exception {
        List<Map<String, Object>> pivotRows = pivot.rowsForParents(parentKeys);
        Map<Object, T> relatedByPk = new HashMap<>();
        for (T model : related) {
            Object pk = model.getFieldValue(relatedKeyName);
            relatedByPk.put(pk, model);
            relatedByPk.put(String.valueOf(pk), model);
        }
        Map<Object, ModelCollection<T>> grouped = new HashMap<>();
        for (Map<String, Object> row : pivotRows) {
            Object parentKey = row.get(pivot.getForeignPivotKey());
            Object relatedKey = row.get(pivot.getRelatedPivotKey());
            T model = relatedByPk.get(relatedKey);
            if (model == null) {
                model = relatedByPk.get(String.valueOf(relatedKey));
            }
            if (model == null) {
                continue;
            }
            pivotDataByRelatedKey.put(relatedKey, row);
            grouped.computeIfAbsent(parentKey, k -> new ModelCollection<>()).add(model);
            grouped.put(String.valueOf(parentKey), grouped.get(parentKey));
        }
        return grouped;
    }
}
