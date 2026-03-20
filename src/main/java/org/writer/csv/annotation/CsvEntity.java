package org.writer.csv.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Помечает класс как доступный для сериализации в CSV.
 *
 * <p>Аннотация задаёт разделитель, используемый между столбцами
 * в сгенерированном CSV-документе.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CsvEntity {

    /**
     * Возвращает разделитель, используемый между столбцами CSV.
     *
     * @return разделитель столбцов (по умолчанию — запятая)
     */
    String delimiter() default ",";
}
