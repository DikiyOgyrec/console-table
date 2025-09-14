package com.table.entity;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private List<Column> columns;
    private List<Row> rows;

    public Table(List<Column> columns, List<Row> rows) {
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
    }
}
