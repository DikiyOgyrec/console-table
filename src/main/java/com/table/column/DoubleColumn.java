package com.table.column;

import com.table.smt.ColumnType;

public class DoubleColumn implements ColumnType<Double> {
    private final String TYPE_NAME = "Double";

    @Override
    public Double parse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unable casting " + value + " to " + TYPE_NAME);
        }
    }

    @Override
    public String format(Double value) {
        return value == null ? "0.0" : value.toString();
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public Class<Double> getValueClass() {
        return Double.class;
    }

    @Override
    public Double getDefaultValue() {
        return 0.0;
    }
}
