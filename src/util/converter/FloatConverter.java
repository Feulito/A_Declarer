package util.converter;

import javafx.util.StringConverter;

public class FloatConverter extends StringConverter<Double> {
    @Override
    public String toString(Double f) {
        return String.valueOf(f);
    }

    @Override
    public Double fromString(String s) {
        return Double.valueOf(s);
    }
}
