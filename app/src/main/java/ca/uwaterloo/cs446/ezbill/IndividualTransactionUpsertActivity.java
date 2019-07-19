package ca.uwaterloo.cs446.ezbill;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.UUID;

public class IndividualTransactionUpsertActivity extends TransactionUpsertActivityTemplate {

    private EditText mAmountedit;

    @Override
    public void initView(){
        LinearLayout transLayout = (LinearLayout) findViewById(R.id.addtransTemplate);
        View newView = getLayoutInflater().inflate(R.layout.add_trans_detail_individual, transLayout, false);
        transLayout.addView(newView);

        mAmountedit = (EditText) findViewById(R.id.editIndividual);


    }

    @Override
    public void cameraScan(View view) {
        startActivity(new Intent(this, TextRecognitionActivity.class));
    }

    @Override
    public void saveButtonHandler(View v) {
        String uuid = UUID.randomUUID().toString();
        String note = getNote();
        String date = getDate();
        String currency = getSelectedCurrency();
        String category = getCategory();

        IndividualTransaction newIndividualTransaction = new IndividualTransaction(uuid, category, "Expense",
                                                                                    Float.valueOf(mAmountedit.getText().toString()), currency, note, date);
        model.addToCurrentTransactionList(newIndividualTransaction, false);
        finish();
    }
}
