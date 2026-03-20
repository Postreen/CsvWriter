package org.writer.csv;

import org.writer.Writable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvWritable implements Writable {
    private final CsvSerializer csvSerializer;

    public CsvWritable() {
        this.csvSerializer = new CsvSerializer();
    }

    public CsvWritable(CsvSerializer csvSerializer) {
        this.csvSerializer = csvSerializer;
    }

    @Override
    public void writeToFile(List<?> data, String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name must not be null or blank");
        }

        String content = csvSerializer.serialize(data);
        Path path = Path.of(fileName);

        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            Files.writeString(path, content);

        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSV file: " + fileName, e);
        }
    }
}
