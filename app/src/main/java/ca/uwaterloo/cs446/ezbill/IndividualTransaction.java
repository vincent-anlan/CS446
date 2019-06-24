package ca.uwaterloo.cs446.ezbill;

public class IndividualTransaction extends Transaction {
    public IndividualTransaction(String category, String type, Float amount, String currency, String note, String date) {
        super(category, type, amount, currency, note, date);
    }
}
