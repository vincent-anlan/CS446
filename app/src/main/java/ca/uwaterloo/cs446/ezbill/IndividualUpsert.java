package ca.uwaterloo.cs446.ezbill;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import java.util.UUID;

public class IndividualUpsert extends Template {

    private EditText mAmountedit;

    @Override
    public void initView(){
        mAmountedit = (EditText) findViewById(R.id.editIndividual);


    }

    @Override
    public void cancelButtonHandler(View v) {
        startActivity(new Intent(this, IndividualAccountBookDetailsActivity.class));
    }

    @Override
    public void saveButtonHandler(View v) {
        String uuid = UUID.randomUUID().toString();

        String note = getNote();
        String date = getDate();
        String currency = getSelectedCurrency();

        IndividualTransaction newIndividualTransaction = new IndividualTransaction(uuid, "Food", "Expense",
                                                                                    Float.valueOf(mAmountedit.getText().toString()), currency, note, date);


        Intent intent = new Intent(this, IndividualAccountBookDetailsActivity.class);
        intent.putExtra("transactionId", newIndividualTransaction.getUuid());
        startActivity(intent);
    }
}
