package com.github.megbailey.butter.relation;

import com.github.megbailey.butter.Model;
import com.github.megbailey.butter.ModelCollection;
import com.github.megbailey.butter.query.QueryBuilder;

import java.util.Map;

public class HasMany<T extends Model> extends Relation<T> {
    public HasMany(Model parent, Class<T> relatedClass, String foreignKey, String localKey) {
        super(parent, relatedClass, foreignKey, localKey);
    }

    @Override
    public ModelCollection<T> getResults() throws Exception {
        Object key = parentKey();
        if (key == null) {
            return new ModelCollection<>();
        }
        return new QueryBuilder<>(relatedClass).where(foreignKey, key).get();
    }

    public T create(Map<String, Object> attributes) throws Exception {
        attributes.put(foreignKey, parentKey());
        T created = new QueryBuilder<>(relatedClass).create(attributes);
        loaded.add(created);
        loadedFlag = true;
        return created;
    }

    public T save(T related) throws Exception {
        related.setFieldValue(foreignKey, parentKey());
        related.save();
        loaded.add(related);
        loadedFlag = true;
        return related;
    }
}
