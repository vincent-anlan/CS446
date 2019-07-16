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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.UUID;


public class IndividualTransactionUpsertActivity extends TransactionUpsertActivityTemplate {
    Model model;
    private Spinner mSelectCurrency;
    private String currencySaveString;
    private ArrayList<String> currencystring;
    private Toolbar mytoolbar_new_Expense;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private static final String TAG = "Transaction";

    private EditText mNoteedit;
    private EditText mAmountedit;

    @Override
    public void handle_new_group_transaction(){
        LinearLayout layout_payer = (LinearLayout) findViewById(R.id.payerlayout);
        layout_payer.setVisibility(View.GONE);
        LinearLayout layout_parti = (LinearLayout) findViewById(R.id.partilayout);
        layout_parti.setVisibility(View.GONE);
        LinearLayout layout_autosum = (LinearLayout) findViewById(R.id.autosumlayout);
        layout_autosum.setVisibility(View.GONE);
        LinearLayout layout_list = (LinearLayout) findViewById(R.id.listparticipant);
        layout_list.setVisibility(View.GONE);
    }

    @Override
    public void handle_new_individual_transaction(){
        //set amount
        mAmountedit = (EditText) findViewById(R.id.editIndividual);
    }
    @Override
    protected void initial_transaction_page(){
        //set up toolbar
        mytoolbar_new_Expense = (Toolbar) findViewById(R.id.group_toolbar_add_new_expense);
        setSupportActionBar(mytoolbar_new_Expense);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //set note
        mNoteedit = (EditText) findViewById(R.id.editNote);

        //select date
        mDisplayDate = (TextView) findViewById(R.id.selectDate);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        IndividualTransactionUpsertActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyyy: " + month + "/" + day + "/" + year);
                String date = month + "/" + day + "/" + year;
                if(month < 10){
                    date = "0" + month + "/" + day + "/" + year;
                    if(day < 10){
                        date = "0" + month + "/" + "0" + day + "/" + year;
                    }else{ }
                }else{ }
                mDisplayDate.setText(date);
            }
        };

        //select currency
        mSelectCurrency = (Spinner) findViewById(R.id.currency_spinner);
        currencySaveString = "CAD";
        currencystring = new ArrayList<>();
        currencystring.add("CAD");
        currencystring.add("USD");
        currencystring.add("RMB");
        currencystring.add("JPY");
        currencystring.add("EURO");

        ArrayAdapter<String> adapter_curr = new ArrayAdapter<>(this, R.layout.spinner_item, currencystring);
        adapter_curr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSelectCurrency.setAdapter(adapter_curr);
        mSelectCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selecvalue = parent.getItemAtPosition(position).toString();
                Toast.makeText(IndividualTransactionUpsertActivity.this, "Selected:" + selecvalue, Toast.LENGTH_SHORT).show();
                currencySaveString = selecvalue;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_trans);
        model = Model.getInstance();
//        model.addObserver(this);
        startActivityInitProcess();
    }

    public void cancelButtonHandler(View v) {
        startActivity(new Intent(IndividualTransactionUpsertActivity.this, GroupAccountBookDetailsActivity.class));
    }

    public void saveButtonHandler(View v) {
        String uuid = UUID.randomUUID().toString();

        IndividualTransaction newIndividualTransaction = new IndividualTransaction(uuid, "Food", "Expense", Float.valueOf(mAmountedit.getText().toString()), currencySaveString, "None", "04/07/1998");
        newIndividualTransaction.setNote(mNoteedit.getText().toString());
        newIndividualTransaction.setDate(mDisplayDate.getText().toString());
//        model.addToCurrentIndividualransactionList(newIndividualTransaction);

        Intent intent = new Intent(IndividualTransactionUpsertActivity.this, GroupAccountBookDetailsActivity.class);
        intent.putExtra("transactionId", newIndividualTransaction.getUuid());
        startActivity(intent);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Remove observer when activity is destroyed.
//        model.deleteObserver(this);
//    }
//
//    @Override
//    public void update(Observable o, Object arg) {
//    }

}
