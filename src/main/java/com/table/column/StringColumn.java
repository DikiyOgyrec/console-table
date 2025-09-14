package com.table.column;

import com.table.smt.ColumnType;

public class StringColumn implements ColumnType<String> {
    private final String TYPE_NAME = "String";

    @Override
    public String parse(String value) {
        return value == null ? "" : value;
    }

    @Override
    public String format(String value) {
        return value == null ? "" : value;
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public Class<String> getValueClass() {
        return String.class;
    }

    @Override
    public String getDefaultValue() {
        return "";
    }
}
