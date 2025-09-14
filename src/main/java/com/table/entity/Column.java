package com.table.entity;

import com.table.smt.ColumnType;

import java.util.Objects;

public class Column {
    private String name;
    private ColumnType<?> type;

    public Column(String name, ColumnType<?> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public ColumnType<?> getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return Objects.equals(name, column.name) && Objects.equals(type, column.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return name + " ( " + type.getTypeName() + " ) ";
    }
}
