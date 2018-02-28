package com.kitkat.crossroads.Profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.R;

import java.util.ArrayList;


public class ViewProfileActivity extends AppCompatActivity {
    private static final String TAG = "ViewProfileActivity";

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    private ListView mListView;
    private TextView textViewName;
    private TextView viewPostalAddress;
    private TextView viewDateOfBirth;
    private TextView viewPhoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        ImageView imageView = (ImageView) findViewById(R.id.profileImage);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.selfie);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        imageView.setImageDrawable(roundedBitmapDrawable);

        textViewName = (TextView) findViewById(R.id.textViewName);
        viewPostalAddress = (TextView) findViewById(R.id.viewPostalAddress);
        viewDateOfBirth = (TextView) findViewById(R.id.viewDateOfBirth);
        viewPhoneNumber = (TextView) findViewById(R.id.viewPhoneNumber);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("users");
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
            }
        };

        myRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String address = dataSnapshot.child("address").getValue(String.class);
                String dateOfBirth = dataSnapshot.child("dateOfBirth").getValue(String.class);
                String name = dataSnapshot.child("name").getValue(String.class);
                String phoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
                Log.d(TAG, "Address Is: " + address);
                Log.d(TAG, "Date Of Birth Is: " + dateOfBirth);
                Log.d(TAG, "Name Is: " + name);
                Log.d(TAG, "Phone Number Is" + phoneNumber);

                textViewName.setText(name);
                viewPostalAddress.setText(address);
                viewDateOfBirth.setText(dateOfBirth);
                viewPhoneNumber.setText(phoneNumber);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
