package org.writer.csv;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.writer.Writable;
import org.writer.model.Months;
import org.writer.model.Person;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CsvWritableTest {

    @TempDir
    Path tempDir;

    private final Writable writable = new CsvWritable();
    private static final Faker FAKER = new Faker();
    private static final Random RANDOM = new Random();

    @Test
    @DisplayName("✅ Успешно: записывает CSV в файл")
    void shouldWriteCsvToFile() throws IOException {
        Person person = Person.builder()
                .firstName(FAKER.name().firstName())
                .lastName(FAKER.name().lastName())
                .dayOfBirth(RANDOM.nextInt(1, 29))
                .monthOfBirth(Months.values()[RANDOM.nextInt(Months.values().length)])
                .yearOfBirth(RANDOM.nextInt(1980, 2010))
                .build();

        Path file = tempDir.resolve("persons.csv");

        writable.writeToFile(List.of(person), file.toString());

        String actual = Files.readString(file);
        String expected = String.join(System.lineSeparator(),
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
    @DisplayName("✅ Успешно: создает вложенные директории и записывать файл")
    void shouldCreateDirectoriesAndWriteFile() throws IOException {
        Person person = Person.builder()
                .firstName(FAKER.name().firstName())
                .lastName(FAKER.name().lastName())
                .dayOfBirth(RANDOM.nextInt(1, 29))
                .monthOfBirth(Months.values()[RANDOM.nextInt(Months.values().length)])
                .yearOfBirth(RANDOM.nextInt(1980, 2010))
                .build();

        Path file = tempDir.resolve("nested/dir/persons.csv");

        writable.writeToFile(List.of(person), file.toString());

        assertTrue(Files.exists(file));
        assertTrue(Files.isRegularFile(file));
    }

    @Test
    @DisplayName("❌ Исключение: имя файла равно null")
    void shouldThrowExceptionWhenFileNameIsNull() {
        Person person = Person.builder()
                .firstName(FAKER.name().firstName())
                .lastName(FAKER.name().lastName())
                .dayOfBirth(RANDOM.nextInt(1, 29))
                .monthOfBirth(Months.values()[RANDOM.nextInt(Months.values().length)])
                .yearOfBirth(RANDOM.nextInt(1980, 2010))
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> writable.writeToFile(List.of(person), null)
        );

        assertEquals("File name must not be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("❌ Исключение: имя файла пустое или состоит из пробелов")
    void shouldThrowExceptionWhenFileNameIsBlank() {
        Person person = Person.builder()
                .firstName(FAKER.name().firstName())
                .lastName(FAKER.name().lastName())
                .dayOfBirth(RANDOM.nextInt(1, 29))
                .monthOfBirth(Months.values()[RANDOM.nextInt(Months.values().length)])
                .yearOfBirth(RANDOM.nextInt(1980, 2010))
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> writable.writeToFile(List.of(person), " ")
        );

        assertEquals("File name must not be null or blank", exception.getMessage());
    }
}