package ca.uwaterloo.cs446.ezbill;

import android.view.View;

import java.util.Observable;

import android.widget.LinearLayout;


public class IndividualTransactionUpsertActivity extends TransactionUpsertActivityTemplate {

    @Override
    public void handle_new_group_transaction(){
        LinearLayout layout_payer = (LinearLayout) findViewById(R.id.payerlayout);
        layout_payer.setVisibility(View.GONE);
        LinearLayout layout_parti = (LinearLayout) findViewById(R.id.partilayout);
        layout_parti.setVisibility(View.GONE);
        LinearLayout layout_autosum = (LinearLayout) findViewById(R.id.autosumlayout);
        layout_autosum.setVisibility(View.GONE);
        LinearLayout layout_list = (LinearLayout) findViewById(R.id.listparticipant);
        layout_list.setVisibility(View.GONE);
    }

    @Override
    public void handle_new_individual_transaction(){
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
