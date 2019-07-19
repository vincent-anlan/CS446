package ca.uwaterloo.cs446.ezbill;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
        model = Model.getInstance();
        showDetails();
    }

    private void showDetails() {
        setContentView(R.layout.individual_transaction_details);
        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.transaction_details_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Transaction Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // get elements
        type = (TextView) findViewById(R.id.type);
        category = (TextView) findViewById(R.id.category);
        note = (TextView) findViewById(R.id.note);
        date = (TextView) findViewById(R.id.date);
        amount = (TextView) findViewById(R.id.currency_and_amount);

        // determine which transaction is clicked
        int transactionIndex = getIntent().getIntExtra("transactionIndex", 0);
        currTransaction = model.getCurrentTransactionList().get(transactionIndex);

        //set text
        type.setText(currTransaction.getType());
        category.setText(currTransaction.getCategory());
        note.setText(currTransaction.getNote());
        date.setText(currTransaction.getDate());
        amount.setText(currTransaction.getCurrency() + " " + currTransaction.getAmount());
    }

    public void onEdit(View view) {
        Intent intent = new Intent(this, IndividualTransactionUpsertActivity.class);
        intent.putExtra("transactionId", currTransaction.getUuid());
        startActivity(intent);
        Log.d("WRITE", "Edit Btn clicked!!!");
    }

    public void onDelete(View view) {
        Log.d("WRITE", "Delete Btn clicked!!!");
        model.removeFromCurrentTransactionList(currTransaction);
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
        // determine which transaction is clicked
        int transactionIndex = getIntent().getIntExtra("transactionIndex", 0);
        currTransaction = model.getCurrentTransactionList().get(transactionIndex);
        //set text
        type.setText(currTransaction.getType());
        category.setText(currTransaction.getCategory());
        note.setText(currTransaction.getNote());
        date.setText(currTransaction.getDate());
        amount.setText(currTransaction.getCurrency() + " " + currTransaction.getAmount());
    }

}