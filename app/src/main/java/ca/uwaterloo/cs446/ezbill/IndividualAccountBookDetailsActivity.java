package ca.uwaterloo.cs446.ezbill;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class IndividualAccountBookDetailsActivity extends AppCompatActivity implements Observer {

    Model model;
    LinearLayout transactionHistoryLayout;
    LinearLayout.LayoutParams transactionElementParams;
    LinearLayout.LayoutParams params_h;
    LayoutInflater inflater;
    View lineSeparator;
    TextView viewAllBills;
    TextView income;
    TextView expense;
    int numToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_account_book_details);

        model = Model.getInstance();
        model.addObserver(this);
        viewAllBills = (TextView) findViewById(R.id.viewAllBills);
        income = (TextView) findViewById(R.id.income);
        expense = (TextView) findViewById(R.id.expense);

        model.readTransactionsFromDB(false);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        displayTransactions();
        model.setViewAllBillClicked(false);
        income.setText(String.valueOf(model.getIndividualAccountBook(model.getClickedAccountBookId()).getIncome()));
        expense.setText(String.valueOf(model.getIndividualAccountBook(model.getClickedAccountBookId()).getExpense()));

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

    public void addRowToLayout(String text1, String text2){
        TextView tv1 = createTextView(text1, transactionElementParams, 1);
        TextView tv2 = createTextView(text2, transactionElementParams, 3);
        LinearLayout row_layout = new LinearLayout(this);
        row_layout.setOrientation(LinearLayout.HORIZONTAL);
//            linearLayout_h.setGravity(Gravity.START);
        row_layout.addView(tv1);
        row_layout.addView(tv2);
        transactionHistoryLayout.addView(row_layout);
    }

    public void displayTransactions() {
        setupTransactionLayout();

        int totalTransactionNum = model.currentTransactionList.size();
        if (model.getViewAllBillClicked()) {
            numToDisplay = totalTransactionNum;
        } else {
            numToDisplay = totalTransactionNum > 1 ? 1 : totalTransactionNum;
        }

        for (int i = 0; i < numToDisplay; ++i) {
            IndividualTransaction transaction = (IndividualTransaction) model.currentTransactionList.get(i);
            addRowToLayout(transaction.getCategory(), Float.toString(transaction.getAmount()));
            addRowToLayout(transaction.getDate(), transaction.getCurrency());
            lineSeparator = getLayoutInflater().inflate(R.layout.line_separator, transactionHistoryLayout, false);
            transactionHistoryLayout.addView(lineSeparator);
        }
    }

    public void viewAllBillsClicked(View view) {
        model.setViewAllBillClicked(!model.getViewAllBillClicked());
    }

    public int dpTopx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);

    }

    public void addTransactionBtnClick(View view) {
        Intent transactionIntent = new Intent(this, IndividualTransactionUpsertActivity.class);
        startActivity(transactionIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove observer when activity is destroyed.
        model.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        displayTransactions();
        if (model.getViewAllBillClicked()) {
            viewAllBills.setText("Hide");
        } else {
            viewAllBills.setText("View All Bills");
        }
        income.setText(String.valueOf(model.getIndividualAccountBook(model.getClickedAccountBookId()).getIncome()));
        expense.setText(String.valueOf(model.getIndividualAccountBook(model.getClickedAccountBookId()).getExpense()));
    }
}