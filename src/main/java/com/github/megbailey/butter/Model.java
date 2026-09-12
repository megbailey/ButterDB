package com.github.megbailey.butter;

import com.github.megbailey.butter.cache.ModelCache;
import com.github.megbailey.butter.event.ModelEvent;
import com.github.megbailey.butter.event.ModelEventDispatcher;
import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.GoggleAccessException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import com.github.megbailey.butter.google.exception.SystemErrorException;
import com.github.megbailey.butter.meta.ModelMeta;
import com.github.megbailey.butter.query.QueryBuilder;
import com.github.megbailey.butter.relation.BelongsTo;
import com.github.megbailey.butter.relation.BelongsToMany;
import com.github.megbailey.butter.relation.HasMany;
import com.github.megbailey.butter.relation.HasOne;
import com.github.megbailey.butter.relation.Relation;
import com.github.megbailey.butter.util.ColumnLetters;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Eloquent-style ActiveRecord base model persisted to a Google Sheets worksheet.
 */
public class Model {
    private GSpreadsheet spreadsheet;
    private final ModelMeta meta;
    private final HashBiMap<String, String> attributeIDMap = HashBiMap.create();
    private final Map<String, Object> attributeValuesMap = new HashMap<>();
    private final Map<String, Relation<?>> relations = new HashMap<>();

    private int autoIncrementPKCounter = 1;
    private int occupiedRowCounter = 1;
    private Integer sheetRowNumber;
    private boolean exists;
    private boolean wasRecentlyCreated;
    private Object lastInsertedId;
    private boolean initialized;

    protected Model(String pkField, String[] fields, boolean autoIncrementPK) {
        this.meta = ModelMeta.fromClass(getClass(), pkField, fields, autoIncrementPK);
    }

    protected Model() {
        this.meta = ModelMeta.fromClass(getClass(), null, null, true);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Model> T newBlank(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Model " + clazz.getName() + " requires a public no-arg constructor", e);
        }
    }

    public QueryBuilder<? extends Model> newQuery() {
        return new QueryBuilder<>(this);
    }

    @SuppressWarnings("unchecked")
    public <T extends Model> QueryBuilder<T> query() {
        return (QueryBuilder<T>) newQuery();
    }

    public void ensureInitialized() {
        if (initialized) {
            return;
        }
        if (spreadsheet == null) {
            spreadsheet = ButterDBManager.getDatabase();
        }
        if (spreadsheet == null) {
            // Offline / unit-test mode: map fields to A,B,C... without Google
            ensureColumnMapFromFields();
            initialized = true;
            return;
        }
        before();
        initialized = true;
    }

    private void before() {
        try {
            spreadsheet.firstOrNewSheet(tableName());
            if (attributeIDMap.isEmpty()) {
                initAttributes();
            }
            if (meta.isIncrements()) {
                initAutoIncrementPK();
            }
        } catch (IOException | BadRequestException | ResourceNotFoundException e) {
            throw new IllegalStateException("Failed to initialize model " + tableName(), e);
        }
    }

    /** Populate attribute→letter map from declared field order when no sheet is available. */
    private void ensureColumnMapFromFields() {
        if (!attributeIDMap.isEmpty()) {
            return;
        }
        List<String> fields = meta.getFields();
        for (int i = 0; i < fields.size(); i++) {
            attributeIDMap.put(fields.get(i), ColumnLetters.toLetter(i));
        }
    }

    public void save() throws BadRequestException, ResourceNotFoundException, SystemErrorException,
            GoggleAccessException, IOException {
        ensureInitialized();
        touchTimestamps();

        Object pk = attributeValuesMap.get(primaryKeyName());
        boolean creating = pk == null || !exists;

        if (creating && pk == null && meta.isIncrements()) {
            pk = incrementPK();
            attributeValuesMap.put(primaryKeyName(), pk);
            wasRecentlyCreated = true;
        } else {
            wasRecentlyCreated = false;
        }

        ModelEventDispatcher.dispatch(this, ModelEvent.SAVING);
        if (creating) {
            ModelEventDispatcher.dispatch(this, ModelEvent.CREATING);
        } else {
            ModelEventDispatcher.dispatch(this, ModelEvent.UPDATING);
        }

        Object[] rowData = buildOrderedRow();
        if (creating) {
            String firstCell = "A" + occupiedRowCounter;
            String lastCell = ColumnLetters.toLetter(Math.max(attributeIDMap.size() - 1, 0)) + occupiedRowCounter;
            spreadsheet.insertRow(tableName(), firstCell + ":" + lastCell, Arrays.asList(rowData));
            sheetRowNumber = occupiedRowCounter;
            occupiedRowCounter += 1;
            exists = true;
            lastInsertedId = pk;
            ModelCache.getInstance().put(ModelCache.key(tableName(), pk), this);
            ModelEventDispatcher.dispatch(this, ModelEvent.CREATED);
        } else {
            int row = resolveSheetRowNumber();
            String lastCol = ColumnLetters.toLetter(Math.max(attributeIDMap.size() - 1, 0));
            spreadsheet.updateDataRow(tableName(), "A" + row + ":" + lastCol + row, Arrays.asList(rowData));
            sheetRowNumber = row;
            exists = true;
            ModelCache.getInstance().put(ModelCache.key(tableName(), pk), this);
            ModelEventDispatcher.dispatch(this, ModelEvent.UPDATED);
        }
        ModelEventDispatcher.dispatch(this, ModelEvent.SAVED);
    }

    public Boolean delete() throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException,
            BadRequestException {
        ensureInitialized();
        Object pk = attributeValuesMap.get(primaryKeyName());
        if (pk == null) {
            return false;
        }
        ModelEventDispatcher.dispatch(this, ModelEvent.DELETING);
        boolean result;
        if (usesSoftDeletes()) {
            attributeValuesMap.put(deletedAtColumn(), Instant.now().toString());
            save();
            result = true;
        } else {
            result = spreadsheet.deleteRow(
                    tableName(),
                    Set.copyOf(attributeIDMap.values()),
                    "where " + attributeIDMap.get(primaryKeyName()) + "=" + quoteGViz(pk)
            );
            if (result) {
                occupiedRowCounter = Math.max(1, occupiedRowCounter - 1);
                exists = false;
                sheetRowNumber = null;
            }
        }
        ModelCache.getInstance().invalidate(ModelCache.key(tableName(), pk));
        if (result) {
            ModelEventDispatcher.dispatch(this, ModelEvent.DELETED);
        }
        return result;
    }

    public Boolean forceDelete() throws SystemErrorException, GoggleAccessException, IOException,
            ResourceNotFoundException {
        ensureInitialized();
        Object pk = attributeValuesMap.get(primaryKeyName());
        if (pk == null) {
            return false;
        }
        ModelEventDispatcher.dispatch(this, ModelEvent.DELETING);
        boolean result = spreadsheet.deleteRow(
                tableName(),
                Set.copyOf(attributeIDMap.values()),
                "where " + attributeIDMap.get(primaryKeyName()) + "=" + quoteGViz(pk)
        );
        if (result) {
            exists = false;
            sheetRowNumber = null;
            ModelCache.getInstance().invalidate(ModelCache.key(tableName(), pk));
            ModelEventDispatcher.dispatch(this, ModelEvent.DELETED);
        }
        return result;
    }

    public Boolean restore() throws Exception {
        if (!usesSoftDeletes()) {
            return false;
        }
        ModelEventDispatcher.dispatch(this, ModelEvent.RESTORING);
        attributeValuesMap.put(deletedAtColumn(), null);
        save();
        ModelEventDispatcher.dispatch(this, ModelEvent.RESTORED);
        return true;
    }

    public Object getFieldValue(String fieldName) {
        return attributeValuesMap.get(fieldName);
    }

    public void setFieldValue(String fieldName, Object fieldValue) {
        attributeValuesMap.put(fieldName, fieldValue);
    }

    public boolean isFillableAttribute(String attribute) {
        if (meta.getFields().contains(attribute) && attribute.equals(primaryKeyName()) && meta.isIncrements()) {
            return false;
        }
        return meta.isFillable(attribute) || meta.getFields().contains(attribute);
    }

    @SuppressWarnings("unchecked")
    public <T extends Model> ModelCollection<T> executeQuery(String gVizQuery)
            throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        ensureInitialized();
        JsonArray jsonArr = spreadsheet.findRows(tableName(), gVizQuery);
        return (ModelCollection<T>) hydrateCollection(jsonArr);
    }

    public <T extends Model> ModelCollection<T> executeAggregateQuery(String gVizQuery)
            throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        return executeQuery(gVizQuery);
    }

    // Convenience ActiveRecord-style APIs (delegate to QueryBuilder)
    public QueryBuilder<? extends Model> where(String column, String operator, Object value) {
        return newQuery().where(column, operator, value);
    }

    public QueryBuilder<? extends Model> where(String column, Object value) {
        return newQuery().where(column, value);
    }

    public QueryBuilder<? extends Model> orWhere(String column, String operator, Object value) {
        return newQuery().orWhere(column, operator, value);
    }

    public Model find(Integer primaryKeyValue)
            throws SystemErrorException, IOException, GoggleAccessException, ResourceNotFoundException {
        return newQuery().find(primaryKeyValue);
    }

    public ModelCollection<? extends Model> get()
            throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        return newQuery().get();
    }

    protected <R extends Model> HasMany<R> hasMany(Class<R> related, String foreignKey) {
        return hasMany(related, foreignKey, primaryKeyName());
    }

    protected <R extends Model> HasMany<R> hasMany(Class<R> related, String foreignKey, String localKey) {
        return new HasMany<>(this, related, foreignKey, localKey);
    }

    protected <R extends Model> HasOne<R> hasOne(Class<R> related, String foreignKey) {
        return hasOne(related, foreignKey, primaryKeyName());
    }

    protected <R extends Model> HasOne<R> hasOne(Class<R> related, String foreignKey, String localKey) {
        return new HasOne<>(this, related, foreignKey, localKey);
    }

    protected <R extends Model> BelongsTo<R> belongsTo(Class<R> related, String foreignKey) {
        return belongsTo(related, foreignKey, relatedPrimaryKey(related));
    }

    protected <R extends Model> BelongsTo<R> belongsTo(Class<R> related, String foreignKey, String ownerKey) {
        return new BelongsTo<>(this, related, foreignKey, ownerKey);
    }

    protected <R extends Model> BelongsToMany<R> belongsToMany(
            Class<R> related, String table, String foreignPivotKey, String relatedPivotKey) {
        return belongsToMany(related, table, foreignPivotKey, relatedPivotKey, primaryKeyName(), relatedPrimaryKey(related));
    }

    protected <R extends Model> BelongsToMany<R> belongsToMany(
            Class<R> related,
            String table,
            String foreignPivotKey,
            String relatedPivotKey,
            String parentKey,
            String relatedKey
    ) {
        return new BelongsToMany<>(this, related, table, foreignPivotKey, relatedPivotKey, parentKey, relatedKey);
    }

    private String relatedPrimaryKey(Class<? extends Model> related) {
        return newBlank(related).primaryKeyName();
    }

    public void setRelation(String name, Relation<?> relation) {
        relations.put(name, relation);
    }

    public Relation<?> getRelation(String name) {
        return relations.get(name);
    }

    public String tableName() { return meta.getTableName(); }
    public String primaryKeyName() { return meta.getPrimaryKey(); }
    public List<String> fields() { return meta.getFields(); }
    public boolean usesSoftDeletes() { return meta.isSoftDeletes(); }
    public String deletedAtColumn() { return meta.getDeletedAtColumn(); }
    public boolean usesTimestamps() { return meta.isTimestamps(); }
    public boolean wasRecentlyCreated() { return wasRecentlyCreated; }
    public boolean exists() { return exists; }
    public Object lastInsertedId() { return lastInsertedId; }
    public Integer getSheetRowNumber() { return sheetRowNumber; }

    public List<String> columnLetters() {
        ensureInitialized();
        List<String> letters = new ArrayList<>();
        for (String field : orderedFields()) {
            letters.add(attributeIDMap.get(field));
        }
        return letters;
    }

    public String columnLetterFor(String column) {
        ensureInitialized();
        return attributeIDMap.get(column);
    }

    private List<String> orderedFields() {
        List<String> ordered = new ArrayList<>();
        for (String field : meta.getFields()) {
            if (attributeIDMap.containsKey(field) && !ordered.contains(field)) {
                ordered.add(field);
            }
        }
        for (String field : attributeIDMap.keySet()) {
            if (!ordered.contains(field)) {
                ordered.add(field);
            }
        }
        return ordered;
    }

    private Object[] buildOrderedRow() {
        Object[] rowData = new Object[attributeIDMap.size()];
        BiMap<String, String> cellToAttr = attributeIDMap.inverse();
        for (int i = 0; i < attributeIDMap.size(); i++) {
            String cellID = ColumnLetters.toLetter(i);
            String attributeName = cellToAttr.get(cellID);
            rowData[i] = attributeValuesMap.get(attributeName);
        }
        return rowData;
    }

    private void touchTimestamps() {
        if (!meta.isTimestamps()) {
            return;
        }
        String now = Instant.now().toString();
        if (!exists && wasRecentlyCreated || attributeValuesMap.get(primaryKeyName()) == null) {
            if (attributeValuesMap.get(meta.getCreatedAtColumn()) == null) {
                attributeValuesMap.put(meta.getCreatedAtColumn(), now);
            }
        }
        attributeValuesMap.put(meta.getUpdatedAtColumn(), now);
    }

    private int resolveSheetRowNumber() throws ResourceNotFoundException, BadRequestException {
        if (sheetRowNumber != null) {
            return sheetRowNumber;
        }
        Object pk = attributeValuesMap.get(primaryKeyName());
        String columnID = attributeIDMap.get(primaryKeyName());
        List<List<Object>> keys = spreadsheet.getWithRange(tableName(), columnID + "2:" + columnID);
        if (keys != null) {
            for (int i = 0; i < keys.size(); i++) {
                if (!keys.get(i).isEmpty() && Objects.equals(String.valueOf(keys.get(i).get(0)), String.valueOf(pk))) {
                    sheetRowNumber = i + 2;
                    return sheetRowNumber;
                }
            }
        }
        throw new ResourceNotFoundException();
    }

    private void addAttributeToIDMap(String attrName, int i) {
        attributeIDMap.put(attrName, ColumnLetters.toLetter(i));
    }

    private void initAttributes() throws BadRequestException, ResourceNotFoundException {
        if (meta.getFields().isEmpty()) {
            return;
        }
        List<List<Object>> headerRows = spreadsheet.getWithRange(tableName(), "A1:ZZ1");
        List<Object> firstRow = (headerRows == null || headerRows.isEmpty()) ? List.of() : headerRows.get(0);

        for (int i = 0; i < firstRow.size(); i++) {
            addAttributeToIDMap(firstRow.get(i).toString(), i);
        }

        boolean rewritten = false;
        for (int i = 0; i < meta.getFields().size(); i++) {
            String attributeName = meta.getFields().get(i);
            if (!attributeIDMap.containsKey(attributeName)) {
                addAttributeToIDMap(attributeName, attributeIDMap.size());
                rewritten = true;
            }
        }
        if (rewritten || firstRow.isEmpty()) {
            spreadsheet.updateRow(tableName(), new ArrayList<>(orderedFields()));
            // rebuild map in field order
            attributeIDMap.clear();
            List<String> ordered = orderedFields().isEmpty() ? meta.getFields() : orderedFields();
            for (int i = 0; i < ordered.size(); i++) {
                addAttributeToIDMap(ordered.get(i), i);
            }
        }
    }

    private void initAutoIncrementPK() throws ResourceNotFoundException, BadRequestException {
        String columnID = attributeIDMap.get(primaryKeyName());
        if (columnID == null) {
            throw new RuntimeException("Primary Key field not identified");
        }
        List<List<Object>> allPrimaryKeys = spreadsheet.getWithRange(tableName(), columnID + "2:" + columnID);
        autoIncrementPKCounter = 1;
        occupiedRowCounter = 2;
        if (allPrimaryKeys != null) {
            occupiedRowCounter = allPrimaryKeys.size() + 2;
            for (List<Object> primaryKeyCell : allPrimaryKeys) {
                if (!primaryKeyCell.isEmpty()) {
                    try {
                        int primaryKey = Integer.parseInt(primaryKeyCell.get(0).toString());
                        if (primaryKey >= autoIncrementPKCounter) {
                            autoIncrementPKCounter = primaryKey + 1;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
    }

    private Integer incrementPK() {
        int curr = autoIncrementPKCounter;
        autoIncrementPKCounter += 1;
        return curr;
    }

    @SuppressWarnings("unchecked")
    private ModelCollection<Model> hydrateCollection(JsonArray jsonArr) {
        ModelCollection<Model> collection = new ModelCollection<>();
        if (jsonArr == null) {
            return collection;
        }
        BiMap<String, String> cellIDAttributeMap = attributeIDMap.inverse();
        for (int rowIndex = 0; rowIndex < jsonArr.size(); rowIndex++) {
            JsonElement el = jsonArr.get(rowIndex);
            Model instance = newBlank(getClass());
            instance.spreadsheet = this.spreadsheet;
            instance.ensureInitialized();
            instance.attributeIDMap.clear();
            instance.attributeIDMap.putAll(this.attributeIDMap);

            JsonArray row = el.getAsJsonObject().get("c").getAsJsonArray();
            for (int i = 0; i < row.size(); i++) {
                JsonElement cellElement = row.get(i);
                JsonPrimitive cellValue = null;
                if (cellElement != null && !cellElement.isJsonNull()) {
                    JsonObject cellObject = cellElement.getAsJsonObject();
                    if (cellObject.has("f") && !cellObject.get("f").isJsonNull()) {
                        cellValue = cellObject.get("f").getAsJsonPrimitive();
                    } else if (cellObject.has("v") && !cellObject.get("v").isJsonNull()) {
                        cellValue = cellObject.get("v").getAsJsonPrimitive();
                    }
                }
                String cellID = ColumnLetters.toLetter(i);
                String attributeName = cellIDAttributeMap.get(cellID);
                if (attributeName != null) {
                    instance.attributeValuesMap.put(attributeName, safeCastCellValue(cellValue));
                }
            }
            instance.exists = true;
            Object pk = instance.attributeValuesMap.get(primaryKeyName());
            if (pk != null) {
                ModelCache.getInstance().put(ModelCache.key(tableName(), pk), instance);
            }
            collection.add(instance);
        }
        return collection;
    }

    private String quoteGViz(Object value) {
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        return "'" + value.toString().replace("'", "\\'") + "'";
    }

    private Object safeCastCellValue(JsonPrimitive primitiveCellValue) {
        if (primitiveCellValue == null) {
            return null;
        }
        String stringCellValue = primitiveCellValue.getAsString();
        try { return Integer.parseInt(stringCellValue); } catch (NumberFormatException ignored) {}
        try { return Float.parseFloat(stringCellValue); } catch (NumberFormatException ignored) {}
        try { return new BigInteger(stringCellValue); } catch (NumberFormatException ignored) {}
        try { return new BigDecimal(stringCellValue); } catch (NumberFormatException ignored) {}
        return stringCellValue;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + attributeValuesMap;
    }
}
