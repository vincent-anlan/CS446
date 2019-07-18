package ca.uwaterloo.cs446.ezbill;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.UUID;


public class IndividualTransactionUpsertActivity extends TransactionUpsertActivityTemplate {
    Model model;
    private Spinner mSelectCurrency;
    private String currencySaveString;
    private ArrayList<String> currencystring;
    private Toolbar mytoolbar_new_Expense;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private static final String TAG = "Transaction";

    private EditText mNoteedit;
    private EditText mAmountedit;


}
