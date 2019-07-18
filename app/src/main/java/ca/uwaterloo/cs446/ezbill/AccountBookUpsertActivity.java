package ca.uwaterloo.cs446.ezbill;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class AccountBookUpsertActivity extends AppCompatActivity {

    Model model;
    private Toolbar toolbar;
    private EditText mNameEdit;
    private Spinner mSelectCurrency;
    private String currencySaveString;
    private ArrayList<String> currencystring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upsert_account_book);

        model = Model.getInstance();

        //set up toolbar
        toolbar = (Toolbar) findViewById(R.id.group_toolbar_add_new_expense);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //set note
        mNameEdit = findViewById(R.id.editNote);

        //select currency
        mSelectCurrency = (Spinner) findViewById(R.id.currency_spinner);
        currencySaveString = "CAD";
        currencystring = new ArrayList<>();
        currencystring.add("CAD");
        currencystring.add("USD");
        currencystring.add("RMB");
        currencystring.add("JPY");
        currencystring.add("EURO");

        ArrayAdapter<String> adapter_curr = new ArrayAdapter<>(this, R.layout.spinner_item, currencystring);
        adapter_curr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSelectCurrency.setAdapter(adapter_curr);
        mSelectCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selecvalue = parent.getItemAtPosition(position).toString();
                Toast.makeText(AccountBookUpsertActivity.this, "Selected:" + selecvalue, Toast.LENGTH_SHORT).show();
                currencySaveString = selecvalue;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void cancelButtonHandler(View v) {
        startActivity(new Intent(AccountBookUpsertActivity.this, MainActivity.class));
    }

    public void saveButtonHandler(View v) {
        String uuid = UUID.randomUUID().toString();
        String name = mNameEdit.getText().toString();
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String date = formatter.format(new Date());
        if (model.isMainPageGroupViewOnSelect()) {
            GroupAccountBook accountBook = new GroupAccountBook(uuid, name, date, date, currencySaveString);
            model.addToCurrentGroupAccountBookList(accountBook, model.userEmail, model.getCurrentUserId(), model.getCurrentUsername());
        } else {
            IndividualAccountBook accountBook = new IndividualAccountBook(uuid, name, date, date, currencySaveString);
            model.addToCurrentIndividualAccountBookList(accountBook, model.userEmail, model.getCurrentUserId(), model.getCurrentUsername());
        }
        Intent intent = new Intent(AccountBookUpsertActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
