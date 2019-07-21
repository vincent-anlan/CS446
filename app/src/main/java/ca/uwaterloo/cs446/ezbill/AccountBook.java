package ca.uwaterloo.cs446.ezbill;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccountBook implements Serializable, Comparable<AccountBook> {

    private String id;
    private String name;
    private String startDate;
    private String endDate;
    private String defaultCurrency;
    private String creatorId;

    AccountBook(String id, String name, String startDate, String endDate, String defaultCurrency, String creatorId) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.defaultCurrency = defaultCurrency;
        this.creatorId = creatorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
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
    public int compareTo(AccountBook accountBook) {
        Date d1 = new Date();
        Date d2 = new Date();
        try {
            d1 = parseStringToDate(getEndDate());
            d2 = parseStringToDate(accountBook.getEndDate());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return d2.compareTo(d1);
    }
}
