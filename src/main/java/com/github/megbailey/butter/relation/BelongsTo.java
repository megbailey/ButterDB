package com.github.megbailey.butter.relation;

import com.github.megbailey.butter.Model;
import com.github.megbailey.butter.ModelCollection;
import com.github.megbailey.butter.query.QueryBuilder;

import java.util.Map;

public class BelongsTo<T extends Model> extends Relation<T> {
    public BelongsTo(Model parent, Class<T> relatedClass, String foreignKey, String ownerKey) {
        super(parent, relatedClass, foreignKey, ownerKey);
    }

    @Override
    public ModelCollection<T> getResults() throws Exception {
        Object fk = parent.getFieldValue(foreignKey);
        ModelCollection<T> results = new ModelCollection<>();
        if (fk != null) {
            T related = new QueryBuilder<>(relatedClass).where(localKey, fk).first();
            if (related != null) {
                results.add(related);
            }
        }
        return results;
    }

    @Override
    public void match(Map<Object, ModelCollection<T>> grouped) {
        Object fk = parent.getFieldValue(foreignKey);
        ModelCollection<T> matched = fk == null
                ? new ModelCollection<>()
                : grouped.getOrDefault(fk, grouped.getOrDefault(String.valueOf(fk), new ModelCollection<>()));
        markLoaded(matched);
    }

    public T associate(T related) throws Exception {
        Object ownerKey = related.getFieldValue(localKey);
        parent.setFieldValue(foreignKey, ownerKey);
        parent.save();
        ModelCollection<T> results = new ModelCollection<>();
        results.add(related);
        markLoaded(results);
        return related;
    }

    public void dissociate() throws Exception {
        parent.setFieldValue(foreignKey, null);
        parent.save();
        markLoaded(new ModelCollection<>());
    }
}
