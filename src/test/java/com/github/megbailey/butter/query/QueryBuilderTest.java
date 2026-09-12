package com.github.megbailey.butter.query;

import com.github.megbailey.butter.Model;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * String-building tests that do not require Google credentials.
 * Model.ensureInitialized falls back to field-order column letters when no DB is configured.
 */
public class QueryBuilderTest {

    public static class Widget extends Model {
        public Widget() {
            super("id", new String[]{"id", "name", "year"}, true);
        }
    }

    @Test
    public void buildsSelectWhereOrderLimit() {
        String query = new QueryBuilder<>(Widget.class)
                .where("name", "Ada")
                .orWhere("year", ">=", 2020)
                .orderByDesc("id")
                .limit(5)
                .toGVizQuery();
        Assert.assertEquals(
                "select A,B,C where (B='Ada') or (C>=2020) order by A desc limit 5",
                query
        );
    }

    @Test
    public void buildsWhereInAndNull() {
        String query = new QueryBuilder<>(Widget.class)
                .whereIn("id", List.of(1, 2, 3))
                .whereNull("name")
                .toGVizQuery();
        Assert.assertEquals(
                "select A,B,C where (A=1 or A=2 or A=3) and (B is null)",
                query
        );
    }

    @Test
    public void buildsGroupedWhere() {
        String query = new QueryBuilder<>(Widget.class)
                .where(q -> q.where("name", "Ada").orWhere("name", "Grace"))
                .where("year", ">", 2010)
                .toGVizQuery();
        Assert.assertEquals(
                "select A,B,C where ((B='Ada') or (B='Grace')) and (C>2010)",
                query
        );
    }

    @Test
    public void whereExpressionLeafAndGroupCompile() {
        WhereExpression leaf = WhereExpression.leaf(WhereExpression.BooleanOp.AND, "(A=1)");
        Assert.assertEquals("(A=1)", leaf.compile());

        WhereExpression group = WhereExpression.group(
                WhereExpression.BooleanOp.AND,
                List.of(
                        WhereExpression.leaf(WhereExpression.BooleanOp.AND, "(B='x')"),
                        WhereExpression.leaf(WhereExpression.BooleanOp.OR, "(C=2)")
                )
        );
        Assert.assertEquals("((B='x') or (C=2))", group.compile());
    }

    @Test
    public void equalityShorthandUsesEquals() {
        String query = new QueryBuilder<>(Widget.class)
                .where("id", 42)
                .toGVizQuery();
        Assert.assertEquals("select A,B,C where (A=42)", query);
    }
}
