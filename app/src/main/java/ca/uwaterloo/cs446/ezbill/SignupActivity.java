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

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText email;
    private EditText password;
    private EditText Confirmpassword;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        //pre
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        Confirmpassword = findViewById(R.id.input_password_confirm);
        dialog =  new ProgressDialog(this,R.style.AppTheme);
        // add listener
        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {

            public void onClick(View paramView) {
                creatacc(email.getText().toString(), password.getText().toString());
                //loading("Logging in your account, please wait.");
            }
        });
    }

    private void creatacc(String eml, String pw){
        if(!checkinput()){
            return;
        }
        auth.createUserWithEmailAndPassword(eml,pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    //findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
                    //findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
                    //do something;
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //do something;
                }
            }
        });
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
}
