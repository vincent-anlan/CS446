package ca.uwaterloo.cs446.ezbill;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class GroupAccountBookUpsertActivity extends AccountBookUpsertActivityTemplate {

    @Override
    public void setInitValues() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String accountBookId = extras.getString("accountBookId");
            accountBook = model.getGroupAccountBook(accountBookId);
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
            model.updateAccountBookInGroupList((GroupAccountBook) accountBook);
        } else {
            String uuid = UUID.randomUUID().toString();
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            String date = formatter.format(new Date());
            String creator = model.currentUserId;
            GroupAccountBook newAccountBook = new GroupAccountBook(uuid, name, date, date, currency, creator);
            Participant participant = new Participant(model.getCurrentUserId(), model.getCurrentUsername(), model.getProfilePhotoURL(), model.getUserEmail());
            newAccountBook.addParticipant(participant);
            model.addToCurrentGroupAccountBookList(newAccountBook, model.userEmail, model.getCurrentUserId(), model.getCurrentUsername(), model.getProfilePhotoURL());
        }
        finish();
    }
}
