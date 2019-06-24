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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class GroupAccountBookActivity extends AppCompatActivity implements Observer {

    Model model;
    Button calculateBtn;
    //    TextView addMorePeopleBtn;
    TextView myExpense;
    TextView totalExpense;
    TextView numOfParticipants;
    LinearLayout linearLayout;
    LinearLayout.LayoutParams params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_account_book);

        // Get the intent that started this activity
        Intent intent = getIntent();

        model = Model.getInstance();
        model.addObserver(this);

        calculateBtn = (Button) findViewById(R.id.calculateBtn);
//        addMorePeopleBtn = (TextView) findViewById(R.id.addMorePeopleBtn);
        myExpense = (TextView) findViewById(R.id.myExpense);
        totalExpense = (TextView) findViewById(R.id.totalExpense);
        numOfParticipants = (TextView) findViewById(R.id.num_of_participants);
        linearLayout = (LinearLayout) findViewById(R.id.participantIcons);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title.setText(model.getGroupAccountBook(model.getClickedAccountBookId()).getName());

        params = new LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dpTopx(10), 0, dpTopx(10));
        linearLayout.removeAllViews();

        drawParticipantIcons();

        model.initObservers();

    }

    public void addTransactionBtnClick(View view) {
        Intent transactionIntent = new Intent(GroupAccountBookActivity.this,GroupTransactionActivity.class);
        startActivity(transactionIntent);
    }

    public int dpTopx(int dp) {
        return (int) (10 * Resources.getSystem().getDisplayMetrics().density);

    }

    public void drawParticipantIcons() {
        int numOfParticipants = model.getParticipantsById(model.getClickedAccountBookId()).size();
        for (int i = 1; i <= numOfParticipants; i++) {
            if (i > 4) {
                break;
            }
            addParticipantTextView(false, String.valueOf(i));
        }

        addParticipantTextView(true, "\u2022\u2022\u2022");


    }


    public void addMorePeople(View view) {
        Log.d("WRITE", "Add more people clicked!");
        model.addParticipant(model.getClickedAccountBookId());

        int numOfParticipants = model.getParticipantsById(model.getClickedAccountBookId()).size();
        if (numOfParticipants > 4) {
            return;
        }

        linearLayout.removeAllViews();
        drawParticipantIcons();



    }

    public void addParticipantTextView(boolean isClickable, String text) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextSize(20);
//        tv.setPadding(0, 0, 10, 0);
        btn.setLayoutParams(params);
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
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(btn);
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
        numOfParticipants.setText(model.getParticipantsById(model.getClickedAccountBookId()).size() + " People");
        myExpense.setText(String.valueOf(model.getGroupAccountBook(model.getClickedAccountBookId()).getMyExpense()));
        totalExpense.setText(String.valueOf(model.getGroupAccountBook(model.getClickedAccountBookId()).getGroupExpense()));
    }

}