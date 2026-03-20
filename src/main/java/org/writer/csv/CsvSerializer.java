package org.writer.csv;


import org.writer.csv.annotation.CsvColumn;
import org.writer.csv.annotation.CsvEntity;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сериализует список объектов в CSV-строку.
 *
 * <p>Для сериализации класс объекта должен быть помечен аннотацией {@link CsvEntity},
 * а поля, которые должны попасть в CSV - аннотацией {@link CsvColumn}.
 *
 * <p>Поддерживаются:
 * <ul>
 *     <li>строки, числа, enum и другие типы через {@code toString()}</li>
 *     <li>{@link Collection} - элементы объединяются в одну ячейку через {@value #DEFAULT_LIST_DELIMITER}</li>
 *     <li>{@code null} значения полей - преобразуются в пустую строку</li>
 * </ul>
 */
public class CsvSerializer {

    private static final String DEFAULT_LIST_DELIMITER = ";";
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * Преобразует список объектов в CSV-строку.
     *
     * @param data список объектов одного типа
     * @return CSV-представление с заголовком и строками данных
     * @throws IllegalArgumentException если входные данные некорректны,
     *                                  класс не помечен {@link CsvEntity},
     *                                  или отсутствуют поля с {@link CsvColumn}
     */
    public String serialize(List<?> data) {
        validateInput(data);

        Class<?> clazz = data.get(0).getClass();
        CsvEntity csvEntity = getCsvEntityAnnotation(clazz);
        String delimiter = csvEntity.delimiter();
        List<Field> fields = getAnnotatedFields(clazz);

        String header = buildHeader(fields, delimiter);
        String rows = data.stream()
                .map(object -> buildRow(object, fields, delimiter))
                .collect(Collectors.joining(LINE_SEPARATOR));

        return header + LINE_SEPARATOR + rows;
    }

    private CsvEntity getCsvEntityAnnotation(Class<?> clazz) {
        CsvEntity csvEntity = clazz.getAnnotation(CsvEntity.class);
        if (csvEntity == null) {
            throw new IllegalArgumentException(
                    "Class " + clazz.getName() + " is not annotated with @CsvEntity"
            );
        }
        return csvEntity;
    }

    private List<Field> getAnnotatedFields(Class<?> clazz) {
        List<Field> fields = Stream.of(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(CsvColumn.class))
                .sorted(Comparator.comparingInt(field -> field.getAnnotation(CsvColumn.class).order()))
                .toList();

        if (fields.isEmpty()) {
            throw new IllegalArgumentException(
                    "Class " + clazz.getName() + " does not contain fields annotated with @CsvColumn"
            );
        }

        validateUniqueOrder(fields, clazz);

        return fields;
    }

    private void validateUniqueOrder(List<Field> fields, Class<?> clazz) {
        Map<Integer, Long> orderCounts = fields.stream()
                .collect(Collectors.groupingBy(
                        field -> field.getAnnotation(CsvColumn.class).order(),
                        Collectors.counting()
                ));

        boolean hasDuplicates = orderCounts.values().stream().anyMatch(count -> count > 1);
        if (hasDuplicates) {
            throw new IllegalArgumentException(
                    "Class " + clazz.getName() + " contains duplicate @CsvColumn order values"
            );
        }
    }

    private String buildHeader(List<Field> fields, String delimiter) {
        return fields.stream()
                .map(field -> field.getAnnotation(CsvColumn.class).name())
                .map(value -> escape(value, delimiter))
                .collect(Collectors.joining(delimiter));
    }

    private String buildRow(Object object, List<Field> fields, String delimiter) {
        return fields.stream()
                .map(field -> extractValue(object, field))
                .map(value -> escape(value, delimiter))
                .collect(Collectors.joining(delimiter));
    }

    private String extractValue(Object object, Field field) {
        try {
            field.setAccessible(true);
            Object value = field.get(object);

            if (value == null) {
                return "";
            }

            if (value instanceof Collection<?> collection) {
                return collection.stream()
                        .map(item -> item == null ? "" : String.valueOf(item))
                        .collect(Collectors.joining(DEFAULT_LIST_DELIMITER));
            }

            return String.valueOf(value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access field: " + field.getName(), e);
        }
    }

    private String escape(String value, String delimiter) {
        if (value == null) {
            return "";
        }

        boolean mustQuote = value.contains(delimiter)
                || value.contains("\"")
                || value.contains("\n")
                || value.contains("\r");

        String escaped = value.replace("\"", "\"\"");

        return mustQuote ? "\"" + escaped + "\"" : escaped;
    }

    private void validateInput(List<?> data) {
        validateNotEmpty(data);
        validateNoNullElements(data);
        validateSameType(data);
    }

    private void validateNotEmpty(List<?> data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data must not be null or empty");
        }
    }

    private void validateNoNullElements(List<?> data) {
        if (data.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Data must not contain null elements");
        }
    }

    private void validateSameType(List<?> data) {
        Class<?> firstClass = data.get(0).getClass();
        boolean sameType = data.stream().allMatch(item -> item.getClass().equals(firstClass));
        if (!sameType) {
            throw new IllegalArgumentException("All objects must be of the same type");
        }
    }
}