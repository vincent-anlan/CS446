package ca.uwaterloo.cs446.ezbill;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public abstract class AccountBookUpsertActivityTemplate extends AppCompatActivity {

    Model model;
    private Toolbar toolbar;
    private EditText mNameEdit;
    private Spinner mSelectCurrency;
    private String currencySaveString;


    public void setToolbar() {
        toolbar = findViewById(R.id.group_toolbar_add_new_expense);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public String getName() {
        return mNameEdit.getText().toString();
    }

    public void setNameEdit() {
        mNameEdit = findViewById(R.id.editName);
    }

    public String getSelectedCurrency() {
        return currencySaveString;
    }

    public void setSelectCurrency() {
        mSelectCurrency = findViewById(R.id.currency_spinner);
        currencySaveString = "CAD";
        ArrayList<String> currencys = new ArrayList<>();
        currencys.add("CAD");
        currencys.add("USD");
        currencys.add("RMB");
        currencys.add("JPY");
        currencys.add("EURO");

        ArrayAdapter<String> adapter_curr = new ArrayAdapter<>(this, R.layout.spinner_item, currencys);
        adapter_curr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSelectCurrency.setAdapter(adapter_curr);
        mSelectCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = parent.getItemAtPosition(position).toString();
                Toast.makeText(AccountBookUpsertActivityTemplate.this, "Selected:" + selectedValue, Toast.LENGTH_SHORT).show();
                currencySaveString = selectedValue;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upsert_account_book);

        model = Model.getInstance();

        //set up toolbar
        setToolbar();

        //set name
        setNameEdit();

        //select currency
        setSelectCurrency();
    }

    public void cancelButtonHandler(View v) {
        finish();
    }

    public abstract void saveButtonHandler(View v);
}
