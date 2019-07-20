package ca.uwaterloo.cs446.ezbill;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.widget.LinearLayout;

public class GroupAccountBookDetailsActivity extends AppCompatActivity implements Observer {

    Model model;
    Button calculateBtn;
    //    TextView addMorePeopleBtn;
    TextView title;
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
    View editDeleteView;
    int numToDisplay;

    FloatingActionButton menu;
    FloatingActionButton delete;
    FloatingActionButton edit;
    FloatingActionButton add;
    boolean isMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_account_book_details);

        model = Model.getInstance();
        model.addObserver(this);

        editDeleteView = (View) findViewById(R.id.edit_delete);

        calculateBtn = (Button) findViewById(R.id.calculateBtn);
//        addMorePeopleBtn = (TextView) findViewById(R.id.addMorePeopleBtn);
        myExpense = (TextView) findViewById(R.id.myExpense);
        totalExpense = (TextView) findViewById(R.id.totalExpense);
        viewAllBills = (TextView) findViewById(R.id.viewAllBills);
        numOfParticipants = (TextView) findViewById(R.id.num_of_participants);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        model.readParticipantsFromDB();
        model.readTransactionsFromDB(true);

        model.setViewAllBillClicked(false);

        if (!model.getGroupAccountBook(model.getClickedAccountBookId()).getCreatorId().equals(model.currentUserId)) {
            editDeleteView.setVisibility(View.GONE);
        }

        drawParticipantIcons();
        displayTransactions();

        title.setText(model.getGroupAccountBook(model.getClickedAccountBookId()).getName());
        myExpense.setText(String.valueOf(model.getGroupAccountBook(model.getClickedAccountBookId()).getMyExpense()));
        totalExpense.setText(String.valueOf(model.getGroupAccountBook(model.getClickedAccountBookId()).getGroupExpense()));

        initFloatingActionMenu();
        model.initObservers();
    }

    private void initFloatingActionMenu() {
        menu = (FloatingActionButton) findViewById(R.id.menu);
        delete = (FloatingActionButton) findViewById(R.id.delete);
        edit = (FloatingActionButton) findViewById(R.id.edit);
        add = (FloatingActionButton) findViewById(R.id.add);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMenu();
            }
        });

        delete.setOnClickListener(onButtonClick());
        edit.setOnClickListener(onButtonClick());
        add.setOnClickListener(onButtonClick());
    }

    private void toggleMenu() {
        if (!isMenuOpen) {
            delete.animate().translationY(-getResources().getDimension(R.dimen.delete));
            edit.animate().translationY(-getResources().getDimension(R.dimen.edit));
            add.animate().translationY(-getResources().getDimension(R.dimen.add));
            isMenuOpen = true;
        } else {
            delete.animate().translationY(0);
            edit.animate().translationY(0);
            add.animate().translationY(0);
            isMenuOpen = false;
        }
    }

    private View.OnClickListener onButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMenu();
                Intent intent;
                if (view.getId() == R.id.delete) {
                    intent = new Intent(GroupAccountBookDetailsActivity.this, GroupTransactionUpsertActivity.class);
                } else if (view.getId() == R.id.edit) {
                    intent = new Intent(GroupAccountBookDetailsActivity.this, GroupTransactionUpsertActivity.class);
                } else {
                    intent = new Intent(GroupAccountBookDetailsActivity.this, GroupTransactionUpsertActivity.class);
                }
                startActivity(intent);
            }
        };
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
                transactionIntent.putExtra("transactionID", model.getCurrentTransactionList().get(index).getUuid());
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
        startActivity(new Intent(this, BillSplitActivity.class));
    }

    public void onEdit(View view) {
        Log.d("WRITE", "Edit Btn clicked!!!");
        Intent intent = new Intent(this, GroupAccountBookUpsertActivity.class);
        intent.putExtra("accountBookId", model.getClickedAccountBookId());
        startActivity(intent);
    }

    public void onDelete(View view) {
        Log.d("WRITE", "Delete Btn clicked!!!");
        model.removeFromGroupAccountBookList(model.getClickedAccountBookId());
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove observer when activity is destroyed.
        model.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        GroupAccountBook groupAccountBook = model.getGroupAccountBook(model.getClickedAccountBookId());
        if (groupAccountBook != null) {
            if (!groupAccountBook.getCreatorId().equals(model.currentUserId)) {
                editDeleteView.setVisibility(View.GONE);
            }
            int num = model.getParticipantsById(model.getClickedAccountBookId()).size();
            numOfParticipants.setText(num + " People");
            title.setText(groupAccountBook.getName());
            myExpense.setText(String.valueOf(groupAccountBook.getMyExpense()));
            totalExpense.setText(String.valueOf(groupAccountBook.getGroupExpense()));
            drawParticipantIcons();
            displayTransactions();
            if (model.getViewAllBillClicked()) {
                viewAllBills.setText("Hide");
            } else {
                viewAllBills.setText("View All Bills");
            }
        }
    }

}