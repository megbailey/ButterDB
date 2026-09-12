package com.github.megbailey.butter.util;

import com.google.common.collect.HashBiMap;
import org.junit.Assert;
import org.junit.Test;

public class ColumnLettersTest {

    @Test
    public void convertsSingleLetters() {
        Assert.assertEquals("A", ColumnLetters.toLetter(0));
        Assert.assertEquals("Z", ColumnLetters.toLetter(25));
    }

    @Test
    public void convertsDoubleLetters() {
        Assert.assertEquals("AA", ColumnLetters.toLetter(26));
        Assert.assertEquals("AB", ColumnLetters.toLetter(27));
        Assert.assertEquals("AZ", ColumnLetters.toLetter(51));
        Assert.assertEquals("BA", ColumnLetters.toLetter(52));
    }

    @Test
    public void defaultMapCoversAThroughZZ() {
        HashBiMap<Integer, String> map = ColumnLetters.defaultMap();
        Assert.assertEquals(702, map.size());
        Assert.assertEquals("A", map.get(0));
        Assert.assertEquals("Z", map.get(25));
        Assert.assertEquals("AA", map.get(26));
        Assert.assertEquals("ZZ", map.get(701));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNegativeIndex() {
        ColumnLetters.toLetter(-1);
    }
}
