package ca.uwaterloo.cs446.ezbill;

public class IndividualAccountBook extends AccountBook {

    private int income;
    private int expense;

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
