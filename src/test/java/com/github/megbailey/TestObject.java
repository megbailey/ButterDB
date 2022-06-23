package com.github.megbailey;

import com.github.megbailey.google.ObjectModel;

import java.util.ArrayList;
import java.util.List;

public class TestObject implements ObjectModel {
    private String id;
    private String some;

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

    @Override
    public String toString() {
        return "{" +
                "'@class':'" + this.getClass() + '\'' +
                ", 'id':'" + id + '\'' +
                ", 'some':'" + some + '\'' +
                '}';
    }
}
