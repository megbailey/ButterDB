package com.github.megbailey.butter.query;

import com.github.megbailey.butter.Model;
import com.github.megbailey.butter.ModelCollection;
import com.github.megbailey.butter.exception.ModelNotFoundException;
import com.github.megbailey.butter.exception.GoggleAccessException;
import com.github.megbailey.butter.exception.ResourceNotFoundException;
import com.github.megbailey.butter.exception.SystemErrorException;
import com.github.megbailey.butter.relation.RelationLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Eloquent-style fluent query builder backed by Google Visualization Query Language.
 */
@SuppressWarnings("unchecked")
public class QueryBuilder<T extends Model> {
    private final Class<T> modelClass;
    private final T prototype;
    private final List<WhereExpression> wheres = new ArrayList<>();
    private final List<String> orderBys = new ArrayList<>();
    private final List<String> groupBys = new ArrayList<>();
    private final List<String> eagerLoads = new ArrayList<>();
    private String[] selectColumns;
    private Integer limit;
    private Integer offset;
    private boolean includeTrashed;
    private boolean onlyTrashed;
    private boolean bypassGlobalScopes;

    public QueryBuilder(Class<T> modelClass) {
        this.modelClass = modelClass;
        this.prototype = Model.newBlank(modelClass);
        applyRegisteredGlobalScopes();
    }

    public QueryBuilder(T prototype) {
        this.modelClass = (Class<T>) prototype.getClass();
        this.prototype = prototype;
        applyRegisteredGlobalScopes();
    }

    private void applyRegisteredGlobalScopes() {
        if (bypassGlobalScopes) {
            return;
        }
        for (GlobalScope scope : GlobalScopeRegistry.forModel(modelClass)) {
            scope.apply(this);
        }
    }

    private void applySoftDeleteConstraint(StringBuilder query, boolean hasWhere) {
        if (bypassGlobalScopes || !prototype.usesSoftDeletes()) {
            return;
        }
        if (includeTrashed && !onlyTrashed) {
            return;
        }
        String constraint = onlyTrashed
                ? formatNull(prototype.deletedAtColumn(), false)
                : formatNull(prototype.deletedAtColumn(), true);
        if (hasWhere) {
            query.append(" and ").append(constraint);
        } else {
            query.append(" where ").append(constraint);
        }
    }

    public static <M extends Model> void addGlobalScope(Class<M> modelClass, String name, GlobalScope scope) {
        GlobalScopeRegistry.add(modelClass, name, scope);
    }

    public static <M extends Model> void removeGlobalScope(Class<M> modelClass, String name) {
        GlobalScopeRegistry.remove(modelClass, name);
    }

    public QueryBuilder<T> withoutGlobalScopes() {
        this.bypassGlobalScopes = true;
        this.wheres.clear();
        return this;
    }

    public QueryBuilder<T> withTrashed() {
        this.includeTrashed = true;
        this.onlyTrashed = false;
        return this;
    }

    public QueryBuilder<T> onlyTrashed() {
        this.onlyTrashed = true;
        this.includeTrashed = true;
        return this;
    }

    public QueryBuilder<T> select(String... columns) {
        this.selectColumns = columns;
        return this;
    }

    public QueryBuilder<T> where(String column, Object value) {
        return where(column, "=", value);
    }

    public QueryBuilder<T> where(String column, String operator, Object value) {
        wheres.add(WhereExpression.leaf(WhereExpression.BooleanOp.AND, formatCondition(column, operator, value)));
        return this;
    }

    public QueryBuilder<T> orWhere(String column, Object value) {
        return orWhere(column, "=", value);
    }

    public QueryBuilder<T> orWhere(String column, String operator, Object value) {
        wheres.add(WhereExpression.leaf(WhereExpression.BooleanOp.OR, formatCondition(column, operator, value)));
        return this;
    }

    public QueryBuilder<T> whereNull(String column) {
        wheres.add(WhereExpression.leaf(WhereExpression.BooleanOp.AND, formatNull(column, true)));
        return this;
    }

    public QueryBuilder<T> whereNotNull(String column) {
        wheres.add(WhereExpression.leaf(WhereExpression.BooleanOp.AND, formatNull(column, false)));
        return this;
    }

    public QueryBuilder<T> whereIn(String column, Collection<?> values) {
        if (values == null || values.isEmpty()) {
            wheres.add(WhereExpression.leaf(WhereExpression.BooleanOp.AND, "(1=0)"));
            return this;
        }
        String joined = values.stream()
                .map(this::quoteValue)
                .map(v -> resolveColumn(column) + "=" + v)
                .collect(Collectors.joining(" or "));
        wheres.add(WhereExpression.leaf(WhereExpression.BooleanOp.AND, "(" + joined + ")"));
        return this;
    }

    public QueryBuilder<T> whereNotIn(String column, Collection<?> values) {
        if (values == null || values.isEmpty()) {
            return this;
        }
        String joined = values.stream()
                .map(this::quoteValue)
                .map(v -> resolveColumn(column) + "!=" + v)
                .collect(Collectors.joining(" and "));
        wheres.add(WhereExpression.leaf(WhereExpression.BooleanOp.AND, "(" + joined + ")"));
        return this;
    }

    public QueryBuilder<T> whereBetween(String column, Object min, Object max) {
        String col = resolveColumn(column);
        wheres.add(WhereExpression.leaf(
                WhereExpression.BooleanOp.AND,
                "(" + col + ">=" + quoteValue(min) + " and " + col + "<=" + quoteValue(max) + ")"
        ));
        return this;
    }

    public QueryBuilder<T> whereNotBetween(String column, Object min, Object max) {
        String col = resolveColumn(column);
        wheres.add(WhereExpression.leaf(
                WhereExpression.BooleanOp.AND,
                "(" + col + "<" + quoteValue(min) + " or " + col + ">" + quoteValue(max) + ")"
        ));
        return this;
    }

    public QueryBuilder<T> where(Consumer<WhereExpression.GroupBuilder> group) {
        WhereExpression.GroupBuilder builder = new WhereExpression.GroupBuilder(columnResolver());
        group.accept(builder);
        wheres.add(WhereExpression.group(WhereExpression.BooleanOp.AND, builder.getExpressions()));
        return this;
    }

    public QueryBuilder<T> orWhere(Consumer<WhereExpression.GroupBuilder> group) {
        WhereExpression.GroupBuilder builder = new WhereExpression.GroupBuilder(columnResolver());
        group.accept(builder);
        wheres.add(WhereExpression.group(WhereExpression.BooleanOp.OR, builder.getExpressions()));
        return this;
    }

    private WhereExpression.ColumnResolver columnResolver() {
        return new WhereExpression.ColumnResolver() {
            @Override
            public String condition(String column, String operator, Object value) {
                return formatCondition(column, operator, value);
            }

            @Override
            public String nullCondition(String column, boolean isNull) {
                return formatNull(column, isNull);
            }
        };
    }

    public QueryBuilder<T> orderBy(String column) {
        return orderBy(column, "asc");
    }

    public QueryBuilder<T> orderByDesc(String column) {
        return orderBy(column, "desc");
    }

    public QueryBuilder<T> orderBy(String column, String direction) {
        String dir = "desc".equalsIgnoreCase(direction) ? "desc" : "asc";
        orderBys.add(resolveColumn(column) + " " + dir);
        return this;
    }

    public QueryBuilder<T> groupBy(String... columns) {
        for (String column : columns) {
            groupBys.add(resolveColumn(column));
        }
        return this;
    }

    public QueryBuilder<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public QueryBuilder<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    public QueryBuilder<T> take(int n) {
        return limit(n);
    }

    public QueryBuilder<T> skip(int n) {
        return offset(n);
    }

    public QueryBuilder<T> with(String... relations) {
        eagerLoads.addAll(Arrays.asList(relations));
        return this;
    }

    public String toGVizQuery() {
        prototype.ensureInitialized();
        StringBuilder query = new StringBuilder("select ");
        if (selectColumns == null || selectColumns.length == 0) {
            query.append(String.join(",", prototype.columnLetters()));
        } else {
            query.append(Arrays.stream(selectColumns)
                    .map(this::resolveColumn)
                    .collect(Collectors.joining(",")));
        }

        boolean hasWhere = !wheres.isEmpty();
        if (hasWhere) {
            query.append(" where ");
            for (int i = 0; i < wheres.size(); i++) {
                WhereExpression expr = wheres.get(i);
                if (i > 0) {
                    query.append(expr.getBooleanOp() == WhereExpression.BooleanOp.OR ? " or " : " and ");
                }
                query.append(expr.compile());
            }
        }
        applySoftDeleteConstraint(query, hasWhere);

        if (!groupBys.isEmpty()) {
            query.append(" group by ").append(String.join(",", groupBys));
        }
        if (!orderBys.isEmpty()) {
            query.append(" order by ").append(String.join(",", orderBys));
        }
        if (limit != null) {
            query.append(" limit ").append(limit);
        }
        if (offset != null) {
            query.append(" offset ").append(offset);
        }
        return query.toString();
    }

    public ModelCollection<T> get() throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        ModelCollection<T> collection = prototype.executeQuery(toGVizQuery());
        if (!eagerLoads.isEmpty() && !collection.isEmpty()) {
            try {
                RelationLoader.load(collection, eagerLoads);
            } catch (SystemErrorException | GoggleAccessException | IOException | ResourceNotFoundException e) {
                throw e;
            } catch (Exception e) {
                throw new SystemErrorException(e.getMessage());
            }
        }
        return collection;
    }

    public T first() throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        Integer previous = this.limit;
        this.limit = 1;
        try {
            return get().first();
        } finally {
            this.limit = previous;
        }
    }

    public T firstOrFail() throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        T model = first();
        if (model == null) {
            throw new ModelNotFoundException("No rows matched query for " + modelClass.getSimpleName());
        }
        return model;
    }

    public T find(Object primaryKey)
            throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        return where(prototype.primaryKeyName(), "=", primaryKey).first();
    }

    public T findOrFail(Object primaryKey)
            throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        T model = find(primaryKey);
        if (model == null) {
            throw new ModelNotFoundException(modelClass.getSimpleName() + " not found for key " + primaryKey);
        }
        return model;
    }

    public Object value(String column)
            throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        T model = first();
        return model == null ? null : model.getFieldValue(column);
    }

    public List<Object> pluck(String column)
            throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        return get().pluck(column);
    }

    public boolean exists() throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        return first() != null;
    }

    public int count() throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        return get().count();
    }

    public Object aggregate(String function, String column)
            throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        prototype.ensureInitialized();
        String col = resolveColumn(column);
        String query = "select " + function + "(" + col + ")";
        if (!wheres.isEmpty()) {
            query += " where " + compileWhereOnly();
        }
        ModelCollection<T> result = prototype.executeAggregateQuery(query);
        if (result.isEmpty()) {
            return null;
        }
        T row = result.first();
        return row.getFieldValue(row.fields().get(0));
    }

    public Object sum(String column) throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        return aggregate("sum", column);
    }

    public Object avg(String column) throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        return aggregate("avg", column);
    }

    public Object min(String column) throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        return aggregate("min", column);
    }

    public Object max(String column) throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        return aggregate("max", column);
    }

    public int update(Map<String, Object> attributes) throws Exception {
        ModelCollection<T> matches = get();
        int updated = 0;
        for (T model : matches) {
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                if (model.isFillableAttribute(entry.getKey())) {
                    model.setFieldValue(entry.getKey(), entry.getValue());
                }
            }
            model.save();
            updated++;
        }
        return updated;
    }

    public int delete() throws Exception {
        ModelCollection<T> matches = withTrashed().get();
        int deleted = 0;
        for (T model : matches) {
            if (Boolean.TRUE.equals(model.delete())) {
                deleted++;
            }
        }
        return deleted;
    }

    public T firstOrCreate(Map<String, Object> attributes) throws Exception {
        T existing = whereAll(attributes).first();
        if (existing != null) {
            return existing;
        }
        return create(attributes);
    }

    public T updateOrCreate(Map<String, Object> search, Map<String, Object> values) throws Exception {
        T existing = whereAll(search).first();
        if (existing != null) {
            Map<String, Object> merged = new LinkedHashMap<>(search);
            merged.putAll(values);
            for (Map.Entry<String, Object> entry : merged.entrySet()) {
                if (existing.isFillableAttribute(entry.getKey())) {
                    existing.setFieldValue(entry.getKey(), entry.getValue());
                }
            }
            existing.save();
            return existing;
        }
        Map<String, Object> merged = new LinkedHashMap<>(search);
        merged.putAll(values);
        return create(merged);
    }

    public T create(Map<String, Object> attributes) throws Exception {
        T model = Model.newBlank(modelClass);
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            if (model.isFillableAttribute(entry.getKey())) {
                model.setFieldValue(entry.getKey(), entry.getValue());
            }
        }
        model.save();
        return model;
    }

    public int insert(List<Map<String, Object>> rows) throws Exception {
        int count = 0;
        for (Map<String, Object> row : rows) {
            create(row);
            count++;
        }
        return count;
    }

    public void chunk(int size, Consumer<ModelCollection<T>> callback)
            throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        int page = 0;
        while (true) {
            this.limit = size;
            this.offset = page * size;
            ModelCollection<T> batch = get();
            if (batch.isEmpty()) {
                break;
            }
            callback.accept(batch);
            if (batch.count() < size) {
                break;
            }
            page++;
        }
    }

    private QueryBuilder<T> whereAll(Map<String, Object> attributes) {
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            where(entry.getKey(), entry.getValue());
        }
        return this;
    }

    private String compileWhereOnly() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wheres.size(); i++) {
            WhereExpression expr = wheres.get(i);
            if (i > 0) {
                sb.append(expr.getBooleanOp() == WhereExpression.BooleanOp.OR ? " or " : " and ");
            }
            sb.append(expr.compile());
        }
        return sb.toString();
    }

    private String formatCondition(String column, String operator, Object value) {
        prototype.ensureInitialized();
        String col = resolveColumn(column);
        String op = normalizeOperator(operator);
        if ("is null".equalsIgnoreCase(op) || "is not null".equalsIgnoreCase(op)) {
            return "(" + col + " " + op + ")";
        }
        if (op.contains(" ")) {
            return "(" + col + " " + op + " " + quoteValue(value) + ")";
        }
        return "(" + col + op + quoteValue(value) + ")";
    }

    private String formatNull(String column, boolean isNull) {
        prototype.ensureInitialized();
        return "(" + resolveColumn(column) + (isNull ? " is null" : " is not null") + ")";
    }

    private String resolveColumn(String column) {
        prototype.ensureInitialized();
        String letter = prototype.columnLetterFor(column);
        return letter != null ? letter : column;
    }

    private String normalizeOperator(String operator) {
        if (operator == null || operator.isBlank()) {
            return "=";
        }
        return operator.trim().toLowerCase();
    }

    private String quoteValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        return "'" + value.toString().replace("'", "\\'") + "'";
    }

    public Class<T> getModelClass() {
        return modelClass;
    }

    public T getPrototype() {
        return prototype;
    }
}
