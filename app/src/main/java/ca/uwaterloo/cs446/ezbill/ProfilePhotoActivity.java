package ca.uwaterloo.cs446.ezbill;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfilePhotoActivity extends AppCompatActivity {

    final int RESULT_IMAGE = 666;

    Model model;
    FirebaseStorage storage;
    StorageReference refstorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = Model.getInstance();

        storage = FirebaseStorage.getInstance();
        refstorage = storage.getReference();

        setContentView(R.layout.profile_photo);
        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_photo_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Photo");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        ImageView img = findViewById(R.id.photo);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(model.getProfilePhotoURL());
        Glide.with(this)
                .load(storageRef)
                .into(img);

    }

    public int dpTopx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);

    }

    public void uploadOnClick(View view) {
        Intent img_intent = new Intent(Intent.ACTION_PICK);
        img_intent.setType("image/*");
        img_intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(img_intent, RESULT_IMAGE);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != SignupActivity.RESULT_CANCELED) {
            if (requestCode == RESULT_IMAGE && resultCode == RESULT_OK && data != null) {
                try {
                    ImageView img = findViewById(R.id.photo);
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    img.setImageBitmap(selectedImage);

                    //upload img:
                    final StorageReference userRef = refstorage.child("images/"+model.userEmail);
                    final UploadTask uploadTask = userRef.putFile(imageUri);

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
                                    model.updateProfilePhotoUrl(uri.toString());
                                    Log.d("TAG", "onSuccess: uri= "+ uri.toString());
                                }
                            });
                            Toast.makeText(getApplicationContext(), "Uploading successfully",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        }
    }

}
