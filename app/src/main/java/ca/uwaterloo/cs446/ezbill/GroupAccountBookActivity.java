package ca.uwaterloo.cs446.ezbill;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
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
    String accountBookId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_account_book);

        // Get the intent that started this activity
        Intent intent = getIntent();

        accountBookId = intent.getStringExtra("accountBookId");

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

        title.setText(model.getGroupAccountBook(accountBookId).getName());

        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dpTopx(10), 0, dpTopx(10));
        linearLayout.removeAllViews();

        drawParticipantIcons();

        model.initObservers();

    }

    public int dpTopx(int dp) {
        return (int) (10 * Resources.getSystem().getDisplayMetrics().density);

    }

    public void drawParticipantIcons() {
        int numOfParticipants = model.getParticipantsById(accountBookId).size();
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
        model.addParticipant(accountBookId);

        int numOfParticipants = model.getParticipantsById(accountBookId).size();
        if (numOfParticipants > 4) {
            return;
        }

        linearLayout.removeAllViews();
        drawParticipantIcons();



    }

    public void addParticipantTextView(boolean isClickable, String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(20);
//        tv.setPadding(0, 0, 10, 0);
        tv.setLayoutParams(params);
        tv.setTextColor(Color.parseColor("#000000"));
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundResource(R.drawable.circle);
        tv.setWidth(dpTopx(35));
        tv.setHeight(dpTopx(35));
        if (isClickable) {
            tv.setClickable(true);
            tv.setFocusable(true);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addMorePeople(v);
                }
            });
        }
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(tv);
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
        numOfParticipants.setText(model.getParticipantsById(accountBookId).size() + " People");
        myExpense.setText(String.valueOf(model.getGroupAccountBook(accountBookId).getMyExpense()));
        totalExpense.setText(String.valueOf(model.getGroupAccountBook(accountBookId).getGroupExpense()));
    }

}