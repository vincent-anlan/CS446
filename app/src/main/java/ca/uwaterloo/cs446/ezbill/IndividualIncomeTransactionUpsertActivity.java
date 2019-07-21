package ca.uwaterloo.cs446.ezbill;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Observable;
import java.util.UUID;

public class IndividualIncomeTransactionUpsertActivity extends AppCompatActivity {

    Model model;

    RadioGroup firstRowCategoryIncome;
    RadioGroup secondRowCategoryIncome;
    private String mSelectedCategory;
    TextView mDisplaySelectedCategory;
    EditText mNoteedit;
    TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    Spinner mSelectCurrency;
    private String currencySaveString;
    EditText mAmountedit;

    Transaction transaction;

    private static final String TAG = "Transaction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_trans_income);

        model = Model.getInstance();

        startActivityInitProcess();
    }

    public void startActivityInitProcess(){
        initialTransactionPage();
    }

    public void addToolbar() {
        //set up toolbar
        Toolbar mytoolbar_new_Expense = findViewById(R.id.individual_toolbar_add_new_income);
        setSupportActionBar(mytoolbar_new_Expense);
        TextView toolbar_title = findViewById(R.id.add_new_trans_toolbar_title);
        toolbar_title.setText("New Income");
    }

    public String getCategory() {
        return mSelectedCategory;
    }

    private RadioGroup.OnCheckedChangeListener listener1Income = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                secondRowCategoryIncome.setOnCheckedChangeListener(null); // remove the listener before clearing so we don't throw that stackoverflow exception(like Vladimir Volodin pointed out)
                secondRowCategoryIncome.clearCheck(); // clear the second RadioGroup!
                secondRowCategoryIncome.setOnCheckedChangeListener(listener2Income); //reset the listener
                Log.e("XXX2", Integer.toString(firstRowCategoryIncome.getCheckedRadioButtonId()));
                int realCheck = firstRowCategoryIncome.getCheckedRadioButtonId();
                if(realCheck == R.id.radioButtonSalary){
                    mSelectedCategory = "Salary";
                    mDisplaySelectedCategory.setText(mSelectedCategory);
                }else if(realCheck == R.id.radioButtonInvestment){
                    mSelectedCategory = "Investment";
                    mDisplaySelectedCategory.setText(mSelectedCategory);
                }else if(realCheck == R.id.radioButtonParttime){
                    mSelectedCategory = "Parttime";
                    mDisplaySelectedCategory.setText(mSelectedCategory);
                }else if(realCheck == R.id.radioButtonRedpocket){
                    mSelectedCategory = "Redpocket";
                    mDisplaySelectedCategory.setText(mSelectedCategory);
                }
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener listener2Income = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                firstRowCategoryIncome.setOnCheckedChangeListener(null);
                firstRowCategoryIncome.clearCheck();
                firstRowCategoryIncome.setOnCheckedChangeListener(listener1Income);
                Log.e("XXX1", Integer.toString(secondRowCategoryIncome.getCheckedRadioButtonId()));
                int realCheck = secondRowCategoryIncome.getCheckedRadioButtonId();
                if(realCheck == R.id.radioButtonBonus){
                    mSelectedCategory = "Bonus";
                    mDisplaySelectedCategory.setText(mSelectedCategory);
                }else if(realCheck == R.id.radioButtonGambling){
                    mSelectedCategory = "Gambling";
                    mDisplaySelectedCategory.setText(mSelectedCategory);
                }else if(realCheck == R.id.radioButtonRental){
                    mSelectedCategory = "Rental";
                    mDisplaySelectedCategory.setText(mSelectedCategory);
                }else if(realCheck == R.id.radioButtonIncomeOther){
                    mSelectedCategory = "Other";
                    mDisplaySelectedCategory.setText(mSelectedCategory);
                }
            }
        }
    };

    public void setCategory() {
        firstRowCategoryIncome = findViewById(R.id.radioGroupIncomeFirstRow);
        secondRowCategoryIncome = findViewById(R.id.radioGroupIncomeSecondRow);
        mDisplaySelectedCategory = findViewById(R.id.select_income_category);
        firstRowCategoryIncome.clearCheck();
        secondRowCategoryIncome.clearCheck();
        firstRowCategoryIncome.setOnCheckedChangeListener(listener1Income);
        secondRowCategoryIncome.setOnCheckedChangeListener(listener2Income);
    }

    public String getNote() { return mNoteedit.getText().toString(); }

    public void setNoteedit() { mNoteedit = findViewById(R.id.income_editNote); }

    public String getDate() { return mDisplayDate.getText().toString(); }

    public void setDateSelector() {
        mDisplayDate = findViewById(R.id.income_selectDate);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        IndividualIncomeTransactionUpsertActivity.this,
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
        mSelectCurrency = (Spinner) findViewById(R.id.income_currency_spinner);
        currencySaveString = "CAD";
        ArrayList<String> currencystring = new ArrayList<>();
        currencystring.add("CAD");
        currencystring.add("USD");
        currencystring.add("CNY");
        currencystring.add("JPY");
        currencystring.add("EUR");

        ArrayAdapter<String> adapter_curr = new ArrayAdapter<>(this, R.layout.spinner_item, currencystring);
        adapter_curr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSelectCurrency.setAdapter(adapter_curr);
        mSelectCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedvalue = parent.getItemAtPosition(position).toString();
                Toast.makeText(IndividualIncomeTransactionUpsertActivity.this, "Selected:" + selectedvalue, Toast.LENGTH_SHORT).show();
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

        //set amount
        mAmountedit = (EditText) findViewById(R.id.income_editIndividual);

        //initial view
        initView();

        setInitValues();
    }

    public void initView(){
        LinearLayout transLayout = (LinearLayout) findViewById(R.id.addtransIncome);
    }

    public void cancelButtonHandler(View v) {
        finish();
    }

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
            transaction.setType("Income");
            model.updateTransactionInCurrentList(transaction, false);
        } else {
            String uuid = UUID.randomUUID().toString();
            IndividualTransaction newIndividualTransaction = new IndividualTransaction(uuid, category, "Income",
                    amount, currency, note, date);
            model.addToCurrentTransactionList(newIndividualTransaction, false);
        }
        finish();
    }

    public void setInitValues() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String transactionId = extras.getString("transactionId");
            transaction = model.getTransaction(transactionId);

            String category = transaction.getCategory();
            int id;
            if (category.equals("Salary")) {
                id = R.id.radioButtonSalary;
            } else if (category.equals("Investment")) {
                id = R.id.radioButtonInvestment;
            } else if (category.equals("Parttime")) {
                id = R.id.radioButtonParttime;
            } else if (category.equals("Redpocket")) {
                id = R.id.radioButtonRedpocket;
            } else if (category.equals("Bonus")) {
                id = R.id.radioButtonBonus;
            } else if (category.equals("Gambling")) {
                id = R.id.radioButtonGambling;
            } else if (category.equals("Rental")) {
                id = R.id.radioButtonRental;
            } else {
                id = R.id.radioButtonIncomeOther;
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
