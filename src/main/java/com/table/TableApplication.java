package com.table;

import com.table.column.*;
import com.table.entity.Column;
import com.table.entity.Table;
import com.table.smt.ColumnType;

import java.util.*;

public class TableApplication {
    private static Scanner scanner = new Scanner(System.in);
    private static Table table = new Table();

    public static void main(String[] args) {
        System.out.println("=== Приложение для работы с многотипными таблицами ===");

        while (true) {
            showMenu();
            int choice = getIntInput("Выберите действие: ");

            switch (choice) {
                case 1:
                    createTable();
                    break;
                case 2:
                    addColumn();
                    break;
                case 3:
                    addRow();
                    break;
                case 4:
                    showTable();
                    break;
                case 5:
                    showSchema();
                    break;
                case 6:
                    selectColumns();
                    break;
                case 7:
                    selectRows();
                    break;
                case 8:
                    filterRows();
                    break;
                case 9:
                    groupByColumn();
                    break;
                case 10:
                    aggregateOperations();
                    break;
                case 11:
                    System.out.println("До свидания!");
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n=== МЕНЮ ===");
        System.out.println("1. Создать новую таблицу");
        System.out.println("2. Добавить столбец");
        System.out.println("3. Добавить строку");
        System.out.println("4. Показать таблицу");
        System.out.println("5. Показать схему таблицы");
        System.out.println("6. Выборка по столбцам");
        System.out.println("7. Выборка по строкам");
        System.out.println("8. Фильтрация строк");
        System.out.println("9. Группировка по столбцу");
        System.out.println("10. Агрегатные операции");
        System.out.println("11. Выход");
    }

    private static void createTable() {
        System.out.println("\nСоздание новой таблицы");
        table = new Table();
        System.out.println("Новая пустая таблица создана.");
        System.out.println("Используйте пункт 2 для добавления столбцов.");
    }

    private static void addColumn() {
        System.out.println("\nДобавление столбца");
        System.out.print("Введите название столбца: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Название столбца не может быть пустым!");
            return;
        }

        System.out.println("Выберите тип данных:");
        System.out.println("1. String (текст)");
        System.out.println("2. Integer (целое число)");
        System.out.println("3. Double (дробное число)");
        System.out.println("4. Boolean (да/нет)");
        System.out.println("5. Date (дата в формате dd.MM.yyyy)");

        int typeChoice = getIntInput("Выберите тип: ");

        ColumnType<?> columnType;
        switch (typeChoice) {
            case 1:
                columnType = new StringColumn();
                break;
            case 2:
                columnType = new IntegerColumn();
                break;
            case 3:
                columnType = new DoubleColumn();
                break;
            case 4:
                columnType = new BooleanColumn();
                break;
            case 5:
                columnType = new LocalDateColumn();
                break;
            default:
                System.out.println("Неверный выбор, используется String");
                columnType = new StringColumn();
        }

        table.addColumn(name, columnType);
        System.out.println("Столбец '" + name + "' типа " + columnType.getTypeName() + " добавлен.");
    }

    private static void addRow() {
        if (table.getColumnCount() == 0) {
            System.out.println("Сначала создайте столбцы!");
            return;
        }

        System.out.println("\nДобавление строки");
        table.printSchema();
        System.out.println("Введите значения для каждого столбца:");

        List<String> values = new ArrayList<>();
        for (Column column : table.getColumns()) {
            System.out.print(column.getName() + " (" + column.getType().getTypeName() + "): ");
            String value = scanner.nextLine();
            values.add(value);
        }

        table.addRow(values);
        System.out.println("Строка добавлена.");
    }

    private static void showTable() {
        System.out.println("\n=== ТАБЛИЦА ===");
        table.printTable();
        System.out.println("Строк: " + table.getRowCount() + ", Столбцов: " + table.getColumnCount());
    }

    private static void showSchema() {
        System.out.println("\n=== СХЕМА ТАБЛИЦЫ ===");
        table.printSchema();
    }

    private static void selectColumns() {
        if (table.getColumnCount() == 0) {
            System.out.println("Таблица не содержит столбцов!");
            return;
        }

        System.out.println("\nВыборка по столбцам");
        table.printSchema();
        System.out.println("1. По именам столбцов");
        System.out.println("2. По номерам столбцов");

        int choice = getIntInput("Выберите способ: ");

        if (choice == 1) {
            System.out.print("Введите имена столбцов через запятую: ");
            String input = scanner.nextLine();
            String[] columnNames = input.split(",");
            for (int i = 0; i < columnNames.length; i++) {
                columnNames[i] = columnNames[i].trim();
            }

            Table result = table.selectColumns(columnNames);
            System.out.println("\n=== РЕЗУЛЬТАТ ВЫБОРКИ ===");
            result.printTable();
        } else if (choice == 2) {
            System.out.print("Введите номера столбцов через запятую (1-" + table.getColumnCount() + "): ");
            String input = scanner.nextLine();
            try {
                int[] indexes = Arrays.stream(input.split(","))
                        .mapToInt(s -> Integer.parseInt(s.trim()) - 1)
                        .toArray();

                Table result = table.selectColumns(indexes);
                System.out.println("\n=== РЕЗУЛЬТАТ ВЫБОРКИ ===");
                result.printTable();
            } catch (NumberFormatException e) {
                System.out.println("Ошибка в формате номеров!");
            }
        }
    }

    private static void selectRows() {
        if (table.getRowCount() == 0) {
            System.out.println("В таблице нет строк!");
            return;
        }

        System.out.println("\nВыборка по строкам");
        System.out.println("1. По номерам строк");
        System.out.println("2. По диапазону строк");

        int choice = getIntInput("Выберите способ: ");

        if (choice == 1) {
            System.out.print("Введите номера строк через запятую (1-" + table.getRowCount() + "): ");
            String input = scanner.nextLine();
            try {
                int[] indexes = Arrays.stream(input.split(","))
                        .mapToInt(s -> Integer.parseInt(s.trim()) - 1)
                        .toArray();

                Table result = table.selectRows(indexes);
                System.out.println("\n=== РЕЗУЛЬТАТ ВЫБОРКИ ===");
                result.printTable();
            } catch (NumberFormatException e) {
                System.out.println("Ошибка в формате номеров!");
            }
        } else if (choice == 2) {
            int start = getIntInput("Начальная строка (1-" + table.getRowCount() + "): ") - 1;
            int end = getIntInput("Конечная строка (1-" + table.getRowCount() + "): ") - 1;

            Table result = table.selectRowsRange(start, end);
            System.out.println("\n=== РЕЗУЛЬТАТ ВЫБОРКИ ===");
            result.printTable();
        }
    }

    private static void filterRows() {
        if (table.getRowCount() == 0 || table.getColumnCount() == 0) {
            System.out.println("Таблица пуста!");
            return;
        }

        System.out.println("\nФильтрация строк по значению");
        table.printSchema();

        int columnIndex = getIntInput("Номер столбца для фильтрации (1-" + table.getColumnCount() + "): ") - 1;
        if (columnIndex < 0 || columnIndex >= table.getColumnCount()) {
            System.out.println("Неверный номер столбца!");
            return;
        }

        Column column = table.getColumns().get(columnIndex);
        System.out.print("Введите значение для поиска (" + column.getType().getTypeName() + "): ");
        String value = scanner.nextLine();

        Table result = table.selectRowsWhere(columnIndex, value);
        System.out.println("\n=== РЕЗУЛЬТАТ ФИЛЬТРАЦИИ ===");
        result.printTable();
    }

    private static void groupByColumn() {
        if (table.getRowCount() == 0 || table.getColumnCount() == 0) {
            System.out.println("Таблица пуста!");
            return;
        }

        System.out.println("\nГруппировка по столбцу");
        table.printSchema();

        int columnIndex = getIntInput("Номер столбца для группировки (1-" + table.getColumnCount() + "): ") - 1;
        if (columnIndex < 0 || columnIndex >= table.getColumnCount()) {
            System.out.println("Неверный номер столбца!");
            return;
        }

        Map<Object, Table> groups = table.groupByColumn(columnIndex);

        System.out.println("\n=== РЕЗУЛЬТАТ ГРУППИРОВКИ ===");
        for (Map.Entry<Object, Table> entry : groups.entrySet()) {
            System.out.println("\n--- Группа: " + entry.getKey() + " ---");
            entry.getValue().printTable();
        }
    }

    private static void aggregateOperations() {
        if (table.getRowCount() == 0 || table.getColumnCount() == 0) {
            System.out.println("Таблица пуста!");
            return;
        }

        System.out.println("\nАгрегатные операции");
        table.printSchema();

        int columnIndex = getIntInput("Номер столбца (1-" + table.getColumnCount() + "): ") - 1;
        if (columnIndex < 0 || columnIndex >= table.getColumnCount()) {
            System.out.println("Неверный номер столбца!");
            return;
        }

        Column column = table.getColumns().get(columnIndex);
        String typeName = column.getType().getTypeName();

        System.out.println("Доступные операции для типа " + typeName + ":");

        if (typeName.equals("Integer") || typeName.equals("Double")) {
            System.out.println("1. Сумма");
            System.out.println("2. Среднее значение");
            System.out.println("3. Подсчет значений");

            int operation = getIntInput("Выберите операцию: ");

            switch (operation) {
                case 1:
                    Optional<Number> sum = table.sum(columnIndex);
                    if (sum.isPresent()) {
                        System.out.println("Сумма: " + sum.get());
                    } else {
                        System.out.println("Невозможно вычислить сумму");
                    }
                    break;
                case 2:
                    Optional<Number> avg = table.average(columnIndex);
                    if (avg.isPresent()) {
                        System.out.println("Среднее значение: " + avg.get());
                    } else {
                        System.out.println("Невозможно вычислить среднее значение");
                    }
                    break;
                case 3:
                    System.out.print("Введите значение для подсчета: ");
                    String value = scanner.nextLine();
                    long count = table.count(columnIndex, value);
                    System.out.println("Количество: " + count);
                    break;
                default:
                    System.out.println("Неверная операция");
            }
        } else {
            System.out.println("1. Подсчет значений");

            System.out.print("Введите значение для подсчета: ");
            String value = scanner.nextLine();
            long count = table.count(columnIndex, value);
            System.out.println("Количество: " + count);
        }
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите корректное число!");
            }
        }
    }
}