package org.writer.factory;

import net.datafaker.Faker;
import org.writer.model.Months;
import org.writer.model.Person;
import org.writer.model.Student;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class SampleDataFactory {
    private static final Faker FAKER = new Faker();
    private static final Random RANDOM = new Random();

    private SampleDataFactory() {
    }

    public static List<Person> createPersons(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> randomPerson())
                .toList();
    }

    public static List<Student> createStudents(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> randomStudent())
                .toList();
    }

    private static Person randomPerson() {
        return Person.builder()
                .firstName(FAKER.name().firstName())
                .lastName(FAKER.name().lastName())
                .dayOfBirth(RANDOM.nextInt(1, 29))
                .monthOfBirth(randomMonth())
                .yearOfBirth(RANDOM.nextInt(1980, 2010))
                .build();
    }

    private static Student randomStudent() {
        return Student.builder()
                .name(FAKER.name().fullName())
                .score(List.of(
                        String.valueOf(RANDOM.nextInt(3, 6)),
                        String.valueOf(RANDOM.nextInt(3, 6)),
                        String.valueOf(RANDOM.nextInt(3, 6))
                ))
                .build();
    }

    private static Months randomMonth() {
        Months[] months = Months.values();
        return months[RANDOM.nextInt(months.length)];
    }
}
