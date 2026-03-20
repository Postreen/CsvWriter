package org.writer.csv;

import org.writer.Writable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Реализация {@link Writable}, сохраняющая коллекции объектов в виде CSV-файлов.
 *
 * <p>Преобразование объектов в CSV-текст делегируется классу {@link CsvSerializer}.
 */
public class CsvWritable implements Writable {
    private final CsvSerializer csvSerializer;

    /**
     * Создаёт объект записи с использованием {@link CsvSerializer} по умолчанию.
     */
    public CsvWritable() {
        this.csvSerializer = new CsvSerializer();
    }

    /**
     * Создаёт объект записи с пользовательским сериализатором.
     *
     * @param csvSerializer сериализатор, используемый для преобразования объектов в CSV
     */
    public CsvWritable(CsvSerializer csvSerializer) {
        this.csvSerializer = csvSerializer;
    }

    /**
     * Сериализует переданные объекты в CSV и записывает результат в указанный файл.
     *
     * <p>Если родительские директории не существуют, они создаются автоматически.
     *
     * @param data объекты, которые необходимо сохранить в файл
     * @param fileName путь к целевому CSV-файлу
     * @throws IllegalArgumentException если {@code fileName} равно {@code null}, пустое или состоит только из пробелов,
     *                                  либо если данные не могут быть сериализованы
     * @throws RuntimeException если при создании или записи файла возникает ошибка ввода-вывода
     */
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
