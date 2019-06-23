package ca.uwaterloo.cs446.ezbill;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class AccountBook implements Serializable {

    private String id;
    private String name;
    private String startDate;
    private String endDate;
    private String defaultCurrency;

    AccountBook(String id, String name, String startDate, String endDate, String defaultCurrency) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.defaultCurrency = defaultCurrency;
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
}
