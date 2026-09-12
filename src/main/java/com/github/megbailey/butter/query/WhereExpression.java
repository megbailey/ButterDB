package com.github.megbailey.butter.query;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Immutable-ish fragment of a GViz WHERE expression.
 */
public class WhereExpression {
    public enum BooleanOp { AND, OR }

    private final BooleanOp booleanOp;
    private final String sqlFragment;
    private final List<WhereExpression> children;
    private final boolean group;

    private WhereExpression(BooleanOp booleanOp, String sqlFragment, List<WhereExpression> children, boolean group) {
        this.booleanOp = booleanOp;
        this.sqlFragment = sqlFragment;
        this.children = children;
        this.group = group;
    }

    public static WhereExpression leaf(BooleanOp op, String fragment) {
        return new WhereExpression(op, fragment, null, false);
    }

    public static WhereExpression group(BooleanOp op, List<WhereExpression> children) {
        return new WhereExpression(op, null, new ArrayList<>(children), true);
    }

    public BooleanOp getBooleanOp() {
        return booleanOp;
    }

    public String compile() {
        if (!group) {
            return sqlFragment;
        }
        if (children == null || children.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < children.size(); i++) {
            WhereExpression child = children.get(i);
            if (i > 0) {
                sb.append(child.booleanOp == BooleanOp.OR ? " or " : " and ");
            }
            sb.append(child.compile());
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Nested group builder used by {@code where(q -> ...)}.
     */
    public static class GroupBuilder {
        private final List<WhereExpression> expressions = new ArrayList<>();
        private final ColumnResolver resolver;

        public GroupBuilder(ColumnResolver resolver) {
            this.resolver = resolver;
        }

        public GroupBuilder where(String column, Object value) {
            return where(column, "=", value);
        }

        public GroupBuilder where(String column, String operator, Object value) {
            expressions.add(WhereExpression.leaf(BooleanOp.AND, resolver.condition(column, operator, value)));
            return this;
        }

        public GroupBuilder orWhere(String column, Object value) {
            return orWhere(column, "=", value);
        }

        public GroupBuilder orWhere(String column, String operator, Object value) {
            expressions.add(WhereExpression.leaf(BooleanOp.OR, resolver.condition(column, operator, value)));
            return this;
        }

        public GroupBuilder whereNull(String column) {
            expressions.add(WhereExpression.leaf(BooleanOp.AND, resolver.nullCondition(column, true)));
            return this;
        }

        public GroupBuilder whereNotNull(String column) {
            expressions.add(WhereExpression.leaf(BooleanOp.AND, resolver.nullCondition(column, false)));
            return this;
        }

        public GroupBuilder where(Consumer<GroupBuilder> nested) {
            GroupBuilder child = new GroupBuilder(resolver);
            nested.accept(child);
            expressions.add(WhereExpression.group(BooleanOp.AND, child.expressions));
            return this;
        }

        List<WhereExpression> getExpressions() {
            return expressions;
        }
    }

    @FunctionalInterface
    public interface ColumnResolver {
        String condition(String column, String operator, Object value);

        default String nullCondition(String column, boolean isNull) {
            String col = column;
            return "(" + col + (isNull ? " is null" : " is not null") + ")";
        }
    }
}
