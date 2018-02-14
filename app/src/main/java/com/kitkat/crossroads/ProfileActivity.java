package com.kitkat.crossroads;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView textViewUserEmail;

    private EditText editTextName;
    private EditText editTextPhoneNumber;
    private EditText editTextPostalAddress;
    private EditText editTextDateOfBirth;


    private Button buttonSaveProfile;
    private Button buttonLogout;


    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }

        FirebaseUser user = auth.getCurrentUser();


        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText("Welcome " + user.getEmail());

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonSaveProfile = (Button) findViewById(R.id.buttonSaveProfile);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        editTextPostalAddress = (EditText) findViewById(R.id.editTextPostalAddress);
        editTextDateOfBirth = (EditText) findViewById(R.id.editTextDateOfBirth);


        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                finish();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

        buttonSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });
    }


    private void saveUserInformation()
    {
        String name = editTextName.getText().toString().trim();
        String address = editTextPostalAddress.getText().toString().trim();
        String dateOfBirth = editTextDateOfBirth.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();

        UserInformation userInformation = new UserInformation(name, address, dateOfBirth, phoneNumber);

        FirebaseUser user = auth.getCurrentUser();
        databaseReference.child(user.getUid()).setValue(userInformation);

        Toast.makeText(this, "Information Saved...", Toast.LENGTH_SHORT).show();
    }
}
