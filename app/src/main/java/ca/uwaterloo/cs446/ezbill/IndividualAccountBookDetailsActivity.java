package ca.uwaterloo.cs446.ezbill;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
    View viewAllBillsLineSeperator;
    TextView title;
    TextView viewAllBills;
    TextView income;
    TextView expense;
    int numToDisplay;

    LinearLayout menu;
    LinearLayout delete;
    LinearLayout edit;
    LinearLayout pie_chart;
    LinearLayout add;
    boolean isMenuOpen;
    boolean isViewAllBillClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_account_book_details);

        model = Model.getInstance();
        model.addObserver(this);
        viewAllBills = (TextView) findViewById(R.id.viewAllBills);
        income = (TextView) findViewById(R.id.income);
        expense = (TextView) findViewById(R.id.expense);
        viewAllBillsLineSeperator = (View) findViewById(R.id.viewAllBillsLineSeperator);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.individual_toolbar);
        title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        model.readTransactionsFromDB(false);
        isMenuOpen = false;
        isViewAllBillClicked = false;
        displayTransactions();
        updateText();

        initFloatingActionMenu();
        model.initObservers();
    }

    private void initFloatingActionMenu() {
        menu = (LinearLayout) findViewById(R.id.menu);
        delete = (LinearLayout) findViewById(R.id.delete);
        edit = (LinearLayout) findViewById(R.id.edit);
        pie_chart = (LinearLayout) findViewById(R.id.pie_chart);
        add = (LinearLayout) findViewById(R.id.add);

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

        delete.setOnClickListener(onButtonClick());
        edit.setOnClickListener(onButtonClick());
        pie_chart.setOnClickListener(onButtonClick());
        add.setOnClickListener(onButtonClick());
    }

    private void closeMenu() {
        if (isMenuOpen) {
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
            pie_chart.animate().translationY(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    pie_chart.setVisibility(View.GONE);
                }
            }).start();
            add.animate().translationY(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    add.setVisibility(View.GONE);
                }
            }).start();
            isMenuOpen = false;
        }
    }

    private void openMenu() {
        delete.animate().translationY(-getResources().getDimension(R.dimen.delete));
        edit.animate().translationY(-getResources().getDimension(R.dimen.edit));
        pie_chart.animate().translationY(-getResources().getDimension(R.dimen.pie_chart));
        add.animate().translationY(-getResources().getDimension(R.dimen.add));
        isMenuOpen = true;
        delete.setVisibility(View.VISIBLE);
        edit.setVisibility(View.VISIBLE);
        pie_chart.setVisibility(View.VISIBLE);
        add.setVisibility(View.VISIBLE);
    }

    private View.OnClickListener onButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreDefaultSetting();
                Intent intent;
                if (view.getId() == R.id.delete) {
                    intent = new Intent(IndividualAccountBookDetailsActivity.this, IndividualTransactionUpsertActivity.class);
                } else if (view.getId() == R.id.edit) {
                    intent = new Intent(IndividualAccountBookDetailsActivity.this, IndividualTransactionUpsertActivity.class);
                } else if (view.getId() == R.id.pie_chart) {
                    intent = new Intent(IndividualAccountBookDetailsActivity.this, SummaryActivity.class);
                } else {
                    intent = new Intent(IndividualAccountBookDetailsActivity.this, IndividualTransactionUpsertActivity.class);
                }
                startActivity(intent);
            }
        };
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
        textView.setLayoutParams(column_params);
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
        TextView tv1 = createTextView(text1);
        tv1.setGravity(Gravity.START);

        TextView tv2 = createTextView(text2);
        tv2.setGravity(Gravity.END);

        LinearLayout row_layout = new LinearLayout(this);
        row_layout.setOrientation(LinearLayout.HORIZONTAL);
        row_layout.addView(tv1);
        row_layout.addView(tv2);
        row_layout.setId(index);
        row_layout.setFocusable(true);
        row_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreDefaultSetting();
                int index = view.getId();
                Intent transactionIntent = new Intent(IndividualAccountBookDetailsActivity.this, IndividualTransactionDetailsActivity.class);
                transactionIntent.putExtra("transactionID", model.getCurrentTransactionList().get(index).getUuid());
                startActivity(transactionIntent);
            }
        });
        column_layout.addView(row_layout);
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
            IndividualTransaction transaction = (IndividualTransaction) model.currentTransactionList.get(i);
            addColumnToLayout(transaction.getCategory(), Float.toString(transaction.getAmount()), i);
            addColumnToLayout(transaction.getDate(), transaction.getCurrency(), i);
            lineSeparator = getLayoutInflater().inflate(R.layout.line_separator, column_layout, false);
            column_layout.addView(lineSeparator);
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

    private void updateText() {
        IndividualAccountBook individualAccountBook = model.getIndividualAccountBook(model.getClickedAccountBookId());
        title.setText(individualAccountBook.getName());
        income.setText(String.valueOf(individualAccountBook.getIncome()));
        expense.setText(String.valueOf(individualAccountBook.getExpense()));
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
        IndividualAccountBook individualAccountBook = model.getIndividualAccountBook(model.getClickedAccountBookId());
        if (individualAccountBook != null) {
            displayTransactions();
            updateText();
        }
    }
}