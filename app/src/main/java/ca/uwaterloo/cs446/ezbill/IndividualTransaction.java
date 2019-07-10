package ca.uwaterloo.cs446.ezbill;

public class IndividualTransaction extends Transaction {
    public IndividualTransaction(String uuid, String category, String type, Float amount, String currency, String note, String date) {
        super(uuid, category, type, amount, currency, note, date);
    }
}
