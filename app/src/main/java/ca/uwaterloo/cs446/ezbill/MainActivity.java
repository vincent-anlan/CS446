package ca.uwaterloo.cs446.ezbill;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
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


public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private ArrayList<AccountBook> accountBooks;
    private ArrayList<String> listItem;
    private ArrayList<String> dates;
    private TimeAdapter myAdapter;
    private RecyclerView Rv;

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

        // Init data for timeline
        listItem = new ArrayList<String>();
        dates = new ArrayList<String>();

//        for (int i = 0; i < accountBooks.size(); i++) {
//            listItem.add(accountBooks.get(i).getName());
//            dates.add(accountBooks.get(i).getStartDate());
//        }

        listItem.add("Add New Account Book");
        listItem.add("Eve's Account Book");
        listItem.add("2019 Winter Coop");
        listItem.add("Account Book 2");
        listItem.add("Account Book 3");
        listItem.add("Account Book 4");

        dates.add("2017.4.03");
        dates.add("2017.4.03");
        dates.add("2017.4.03");
        dates.add("2017.4.03");
        dates.add("2017.4.04");
        dates.add("2017.4.04");

        // Init RecyclerView for timeline
        Rv = (RecyclerView) findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        Rv.setLayoutManager(layoutManager);
        Rv.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration();
        dividerItemDecoration.setDates(dates);
        Rv.addItemDecoration(dividerItemDecoration);
        myAdapter = new TimeAdapter(this,listItem);
        Rv.setAdapter(myAdapter);


        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "0000");

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("WRITE", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("WRITE", "Error adding document", e);
                    }
                });


        // read data from database
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("READ", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("READ", "Error getting documents.", task.getException());
                        }
                    }
                });

    }

}
