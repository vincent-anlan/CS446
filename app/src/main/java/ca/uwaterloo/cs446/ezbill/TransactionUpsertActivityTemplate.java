package ca.uwaterloo.cs446.ezbill;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class TransactionUpsertActivityTemplate extends AppCompatActivity {

    Model model;

    RadioGroup firstRowCategory;
    RadioGroup secondRowCategory;
    private String mSelectedCategory;
    TextView mDisplaySelectedCategory;
    EditText mNoteedit;
    TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    Spinner mSelectCurrency;
    private String currencySaveString;

    Transaction transaction;


    private static final String TAG = "Transaction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_trans);

        model = Model.getInstance();

        startActivityInitProcess();
    }

    public void startActivityInitProcess(){
        initialTransactionPage();
    }

    public void addToolbar() {
        //set up toolbar
        Toolbar mytoolbar_new_Expense = findViewById(R.id.group_toolbar_add_new_expense);
        setSupportActionBar(mytoolbar_new_Expense);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public String getCategory() {
        return mSelectedCategory;
    }

    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                secondRowCategory.setOnCheckedChangeListener(null); // remove the listener before clearing so we don't throw that stackoverflow exception(like Vladimir Volodin pointed out)
                secondRowCategory.clearCheck(); // clear the second RadioGroup!
                secondRowCategory.setOnCheckedChangeListener(listener2); //reset the listener
                Log.e("XXX2", Integer.toString(firstRowCategory.getCheckedRadioButtonId()));
                int realCheck = firstRowCategory.getCheckedRadioButtonId();
                if(realCheck == R.id.radioButtonFood){
                    mSelectedCategory = "Food";
                    mDisplaySelectedCategory.setText(mSelectedCategory);
                }else if(realCheck == R.id.radioButtonTransport){
                    mSelectedCategory = "Transportation";
                    mDisplaySelectedCategory.setText(mSelectedCategory);
                }else if(realCheck == R.id.radioButtonEntertainment){
                    mSelectedCategory = "Entertainment";
                    mDisplaySelectedCategory.setText(mSelectedCategory);
                }else if(realCheck == R.id.radioButtonClothing){
                    mSelectedCategory = "Clothing";
                    mDisplaySelectedCategory.setText(mSelectedCategory);
                }
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                firstRowCategory.setOnCheckedChangeListener(null);
                firstRowCategory.clearCheck();
                firstRowCategory.setOnCheckedChangeListener(listener1);
                Log.e("XXX1", Integer.toString(secondRowCategory.getCheckedRadioButtonId()));
                int realCheck = secondRowCategory.getCheckedRadioButtonId();
                if(realCheck == R.id.radioButtonCoffee){
                    mSelectedCategory = "Coffee";
                    mDisplaySelectedCategory.setText(mSelectedCategory);
                }else if(realCheck == R.id.radioButtonGrocery){
                    mSelectedCategory = "Grocery";
                    mDisplaySelectedCategory.setText(mSelectedCategory);
                }else if(realCheck == R.id.radioButtonTickets){
                    mSelectedCategory = "Tickets";
                    mDisplaySelectedCategory.setText(mSelectedCategory);
                }else if(realCheck == R.id.radioButtonOther){
                    mSelectedCategory = "Other";
                    mDisplaySelectedCategory.setText(mSelectedCategory);
                }
            }
        }
    };

    public void setCategory() {
        firstRowCategory = findViewById(R.id.radioGroupFirstRow);
        secondRowCategory = findViewById(R.id.radioGroupSecondRow);
        mDisplaySelectedCategory = findViewById(R.id.select_category);
        firstRowCategory.clearCheck();
        secondRowCategory.clearCheck();
        firstRowCategory.setOnCheckedChangeListener(listener1);
        secondRowCategory.setOnCheckedChangeListener(listener2);
    }
    

    public String getNote() { return mNoteedit.getText().toString(); }

    public void setNoteedit() { mNoteedit = findViewById(R.id.editNote); }

    public String getDate() { return mDisplayDate.getText().toString(); }

    public void setDateSelector() {
        mDisplayDate = findViewById(R.id.selectDate);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        TransactionUpsertActivityTemplate.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyyy: " + month + "/" + day + "/" + year);
                String date = month + "/" + day + "/" + year;
                if(month < 10){
                    date = "0" + month + "/" + day + "/" + year;
                    if(day < 10){
                        date = "0" + month + "/" + "0" + day + "/" + year;
                    }else{ }
                }else{ }
                mDisplayDate.setText(date);
            }
        };
    }

    public String getSelectedCurrency() { return currencySaveString; }

    public void setSelectCurrency() {
        //select currency
        mSelectCurrency = (Spinner) findViewById(R.id.currency_spinner);
        currencySaveString = "CAD";
        ArrayList<String> currencystring = new ArrayList<>();
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
                String selectedvalue = parent.getItemAtPosition(position).toString();
                Toast.makeText(TransactionUpsertActivityTemplate.this, "Selected:" + selectedvalue, Toast.LENGTH_SHORT).show();
                currencySaveString = selectedvalue;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    protected void initialTransactionPage(){
        //set up toolbar
        addToolbar();

        //set category
        setCategory();

        //set note
        setNoteedit();

        //select date
        setDateSelector();

        //select currency
        setSelectCurrency();

        //initial view
        initView();

        setInitValues();
    }

    public abstract void initView();

    public void cancelButtonHandler(View v) {
        finish();
    }

    public abstract void saveButtonHandler(View v);
    public abstract void cameraScan(View v);

    public abstract void setInitValues();
}
