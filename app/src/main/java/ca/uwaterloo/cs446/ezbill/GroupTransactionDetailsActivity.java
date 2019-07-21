
package ca.uwaterloo.cs446.ezbill;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class GroupTransactionDetailsActivity extends AppCompatActivity implements Observer {

    Model model;
    GroupTransaction currTransaction;

    TextView category;
    TextView note;
    TextView date;
    TextView amount;
    TextView creator;
    TextView payer;

    LinearLayout linearLayout_v;
    LinearLayout.LayoutParams params_v;
    LinearLayout.LayoutParams params_h;
    LinearLayout.LayoutParams params_tv;

    LinearLayout menu;
    LinearLayout delete;
    LinearLayout edit;
    FloatingActionButton addActionButton;
    boolean isMenuOpen;
    boolean isCreator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = Model.getInstance();
        model.addObserver(this);
        showDetails();
    }

    private void initFloatingActionMenu() {
        isMenuOpen = false;
        isCreator = currTransaction.getCreator().getId().equals(model.currentUserId);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.group_transaction_details_page);
        if (isCreator) {
            View newView = getLayoutInflater().inflate(R.layout.floating_menu, layout, false);
            layout.addView(newView);

            menu = (LinearLayout) findViewById(R.id.menu);
            delete = (LinearLayout) findViewById(R.id.delete);
            edit = (LinearLayout) findViewById(R.id.edit);

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupTransactionDetailsActivity.this);
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
                    Intent intent = new Intent(GroupTransactionDetailsActivity.this, GroupTransactionUpsertActivity.class);
                    intent.putExtra("transactionId", currTransaction.getUuid());
                    startActivity(intent);
                }
            });
        }
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
        setContentView(R.layout.group_transaction_details);
        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.transaction_details_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Transaction Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // get elements
        category = (TextView) findViewById(R.id.category);
        note = (TextView) findViewById(R.id.note);
        date = (TextView) findViewById(R.id.date);
        amount = (TextView) findViewById(R.id.currency_and_amount);
        creator = (TextView) findViewById(R.id.creator);
        payer = (TextView) findViewById(R.id.payer);

        // determine which transaction is clicked
        String transactionID = getIntent().getStringExtra("transactionID");
        currTransaction = (GroupTransaction) model.getTransaction(transactionID);

        //set text
        category.setText(currTransaction.getCategory());
        note.setText(currTransaction.getNote());
        date.setText(currTransaction.getDate());
        amount.setText(currTransaction.getCurrency() + " " + currTransaction.getAmount());
        creator.setText(currTransaction.getCreator().getName());
        payer.setText(currTransaction.getPayer().getName());
        displayParticipants();
        initFloatingActionMenu();
    }


    public void addParticipantToLayout(String name, String amount) {
        TextView textview_name = new TextView(this);
        textview_name.setText(name);
        textview_name.setTextSize(20);
        textview_name.setTypeface(textview_name.getTypeface(), Typeface.ITALIC);
        textview_name.setLayoutParams(params_tv);

        TextView textview_value = new TextView(this);
        textview_value.setText(amount);
        textview_value.setTextSize(20);
        textview_value.setLayoutParams(params_tv);

        LinearLayout linearLayout_h = new LinearLayout(this);
        linearLayout_h.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout_h.setLayoutParams(params_h);
        linearLayout_h.addView(textview_name);
        linearLayout_h.addView(textview_value);
        linearLayout_v.addView(linearLayout_h);
    }

    public void displayParticipants() {
        // setup linearlayout
        linearLayout_v = (LinearLayout) findViewById(R.id.participants_list);
        linearLayout_v.setOrientation(LinearLayout.VERTICAL);
        params_v = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout_v.setLayoutParams(params_v);
        params_tv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1);
        params_tv.setMargins( 0, 0, dpTopx(5), 0);
        params_tv.gravity = Gravity.CENTER_VERTICAL;
        params_h = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params_h.setMargins( dpTopx(50), 0, dpTopx(50), 0);
        // draw participants
        HashMap<Participant, Float> participants =   currTransaction.getParticipants();
        for (HashMap.Entry<Participant,Float> entry : participants.entrySet()) {
            addParticipantToLayout(entry.getKey().getName(), Float.toString(entry.getValue()));
        }


    }

    public int dpTopx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);

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
        currTransaction = (GroupTransaction) model.getTransaction(transactionID);
        if (currTransaction != null) {
            //set text
            category.setText(currTransaction.getCategory());
            note.setText(currTransaction.getNote());
            date.setText(currTransaction.getDate());
            amount.setText(currTransaction.getCurrency() + " " + currTransaction.getAmount());
            creator.setText(currTransaction.getCreator().getName());
            payer.setText(currTransaction.getPayer().getName());

            linearLayout_v.removeAllViews();
            // draw participants
            HashMap<Participant, Float> participants =   currTransaction.getParticipants();
            for (HashMap.Entry<Participant,Float> entry : participants.entrySet()) {
                addParticipantToLayout(entry.getKey().getName(), Float.toString(entry.getValue()));
            }
        }
    }
}