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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nonnull;

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

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // read data from database
        db.collection("transactions")
                .whereEqualTo("accountBookId", model.getClickedAccountBookId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String transactionId = document.getData().get("id").toString();
                                String category = document.getData().get("category").toString();
                                float amount = Float.valueOf(document.getData().get("amount").toString());
                                String creatorId = document.getData().get("creator").toString();
                                String creatorName = model.getUsername(creatorId);
                                Participant creator = new Participant(creatorId, creatorName);
                                String date = document.getData().get("date").toString();
                                String note = document.getData().get("note").toString();
                                String payerId = document.getData().get("payer").toString();
                                String payerName = model.getUsername(payerId);
                                Participant payer = new Participant(payerId, payerName);
                                String type = document.getData().get("type").toString();
                                String currency = document.getData().get("currency").toString();
                                String data = document.getData().get("participant").toString();
                                data = data.substring(1,data.length()-2);
                                ArrayList<HashMap<Participant, Float>> participants = new ArrayList<>();
                                String[] pairs = data.split(",");
                                for (int i=0;i<pairs.length;i++) {

                                    HashMap<Participant, Float> map = new HashMap<>();
                                    String pair = pairs[i];
                                    String[] keyValue = pair.split("=");
                                    Participant participant = new Participant(keyValue[0], model.getUsername(keyValue[0]));
                                    map.put(participant, Float.valueOf(keyValue[1]));
                                    participants.add(map);
                                }

                                GroupTransaction groupTransaction = new GroupTransaction(category, type, amount, currency, note, date, creator, payer, participants);
                                model.addGroupTransaction(groupTransaction);

                                Log.d("READ", document.getId() + " => " + document.getData());
                            }
                            model.getGroupAccountBook(model.getClickedAccountBookId()).setMyExpense(model.calculateMyExpense(model.getClickedAccountBookId()));
                            model.getGroupAccountBook(model.getClickedAccountBookId()).setGroupExpense(model.calculateTotalExpense(model.getClickedAccountBookId()));
                            myExpense.setText(String.valueOf(model.getGroupAccountBook(model.getClickedAccountBookId()).getMyExpense()));
                            totalExpense.setText(String.valueOf(model.getGroupAccountBook(model.getClickedAccountBookId()).getGroupExpense()));
                            // add to view (transaction)

                        } else {
                            Log.w("READ", "Error getting documents.", task.getException());
                        }
                    }
                });

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