package ca.uwaterloo.cs446.ezbill;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MyInfoActivity extends AppCompatActivity {

    Model model;

    TextView email;
    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = Model.getInstance();

        setContentView(R.layout.my_info);
        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_info_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("My Info");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // get elements
        email = (TextView) findViewById(R.id.user_email);
        email.setText(model.userEmail);

        username = (TextView) findViewById(R.id.username);
        username.setText(model.getCurrentUsername());

    }

    public void signOutOnClick(View view) {
        Log.d("WRITE", "Sign Out Btn clicked!!!");
    }
}
