package ca.uwaterloo.cs446.ezbill;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import java.util.concurrent.TimeUnit;


//import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.quickstart.auth.R;

public class Signup extends AppCompatActivity {


    //define var
    private FirebaseAuth auth;
    private TextView status;
    private TextView detail;
    private EditText email;
    private EditText password;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //init
        auth = FirebaseAuth.getInstance();
        status = findViewById(R.id.status);
        detail = findViewById(R.id.detail);
        email = findViewById(R.id.fieldEmail);
        password = findViewById(R.id.fieldPassword);
        dialog =  new ProgressDialog(this);


        // add listener
        findViewById(R.id.emailSignInButton).setOnClickListener(new View.OnClickListener() {

            public void onClick(View paramView) {
                signin(email.getText().toString(), password.getText().toString());

                loading("Logging in your account, please wait.");
            }
        });
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(new View.OnClickListener() {

            public void onClick(View paramView) {
                creatacc(email.getText().toString(), password.getText().toString());
                loading("Creating your account, please wait.");

            }
        });
        findViewById(R.id.signOutButton).setOnClickListener(new View.OnClickListener() {

            public void onClick(View paramView) {
                signout();
            }
        });
        findViewById(R.id.verifyEmailButton).setOnClickListener(new View.OnClickListener() {

            public void onClick(View paramView) {
                sendEmailVerification();
            }
        });

    }
    //waiting function and show message.
    private void loading(String msg){
        dialog.setMessage(msg);
        dialog.show();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    //check email and password
    private boolean checkinput() {
        boolean result = true;
        String eml = email.getText().toString();
        String pw = password.getText().toString();
        if (eml.length() == 0) {
            result  = false;
            email.setError("Please enter email");
        }
        if (pw.length() == 0) {
            result  = false;
            email.setError("Please enter password");
        }
        return result;
    }

    //create account:
    private void creatacc(String eml, String pw){
        if(!checkinput()){
            return;
        }
        auth.createUserWithEmailAndPassword(eml,pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    status.setText(user.getEmail());
                    detail.setText("register success");
                    findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
                    findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
                    //do something;
                } else {
                    Toast.makeText(Signup.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //do something;
                }
            }
        });
    }

    private void signin(String eml, String pw){
        if(!checkinput()){
            return;
        }
        auth.signInWithEmailAndPassword(eml,pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    status.setText(user.getEmail());
                    detail.setText("Login success");
                    findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
                    findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
                    //do something;
                } else {
                    Toast.makeText(Signup.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //do something;
                }
            }
        });
    }

    private void signout(){

    }

    private void sendEmailVerification(){

    }
}
