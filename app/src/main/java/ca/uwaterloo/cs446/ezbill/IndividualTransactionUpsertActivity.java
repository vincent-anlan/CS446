package ca.uwaterloo.cs446.ezbill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

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
        String note = getNote();
        String date = getDate();
        String currency = getSelectedCurrency();
        String category = getCategory();
        Float amount = Float.valueOf(mAmountedit.getText().toString());

        if (transaction != null) {
            transaction.setCategory(category);
            transaction.setCurrency(currency);
            transaction.setDate(date);
            transaction.setNote(note);
            transaction.setAmount(amount);
            model.updateTransactionInCurrentList(transaction, false);
        } else {
            String uuid = UUID.randomUUID().toString();
            IndividualTransaction newIndividualTransaction = new IndividualTransaction(uuid, category, "Expense",
                    amount, currency, note, date);
            model.addToCurrentTransactionList(newIndividualTransaction, false);
        }
        finish();
    }

    @Override
    public void setInitValues() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String transactionId = extras.getString("transactionId");
            transaction = model.getTransaction(transactionId);

            String category = transaction.getCategory();
            int id;
            if (category.equals("Food")) {
                id = R.id.radioButtonFood;
            } else if (category.equals("Transportation")) {
                id = R.id.radioButtonTransport;
            } else if (category.equals("Entertainment")) {
                id = R.id.radioButtonEntertainment;
            } else if (category.equals("Clothing")) {
                id = R.id.radioButtonClothing;
            } else if (category.equals("Coffee")) {
                id = R.id.radioButtonCoffee;
            } else if (category.equals("Grocery")) {
                id = R.id.radioButtonGrocery;
            } else if (category.equals("Tickets")) {
                id = R.id.radioButtonTickets;
            } else {
                id = R.id.radioButtonOther;
            }
            RadioButton radioButton = (RadioButton) findViewById(id);
            radioButton.setChecked(true);
            mDisplaySelectedCategory.setText(category);

            mNoteedit.setText(transaction.getNote());
            mDisplayDate.setText(transaction.getDate());

            ArrayAdapter<String> adapter = (ArrayAdapter<String>) mSelectCurrency.getAdapter();
            int position = adapter.getPosition(transaction.getCurrency());
            mSelectCurrency.setSelection(position);

            mAmountedit.setText(Float.toString(transaction.getAmount()));
        }
    }
}
