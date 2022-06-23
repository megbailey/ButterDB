package com.github.megbailey;

import com.github.megbailey.google.ObjectModel;

import java.util.ArrayList;
import java.util.List;

public class TestObject implements ObjectModel {
    private String id;
    private String some;

    public TestObject setId(String id) {
        this.id = id;
        return this;
    }

    public TestObject setSome(String some) {
        this.some = some;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getSome() {
        return some;
    }

    public List<String> toList() {
        ArrayList<String> arrayList = new ArrayList<>(2);
        arrayList.add(getId());
        arrayList.add(getSome());
        return arrayList;
    }

    public String toJSON() {
        return "{" +
                "'@class':'" + this.getClass() + '\'' +
                ", 'id':'" + id + '\'' +
                ", 'some':'" + some + '\'' +
                '}';
    }
}
