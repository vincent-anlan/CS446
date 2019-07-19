
package ca.uwaterloo.cs446.ezbill;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

public class BillSplitActivity extends AppCompatActivity {

    Model model;
    HashMap<String, HashMap<String, Float>> settleResult = new HashMap<>();
    View lineSeparator;
    LinearLayout linearLayout_v;
    LinearLayout linearLayout_h;
    LinearLayout.LayoutParams params_v;
    LinearLayout.LayoutParams params_h;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = Model.getInstance();

        calculate();
        if (settleResult.size() == 0) {
            setContentView(R.layout.bill_split_empty_page);
        } else {
            setContentView(R.layout.bill_split_page);
            initPage();
        }

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.bill_split_page_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Settle Account");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private String findAbsMaxBalance(HashMap<String, Float> balanceMap) {
        String curMaxName = null;
        float currMaxValue = (float) 0;
        for (HashMap.Entry<String, Float> entry : balanceMap.entrySet()) {
            Float value = Math.abs(entry.getValue());
            if (value > currMaxValue) {
                currMaxValue = value;
                curMaxName = entry.getKey();
            }
        }
        return curMaxName;
    }

    private void calculate() {
        // compute the balance of each participants and store into a map
        HashMap<String, Float> balanceMap = new HashMap<>();
        for (Transaction transaction : model.getCurrentTransactionList()) {
            // add total expense value to the payer
            GroupTransaction groupTransaction = (GroupTransaction) transaction;
            String payerName = groupTransaction.getPayer().getName();
            Float totalExpense = groupTransaction.getAmount();
            if (balanceMap.containsKey(payerName)) {
                balanceMap.put(payerName, balanceMap.get(payerName) + totalExpense);
            } else {
                balanceMap.put(payerName, totalExpense);
            }
            // subtract the individual expense from each participant
            HashMap<Participant, Float> participantsMap = groupTransaction.getParticipants();
            for (HashMap.Entry<Participant, Float> participant : participantsMap.entrySet()) {
                String participantName = participant.getKey().getName();
                Float participantExpense = participant.getValue();
                if (balanceMap.containsKey(participantName)) {
                    balanceMap.put(participantName, balanceMap.get(participantName) - participantExpense);
                } else {
                    balanceMap.put(participantName, participantExpense * -1);
                }
            }
        }

        // put participants with positive balance into posBalance map
        // put participants with negative balance into negBalance map
        HashMap<String, Float> posBalance = new HashMap<>();
        HashMap<String, Float> negBalance = new HashMap<>();
        for (HashMap.Entry<String, Float> participant : balanceMap.entrySet()) {
            String participantName = participant.getKey();
            Float participantBalance = participant.getValue();
            if (participantBalance > 0) {
                posBalance.put(participantName, participantBalance);
            } else {
                negBalance.put(participantName, participantBalance);
            }
        }

        // find two participants with largest positive/negative balance
        while (posBalance.size() > 0) {
            String payerName = findAbsMaxBalance(negBalance);
            String receiverName = findAbsMaxBalance(posBalance);
            Float lowestBalance = negBalance.get(payerName); // a negative value
            Float highestBalance = posBalance.get(receiverName); // a positive value
            Float difference = highestBalance + lowestBalance;

            if (! settleResult.containsKey(payerName)) {
                HashMap<String, Float> tmp_map = new HashMap<>();
                tmp_map.put(receiverName, (float) 0);
                settleResult.put(payerName, tmp_map);
            }

            if (difference > 0) {
                posBalance.put(receiverName, difference);
                negBalance.remove(payerName);
                settleResult.get(payerName).put(receiverName, lowestBalance * -1);
            } else if (difference < 0) {
                negBalance.put(payerName, difference);
                posBalance.remove(receiverName);
                settleResult.get(payerName).put(receiverName, highestBalance);
            } else {
                negBalance.remove(payerName);
                posBalance.remove(receiverName);
                settleResult.get(payerName).put(receiverName, highestBalance);
            }
        }
    }


    private void addTextView(String name, String type) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.gravity = Gravity.CENTER_VERTICAL;
        TextView textview = new TextView(this);
        textview.setText(name);

        switch (type) {
            case "text":
                textview.setTextSize(13);
                textview.setGravity(Gravity.END);
                params.setMargins(0, 0, dpTopx(5), 0);
                break;
            case "amount":
                textview.setTextSize(16);
                textview.setGravity(Gravity.END);
                break;
            default:
                textview.setTextSize(16);
                textview.setGravity(Gravity.START);
                params.setMargins(0, 0, dpTopx(5), 0);
        }
        textview.setLayoutParams(params);
        linearLayout_h.addView(textview);
    }

    private void initPage() {
        linearLayout_v = (LinearLayout) findViewById(R.id.participants_list);
        linearLayout_v.setOrientation(LinearLayout.VERTICAL);
        params_v = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout_v.setLayoutParams(params_v);
        params_h = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params_h.setMargins(dpTopx(30), 0, dpTopx(30), 0);

        for (HashMap.Entry<String, HashMap<String, Float>> payer : settleResult.entrySet()) {
            for (HashMap.Entry<String, Float> receiver : payer.getValue().entrySet()) {
                linearLayout_h = new LinearLayout(this);
                linearLayout_h.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout_h.setLayoutParams(params_h);
                addTextView(payer.getKey(), "payer");
                addTextView("should pay ", "text");
                addTextView(receiver.getKey(), "receiver");
                addTextView(Float.toString(receiver.getValue()), "amount");
                linearLayout_v.addView(linearLayout_h);
                lineSeparator = getLayoutInflater().inflate(R.layout.line_separator, linearLayout_v, false);
                linearLayout_v.addView(lineSeparator);
            }
        }
    }

    private int dpTopx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);

    }
}