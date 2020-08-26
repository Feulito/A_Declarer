package jfx.model;

import javafx.beans.property.*;
import util.DateUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Payment {
    private StringProperty name;
    private StringProperty paymentWay;
    private DoubleProperty amount;
    private ObjectProperty<LocalDate> date;

    public Payment(String name, String paymentWay, double amount, String date) {
        this.name = new SimpleStringProperty(name);
        this.paymentWay = new SimpleStringProperty(paymentWay);
        this.amount = new SimpleDoubleProperty(amount);
        this.date = new SimpleObjectProperty<>(LocalDate.parse(date, DateUtil.formatter));
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getPaymentWay() {
        return paymentWay.get();
    }

    public void setPaymentWay(String paymentWay) {
        this.paymentWay.set(paymentWay);
    }

    public double getAmount() {
        return amount.get();
    }

    public void setAmount(double amount) {
        if (amount >= 0)
            this.amount.set(amount);
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(DateUtil.parse(date));
    }

    public DoubleProperty getAmountProperty() {
        return amount;
    }

    public ObjectProperty<LocalDate> getDateProperty() {
        return date;
    }

    public StringProperty getNameProperty() {
        return name;
    }

    public StringProperty getPaymentWayProperty() {
        return paymentWay;
    }

    @Override
    public String toString() {
        return "Paiement de " + name.getValue() + " en " + paymentWay.getValue() + " d'un montant de " + amount.get() + " le " + date.get().toString();
    }
}
