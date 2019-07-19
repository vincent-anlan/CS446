package ca.uwaterloo.cs446.ezbill;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class IndividualAccountBookDetailsActivity extends AppCompatActivity implements Observer {

    Model model;
    LinearLayout column_layout;
    LinearLayout.LayoutParams column_params;
    LinearLayout.LayoutParams row_params;
    View lineSeparator;
    TextView title;
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

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.individual_toolbar);
        title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        model.readTransactionsFromDB(false);
        displayTransactions();
        model.setViewAllBillClicked(false);
        title.setText(model.getIndividualAccountBook(model.getClickedAccountBookId()).getName());
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
        column_layout = (LinearLayout) findViewById(R.id.transactionHistory);
        column_layout.setOrientation(LinearLayout.VERTICAL);
        column_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        column_params.setMargins(dpTopx(30), 0, dpTopx(30), 0);
        row_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        column_layout.setLayoutParams(row_params);
        column_layout.removeAllViews();
    }

    public void addColumnToLayout(String text1, String text2, int index) {
        TextView tv1 = createTextView(text1, column_params, 1);
        TextView tv2 = createTextView(text2, column_params, 3);
        LinearLayout row_layout = new LinearLayout(this);
        row_layout.setOrientation(LinearLayout.HORIZONTAL);
        row_layout.addView(tv1);
        row_layout.addView(tv2);
        row_layout.setId(index);
        row_layout.setFocusable(true);
        row_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = view.getId();
                Intent transactionIntent = new Intent(IndividualAccountBookDetailsActivity.this, IndividualTransactionDetailsActivity.class);
                transactionIntent.putExtra("transactionIndex", index);
                startActivity(transactionIntent);
            }
        });
        column_layout.addView(row_layout);
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
            IndividualTransaction transaction = (IndividualTransaction) model.currentTransactionList.get(i);
            addColumnToLayout(transaction.getCategory(), Float.toString(transaction.getAmount()), i);
            addColumnToLayout(transaction.getDate(), transaction.getCurrency(), i);
            lineSeparator = getLayoutInflater().inflate(R.layout.line_separator, column_layout, false);
            column_layout.addView(lineSeparator);
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

    public void onEdit(View view) {
        Intent intent = new Intent(this, IndividualAccountBookUpsertActivity.class);
        intent.putExtra("accountBookId", model.getClickedAccountBookId());
        startActivity(intent);
        Log.d("WRITE", "Edit Btn clicked!!!");
    }

    public void onDelete(View view) {
        Log.d("WRITE", "Delete Btn clicked!!!");
        model.removeFromIndividualAccountBookList(model.getClickedAccountBookId());
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
        displayTransactions();
        if (model.getViewAllBillClicked()) {
            viewAllBills.setText("Hide");
        } else {
            viewAllBills.setText("View All Bills");
        }
        IndividualAccountBook individualAccountBook = model.getIndividualAccountBook(model.getClickedAccountBookId());
        if (individualAccountBook != null) {
            title.setText(individualAccountBook.getName());
            income.setText(String.valueOf(individualAccountBook.getIncome()));
            expense.setText(String.valueOf(individualAccountBook.getExpense()));
        }
    }
}