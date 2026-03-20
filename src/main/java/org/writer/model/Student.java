package org.writer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.writer.csv.annotation.CsvColumn;
import org.writer.csv.annotation.CsvEntity;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@CsvEntity(delimiter = ",")
public class Student {

    @CsvColumn(name = "name", order = 1)
    private String name;

    @CsvColumn(name = "score", order = 2)
    private List<String> score;
}