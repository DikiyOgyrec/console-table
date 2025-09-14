package com.table.column;

import com.table.smt.ColumnType;

public class IntegerColumn implements ColumnType<Integer> {
    private final String TYPE_NAME = "Integer";

    @Override
    public Integer parse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unable casting " + value + " to " + TYPE_NAME);
        }
    }

    @Override
    public String format(Integer value) {
        return value == null ? "0" : value.toString();
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public Class<Integer> getValueClass() {
        return Integer.class;
    }

    @Override
    public Integer getDefaultValue() {
        return 0;
    }
}
