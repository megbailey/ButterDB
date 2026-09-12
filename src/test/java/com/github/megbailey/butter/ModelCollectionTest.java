package com.github.megbailey.butter;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ModelCollectionTest {

    public static class Widget extends Model {
        public Widget() {
            super("id", new String[]{"id", "name"}, true);
        }
    }

    @Test
    public void firstLastCountAndPluck() {
        Widget a = new Widget();
        a.setFieldValue("id", 1);
        a.setFieldValue("name", "Ada");
        Widget b = new Widget();
        b.setFieldValue("id", 2);
        b.setFieldValue("name", "Grace");

        ModelCollection<Widget> collection = new ModelCollection<>();
        collection.add(a);
        collection.add(b);

        Assert.assertEquals(2, collection.count());
        Assert.assertFalse(collection.isEmpty());
        Assert.assertEquals(a, collection.first());
        Assert.assertEquals(b, collection.last());
        Assert.assertEquals(List.of("Ada", "Grace"), collection.pluck("name"));
    }

    @Test
    public void filterAndMap() {
        Widget a = new Widget();
        a.setFieldValue("name", "Ada");
        Widget b = new Widget();
        b.setFieldValue("name", "Bob");

        ModelCollection<Widget> collection = new ModelCollection<>(List.of(a, b));
        ModelCollection<Widget> filtered = collection.filter(w -> "Ada".equals(w.getFieldValue("name")));
        Assert.assertEquals(1, filtered.count());
        Assert.assertEquals(List.of("Ada", "Bob"), collection.map(w -> (String) w.getFieldValue("name")));
    }

    @Test
    public void emptyCollectionHelpers() {
        ModelCollection<Widget> empty = new ModelCollection<>();
        Assert.assertTrue(empty.isEmpty());
        Assert.assertNull(empty.first());
        Assert.assertNull(empty.last());
        Assert.assertEquals(0, empty.count());
    }
}
