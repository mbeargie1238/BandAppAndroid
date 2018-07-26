package com.mbeargie.bandapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mbeargie.bandapp.models.Genres;
import com.mbeargie.bandapp.models.User;
import com.mbeargie.bandapp.models.Register;

import java.io.IOException;
import java.util.UUID;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends BaseActivity implements
        View.OnClickListener  {

    // UI references.
    private EditText mBandName, mBandCity;
    private CheckBox mGenreRock, mGenreHipHop, mGenreCountry;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Button btnChoose, btnUpload;
    private ImageView imageView;
    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Set up the login form.

        findViewById(R.id.register_button).setOnClickListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mBandName = findViewById(R.id.bandname);
        mBandCity = findViewById(R.id.bandcity);
        mGenreRock = findViewById(R.id.genre_rock);
        mGenreHipHop = findViewById(R.id.genre_hip_hop);
        mGenreCountry = findViewById(R.id.genre_country);
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        imageView = findViewById(R.id.imgView);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();


    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.register_button) {

            int n = 0;

            FirebaseUser user = mAuth.getCurrentUser();

            mDatabase.child("users").child(user.getUid()).child("bandname").setValue(mBandName.getText().toString());
            mDatabase.child("users").child(user.getUid()).child("city").setValue(mBandCity.getText().toString());


            if(mGenreRock.isChecked()) {
                n++;
                mDatabase.child("users").child(user.getUid()).child("genres").child("genre" + n).setValue("Rock");
            }
            if(mGenreHipHop.isChecked()) {
                n++;
                mDatabase.child("users").child(user.getUid()).child("genres").child("genre" + n).setValue("Hip Hop");
            }
            if(mGenreCountry.isChecked()) {
                n++;
                mDatabase.child("users").child(user.getUid()).child("genres").child("genre" + n).setValue("Country");
            }

            startActivity(new Intent(this, HomeActivity.class));


        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            FirebaseUser user = mAuth.getCurrentUser();

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ user.getUid() + "/profile");
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }







}

