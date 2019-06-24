package ca.uwaterloo.cs446.ezbill;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.util.Observer;
import java.util.UUID;

import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class GroupTransactionActivity extends AppCompatActivity implements Observer{

    Model model;

    private Spinner mSelectPayer;
    private Spinner mSelectCurrency;
    private ArrayList<HashMap<Participant, Float>> select_participants;
    private ArrayList<String> pstring;
    private ArrayList<String> currencystring;
    private TextView mSum;

    private Toolbar mytoolbar_new_Expense;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "Transaction";

    private EditText mNoteedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_trans);

        model = Model.getInstance();
        model.addObserver(this);

        //set up toolbar
        mytoolbar_new_Expense = (Toolbar) findViewById(R.id.group_toolbar_add_new_expense);
        setSupportActionBar(mytoolbar_new_Expense);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        mytoolbar_new_Expense.setNavigationIcon(R.drawable.cancel);
//        mytoolbar_new_Expense.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), GroupAccountBook.class));
//            }
//        });
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
                        GroupTransactionActivity.this,
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
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);
                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
            }
        };

        //select currency
        mSelectCurrency = (Spinner) findViewById(R.id.currency_spinner);

        currencystring = new ArrayList<>();
        currencystring.add("CAD");
        currencystring.add("USD");
        currencystring.add("RMB");
        currencystring.add("JPY");
        currencystring.add("EURO");

        ArrayAdapter<String> adapter_curr = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencystring);
        adapter_curr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSelectCurrency.setAdapter(adapter_curr);
        mSelectCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selecvalue = parent.getItemAtPosition(position).toString();
                Toast.makeText(GroupTransactionActivity.this, "Selected:" + selecvalue, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //set payer
        mSelectPayer = (Spinner) findViewById(R.id.payer_spinner);

        select_participants = new ArrayList<>();
        HashMap<Participant, Float> map1 = new HashMap<>();
        Participant p1 = new Participant("U1", "Alice");
        map1.put(p1, Float.valueOf("50"));
        select_participants.add(map1);

        HashMap<Participant, Float> map2 = new HashMap<>();
        Participant p2 = new Participant("U2", "Bob");
        map2.put(p2, Float.valueOf("50"));
        select_participants.add(map2);

        HashMap<Participant, Float> map3 = new HashMap<>();
        Participant p3 = new Participant("U3", "Carol");
        map3.put(p3, Float.valueOf("50"));
        select_participants.add(map3);

        HashMap<Participant, Float> map4 = new HashMap<>();
        Participant p4 = new Participant("U4", "David");
        map4.put(p4, Float.valueOf("50"));
        select_participants.add(map4);

        pstring = new ArrayList<>();
        pstring.add("Alice");
        pstring.add("Bob");
        pstring.add("Carol");
        pstring.add("David");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pstring);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSelectPayer.setAdapter(adapter);
        mSelectPayer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selecvalue = parent.getItemAtPosition(position).toString();
                Toast.makeText(GroupTransactionActivity.this, "Selected:" + selecvalue, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSum = findViewById(R.id.totalAmount);
        mSum.setText("FSDFSD");
        //select participant

    }

    public void cancelButtonHandler(View v) {
        startActivity(new Intent(GroupTransactionActivity.this, GroupAccountBookActivity.class));
    }

    public void saveButtonHandler(View v) {
        Participant transactionCreator = new Participant(model.getCurrentUserId(), model.getCurrentUsername());
        Participant payer = new Participant("U1", mSelectPayer.getSelectedItem().toString());
        String uuid = UUID.randomUUID().toString();
        GroupTransaction newGroupTransaction = new GroupTransaction(uuid, "Food", "Expense", Float.valueOf("200"), "CAD", "None", "2019-07-01", transactionCreator, payer, select_participants);
        newGroupTransaction.setNote(mNoteedit.getText().toString());
        newGroupTransaction.setDate(mDisplayDate.getText().toString());
        model.addToCurrentGroupTransactionList(newGroupTransaction);

        Intent intent = new Intent(GroupTransactionActivity.this, GroupAccountBookActivity.class);
        intent.putExtra("transactionId", newGroupTransaction.getUuid());
        startActivity(intent);
//        startActivity(new Intent(GroupTransactionActivity.this, GroupAccountBookActivity.class));
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
