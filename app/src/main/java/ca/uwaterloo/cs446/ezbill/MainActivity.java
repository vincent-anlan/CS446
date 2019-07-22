package ca.uwaterloo.cs446.ezbill;

import android.content.Intent;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Model model;
    ProgressBar spinner;

    String email;
    String uid;
    String username;
    String ImgAddr;
    TextView text;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = Model.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //The value passed from sign in successfully
        Bundle info = getIntent().getExtras();
        if (info != null) {
            email = info.getString("email");
            uid = info.getString("uid");
            username = info.getString("username");
            ImgAddr = info.getString("image");
            //need a image view to add profile picture
        }

//        //Update user info
//        text = findViewById(R.id.User_info);
//        text.setText("email:"+email+"\n"+"uid:"+uid+"\n"+"username:"+username+"\n"
//        +"imageAddress:"+ImgAddr);

        model.setUserEmail(email);

        model.setProfilePhotoURL(ImgAddr);
        //Login End

        //another way to get url of profile picture.
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String url= user.getPhotoUrl().toString();
        Log.d("000","URL:"+url);
//        //Update user info
//        text = findViewById(R.id.User_info);
//        text.setText("email:"+email+"\n"+"uid:"+uid+"\n"+"username:"+username+"\n"
//        +"imageAddress:"+ImgAddr);

//        //Update user info
//        text = findViewById(R.id.User_info);
//        text.setText("email:"+email+"\n"+"uid:"+uid+"\n"+"username:"+username+"\n"
//        +"imageAddress:"+ImgAddr);
//        //Login End



        model = Model.getInstance();

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        readDB();

        View header = navigationView.getHeaderView(0);
        TextView user_name = (TextView) header.findViewById(R.id.user_name);
        TextView user_email = (TextView) header.findViewById(R.id.user_email);
        user_name.setText(username);
        user_email.setText(email);

        thread.start();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_account_book:
                Fragment individualAccountBookFragment = new IndividualAccountBookListFragment();
                loadFragment(individualAccountBookFragment);
                break;
            case R.id.group_account_book:
                Fragment groupAccountBookFragement = new GroupAccountBookListFragment();
                loadFragment(groupAccountBookFragement);
                break;
            case R.id.my_qrcode:
                startActivity(new Intent(MainActivity.this, myQRCode.class));
                break;
            case R.id.scan_qrcode:
                startQRScanner();
                break;
            case R.id.logout:
                // logout logic here
                Log.d("WRITE", "Sign Out Btn clicked!!!");
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scanning QR code.");
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
//        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // update database here
                // string decoded = result.getContents()
                showDialog("Added to the account book.");
            } else {
//                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showDialog(String message) {
        final  AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage(message);
        alertDialog.show();

        // Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        };

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 1200);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //    public void viewMyProfile(View view) {
//        Intent intent = new Intent(MainActivity.this, MyInfoActivity.class);
//        startActivity(intent);
//    }
//
    private void readDB() {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // read data from database
        db.collection("user_account_book_info")
                .whereEqualTo("email", model.userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userId = document.getData().get("userId").toString();
                                model.setCurrentUserId(userId);
                                String username = document.getData().get("username").toString();
                                model.setCurrentUsername(username);

                                String accountBookId = document.getData().get("accountBookId").toString();
                                String accountBookName = document.getData().get("accountBookName").toString();
                                String startDate = document.getData().get("accountBookStartDate").toString();
                                String endDate = document.getData().get("accountBookEndDate").toString();
                                String defaultCurrency = document.getData().get("accountBookCurrency").toString();
                                String type = document.getData().get("accountBookType").toString();
                                String creatorId = document.getData().get("accountBookCreator").toString();

                                if (type.equals("Group")) {
                                    if (!model.hasGroupAccountBook(accountBookId)) {
                                        GroupAccountBook groupAccountBook = new GroupAccountBook(accountBookId, accountBookName, startDate, endDate, defaultCurrency, creatorId);
                                        model.addGroupAccountBook(groupAccountBook);
                                    }
                                } else {
                                    if (!model.hasIndividualAccountBook(accountBookId)) {
                                        IndividualAccountBook individualAccountBook = new IndividualAccountBook(accountBookId, accountBookName, startDate, endDate, defaultCurrency, creatorId);
                                        model.addIndividualAccountBook(individualAccountBook);
                                    }
                                }
                                Log.d("READ", document.getId() + " => " + document.getData());
                            }

                            spinner.setVisibility(View.GONE);
                            for (GroupAccountBook groupAccountBook : model.groupAccountBookList) {
                                model.readParticipantsFromDB(groupAccountBook.getId());
                            }
                            loadFragment(new GroupAccountBookListFragment());

                        } else {
                            Log.w("READ", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            HashMap<String, Float> exchangeRates = new HashMap<>();
            try {
                URL url = new URL("https://openexchangerates.org/api/latest.json?app_id=ed4b872b78ba45df9e2bb012f244f046");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setDoOutput(true);
                    urlConnection.setChunkedStreamingMode(0);

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
                    String inputLine = "";
                    String fullStr = "";
                    while ((inputLine = inReader.readLine()) != null) {
                        fullStr += inputLine;
                    }

                    JSONObject jsonObject = new JSONObject(fullStr);
                    JSONObject rates = jsonObject.getJSONObject("rates");

                    float USD = Float.valueOf(rates.getString("USD"));
                    float CAD = Float.valueOf(rates.getString("CAD"));
                    float EUR = Float.valueOf(rates.getString("EUR"));
                    float JPY = Float.valueOf(rates.getString("JPY"));
                    float CNY = Float.valueOf(rates.getString("CNY"));

                    exchangeRates.put("USD", USD);
                    exchangeRates.put("CAD", CAD);
                    exchangeRates.put("EUR", EUR);
                    exchangeRates.put("JPY", JPY);
                    exchangeRates.put("CNY", CNY);
                    model.setExchangeRates(exchangeRates);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

}
