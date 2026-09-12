package com.github.megbailey.butter.meta;

import com.github.megbailey.butter.annotation.Column;
import com.github.megbailey.butter.annotation.Fillable;
import com.github.megbailey.butter.annotation.Guarded;
import com.github.megbailey.butter.annotation.PrimaryKey;
import com.github.megbailey.butter.annotation.SoftDeletes;
import com.github.megbailey.butter.annotation.Table;
import com.github.megbailey.butter.annotation.Timestamps;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Resolved model metadata from constructor args and/or annotations.
 */
public class ModelMeta {
    private final String tableName;
    private final String primaryKey;
    private final boolean increments;
    private final List<String> fields;
    private final boolean softDeletes;
    private final String deletedAtColumn;
    private final boolean timestamps;
    private final String createdAtColumn;
    private final String updatedAtColumn;
    private final Set<String> fillable;
    private final Set<String> guarded;
    private final boolean fillableDefined;

    public ModelMeta(
            String tableName,
            String primaryKey,
            boolean increments,
            List<String> fields,
            boolean softDeletes,
            String deletedAtColumn,
            boolean timestamps,
            String createdAtColumn,
            String updatedAtColumn,
            Set<String> fillable,
            Set<String> guarded,
            boolean fillableDefined
    ) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.increments = increments;
        this.fields = List.copyOf(fields);
        this.softDeletes = softDeletes;
        this.deletedAtColumn = deletedAtColumn;
        this.timestamps = timestamps;
        this.createdAtColumn = createdAtColumn;
        this.updatedAtColumn = updatedAtColumn;
        this.fillable = fillable == null ? Set.of() : Set.copyOf(fillable);
        this.guarded = guarded == null ? Set.of() : Set.copyOf(guarded);
        this.fillableDefined = fillableDefined;
    }

    public static ModelMeta fromClass(Class<?> clazz, String pkField, String[] fields, boolean autoIncrementPK) {
        String tableName = clazz.getSimpleName();
        Table table = clazz.getAnnotation(Table.class);
        if (table != null && !table.value().isBlank()) {
            tableName = table.value();
        }

        String primaryKey = pkField;
        boolean increments = autoIncrementPK;
        List<String> fieldList = fields == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(fields));

        // Annotation-driven fields when constructor list is empty
        if (fieldList.isEmpty()) {
            for (Field field : clazz.getDeclaredFields()) {
                PrimaryKey pk = field.getAnnotation(PrimaryKey.class);
                Column column = field.getAnnotation(Column.class);
                if (pk != null) {
                    primaryKey = resolveColumnName(field, column);
                    increments = pk.increments();
                    if (!fieldList.contains(primaryKey)) {
                        fieldList.add(0, primaryKey);
                    }
                } else if (column != null) {
                    String name = resolveColumnName(field, column);
                    if (!fieldList.contains(name)) {
                        fieldList.add(name);
                    }
                }
            }
        }

        if (primaryKey == null || primaryKey.isBlank()) {
            primaryKey = "id";
        }

        SoftDeletes soft = clazz.getAnnotation(SoftDeletes.class);
        boolean softDeletes = soft != null;
        String deletedAt = softDeletes ? soft.column() : "deleted_at";
        if (softDeletes && !fieldList.contains(deletedAt)) {
            fieldList.add(deletedAt);
        }

        Timestamps ts = clazz.getAnnotation(Timestamps.class);
        boolean timestamps = ts != null;
        String createdAt = timestamps ? ts.createdAt() : "created_at";
        String updatedAt = timestamps ? ts.updatedAt() : "updated_at";
        if (timestamps) {
            if (!fieldList.contains(createdAt)) fieldList.add(createdAt);
            if (!fieldList.contains(updatedAt)) fieldList.add(updatedAt);
        }

        Fillable fillableAnn = clazz.getAnnotation(Fillable.class);
        Guarded guardedAnn = clazz.getAnnotation(Guarded.class);
        Set<String> fillable = fillableAnn != null
                ? new HashSet<>(Arrays.asList(fillableAnn.value()))
                : Collections.emptySet();
        Set<String> guarded = guardedAnn != null
                ? new HashSet<>(Arrays.asList(guardedAnn.value()))
                : Set.of("*");
        boolean fillableDefined = fillableAnn != null;

        return new ModelMeta(
                tableName, primaryKey, increments, fieldList,
                softDeletes, deletedAt, timestamps, createdAt, updatedAt,
                fillable, guarded, fillableDefined
        );
    }

    private static String resolveColumnName(Field field, Column column) {
        if (column != null && !column.value().isBlank()) {
            return column.value();
        }
        return field.getName();
    }

    public boolean isFillable(String attribute) {
        if (fillableDefined) {
            return fillable.contains(attribute);
        }
        if (guarded.contains("*")) {
            return false;
        }
        return !guarded.contains(attribute);
    }

    public String getTableName() { return tableName; }
    public String getPrimaryKey() { return primaryKey; }
    public boolean isIncrements() { return increments; }
    public List<String> getFields() { return fields; }
    public boolean isSoftDeletes() { return softDeletes; }
    public String getDeletedAtColumn() { return deletedAtColumn; }
    public boolean isTimestamps() { return timestamps; }
    public String getCreatedAtColumn() { return createdAtColumn; }
    public String getUpdatedAtColumn() { return updatedAtColumn; }
}
