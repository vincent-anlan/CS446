package ca.uwaterloo.cs446.ezbill;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity{

    Model model;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_my_account_book:
                    Fragment individualAccountBookFragment = new IndividualAccountBookListFragment();
                    loadFragment(individualAccountBookFragment);
                    return true;
                case R.id.navigation_group_account_book:
                    Fragment groupAccountBookFragement = new GroupAccountBookListFragment();
                    loadFragment(groupAccountBookFragement);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = Model.getInstance();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        title.setText("EzBill");

        loadFragment(new GroupAccountBookListFragment());
        thread.start();

    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            HashMap<String, Float> exchangeRates = new HashMap<>();
            Log.d("READ", "API HIT");
            try {
                URL url = new URL("https://openexchangerates.org/api/latest.json?app_id=ed4b872b78ba45df9e2bb012f244f046");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    Log.d("READ", "API RESPONSE");
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
                    Log.d("READ", "API FINISHED");
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

}
