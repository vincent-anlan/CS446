package ca.uwaterloo.cs446.ezbill;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Transaction extends AppCompatActivity implements Comparable<Transaction>{

    private String uuid;
    private String category;
    private String type;
    private Float amount;
    private String currency;
    private String note;
    private String date;

    public Transaction(String uuid, String category, String type, Float amount, String currency, String note, String date) {
        this.uuid = uuid;
        this.category = category;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.note = note;
        this.date = date;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public Date parseStringToDate(String date) throws Exception{
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date parsedDate = (Date) formatter.parse(date);
        return parsedDate;
    }

    public String parseDateToString(Date date) {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = formatter.format(date);
        return formattedDate;
    }

    @Override
    public int compareTo(Transaction transaction) {
        Date d1 = new Date();
        Date d2 = new Date();
        try {
            d1 = parseStringToDate(getDate());
            d2 = parseStringToDate(transaction.getDate());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return d2.compareTo(d1);
    }
}
