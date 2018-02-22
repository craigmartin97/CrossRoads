package com.kitkat.crossroads;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class CreateProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private static final String TAG = "CreateProfileActivity";

    private EditText editTextName;
    private EditText editTextPhoneNumber;
    private EditText editTextPostalAddress;
    private TextView textViewDateOfBirth;

    private Button buttonSaveProfile;
    private Button buttonLogout;

    private DatePickerDialog.OnDateSetListener dateSetListener;

    private DatabaseReference myRef;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        auth = FirebaseAuth.getInstance();

        //comment out the code below to test this single activity

        if(auth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }

        database = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = auth.getCurrentUser();

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonSaveProfile = (Button) findViewById(R.id.buttonSaveProfile);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        editTextPostalAddress = (EditText) findViewById(R.id.editTextPostalAddress);
        textViewDateOfBirth = (TextView) findViewById(R.id.textViewDateOfBirth);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                finish();
                startActivity(new Intent(CreateProfileActivity.this, LoginActivity.class));
            }
        });


        textViewDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dialog = new DatePickerDialog(
                        CreateProfileActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();


            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                month = month + 1;
                Log.d(TAG, "onDateSet: date: " + year + "/" + month + "/" + dayOfMonth);

                if(dayOfMonth >= 1 && dayOfMonth <= 9)
                {
                    String newDay = "0" + dayOfMonth;
                    textViewDateOfBirth.setText(newDay + "/" + month + "/" + year);
                }

                if(month >= 1 && month <= 9)
                {
                    String newMonth = "0" + month;
                    textViewDateOfBirth.setText(dayOfMonth + "/" + newMonth + "/" + year);
                }

                if(dayOfMonth >= 1 && dayOfMonth <= 9 && month >= 1 && month <= 9)
                {
                    String newDay = "0" + dayOfMonth;
                    String newMonth = "0" + month;
                    textViewDateOfBirth.setText(newDay + "/" + newMonth + "/" + year);
                }
                else
                {
                    textViewDateOfBirth.setText(dayOfMonth + "/" + month + "/" + year);
                }
            }
        };

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
        String dateOfBirth = textViewDateOfBirth.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();

        UserInformation userInformation = new UserInformation(name, address, dateOfBirth, phoneNumber);

        FirebaseUser user = auth.getCurrentUser();

        myRef.child("users").child(user.getUid()).setValue(userInformation);


        Toast.makeText(this, "Information Saved...", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(CreateProfileActivity.this, ViewProfileActivity.class));
    }
}
