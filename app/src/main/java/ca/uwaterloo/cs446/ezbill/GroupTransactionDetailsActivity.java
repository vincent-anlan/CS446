
package ca.uwaterloo.cs446.ezbill;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GroupTransactionDetailsActivity extends AppCompatActivity {

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_transaction_details);

        model = Model.getInstance();
        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.transaction_details_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        category = (TextView) findViewById(R.id.category);
        note = (TextView) findViewById(R.id.note);
        date = (TextView) findViewById(R.id.date);
        amount = (TextView) findViewById(R.id.currency_and_amount);
        creator = (TextView) findViewById(R.id.creator);
        payer = (TextView) findViewById(R.id.payer);

        int transactionIndex = getIntent().getExtras().getInt("transactionIndex");
        currTransaction = (GroupTransaction) model.getCurrentTransactionList().get(transactionIndex);

        title.setText("Transaction Details");
        showDetails();
        displayParticipants();
    }

    private void showDetails() {
        category.setText(currTransaction.getCategory());
        note.setText(currTransaction.getNote());
        date.setText(currTransaction.getDate());
        amount.setText(currTransaction.getCurrency() + " " + currTransaction.getAmount());
        creator.setText(currTransaction.getCreator().getName());
        payer.setText(currTransaction.getPayer().getName());
    }

    public void setupTransactionLayout() {
        linearLayout_v = (LinearLayout) findViewById(R.id.participants_list);
        linearLayout_v.setOrientation(LinearLayout.VERTICAL);
        params_v = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout_v.setLayoutParams(params_v);
        params_tv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1);
        params_h = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params_h.setMargins( dpTopx(50), 0, dpTopx(50), 0);
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
//        textview_value.setGravity(Gravity.START);


        LinearLayout linearLayout_h = new LinearLayout(this);
        linearLayout_h.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout_h.setLayoutParams(params_h);
        linearLayout_h.addView(textview_name);
        linearLayout_h.addView(textview_value);
        linearLayout_v.addView(linearLayout_h);
    }

    public void displayParticipants() {
        setupTransactionLayout();
//        for (HashMap<Participant, Float> participant : currTransaction.getParticipants()) {
//
//        }

        addParticipantToLayout("Name 1", "Amount 1");
        addParticipantToLayout("Name 2", "Amount 2");


    }

    public int dpTopx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);

    }
}