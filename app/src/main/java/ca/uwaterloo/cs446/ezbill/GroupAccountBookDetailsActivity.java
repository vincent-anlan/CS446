package ca.uwaterloo.cs446.ezbill;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nonnull;

public class GroupAccountBookDetailsActivity extends AppCompatActivity implements Observer {

    Model model;
    Button calculateBtn;
    //    TextView addMorePeopleBtn;
    TextView myExpense;
    TextView totalExpense;
    TextView viewAllBills;
    TextView numOfParticipants;
    LinearLayout participantsLayout;
    LinearLayout.LayoutParams participantParams;

    LinearLayout transactionHistoryLayout;
    LinearLayout.LayoutParams transactionElementParams;
    LinearLayout.LayoutParams params_h;
    View lineSeparator;
    int numToDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_account_book_details);

        model = Model.getInstance();
        model.addObserver(this);

        calculateBtn = (Button) findViewById(R.id.calculateBtn);
//        addMorePeopleBtn = (TextView) findViewById(R.id.addMorePeopleBtn);
        myExpense = (TextView) findViewById(R.id.myExpense);
        totalExpense = (TextView) findViewById(R.id.totalExpense);
        viewAllBills = (TextView) findViewById(R.id.viewAllBills);
        numOfParticipants = (TextView) findViewById(R.id.num_of_participants);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        model.readTransactionsFromDB(true);
        model.readParticipantsFromDB();
        model.setViewAllBillClicked(false);

        drawParticipantIcons();
        displayTransactions();

        title.setText(model.getGroupAccountBook(model.getClickedAccountBookId()).getName());
        myExpense.setText(String.valueOf(model.getGroupAccountBook(model.getClickedAccountBookId()).getMyExpense()));
        totalExpense.setText(String.valueOf(model.getGroupAccountBook(model.getClickedAccountBookId()).getGroupExpense()));

        model.initObservers();

    }

    public TextView createTextView(String text, LinearLayout.LayoutParams params, int gravity) {
        TextView textView = new TextView(this);
        textView.setText(text);
//            category.setTextSize(25);
        textView.setLayoutParams(params);
        switch (gravity) {
            case 1:
                textView.setGravity(Gravity.START);
                return textView;
            case 2:
                textView.setGravity(Gravity.CENTER);
                return textView;
            case 3:
                textView.setGravity(Gravity.END);
                return textView;
        }
        return textView;
    }

    public void setupTransactionLayout() {
        transactionHistoryLayout = (LinearLayout) findViewById(R.id.transactionHistory);
        transactionHistoryLayout.setOrientation(LinearLayout.VERTICAL);
        transactionElementParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        transactionElementParams.setMargins(dpTopx(30), 0, dpTopx(30), 0);
        params_h = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        transactionHistoryLayout.setLayoutParams(params_h);
        transactionHistoryLayout.removeAllViews();
    }

    public void setupParticipantLayout() {
        participantsLayout = (LinearLayout) findViewById(R.id.participantIcons);
        participantParams = new LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT);
        participantParams.setMargins(0, dpTopx(10), 0, dpTopx(10));
        participantsLayout.removeAllViews();
    }

    public void addRowToLayout(String text1, String text2, int index){
        TextView tv1 = createTextView(text1, transactionElementParams, 1);
        TextView tv2 = createTextView(text2, transactionElementParams, 3);
        LinearLayout row_layout = new LinearLayout(this);
        row_layout.setOrientation(LinearLayout.HORIZONTAL);
//            linearLayout_h.setGravity(Gravity.START);
        row_layout.addView(tv1);
        row_layout.addView(tv2);
        row_layout.setId(index);
        row_layout.setFocusable(true);
        row_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = view.getId();
                Intent transactionIntent = new Intent(GroupAccountBookDetailsActivity.this, GroupTransactionDetailsActivity.class);
                transactionIntent.putExtra("transactionIndex", index);
                startActivity(transactionIntent);
            }
        });
        transactionHistoryLayout.addView(row_layout);
    }


    public void displayTransactions() {
        setupTransactionLayout();

        int totalTransactionNum = model.currentTransactionList.size();
        if (model.getViewAllBillClicked()) {
            numToDisplay = totalTransactionNum;
        } else {
            numToDisplay = totalTransactionNum > 3 ? 3 : totalTransactionNum;
        }

        for (int i = 0; i < numToDisplay; ++i) {
            GroupTransaction transaction = (GroupTransaction) model.currentTransactionList.get(i);
            addRowToLayout(transaction.getCategory(), Float.toString(transaction.getAmount()), i);
            addRowToLayout(transaction.getDate(), transaction.getPayer().getName(), i);
            lineSeparator = getLayoutInflater().inflate(R.layout.line_separator, transactionHistoryLayout, false);
            transactionHistoryLayout.addView(lineSeparator);
        }
    }

    public void viewAllBillsClicked(View view) {
        model.setViewAllBillClicked(!model.getViewAllBillClicked());
    }


    public void addTransactionBtnClick(View view) {
        Intent transactionIntent = new Intent(GroupAccountBookDetailsActivity.this, GroupTransactionUpsertActivity.class);
        startActivity(transactionIntent);
    }

    public int dpTopx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);

    }

    public void drawParticipantIcons() {
        setupParticipantLayout();

        ArrayList<Participant> participants = model.getParticipantsById(model.getClickedAccountBookId());
        int num = participants.size();
        for (int i = 0; i < num; i++) {
            if (i >= 4) {
                break;
            }
            addParticipantTextView(false, participants.get(i).getName());
        }
        addParticipantTextView(true, "\u2022\u2022\u2022");
    }


    public void addMorePeople(View view) {
        Log.d("WRITE", "Add more people clicked!");
        Participant participant = new Participant(model.currentUserId, model.currentUsername);
        model.addParticipant(model.getClickedAccountBookId(), participant);
        int numOfParticipants = model.getParticipantsById(model.getClickedAccountBookId()).size();
        if (numOfParticipants > 4) {
            return;
        }

        participantsLayout.removeAllViews();
        drawParticipantIcons();
    }

    public void addParticipantTextView(boolean isClickable, String text) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextSize(20);
        btn.setLayoutParams(participantParams);
        btn.setTextColor(Color.parseColor("#000000"));
        btn.setGravity(Gravity.CENTER);
        btn.setBackgroundResource(R.drawable.circle);
//        btn.setWidth(dpTopx(35));
//        btn.setHeight(dpTopx(35));
        if (isClickable) {
            btn.setClickable(true);
            btn.setFocusable(true);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addMorePeople(v);
                }
            });
        }
        participantsLayout.setOrientation(LinearLayout.HORIZONTAL);
        participantsLayout.addView(btn);
    }


    public void doCalculation(View view) {
        Log.d("WRITE", "Calculation Btn clicked!!!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove observer when activity is destroyed.
        model.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        int num = model.getParticipantsById(model.getClickedAccountBookId()).size();
        numOfParticipants.setText(num + " People");
        myExpense.setText(String.valueOf(model.getGroupAccountBook(model.getClickedAccountBookId()).getMyExpense()));
        totalExpense.setText(String.valueOf(model.getGroupAccountBook(model.getClickedAccountBookId()).getGroupExpense()));
        drawParticipantIcons();
        displayTransactions();
        if (model.getViewAllBillClicked()) {
            viewAllBills.setText("Hide");
        } else {
            viewAllBills.setText("View All Bills");
        }
    }

}