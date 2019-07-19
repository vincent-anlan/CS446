package ca.uwaterloo.cs446.ezbill;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class IndividualAccountBookUpsertActivity extends AccountBookUpsertActivityTemplate {

    @Override
    public void setInitValues() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String accountBookId = extras.getString("accountBookId");
            accountBook = model.getIndividualAccountBook(accountBookId);
            mNameEdit.setText(accountBook.getName());
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) mSelectCurrency.getAdapter();
            int position = adapter.getPosition(accountBook.getDefaultCurrency());
            mSelectCurrency.setSelection(position);
        }
    }

    @Override
    public void saveButtonHandler(View v) {
        String name = getName();
        String currency = getSelectedCurrency();
        if (accountBook != null) {
            accountBook.setName(name);
            accountBook.setDefaultCurrency(currency);
            model.updateAccountBookInIndividualList((IndividualAccountBook) accountBook);
        } else {
            String uuid = UUID.randomUUID().toString();
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            String date = formatter.format(new Date());
            IndividualAccountBook newAccountBook = new IndividualAccountBook(uuid, name, date, date, currency);
            model.addToCurrentIndividualAccountBookList(newAccountBook, model.userEmail, model.getCurrentUserId(), model.getCurrentUsername());
        }
        finish();
    }
}
