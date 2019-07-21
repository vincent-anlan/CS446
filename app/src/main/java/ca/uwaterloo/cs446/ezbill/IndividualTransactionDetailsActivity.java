package ca.uwaterloo.cs446.ezbill;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    LinearLayout menu;
    LinearLayout delete;
    LinearLayout edit;
    boolean isMenuOpen;
    boolean isCreator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = Model.getInstance();
        model.addObserver(this);
        showDetails();
        initFloatingActionMenu();
    }

    private void initFloatingActionMenu() {
        menu = (LinearLayout) findViewById(R.id.menu);
        delete = (LinearLayout) findViewById(R.id.delete);
        edit = (LinearLayout) findViewById(R.id.edit);
        isMenuOpen = false;

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

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(IndividualTransactionDetailsActivity.this);
                builder.setMessage("You are about to permanently delete all records of this transaction. Do you really want to proceed?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "The transaction is deleted.", Toast.LENGTH_SHORT).show();
                        model.removeFromCurrentTransactionList(currTransaction);
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
                closeMenu();
                Intent intent = new Intent(IndividualTransactionDetailsActivity.this, IndividualTransactionUpsertActivity.class);
                intent.putExtra("transactionId", currTransaction.getUuid());
                startActivity(intent);
            }
        });
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
            isMenuOpen = false;
        }
    }

    private void openMenu() {
        delete.animate().translationY(-getResources().getDimension(R.dimen.delete));
        edit.animate().translationY(-getResources().getDimension(R.dimen.edit));
        isMenuOpen = true;
        delete.setVisibility(View.VISIBLE);
        edit.setVisibility(View.VISIBLE);
    }

    private void showDetails() {
        setContentView(R.layout.individual_transaction_details);
        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.transaction_details_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Transaction Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // get elements
        type = (TextView) findViewById(R.id.type);
        category = (TextView) findViewById(R.id.category);
        note = (TextView) findViewById(R.id.note);
        date = (TextView) findViewById(R.id.date);
        amount = (TextView) findViewById(R.id.currency_and_amount);

        // determine which transaction is clicked
        String transactionID = getIntent().getStringExtra("transactionID");
        currTransaction = (IndividualTransaction) model.getTransaction(transactionID);

        //set text
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
        // determine which transaction is clicked
        String transactionID = getIntent().getStringExtra("transactionID");
        currTransaction = (IndividualTransaction) model.getTransaction(transactionID);
        if (currTransaction != null) {
            //set text
            type.setText(currTransaction.getType());
            category.setText(currTransaction.getCategory());
            note.setText(currTransaction.getNote());
            date.setText(currTransaction.getDate());
            amount.setText(currTransaction.getCurrency() + " " + currTransaction.getAmount());
        }
    }

}