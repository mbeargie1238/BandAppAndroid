package com.mbeargie.bandapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mbeargie.bandapp.models.Genres;
import com.mbeargie.bandapp.models.Register;
import com.mbeargie.bandapp.models.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;


/**
 * Created by mbeargie on 7/18/2018.
 */

public class HomeActivity extends BaseActivity implements
        View.OnClickListener {

    private TextView mDetailTextView;
    private TextView mStatusTextView;
    private TextView mUserView;
    private TextView mBandView;
    private TextView mCityView;
    private TextView mGenreView;
    private TextView mStorageView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    public String email_list;
    public ImageView imageView;
    private Context context;
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference storageReferencePhoto;
    ExifInterface exif;


    public String mDownloadUrl;
    Uri uri;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mDetailTextView = findViewById(R.id.detail);
        mUserView = findViewById(R.id.user);
        mBandView = findViewById(R.id.band);
        mCityView = findViewById(R.id.city);
        mGenreView = findViewById(R.id.genre);
        mStatusTextView = findViewById(R.id.status);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        storageReferencePhoto = storage.getReference();
        context = this;

        findViewById(R.id.sign_out_home_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = mAuth.getCurrentUser();

        mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                user.getEmail(), user.isEmailVerified()));

        mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));



        ImageView ivBasicImage = (ImageView) findViewById(R.id.facebook_photo);



        // Attach a listener to read the data at your profile reference
        mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User get_username = dataSnapshot.getValue(User.class);
                Register get_register = dataSnapshot.getValue(Register.class);

                mUserView.setText(getString(R.string.firebase_user_name, get_username.username));
                mBandView.setText(getString(R.string.firebase_band_name, get_register.bandname));
                mCityView.setText(getString(R.string.firebase_city_name, get_register.city));
                Log.i("RotateImage", "Exif orientation: " + get_register.photo_orientation);

                if(user.getPhotoUrl() != null) {
                    Picasso.with(HomeActivity.this).load(user.getPhotoUrl() + "/picture?width=1500").into(ivBasicImage);
                } else {

                    storageReferencePhoto.child("images/" + user.getUid() + "/profile").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(context).load(uri).rotate(get_register.photo_orientation).into(ivBasicImage);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            Log.i("Failed", "No Image!!");

                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
            // [END initialize_auth]
        });


        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("genres")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        long count = dataSnapshot.getChildrenCount();

                        String combine_genres = "";

                        Genres get_genres = dataSnapshot.getValue(Genres.class);

                        if(get_genres != null) {

                            if (get_genres.genre1 != null && !get_genres.genre1.isEmpty()) {
                                combine_genres = get_genres.genre1;
                            } else {
                                combine_genres = "";
                            }
                            if (get_genres.genre2 != null && !get_genres.genre2.isEmpty()) {
                                combine_genres += ", " + get_genres.genre2;
                            } else {
                                combine_genres += "";
                            }
                            if (get_genres.genre3 != null && !get_genres.genre3.isEmpty()) {
                                combine_genres += ", " + get_genres.genre3;
                            } else {
                                combine_genres += "";
                            }
                            if (get_genres.genre4 != null && !get_genres.genre4.isEmpty()) {
                                combine_genres += ", " + get_genres.genre4;
                            } else {
                                combine_genres += "";
                            }
                            if (get_genres.genre5 != null && !get_genres.genre5.isEmpty()) {
                                combine_genres += ", " + get_genres.genre5;
                            } else {
                                combine_genres += "";
                            }

                            mGenreView.setText(getString(R.string.firebase_genre_name, combine_genres));

                        } else {

                            mGenreView.setText(getString(R.string.firebase_genre_name, "No genres chosen"));

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();

        updateUI(null);
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();

    }

    @Override
    public void onClick(View view) {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        startActivity(new Intent(this, ChooserActivity.class));

    }
}
