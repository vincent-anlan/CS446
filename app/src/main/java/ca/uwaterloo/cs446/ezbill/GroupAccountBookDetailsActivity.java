package ca.uwaterloo.cs446.ezbill;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.widget.LinearLayout;
import android.widget.Toast;

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
    View viewAllBillsLineSeperator;
    int numToDisplay;

    LinearLayout menu;
    LinearLayout delete;
    LinearLayout edit;
    LinearLayout add;
    LinearLayout qr_code;
    boolean isMenuOpen;
    boolean isViewAllBillClicked;
    boolean isCreator;
    RelativeLayout floating_menu;


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
        viewAllBillsLineSeperator = (View) findViewById(R.id.viewAllBillsLineSeperator);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        isMenuOpen = false;
        isViewAllBillClicked = false;
        isCreator = model.getGroupAccountBook(model.getClickedAccountBookId()).getCreatorId().equals(model.currentUserId);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.group_account_book_page);
//        if (isCreator) {
            View newView = getLayoutInflater().inflate(R.layout.floating_menu, layout, false);
            layout.addView(newView);
//        } else {
//            View newView = getLayoutInflater().inflate(R.layout.floating_menu_add_button, layout, false);
//            layout.addView(newView);
//        }

        model.readTransactionsFromDB(true);

        drawParticipantIcons();
        displayTransactions();
        updateText();

        initFloatingActionMenu();
        model.initObservers();
    }

    private void initFloatingActionMenu() {
        if (isCreator) {
            delete = (LinearLayout) findViewById(R.id.delete);
            edit = (LinearLayout) findViewById(R.id.edit);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupAccountBookDetailsActivity.this);
                    builder.setMessage("You are about to permanently delete all records of this account book. Do you really want to proceed?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "The account book is deleted.", Toast.LENGTH_SHORT).show();
                            model.removeFromGroupAccountBookList(model.getClickedAccountBookId());
                            finish();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            closeMenu();
                        }
                    });

                    builder.show();
                }
            });
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    restoreDefaultSetting();
                    Intent intent = new Intent(GroupAccountBookDetailsActivity.this, GroupAccountBookUpsertActivity.class);
                    intent.putExtra("accountBookId", model.getClickedAccountBookId());
                    startActivity(intent);
                }
            });
        }

        floating_menu = (RelativeLayout) findViewById(R.id.floating_menu);
        menu = (LinearLayout) findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
            }
        });

        add = (LinearLayout) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreDefaultSetting();
                startActivity(new Intent(GroupAccountBookDetailsActivity.this, GroupTransactionUpsertActivity.class));
            }
        });

        qr_code = (LinearLayout) findViewById(R.id.generate_qr_code);
        qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreDefaultSetting();
                startActivity(new Intent(GroupAccountBookDetailsActivity.this, myQRCode.class));
            }
        });
    }

    private void closeMenu() {
        if (isMenuOpen) {
            if (isCreator) {
                delete.animate().translationY(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        delete.setVisibility(View.GONE);
                    }
                }).start();
                edit.animate().translationY(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        edit.setVisibility(View.GONE);
                    }
                }).start();
            }
            add.animate().translationY(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    add.setVisibility(View.GONE);
                }
            }).start();
            qr_code.animate().translationY(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    qr_code.setVisibility(View.GONE);
                }
            }).start();
            isMenuOpen = false;
            floating_menu.setBackgroundColor(0);
        }
    }

    private void openMenu() {
        if (isCreator) {
            add.animate().translationY(-getResources().getDimension(R.dimen.add_creator));
            delete.animate().translationY(-getResources().getDimension(R.dimen.delete));
            edit.animate().translationY(-getResources().getDimension(R.dimen.edit));
            qr_code.animate().translationY(-getResources().getDimension(R.dimen.qr_code_creator));
            delete.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
        } else {
            add.animate().translationY(-getResources().getDimension(R.dimen.add_no_creator));
            qr_code.animate().translationY(-getResources().getDimension(R.dimen.qr_code_no_creator));
        }
        isMenuOpen = true;
        add.setVisibility(View.VISIBLE);
        qr_code.setVisibility(View.VISIBLE);
        floating_menu.setBackgroundColor(getResources().getColor(R.color.transparentBackground));
    }

    private void restoreDefaultSetting() {
        closeMenu();
        isViewAllBillClicked = false;
        displayTransactions();
        updateText();
    }

    public TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
//            category.setTextSize(25);
        textView.setLayoutParams(transactionElementParams);
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
        TextView tv1 = createTextView(text1);
        tv1.setGravity(Gravity.START);

        TextView tv2 = createTextView(text2);
        tv2.setGravity(Gravity.END);

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
                restoreDefaultSetting();
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
        if (isViewAllBillClicked) {
            numToDisplay = totalTransactionNum;
        } else {
            numToDisplay = totalTransactionNum > 3 ? 3 : totalTransactionNum;
        }

        if (totalTransactionNum <= 3) {
            viewAllBills.setVisibility(View.GONE);
            viewAllBillsLineSeperator.setVisibility(View.GONE);
        } else {
            viewAllBills.setVisibility(View.VISIBLE);
            viewAllBillsLineSeperator.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < numToDisplay; ++i) {
            GroupTransaction transaction = (GroupTransaction) model.currentTransactionList.get(i);
            addRowToLayout(transaction.getCategory(), Float.toString(transaction.getAmount()), i);
            addRowToLayout(transaction.getDate(), "Paid by "+transaction.getPayer().getName(), i);
            lineSeparator = getLayoutInflater().inflate(R.layout.line_separator, transactionHistoryLayout, false);
            transactionHistoryLayout.addView(lineSeparator);
        }
    }

    public void viewAllBillsClicked(View view) {
        closeMenu();
        isViewAllBillClicked = !isViewAllBillClicked;
        displayTransactions();
        updateText();
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
        closeMenu();
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
                    closeMenu();
                    addMorePeople(v);
                }
            });
        }
        participantsLayout.setOrientation(LinearLayout.HORIZONTAL);
        participantsLayout.addView(btn);
    }

    public void calculateClicked(View view) {
        restoreDefaultSetting();
        startActivity(new Intent(this, BillSplitActivity.class));
    }

    public void updateText() {
        GroupAccountBook groupAccountBook = model.getGroupAccountBook(model.getClickedAccountBookId());
        numOfParticipants.setText(groupAccountBook.getParticipantList().size() + " People");
        title.setText(groupAccountBook.getName());
        myExpense.setText(groupAccountBook.getDefaultCurrency() + " " + groupAccountBook.getMyExpense());
        totalExpense.setText(groupAccountBook.getDefaultCurrency() + " " + groupAccountBook.getGroupExpense());
        if (isViewAllBillClicked) {
            viewAllBills.setText("Hide");
        } else {
            viewAllBills.setText("View All Bills");
        }
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
            drawParticipantIcons();
            displayTransactions();
            updateText();
        }
    }

}