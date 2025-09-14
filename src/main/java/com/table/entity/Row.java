package com.table.entity;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private List<Object> values;

    public Row() {
        this.values = new ArrayList<>();
    }

    public Row(List<Object> values) {
        this.values = new ArrayList<>(values);
    }

    public Object getValue(int index) {
        return index < values.size() ? values.get(index) : null;
    }

    public void setValue(int index, Object value) {
        while (values.size() <= index) {
            values.add(null);
        }
        values.set(index, value);
    }

    public void addValue(Object value) {
        values.add(value);
    }

    public List<Object> getValues() {
        return new ArrayList<>(values);
    }

    public int size() {
        return values.size();
    }
}
