package util.converter;

import javafx.util.StringConverter;
import util.DateUtil;

import java.time.LocalDate;

public class DateConverter extends StringConverter<LocalDate> {
    @Override
    public String toString(LocalDate localDate) {
        return DateUtil.format(localDate);
    }

    @Override
    public LocalDate fromString(String s) {
        return DateUtil.parse(s);
    }
}
