package ca.uwaterloo.cs446.ezbill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


public class MainActivity extends AppCompatActivity implements Observer {

    Model model;
    private TextView mTextMessage;
    private ArrayList<String> dates;
    private TimeAdapter myAdapter;
    private RecyclerView Rv;
    private Context context;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_my_account_book:
                    mTextMessage.setText(R.string.title_my_account_book);
                    return true;
                case R.id.navigation_group_account_book:
                    mTextMessage.setText(R.string.title_group_account_book);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        model = Model.getInstance();
        model.addObserver(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        title.setText("EzBill");
        this.context = this;



        // Init data for timeline
        dates = new ArrayList<>();

        // Init RecyclerView for timeline
        Rv = (RecyclerView) findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        Rv.setLayoutManager(layoutManager);
        Rv.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration();

        for (GroupAccountBook groupAccountBook : model.getGroupAccountBookList()) {
            dates.add(groupAccountBook.getEndDate());
        }
        dividerItemDecoration.setDates(dates);
        Rv.addItemDecoration(dividerItemDecoration);
        myAdapter = new TimeAdapter(context, model.getGroupAccountBookList());
        Rv.setAdapter(myAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove observer when activity is destroyed.
        model.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration();
        for (GroupAccountBook groupAccountBook : model.getGroupAccountBookList()) {
            dates.add(groupAccountBook.getEndDate());
        }
        dividerItemDecoration.setDates(dates);
        Rv.addItemDecoration(dividerItemDecoration);
        myAdapter = new TimeAdapter(context, model.getGroupAccountBookList());
        Rv.setAdapter(myAdapter);
    }

}
