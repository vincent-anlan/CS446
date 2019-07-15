package ca.uwaterloo.cs446.ezbill;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class IndividualTransactionActivity extends TransactionActivityTemplate {

    @Override
    public void handle_new_group_transaction(){
        LinearLayout layout_payer = (LinearLayout) findViewById(R.id.payerlayout);
        layout_payer.setVisibility(View.GONE);
        LinearLayout layout_parti = (LinearLayout) findViewById(R.id.partilayout);
        layout_parti.setVisibility(View.GONE);
        LinearLayout layout_autosum = (LinearLayout) findViewById(R.id.autosumlayout);
        layout_autosum.setVisibility(View.GONE);
        LinearLayout layout_list = (LinearLayout) findViewById(R.id.listparticipant);
        layout_list.setVisibility(View.GONE);
    }

    @Override
    public void handle_new_individual_transaction(){
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove observer when activity is destroyed.
        model.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}
