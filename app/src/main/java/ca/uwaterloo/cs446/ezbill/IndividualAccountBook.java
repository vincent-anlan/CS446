package ca.uwaterloo.cs446.ezbill;

import java.io.Serializable;

public class IndividualAccountBook extends AccountBook implements Serializable {

    private float income;
    private float expense;

    IndividualAccountBook(String id, String name, String startDate, String endDate, String defaultCurrency, String creatorId) {
        super(id, name, startDate, endDate, defaultCurrency, creatorId);
    }

    public float getIncome() {
        return income;
    }

    public void setIncome(float income) {
        this.income = income;
    }

    public float getExpense() {
        return expense;
    }

    public void setExpense(float expense) {
        this.expense = expense;
    }
}
