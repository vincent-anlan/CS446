package ca.uwaterloo.cs446.ezbill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity{

    Model model;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_my_account_book:
                    model.setMainPageGroupViewOnSelect(false);
                    Fragment individualAccountBookFragment = new IndividualAccountBookFragment();
                    loadFragment(individualAccountBookFragment);
                    return true;
                case R.id.navigation_group_account_book:
                    model.setMainPageGroupViewOnSelect(true);
                    Fragment groupAccountBookFragement = new GroupAccountBookFragment();
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

        if (model.mainPageGroupViewOnSelect) {
            navView.setSelectedItemId(R.id.navigation_group_account_book);
            loadFragment(new GroupAccountBookFragment());
        } else {
            navView.setSelectedItemId(R.id.navigation_my_account_book);
            loadFragment(new IndividualAccountBookFragment());
        }


    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
