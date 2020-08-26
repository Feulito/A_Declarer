package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {

    private static final String PATTERN = "dd/MM/uuuu";

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");

    public static String format(LocalDate date) {
        if (date == null) {
            return null;
        }
        return formatter.format(date);
    }

    public static LocalDate parse (String date) {
        try {
            return formatter.parse(date, LocalDate::from);
        } catch(DateTimeParseException e) {
            return null;
        }
    }

    public static boolean isValid(String date) {
        return parse(date) != null;
    }
}
