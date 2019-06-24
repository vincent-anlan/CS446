package ca.uwaterloo.cs446.ezbill;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupTransaction extends Transaction {

//    Model model;
    private Participant creator;
    private Participant payer;
//    private ArrayList<Participant> participants;
    private ArrayList<HashMap<Participant, Float>> participants;

    public GroupTransaction(String category, String type, Float amount, String currency, String note, String date, Participant creator, Participant payer, ArrayList<HashMap<Participant, Float>> participants) {
        super(category, type, amount, currency, note, date);
        this.creator = creator;
        this.payer = payer;
        this.participants = participants;
    }


    public Participant getCreator() { return creator; }

    public void setCreator(Participant creator) {
        this.creator = creator;
    }

    public Participant getPayer() {
        return payer;
    }

    public void setPayer(Participant payer) {
        this.payer = payer;
    }

    public ArrayList<HashMap<Participant, Float>> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<HashMap<Participant, Float>> participants) {
        this.participants = participants;
    }

    private Spinner mSelectPayer;
    private Spinner mSelectCurrency;
    //private ArrayList<Participant> select_participants;
    private ArrayList<String> pstring;
    private ArrayList<String> currencystring;
    private TextView mSum;

    private Toolbar mytoolbar_new_Expense;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "Transaction";

    private EditText mNoteedit;

    public void cancelButtonHandler(View v) {
        startActivity(new Intent(GroupTransaction.this, GroupAccountBookActivity.class));
    }

    public void saveButtonHandler(View v) {
        this.setNote(mNoteedit.getText().toString());
        this.setDate(mDisplayDate.getText().toString());

        startActivity(new Intent(GroupTransaction.this, GroupAccountBookActivity.class));
    }

}
