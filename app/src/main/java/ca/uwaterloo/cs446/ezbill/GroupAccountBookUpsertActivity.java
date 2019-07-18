package ca.uwaterloo.cs446.ezbill;

import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class GroupAccountBookUpsertActivity extends AccountBookUpsertActivityTemplate {

    @Override
    public void saveButtonHandler(View v) {
        String uuid = UUID.randomUUID().toString();
        String name = getName();
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String date = formatter.format(new Date());
        GroupAccountBook accountBook = new GroupAccountBook(uuid, name, date, date, getSelectedCurrency());
        model.addToCurrentGroupAccountBookList(accountBook, model.userEmail, model.getCurrentUserId(), model.getCurrentUsername());
        finish();
    }
}
