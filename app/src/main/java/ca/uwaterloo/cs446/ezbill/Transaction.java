package ca.uwaterloo.cs446.ezbill;

import java.util.ArrayList;

public class Transaction {

    private String category;
    private String type;
    private Float amount;
    private String currency;
    private String note;
    private String date;

    public Transaction(String category, String type, Float amount, String currency, String note, String date) {
        this.category = category;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.note = note;
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
