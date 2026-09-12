package com.github.megbailey.butter.relation;

import com.github.megbailey.butter.Model;
import com.github.megbailey.butter.ModelCollection;
import com.github.megbailey.butter.query.QueryBuilder;

public class HasOne<T extends Model> extends HasMany<T> {
    public HasOne(Model parent, Class<T> relatedClass, String foreignKey, String localKey) {
        super(parent, relatedClass, foreignKey, localKey);
    }

    @Override
    public ModelCollection<T> getResults() throws Exception {
        Object key = parentKey();
        ModelCollection<T> results = new ModelCollection<>();
        if (key == null) {
            return results;
        }
        T first = new QueryBuilder<>(relatedClass).where(foreignKey, key).first();
        if (first != null) {
            results.add(first);
        }
        return results;
    }
}
