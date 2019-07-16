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
    LayoutInflater inflater;
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

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

                                if (!model.hasGroupTransaction(transactionId)) {
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
                                    data = data.substring(1, data.length() - 2);
                                    ArrayList<HashMap<Participant, Float>> participants = new ArrayList<>();
                                    String[] pairs = data.split(",");
                                    for (int i = 0; i < pairs.length; i++) {

                                        HashMap<Participant, Float> map = new HashMap<>();
                                        String pair = pairs[i];
                                        String[] keyValue = pair.split("=");
                                        Participant participant = new Participant(keyValue[0], model.getUsername(keyValue[0]));
                                        map.put(participant, Float.valueOf(keyValue[1]));
                                        participants.add(map);
                                    }

                                    GroupTransaction groupTransaction = new GroupTransaction(transactionId, category, type, amount, currency, note, date, creator, payer, participants);
                                    model.addGroupTransaction(groupTransaction);
                                }

                                Log.d("READ", document.getId() + " => " + document.getData());
                            }
                            model.getGroupAccountBook(model.getClickedAccountBookId()).setMyExpense(model.calculateMyExpense(model.getClickedAccountBookId()));
                            model.getGroupAccountBook(model.getClickedAccountBookId()).setGroupExpense(model.calculateTotalExpense(model.getClickedAccountBookId()));
                            myExpense.setText(String.valueOf(model.getGroupAccountBook(model.getClickedAccountBookId()).getMyExpense()));
                            totalExpense.setText(String.valueOf(model.getGroupAccountBook(model.getClickedAccountBookId()).getGroupExpense()));

                            // add to view (transaction)
                            displayTransactions();
                        } else {
                            Log.w("READ", "Error getting documents.", task.getException());
                        }
                    }
                });

        model.setViewAllBillClicked(false);
        title.setText(model.getGroupAccountBook(model.getClickedAccountBookId()).getName());
        participantsLayout = (LinearLayout) findViewById(R.id.participantIcons);
        participantParams = new LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT);
        participantParams.setMargins(0, dpTopx(10), 0, dpTopx(10));
        participantsLayout.removeAllViews();
        drawParticipantIcons();


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

    public void displayTransactions() {
        transactionHistoryLayout = (LinearLayout) findViewById(R.id.transactionHistory);
        transactionHistoryLayout.setOrientation(LinearLayout.VERTICAL);
        transactionElementParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        transactionElementParams.setMargins(dpTopx(30), 0, dpTopx(30), 0);
        params_h = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        transactionHistoryLayout.setLayoutParams(params_h);
        transactionHistoryLayout.removeAllViews();

        int totalTransactionNum = model.currentGroupTransactionList.size();
        if (model.getViewAllBillClicked()) {
            numToDisplay = totalTransactionNum;
        } else {
            numToDisplay = totalTransactionNum > 3 ? 3 : totalTransactionNum;
        }

        for (int i = 0; i < numToDisplay; ++i) {
            GroupTransaction transaction = model.currentGroupTransactionList.get(i);


            TextView category = createTextView(transaction.getCategory(), transactionElementParams, 1);
            TextView amount = createTextView(Float.toString(transaction.getAmount()), transactionElementParams, 3);
            TextView date = createTextView(transaction.getDate(), transactionElementParams, 1);
            TextView payer = createTextView(transaction.getPayer().getName(), transactionElementParams, 3);


            LinearLayout linearLayout_h1 = new LinearLayout(this);
            linearLayout_h1.setOrientation(LinearLayout.HORIZONTAL);
//            linearLayout_h.setGravity(Gravity.START);
            linearLayout_h1.addView(category);
            linearLayout_h1.addView(amount);
            transactionHistoryLayout.addView(linearLayout_h1);

            LinearLayout linearLayout_h2 = new LinearLayout(this);
            linearLayout_h2.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout_h2.addView(date);
            linearLayout_h2.addView(payer);
            transactionHistoryLayout.addView(linearLayout_h2);

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
//        String[] particiantNames = {"A", "B", "C", "D"};
//        int numOfParticipants = model.getParticipantsById(model.getClickedAccountBookId()).size();
//        for (int i = 0; i < numOfParticipants; i++) {
//            if (i >= 4) {
//                break;
//            }
//            addParticipantTextView(false, particiantNames[i]);
//        }
//        addParticipantTextView(true, "\u2022\u2022\u2022");

        String[] particiantNames = {"A", "B", "C", "D"};
        int num = model.getParticipantsById(model.getClickedAccountBookId()).size();
        if (model.clickedAccountBookId.equals("AB1")) {
            num = 4;
        }
        for (int i = 0; i < num; i++) {
            if (i >= 4) {
                break;
            }
            addParticipantTextView(false, particiantNames[i]);
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
        if (model.clickedAccountBookId.equals("AB1")) {
            num = 4;
        }
        numOfParticipants.setText(num + " People");
        myExpense.setText(String.valueOf(model.getGroupAccountBook(model.getClickedAccountBookId()).getMyExpense()));
        totalExpense.setText(String.valueOf(model.getGroupAccountBook(model.getClickedAccountBookId()).getGroupExpense()));
        displayTransactions();
        if (model.getViewAllBillClicked()) {
            viewAllBills.setText("Hide");
        } else {
            viewAllBills.setText("View All Bills");
        }
    }

}