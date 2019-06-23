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
import java.util.Calendar;

public class GroupTransaction extends Transaction {

    private Participant creator;
    private Participant payer;
    private ArrayList<Participant> participants;


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

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }

    private Spinner mSelectPayer;
    private ArrayList<Participant> select_participants;
    private ArrayList<String> pstring;
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
                        GroupTransaction.this,
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

        //select payer
        mSelectPayer = (Spinner) findViewById(R.id.payer_spinner);

        select_participants = new ArrayList<>();
        Participant p1 = new Participant(1, "name1");
        select_participants.add(p1);
        Participant p2 = new Participant(2, "name2");
        select_participants.add(p2);
        Participant p3 = new Participant(3, "name3");
        select_participants.add(p3);
        Participant p4 = new Participant(4, "name4");
        select_participants.add(p4);
        Participant p5 = new Participant(5, "name5");
        select_participants.add(p5);

        pstring = new ArrayList<>();
        pstring.add(select_participants.get(0).getName());
        pstring.add(select_participants.get(1).getName());
        pstring.add(select_participants.get(2).getName());
        pstring.add(select_participants.get(3).getName());
        pstring.add(select_participants.get(4).getName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pstring);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSelectPayer.setAdapter(adapter);
        mSelectPayer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selecvalue = parent.getItemAtPosition(position).toString();
                Toast.makeText(GroupTransaction.this, "Selected:" + selecvalue, Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(getApplicationContext(), GroupAccountBook.class));
    }

    public void saveButtonHandler(View v) {
        this.setNote(mNoteedit.getText().toString());
        this.setDate(mDisplayDate.getText().toString());

        startActivity(new Intent(getApplicationContext(), GroupAccountBook.class));
    }

}
