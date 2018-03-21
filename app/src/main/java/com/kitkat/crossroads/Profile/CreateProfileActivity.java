package com.kitkat.crossroads.Profile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kitkat.crossroads.Account.LoginActivity;
import com.kitkat.crossroads.MyFirebaseInstanceIDService;
import com.kitkat.crossroads.R;
import com.kitkat.crossroads.Profile.UserInformation;

import java.util.Calendar;

public class CreateProfileActivity extends AppCompatActivity
{
    private static final String TAG = "CreateProfileActivity";
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private FirebaseAuth auth;
    private EditText fullName, phoneNumber, addressOne, addressTwo, town, postCode;
    private CheckBox checkBoxAdvertiser, checkBoxCourier;
    private boolean advertiser, courier;
    private Button saveProfile;

    private DatabaseReference myRef;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        database = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        saveProfile = (Button) findViewById(R.id.buttonSaveProfile);

        fullName = (EditText) findViewById(R.id.editTextFullName);
        phoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        addressOne = (EditText) findViewById(R.id.editTextAddress1);
        addressTwo = (EditText) findViewById(R.id.editTextAddress2);
        town = (EditText) findViewById(R.id.editTextTown);
        postCode = (EditText) findViewById(R.id.editTextPostCode);
        checkBoxAdvertiser = (CheckBox) findViewById(R.id.checkBoxAdvertiser);
        checkBoxCourier = (CheckBox) findViewById(R.id.checkBoxCourier);



        saveProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveUserInformation();
            }
        });
    }

    private void saveUserInformation()
    {
        String fullName = this.fullName.getText().toString().trim();
        String phoneNumber = this.phoneNumber.getText().toString().trim();
        String addressOne = this.addressOne.getText().toString().trim();
        String addressTwo = this.addressTwo.getText().toString().trim();
        String town = this.town.getText().toString().trim();
        String postCode = this.postCode.getText().toString().trim().toUpperCase();

        if (TextUtils.isEmpty(fullName))
        {
            customToastMessage("Please Enter Your Name");
            return;
        }
        if (TextUtils.isEmpty(phoneNumber))
        {
            customToastMessage("Please Enter Your Phone Number");
            return;
        }
        if (TextUtils.isEmpty(addressOne))
        {
            customToastMessage("Please Enter Your House Number & Street");
            return;
        }
        if (TextUtils.isEmpty(addressTwo))
        {
            customToastMessage("Please Enter Your Second Address Line");
            return;
        }
        if (TextUtils.isEmpty(town))
        {
            customToastMessage("Please Enter Your Town");
            return;
        }
        if (TextUtils.isEmpty(postCode))
        {
            customToastMessage("Please Enter Your PostCode");
            return;
        }

        if (fullName.length() < 4)
        {
            customToastMessage("Your Full Name Must Be Greater Than Four Characters");
            return;
        }

        if (phoneNumber.length() != 11)
        {
            customToastMessage("Your Phone Number Must Be 11 Numbers Long");
            return;
        }

        if (!postCode.matches("^(?=.*[A-Z])(?=.*[0-9])[A-Z0-9 ]+$"))
        {
            customToastMessage("Post Code Must Have Numbers and Letters");
            return;
        }

        if (checkBoxAdvertiser.isChecked() && !checkBoxCourier.isChecked())
        {
            advertiser = true;
            courier = false;
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, advertiser, courier, null);

            setUserInformation(userInformation);
        } else if (!checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
        {
            advertiser = false;
            courier = true;
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, advertiser, courier, null);

            setUserInformation(userInformation);
        } else if (checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
        {
            advertiser = true;
            courier = true;
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, advertiser, courier, null);

            setUserInformation(userInformation);
        }

        databaseVerification();
        startActivity(new Intent(CreateProfileActivity.this, LoginActivity.class));
    }

    private void setUserInformation(UserInformation userInformation)
    {
        FirebaseUser user = auth.getCurrentUser();
        myRef.child("Users").child(user.getUid()).setValue(userInformation);
    }

    private void databaseVerification()
    {
        FirebaseUser userEmail = FirebaseAuth.getInstance().getCurrentUser();
        userEmail.sendEmailVerification();
        FirebaseAuth.getInstance().signOut();
    }

    private void customToastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}


