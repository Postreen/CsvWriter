package org.writer.csv;

import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.writer.csv.annotation.CsvColumn;
import org.writer.csv.annotation.CsvEntity;
import org.writer.model.Months;
import org.writer.model.Person;
import org.writer.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


class CsvSerializerTest {

    private final CsvSerializer csvSerializer = new CsvSerializer();
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final Faker FAKER = new Faker();
    private static final Random RANDOM = new Random();

    @Test
    @DisplayName("✅ Успешно: сериализация списка Person в CSV")
    void shouldSerializePersonsToCsv() {
        Person person = Person.builder()
                .firstName(FAKER.name().firstName())
                .lastName(FAKER.name().lastName())
                .dayOfBirth(RANDOM.nextInt(1, 29))
                .monthOfBirth(Months.values()[RANDOM.nextInt(Months.values().length)])
                .yearOfBirth(RANDOM.nextInt(1980, 2010))
                .build();

        String actual = csvSerializer.serialize(List.of(person));

        String expected = String.join(LINE_SEPARATOR,
                "first_name,last_name,day_of_birth,month_of_birth,year_of_birth",
                String.join(",",
                        person.getFirstName(),
                        person.getLastName(),
                        String.valueOf(person.getDayOfBirth()),
                        String.valueOf(person.getMonthOfBirth()),
                        String.valueOf(person.getYearOfBirth())
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("✅ Успешно: сериализация Student со списком оценок в одну CSV-ячейку")
    void shouldSerializeStudentWithList() {
        Student student = Student.builder()
                .name(FAKER.name().fullName())
                .score(List.of(
                        String.valueOf(RANDOM.nextInt(3, 6)),
                        String.valueOf(RANDOM.nextInt(3, 6)),
                        String.valueOf(RANDOM.nextInt(3, 6))
                ))
                .build();

        String actual = csvSerializer.serialize(List.of(student));

        String expected = String.join(LINE_SEPARATOR,
                "name,score",
                student.getName() + "," + String.join(";", student.getScore())
        );

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("✅ Успешно: преобразование null в поле объекта в пустую строку")
    void shouldReturnEmptyStringForNullFieldValue() {
        List<NullablePerson> data = List.of(
                new NullablePerson(FAKER.name().firstName(), null)
        );

        String actual = csvSerializer.serialize(data);

        String expected = String.join(LINE_SEPARATOR,
                "first_name,last_name",
                data.get(0).firstName + ","
        );

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("✅ Успешно: экранирует запятые и кавычки в значении")
    void shouldEscapeCommaAndQuotes() {
        List<EscapedModel> data = List.of(
                new EscapedModel("John, \"Junior\"")
        );

        String actual = csvSerializer.serialize(data);

        String expected = String.join(LINE_SEPARATOR,
                "value",
                "\"John, \"\"Junior\"\"\""
        );

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("✅ Успешно: экранирует перевод строки в значение")
    void shouldEscapeLineBreak() {
        List<EscapedModel> data = List.of(
                new EscapedModel("Hello\nWorld")
        );

        String actual = csvSerializer.serialize(data);

        String expected = String.join(LINE_SEPARATOR,
                "value",
                "\"Hello\nWorld\""
        );

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("❌ Исключение: список данных равен null")
    void shouldThrowExceptionWhenDataIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> csvSerializer.serialize(null)
        );

        assertEquals("Data must not be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("❌ Исключение: список данных пустой")
    void shouldThrowExceptionWhenDataIsEmpty() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> csvSerializer.serialize(List.of())
        );

        assertEquals("Data must not be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("❌ Исключение: список содержит null-элемент")
    void shouldThrowExceptionWhenDataContainsNullElement() {
        List<Object> data = new ArrayList<>();
        data.add(new Object());
        data.add(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> csvSerializer.serialize(data)
        );

        assertEquals("Data must not contain null elements", exception.getMessage());
    }

    @Test
    @DisplayName("❌ Исключение: список содержит объекты разных типов")
    void shouldThrowExceptionWhenObjectsHaveDifferentTypes() {
        List<Object> data = List.of(
                Person.builder()
                        .firstName(FAKER.name().firstName())
                        .lastName(FAKER.name().lastName())
                        .dayOfBirth(RANDOM.nextInt(1, 29))
                        .monthOfBirth(Months.values()[RANDOM.nextInt(Months.values().length)])
                        .yearOfBirth(RANDOM.nextInt(1980, 2010))
                        .build(),
                Student.builder()
                        .name(FAKER.name().fullName())
                        .score(List.of("5"))
                        .build()
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> csvSerializer.serialize(data)
        );

        assertEquals("All objects must be of the same type", exception.getMessage());
    }

    @Test
    @DisplayName("❌ Исключение: класс не помечен аннотацией CsvEntity")
    void shouldThrowExceptionWhenClassIsNotAnnotatedWithCsvEntity() {
        List<NotAnnotatedEntity> data = List.of(
                new NotAnnotatedEntity(FAKER.lorem().word())
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> csvSerializer.serialize(data)
        );

        assertTrue(exception.getMessage().contains("is not annotated with @CsvEntity"));
    }

    @Test
    @DisplayName("❌ Исключение: в классе нет полей с аннотацией CsvColumn")
    void shouldThrowExceptionWhenClassHasNoCsvColumns() {
        List<NoColumnsEntity> data = List.of(
                new NoColumnsEntity(FAKER.lorem().word())
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> csvSerializer.serialize(data)
        );

        assertTrue(exception.getMessage().contains("does not contain fields annotated with @CsvColumn"));
    }

    @Test
    @DisplayName("❌ Исключение: дублировании order у CsvColumn")
    void shouldThrowExceptionWhenCsvColumnOrderIsDuplicated() {
        List<DuplicateOrderEntity> data = List.of(
                new DuplicateOrderEntity("A", "B")
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> csvSerializer.serialize(data)
        );

        assertTrue(exception.getMessage().contains("duplicate @CsvColumn order values"));
    }

    private static class NotAnnotatedEntity {
        private final String value;

        private NotAnnotatedEntity(String value) {
            this.value = value;
        }
    }

    @CsvEntity
    private static class NoColumnsEntity {
        private final String value;

        private NoColumnsEntity(String value) {
            this.value = value;
        }
    }

    @CsvEntity
    private static class EscapedModel {
        @CsvColumn(name = "value", order = 1)
        private final String value;

        private EscapedModel(String value) {
            this.value = value;
        }
    }

    @CsvEntity
    private static class NullablePerson {
        @CsvColumn(name = "first_name", order = 1)
        private final String firstName;

        @CsvColumn(name = "last_name", order = 2)
        private final String lastName;

        private NullablePerson(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    @CsvEntity
    private static class DuplicateOrderEntity {
        @CsvColumn(name = "first", order = 1)
        private final String first;

        @CsvColumn(name = "second", order = 1)
        private final String second;

        private DuplicateOrderEntity(String first, String second) {
            this.first = first;
            this.second = second;
        }
    }
}