package com.table.smt;

public interface ColumnType<T> {
    T parse(String value);
    String format(T value);
    String getTypeName();
    Class<T> getValueClass();
    T getDefaultValue();
}