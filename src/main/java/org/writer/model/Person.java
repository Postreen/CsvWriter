package org.writer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.writer.csv.annotation.CsvColumn;
import org.writer.csv.annotation.CsvEntity;

@Data
@Builder
@AllArgsConstructor
@CsvEntity(delimiter = ",")
public class Person {

    @CsvColumn(name = "first_name", order = 1)
    private String firstName;

    @CsvColumn(name = "last_name", order = 2)
    private String lastName;

    @CsvColumn(name = "day_of_birth", order = 3)
    private int dayOfBirth;

    @CsvColumn(name = "month_of_birth", order = 4)
    private Months monthOfBirth;

    @CsvColumn(name = "year_of_birth", order = 5)
    private int yearOfBirth;
}
