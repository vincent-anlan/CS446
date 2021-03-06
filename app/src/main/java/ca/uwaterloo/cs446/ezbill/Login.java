package ca.uwaterloo.cs446.ezbill;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import android.content.Intent;
import android.os.Handler;
//Google SignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.gms.tasks.OnFailureListener;
import android.net.Uri;
import com.google.android.gms.tasks.OnSuccessListener;
import java.net.URL;
import java.net.MalformedURLException;
import android.graphics.drawable.Drawable;


public class Login extends AppCompatActivity {
    //define var
    private FirebaseAuth auth;
    private TextView status;
    private TextView detail;
    private EditText email;
    private EditText password;
    private ProgressDialog dialog = null;
    private GoogleSignInClient GoogleSignin;
    FirebaseStorage storage;
    StorageReference refstorage;
    ImageView img;
    String imgurl;
    Intent intent1;

    String username1;
    String id2;
    String email1;





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
        dialog =  new ProgressDialog(this,R.style.AppTheme);
        dialog.setIndeterminate(true);
        storage = FirebaseStorage.getInstance();
        refstorage = storage.getReference();
        img = findViewById(R.id.icon);
        intent1  = new Intent(getApplicationContext(), MainActivity.class);



        // add listener
        findViewById(R.id.emailSignInButton).setOnClickListener(new View.OnClickListener() {

            public void onClick(View paramView) {
                //loading("Logging in your account, please wait.");
                signin(email.getText().toString(), password.getText().toString());

            }
        });
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(new View.OnClickListener() {

            public void onClick(View paramView) {
                //loading("Creating your account, please wait.");
                creatacc();

            }
        });
        findViewById(R.id.signOutButton).setOnClickListener(new View.OnClickListener() {

            public void onClick(View paramView) {
                signout();
            }
        });
        findViewById(R.id.verifyEmailButton).setOnClickListener(new View.OnClickListener() {

            public void onClick(View paramView) {

            }
        });
        findViewById(R.id.link_reset).setOnClickListener(new View.OnClickListener() {

            public void onClick(View paramView) {
                String ResetEmail = email.getText().toString();
                Log.d("Reset fails ","length:"+ ResetEmail.length() );
                if(ResetEmail.length() == 0){
                    email.setError("Please enter email");
                    return;
                }

                //send reset email
                auth.sendPasswordResetEmail(ResetEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Email sent.",
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(Login.this, "Email sending fails.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        //Google Sign in listener
        findViewById(R.id.google_login).setOnClickListener(new View.OnClickListener() {

            public void onClick(View paramView) {
                GoogleSignin();
            }
        });


        //Config google option
        GoogleSignInOptions GoogleOption = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //Google sign in
        Log.d("0","id:"+R.string.default_web_client_id);
        GoogleSignin = GoogleSignIn.getClient(this, GoogleOption);

    }

    // when start login activity check whether user sign in
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
        //do something
        }

    }

    //Onactivityresult
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 9001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("1", "Google sign in failed", e);
                //do something

            }
        }
    }

    private void firebaseAuthWithGoogleSign(GoogleSignInAccount acct) {
        Log.d("2", "firebaseAuthWithGoogle:" + acct.getId());


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("3", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            //do something
                            status.setText(user.getEmail());
                            detail.setText("Google Login success");
                            findViewById(R.id.link_reset).setVisibility(View.GONE);
                            findViewById(R.id.google_login).setVisibility(View.GONE);
                            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
                            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("4", "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Google Authentication failed.",
                                    Toast.LENGTH_SHORT).show();                            //do something
                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }

    private void GoogleSignin(){
        Intent googleIntent =GoogleSignin.getSignInIntent();
        startActivityForResult(googleIntent, 9001);

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
    private void creatacc(){
        dialog.setMessage("Creating...");
        dialog.show();
        //close the dialog in 3 secs.
        new Handler().postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
            }
        }, 3000);
        //go to signup activity
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
    }

    private void signin (final String eml, String pw){

        if(!checkinput()){
            return;
        }
        dialog.setMessage("Logging in your account...");
        dialog.show();
        //close the dialog in 3 secs.
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        }, 5000);
        auth.signInWithEmailAndPassword(eml,pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //create user
                    FirebaseUser user = auth.getCurrentUser();
                    status.setText(user.getEmail());
                    //get data from user
                    //String username = null;
                    String id = null;
                    id2 = user.getUid();
                    //imgurl = id2;
                    //String email = null;
                    if (user != null) {
                        for (UserInfo profile : user.getProviderData()) {
                            String providerId = profile.getProviderId();
                            id = profile.getUid();
                            username1 = profile.getDisplayName();
                            email1 = profile.getEmail();
                            //Uri photoUrl = profile.getPhotoUrl();
                        }
                    }
                    Log.d("999","email:"+email1);
                    refstorage.child("images/"+email1).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for profile pics.
                            //final Uri imageUri = data.getData();
                            //String path = getAbsolutePath(data.getData());
                            URL url = null;
                            //try {
                            //add username;
                            FirebaseUser user = auth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(uri.toString()))
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("00", "User pics profile updated.");
                                                FirebaseUser user = auth.getCurrentUser();
                                                String photo = user.getPhotoUrl().toString();
                                                Log.d("001", "url:"+photo);
                                            }
                                        }
                                    });

                                Log.d("300","uri:"+uri);
                                imgurl =  uri.toString()+"";
                                intent1.putExtra("image",imgurl);
                                intent1.putExtra("username", username1);
                                intent1.putExtra("uid", id2);
                                intent1.putExtra("email", email1);
                                //intent1.putExtra("image", imgurl);
                                if(dialog.isShowing()){
                                    dialog.dismiss();
                                }
                                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent1);
                                Log.d("301","uri:"+imgurl);

                                try {
                                    url = new URL(imgurl);
                                } catch(MalformedURLException e) {
                                    e.printStackTrace();
                                }

                                img.setImageDrawable(GetDrawable(imgurl));

//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                                Toast.makeText(getApplicationContext(), "you don't have profile pics", Toast.LENGTH_LONG).show();
//                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            Log.d("999","fail");
                        }
                    });
                    Log.d("302","uri:"+imgurl);
                    //close the dialog





                    Log.d("01","username:"+username1);
                    Log.d("331","uuid:"+imgurl);

                } else {
                    Toast.makeText(Login.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //do something;
                }
            }
        });
    }
    public static Drawable GetDrawable(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    private void signout(){
        auth.getInstance().signOut();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("000", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("001", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("001", "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }

}
