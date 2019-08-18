package ca.uwaterloo.cs446.ezbill;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
//add username
import android.util.Log;
import android.net.Uri;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.InputStream;
import android.graphics.Bitmap;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import android.graphics.BitmapFactory;
import android.database.Cursor;
import android.provider.MediaStore.MediaColumns;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import javax.annotation.Nonnull;


public class SignupActivity extends AppCompatActivity {
    final int RESULT_IMAGE = 666;

    private FirebaseAuth auth;
    private EditText email;
    private EditText password;
    private EditText Confirmpassword;
    private EditText username;
    private String downloadURL;
    FirebaseStorage storage;
    StorageReference refstorage;
    FirebaseFirestore db;
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
        storage = FirebaseStorage.getInstance();
        refstorage = storage.getReference();
        db = FirebaseFirestore.getInstance();

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
        findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {

            public void onClick(View paramView) {
                Intent img_intent = new Intent(Intent.ACTION_PICK);
                img_intent.setType("image/*");
                img_intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(img_intent, RESULT_IMAGE);

            }
        });
        ImageView img = findViewById(R.id.icon_reg);
        img.setImageResource(R.drawable.dining);

    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != SignupActivity.RESULT_CANCELED) {
            if (requestCode == RESULT_IMAGE && resultCode == RESULT_OK && data != null) {
                try {
                    ImageView img = findViewById(R.id.icon_reg);
                    final Uri imageUri = data.getData();
                    String path = getAbsolutePath(data.getData());
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    img.setImageBitmap(selectedImage);

                    //upload img:
                    //Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
                    String EmailAddr = email.getText().toString();
                    if(EmailAddr.length() == 0){
                        email.setError("Required");
                        return;
                    }
                    final StorageReference userRef = refstorage.child("images/"+EmailAddr);
                    UploadTask uploadTask = userRef.putFile(imageUri);

                    // add observers to listen the upload task
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), "Uploading fails",
                                    Toast.LENGTH_SHORT).show();
                            // do something
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            userRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadURL = uri.toString();
                                    Log.d("TAG", "onSuccess: uri= "+ uri.toString());
                                }
                            });
                            Toast.makeText(getApplicationContext(), "Uploading successfully",
                                    Toast.LENGTH_SHORT).show();
                            // do something
                        }
                    });
                    //img.setImageBitmap(decodeFile(path));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        }
    }


    public String getAbsolutePath(Uri uri) {
        String[] projection = { MediaColumns.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
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

                        Map<String, Object> data = new HashMap<>();
                        data.put("id", user.getUid());
                        data.put("email", user.getEmail());
                        data.put("username", username.getText().toString());
                        data.put("photoUrl", downloadURL);

                        // Add a new document with a generated ID
                        db.collection("users")
                                .add(data)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d("WRITE", "DocumentSnapshot added with ID: " + documentReference.getId());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@Nonnull Exception e) {
                                        Log.w("WRITE", "Error adding document", e);
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
