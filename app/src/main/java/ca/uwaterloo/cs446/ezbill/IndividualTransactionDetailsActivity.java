package ca.uwaterloo.cs446.ezbill;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class IndividualTransactionDetailsActivity extends AppCompatActivity implements Observer {

    Model model;
    Transaction currTransaction;

    TextView type;
    TextView category;
    TextView note;
    TextView date;
    TextView amount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_transaction_details);

        model = Model.getInstance();
        model.addObserver(this);
        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.transaction_details_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        type = (TextView) findViewById(R.id.type);
        category = (TextView) findViewById(R.id.category);
        note = (TextView) findViewById(R.id.note);
        date = (TextView) findViewById(R.id.date);
        amount = (TextView) findViewById(R.id.currency_and_amount);
        Intent intent = getIntent();
        int transactionIndex = intent.getIntExtra("transactionIndex", 0);
        currTransaction = model.getCurrentTransactionList().get(transactionIndex);

        title.setText("Transaction Details");
        showDetails();
        model.initObservers();
    }

    private void showDetails() {
        type.setText(currTransaction.getType());
        category.setText(currTransaction.getCategory());
        note.setText(currTransaction.getNote());
        date.setText(currTransaction.getDate());
        amount.setText(currTransaction.getCurrency() + " " + currTransaction.getAmount());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove observer when activity is destroyed.
        model.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}