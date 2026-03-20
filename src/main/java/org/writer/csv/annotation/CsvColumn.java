package org.writer.csv.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Помечает поле, которое должно быть включено в CSV-вывод.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvColumn {

    /**
     * Возвращает имя столбца, которое будет записано в заголовок CSV.
     *
     * @return имя столбца для аннотированного поля
     */
    String name();

    /**
     * Возвращает позицию столбца в сгенерированном CSV.
     *
     * @return порядковый номер столбца (начиная с 1), используемый для сортировки полей
     */
    int order();
}
