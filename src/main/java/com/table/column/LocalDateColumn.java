package com.table.column;

import com.table.smt.ColumnType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateColumn implements ColumnType<LocalDate> {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final String TYPE_NAME = "LocalDate";

    @Override
    public LocalDate parse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(value.trim(), dateTimeFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Unable casting " + value + " to " + TYPE_NAME);
        }
    }

    @Override
    public String format(LocalDate value) {
        return value == null ? LocalDate.now().format(dateTimeFormatter) : value.format(dateTimeFormatter);
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public Class<LocalDate> getValueClass() {
        return null;
    }

    @Override
    public LocalDate getDefaultValue() {
        return null;
    }
}
