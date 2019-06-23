package ca.uwaterloo.cs446.ezbill;

import java.io.Serializable;

public class IndividualAccountBook extends AccountBook implements Serializable {

    private int income;
    private int expense;

    IndividualAccountBook(String id, String name, String startDate, String endDate, String defaultCurrency) {
        super(id, name, startDate, endDate, defaultCurrency);
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getExpense() {
        return expense;
    }

    public void setExpense(int expense) {
        this.expense = expense;
    }
}
