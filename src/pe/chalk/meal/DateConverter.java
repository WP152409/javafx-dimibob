package pe.chalk.meal;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author ChalkPE <chalk@chalk.pe>
 * @since 2017-06-20 21:37
 */
public class DateConverter extends StringConverter<LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");

    @Override
    public String toString(LocalDate date) {
        if (Objects.isNull(date)) return "";
        else return date.format(formatter);
    }

    @Override
    public LocalDate fromString(String str) {
        if (Objects.isNull(str) || str.isEmpty()) return null;
        else return LocalDate.parse(str, formatter);
    }
}
