package ca.uwaterloo.cs446.ezbill;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//add username
import android.util.Log;
import android.net.Uri;
import com.google.firebase.auth.UserProfileChangeRequest;


public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText email;
    private EditText password;
    private EditText Confirmpassword;
    private EditText username;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        //pre
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        username = findViewById(R.id.input_user);
        Confirmpassword = findViewById(R.id.input_password_confirm);
        dialog =  new ProgressDialog(this,R.style.AppTheme);
        dialog.setIndeterminate(true);
        // add listener
        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {

            public void onClick(View paramView) {
                creatacc(email.getText().toString(), password.getText().toString());
            }
        });
        findViewById(R.id.link_login).setOnClickListener(new View.OnClickListener() {

            public void onClick(View paramView) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

    }

    private void creatacc(String eml, String pw){
        boolean flag = true;
        if(!checkinput()){
            flag =false;
            return;
        }
        if(flag == true) {
            dialog.setMessage("Creating Account...");
            dialog.show();
            auth.createUserWithEmailAndPassword(eml, pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        //add username;
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username.getText().toString())
                                //.setPhotoUri(Uri.parse("https://www.google.com/logo.jpg"))
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("00", "User profile updated.");
                                        }
                                    }
                                });

                        //do something;
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        //do something;
                    }
                }
            });
        }
    }

    //check email and password
    private boolean checkinput() {
        boolean result = true;
        String eml = email.getText().toString();
        String pw = password.getText().toString();
        String confirmPW = Confirmpassword.getText().toString();
        if (eml.length() == 0) {
            result  = false;
            email.setError("Please enter email");
        }
        if (pw.length() == 0) {
            result  = false;
            email.setError("Please enter password");
        }
        if(! confirmPW.equals(pw)){
            result  = false;
            Confirmpassword.setError("");
            Toast.makeText(getApplicationContext(), "Password does not match.",
                    Toast.LENGTH_SHORT).show();
        }
        return result;
    }
}
