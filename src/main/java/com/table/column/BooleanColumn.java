package com.table.column;

import com.table.smt.ColumnType;

public class BooleanColumn implements ColumnType<Boolean> {
    private final String CLASS_NAME = "Boolean";
    
    @Override
    public Boolean parse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        String v = value.trim().toLowerCase();
        return v.equals("true");
    }

    @Override
    public String format(Boolean value) {
        return value == null ? "false" : value.toString();
    }

    @Override
    public String getTypeName() {
        return CLASS_NAME;
    }

    @Override
    public Class<Boolean> getValueClass() {
        return Boolean.class;
    }

    @Override
    public Boolean getDefaultValue() {
        return false;
    }
}
