package com.table.entity;

import com.table.column.DoubleColumn;
import com.table.column.IntegerColumn;
import com.table.smt.ColumnType;

import java.util.*;

public class Table {
    private List<Column> columns = new ArrayList<>();
    private List<List<Object>> rows = new ArrayList<>();

    public Table() {}

    public Table(List<Column> columns, List<List<Object>> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    public void addColumn(String name, ColumnType<?> type) {
        columns.add(new Column(name, type));
        Object defaultValue = type.getDefaultValue();
        for (List<Object> row : rows) {
            row.add(defaultValue);
        }
    }

    public void addRow(List<String> stringValues) {
        List<Object> row = new ArrayList<>();

        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            String stringValue = i < stringValues.size() ? stringValues.get(i) : "";

            try {
                Object parsedValue = parseValue(column.getType(), stringValue);
                row.add(parsedValue);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка в столбце '" + column.getName() + "': " + e.getMessage());
                row.add(column.getType().getDefaultValue());
            }
        }

        rows.add(row);
    }

    private Object parseValue(ColumnType<?> type, String value) {
        return type.parse(value);
    }

    public <T> T getValue(int row, int col, Class<T> expectedType) {
        if (row >= 0 && row < rows.size() && col >= 0 && col < columns.size()) {
            Object value = rows.get(row).get(col);
            if (expectedType.isInstance(value)) {
                return (T) value;
            }
        }
        return null;
    }

    public Table selectColumns(int... columnIndexes) {
        Table result = new Table();

        for (int index : columnIndexes) {
            if (index >= 0 && index < columns.size()) {
                Column column = columns.get(index);
                result.addColumn(column.getName(), column.getType());
            }
        }

        for (List<Object> row : rows) {
            List<String> selectedValues = new ArrayList<>();
            for (int index : columnIndexes) {
                if (index >= 0 && index < columns.size()) {
                    Object value = row.get(index);
                    String formattedValue = formatValue(columns.get(index).getType(), value);
                    selectedValues.add(formattedValue);
                } else {
                    selectedValues.add("");
                }
            }
            result.addRow(selectedValues);
        }

        return result;
    }

    public Table selectColumns(String... columnNames) {
        List<Integer> indexes = new ArrayList<>();
        for (String name : columnNames) {
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).getName().equals(name)) {
                    indexes.add(i);
                    break;
                }
            }
        }
        return selectColumns(indexes.stream().mapToInt(i -> i).toArray());
    }

    public Table selectRows(int... rowIndexes) {
        Table result = new Table();

        for (Column column : columns) {
            result.addColumn(column.getName(), column.getType());
        }

        for (int index : rowIndexes) {
            if (index >= 0 && index < rows.size()) {
                List<Object> row = rows.get(index);
                List<String> stringValues = new ArrayList<>();
                for (int i = 0; i < columns.size(); i++) {
                    Object value = row.get(i);
                    String formattedValue = formatValue(columns.get(i).getType(), value);
                    stringValues.add(formattedValue);
                }
                result.addRow(stringValues);
            }
        }

        return result;
    }

    public Table selectRowsRange(int startIndex, int endIndex) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = startIndex; i <= endIndex && i < rows.size(); i++) {
            if (i >= 0) {
                indexes.add(i);
            }
        }
        return selectRows(indexes.stream().mapToInt(i -> i).toArray());
    }

    public Table selectRowsWhere(int columnIndex, String value) {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            return new Table();
        }

        Table result = new Table();

        for (Column column : columns) {
            result.addColumn(column.getName(), column.getType());
        }

        Object targetValue;
        try {
            targetValue = parseValue(columns.get(columnIndex).getType(), value);
        } catch (IllegalArgumentException e) {
            return result;
        }

        for (List<Object> row : rows) {
            Object cellValue = row.get(columnIndex);
            if (Objects.equals(cellValue, targetValue)) {
                List<String> stringValues = new ArrayList<>();
                for (int i = 0; i < columns.size(); i++) {
                    Object rValue = row.get(i);
                    String formattedValue = formatValue(columns.get(i).getType(), rValue);
                    stringValues.add(formattedValue);
                }
                result.addRow(stringValues);
            }
        }

        return result;
    }

    public Map<Object, Table> groupByColumn(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            return new HashMap<>();
        }

        Map<Object, List<List<Object>>> groups = new HashMap<>();

        for (List<Object> row : rows) {
            Object key = row.get(columnIndex);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }

        Map<Object, Table> result = new HashMap<>();
        for (Map.Entry<Object, List<List<Object>>> entry : groups.entrySet()) {
            Table groupTable = new Table();

            for (Column column : columns) {
                groupTable.addColumn(column.getName(), column.getType());
            }

            for (List<Object> row : entry.getValue()) {
                List<String> stringValues = new ArrayList<>();
                for (int i = 0; i < columns.size(); i++) {
                    Object value = row.get(i);
                    String formattedValue = formatValue(columns.get(i).getType(), value);
                    stringValues.add(formattedValue);
                }
                groupTable.addRow(stringValues);
            }

            result.put(entry.getKey(), groupTable);
        }

        return result;
    }

    public Map<Object, Table> groupByColumn(String columnName) {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getName().equals(columnName)) {
                return groupByColumn(i);
            }
        }
        return new HashMap<>();
    }

    public Optional<Number> sum(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            return Optional.empty();
        }

        ColumnType<?> columnType = columns.get(columnIndex).getType();
        if (columnType instanceof IntegerColumn) {
            int sum = rows.stream()
                    .mapToInt(row -> (Integer) row.get(columnIndex))
                    .sum();
            return Optional.of(sum);
        } else if (columnType instanceof DoubleColumn) {
            double sum = rows.stream()
                    .mapToDouble(row -> (Double) row.get(columnIndex))
                    .sum();
            return Optional.of(sum);
        }

        return Optional.empty();
    }

    public Optional<Number> average(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            return Optional.empty();
        }

        ColumnType<?> columnType = columns.get(columnIndex).getType();
        if (columnType instanceof IntegerColumn) {
            OptionalDouble avg = rows.stream()
                    .mapToInt(row -> (Integer) row.get(columnIndex))
                    .average();
            return avg.isPresent() ? Optional.of(avg.getAsDouble()) : Optional.empty();
        } else if (columnType instanceof DoubleColumn) {
            OptionalDouble avg = rows.stream()
                    .mapToDouble(row -> (Double) row.get(columnIndex))
                    .average();
            return avg.isPresent() ? Optional.of(avg.getAsDouble()) : Optional.empty();
        }

        return Optional.empty();
    }

    public long count(int columnIndex, String value) {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            return 0;
        }

        Object targetValue;
        try {
            targetValue = parseValue(columns.get(columnIndex).getType(), value);
        } catch (IllegalArgumentException e) {
            return 0;
        }

        return rows.stream()
                .map(row -> row.get(columnIndex))
                .filter(val -> Objects.equals(val, targetValue))
                .count();
    }

    private String formatValue(ColumnType<?> type, Object value) {
        if (value == null) {
            return formatValueUnsafe(type, type.getDefaultValue());
        }
        return formatValueUnsafe(type, value);
    }

    private <T> String formatValueUnsafe(ColumnType<T> type, Object value) {
        try {
            return type.format((T) value);
        } catch (ClassCastException e) {
            return type.format(type.getDefaultValue());
        }
    }

    public void printTable() {
        if (columns.isEmpty()) {
            System.out.println("Таблица пуста");
            return;
        }

        List<Integer> maxWidths = new ArrayList<>();
        for (Column column : columns) {
            maxWidths.add(column.toString().length());
        }

        for (List<Object> row : rows) {
            for (int i = 0; i < columns.size() && i < maxWidths.size(); i++) {
                Object value = row.get(i);
                String formatted = formatValue(columns.get(i).getType(), value);
                maxWidths.set(i, Math.max(maxWidths.get(i), formatted.length()));
            }
        }

        System.out.print("|");
        for (int i = 0; i < columns.size(); i++) {
            System.out.printf(" %-" + maxWidths.get(i) + "s |", columns.get(i).toString());
        }
        System.out.println();

        System.out.print("|");
        for (int width : maxWidths) {
            System.out.print("-".repeat(width + 2) + "|");
        }
        System.out.println();

        for (List<Object> row : rows) {
            System.out.print("|");
            for (int i = 0; i < columns.size(); i++) {
                Object value = row.get(i);
                String formatted = formatValue(columns.get(i).getType(), value);
                System.out.printf(" %-" + maxWidths.get(i) + "s |", formatted);
            }
            System.out.println();
        }
    }

    public void printSchema() {
        System.out.println("Схема таблицы:");
        for (int i = 0; i < columns.size(); i++) {
            System.out.println((i + 1) + ". " + columns.get(i));
        }
    }

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return columns.size();
    }

    public List<Column> getColumns() {
        return Collections.unmodifiableList(columns);
    }
}
