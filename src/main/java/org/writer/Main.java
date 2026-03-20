package org.writer;

import org.writer.csv.CsvWritable;
import org.writer.model.Person;
import org.writer.model.Student;
import org.writer.factory.SampleDataFactory;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Writable writable = new CsvWritable();

        List<Person> persons = SampleDataFactory.createPersons(10);
        List<Student> students = SampleDataFactory.createStudents(5);

        writable.writeToFile(persons, "output/persons.csv");
        writable.writeToFile(students, "output/students.csv");
    }
}