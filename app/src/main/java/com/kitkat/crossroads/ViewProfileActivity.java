package com.kitkat.crossroads;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ViewProfileActivity extends AppCompatActivity {

    private TextView textViewName;
    private TextView textViewPostalAddress;
    private TextView textViewDateOfBirth;
    private TextView textViewPhoneNumber;
    private TextView textViewEditProfile;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewPostalAddress = (TextView) findViewById(R.id.textViewPostalAddress);
        textViewDateOfBirth = (TextView) findViewById(R.id.textViewDateOfBirth);
        textViewPhoneNumber = (TextView) findViewById(R.id.textViewPhoneNumber);
        textViewEditProfile = (TextView) findViewById(R.id.textViewEditProfile);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        textViewEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewProfileActivity.this, CreateProfileActivity.class));
            }
        });
    }

    private void showData(DataSnapshot dataSnapshot)
    {
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            UserDetails userDetails = new UserDetails();
            userDetails.setName(ds.child(userID).getValue(UserDetails.class).getName());
            userDetails.setAddress(ds.child(userID).getValue(UserDetails.class).getAddress());
            userDetails.setDateOfBirth(ds.child(userID).getValue(UserDetails.class).getDateOfBirth());
            userDetails.setPhoneNumber(ds.child(userID).getValue(UserDetails.class).getPhoneNumber());

            textViewName.setText(userDetails.getName());
            textViewPostalAddress.setText(userDetails.getAddress());
            textViewDateOfBirth.setText(userDetails.getDateOfBirth());
            textViewPhoneNumber.setText(userDetails.getPhoneNumber());
        }
    }
}
