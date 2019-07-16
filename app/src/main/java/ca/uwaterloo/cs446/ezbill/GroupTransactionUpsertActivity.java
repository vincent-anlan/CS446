package ca.uwaterloo.cs446.ezbill;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Observable;
import java.util.UUID;

import android.widget.LinearLayout;
import android.widget.Toast;


public class GroupTransactionUpsertActivity extends TransactionUpsertActivityTemplate {

    Model model;

    private Spinner mSelectPayer;
    private Spinner mSelectCurrency;
    private String currencySaveString;
    private String payerSaveString;
    private String payerIDSaveString;
    private String sumSaveString;
    private ArrayList<String> selectName;
    private ArrayList<HashMap<Participant, Float>> select_participants;
    private ArrayList<String> pstring;
    private ArrayList<String> currencystring;
    private Button mSum;

    private Toolbar mytoolbar_new_Expense;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private static final String TAG = "Transaction";

    private EditText mNoteedit;

    private Button mParticipant;
    private String[] listPart;
    private boolean[] checkedPart;
    private ArrayList<Integer> mUserPart = new ArrayList<>();

    LinearLayout linearLayout_v;
    LinearLayout.LayoutParams params;
    LinearLayout.LayoutParams params_h;

    ArrayList<String> collectSumParticipant;
    float totalExpense;
    int onetimeUse;
    ArrayList<EditText> allEds;

    public int dpTopx(int dp) {
        return (int) (10 * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    protected void handle_new_group_transaction(){
//        LinearLayout layone = (LinearLayout) findViewById(R.id.cateshow);// change id here
//        layone.setVisibility(View.GONE);
//
        //set payer
        mSelectPayer = (Spinner) findViewById(R.id.payer_spinner);

        pstring = new ArrayList<>();
        pstring.add("Alice");
        pstring.add("Bob");
        pstring.add("Carol");
        pstring.add("David");

        payerIDSaveString = "U";
        payerSaveString = "";
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, pstring);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSelectPayer.setAdapter(adapter);
        mSelectPayer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selecvalue = parent.getItemAtPosition(position).toString();
                Toast.makeText(GroupTransactionUpsertActivity.this, "Selected:" + selecvalue, Toast.LENGTH_SHORT).show();
                payerSaveString = selecvalue;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if(payerSaveString.equals("Alice")){
            payerIDSaveString = "U1";
        }else if(payerSaveString.equals("Bob")){
            payerIDSaveString = "U2";
        }else if(payerSaveString.equals("Carol")){
            payerIDSaveString = "U3";
        }else{
            payerIDSaveString = "U4";
        }


        //COLLECT sum
        allEds = new ArrayList<EditText>();
        selectName = new ArrayList<>();
        collectSumParticipant = new ArrayList<>();
        sumSaveString = "";

        mSum = (Button) findViewById(R.id.totalAmount);
        onetimeUse = 0;
        totalExpense = Float.parseFloat("0.0");

        //select participant
        linearLayout_v = (LinearLayout) findViewById(R.id.listparticipant);
        linearLayout_v.setOrientation(LinearLayout.VERTICAL);
        mParticipant = (Button) findViewById(R.id.selectParticipant);

        listPart = new String[4];
        listPart[0] = "Alice";
        listPart[1] = "Bob";
        listPart[2] = "Carol";
        listPart[3] = "David";
        checkedPart = new boolean[listPart.length];
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.setMargins(0, dpTopx(10), dpTopx(1000), dpTopx(10));
        params_h = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        mParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(GroupTransactionUpsertActivity.this);
                mBuilder.setTitle("Select Participants For Current Transaction");
                mBuilder.setMultiChoiceItems(listPart, checkedPart, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            if (!mUserPart.contains(position)) {
                                mUserPart.add(position);
                            }
                        } else if (mUserPart.contains(position)) {
                            mUserPart.remove(position);
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        ArrayList<String> collect = new ArrayList<>();
                        for (int i = 0; i < mUserPart.size(); i++) {
                            String item = listPart[mUserPart.get(i)];
                            collect.add(item);
                        }
                        for (int i = 0; i < collect.size(); i++) {
                            String item = collect.get(i);
                            collectSumParticipant.add(item);

                            TextView btn = new TextView(GroupTransactionUpsertActivity.this);
                            btn.setText(item);
                            btn.setTextSize(25);
                            btn.setLayoutParams(params);
                            btn.setGravity(Gravity.START);
                            selectName.add(item);

                            EditText subExpense = new EditText(GroupTransactionUpsertActivity.this);
                            subExpense.setTextSize(25);
                            subExpense.setLayoutParams(params);
                            subExpense.setGravity(Gravity.CENTER);
                            subExpense.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            allEds.add(subExpense);

                            LinearLayout linearLayout_h = new LinearLayout(GroupTransactionUpsertActivity.this);
                            linearLayout_h.setOrientation(LinearLayout.HORIZONTAL);
                            linearLayout_h.setGravity(Gravity.START);
                            linearLayout_h.addView(btn);
                            linearLayout_h.addView(subExpense);
                            linearLayout_v.setLayoutParams(params_h);
                            linearLayout_v.addView(linearLayout_h);
                        }
                    }
                });
                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                mBuilder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedPart.length; i++) {
                            checkedPart[i] = false;
                            mUserPart.clear();
                        }
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        mSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onetimeUse == 0){
                    for(int i=0; i < allEds.size(); i++){
                        String item = allEds.get(i).getText().toString();
                        float f = Float.parseFloat(item);
                        totalExpense = totalExpense + f;
                    }
                    mSum.setText(Float.toString(totalExpense));
                    sumSaveString = Float.toString(totalExpense);
                    select_participants = new ArrayList<>();
                    for(int i=0; i < allEds.size(); i++){
                        String item = allEds.get(i).getText().toString();
                        float f = Float.parseFloat(item);
                        String checkName = selectName.get(i);
                        String checkId = "U";
                        if(checkName.equals("Alice")){
                            checkId = "U1";
                        }else if(checkName.equals("Bob")){
                            checkId = "U2";
                        }else if(checkName.equals("Carol")){
                            checkId = "U3";
                        }else{
                            checkId = "U4";
                        }
                        HashMap<Participant, Float> map = new HashMap<>();
                        Participant p = new Participant(checkId, checkName);
                        map.put(p, f);
                        select_participants.add(map);
                    }
                    onetimeUse = 1;
                }else{
                }
            }
        });
    }

    @Override
    protected void handle_new_individual_transaction(){
        LinearLayout layone = (LinearLayout) findViewById(R.id.individual);
        layone.setVisibility(View.GONE);
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
                        GroupTransactionUpsertActivity.this,
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
                Toast.makeText(GroupTransactionUpsertActivity.this, "Selected:" + selecvalue, Toast.LENGTH_SHORT).show();
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
        model.addObserver(this);
        startActivityInitProcess();
    }

    public void cancelButtonHandler(View v) {
        startActivity(new Intent(GroupTransactionUpsertActivity.this, GroupAccountBookDetailsActivity.class));
    }

    public void saveButtonHandler(View v) {
        Participant transactionCreator = new Participant(model.getCurrentUserId(), model.getCurrentUsername());
        Participant payer = new Participant(payerIDSaveString, mSelectPayer.getSelectedItem().toString());
        String uuid = UUID.randomUUID().toString();

        GroupTransaction newGroupTransaction = new GroupTransaction(uuid, "Food", "Expense", Float.valueOf(sumSaveString), currencySaveString, "None", "04/07/1998", transactionCreator, payer, select_participants);

        newGroupTransaction.setNote(mNoteedit.getText().toString());
        newGroupTransaction.setDate(mDisplayDate.getText().toString());
        model.addToCurrentTransactionList(newGroupTransaction, "Group");

        Intent intent = new Intent(GroupTransactionUpsertActivity.this, GroupAccountBookDetailsActivity.class);
        intent.putExtra("transactionId", newGroupTransaction.getUuid());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove observer when activity is destroyed.
        model.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}
