package ca.uwaterloo.cs446.ezbill;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

public class TransactionUpsertActivityTemplate extends AppCompatActivity implements Observer {

    Model model;

    protected void handle_new_group_transaction(){
        //does nothing by default
    }
    protected void handle_new_individual_transaction(){
        //does nothing by default
    }
    protected void initial_transaction_page(){
        //does nothing by default
    }

    public void startActivityInitProcess(){
        initial_transaction_page();
        handle_new_group_transaction();
        handle_new_individual_transaction();
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}
