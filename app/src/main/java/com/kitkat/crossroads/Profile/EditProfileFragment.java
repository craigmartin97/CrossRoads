package com.kitkat.crossroads.Profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.R;



/**
 * Variables that set information in views on page.
 * The EditProfileFragment is used to edit the users personal information
 * The user is displayed with all of their information that they entered about themselves.
 * They can then edit this information and submit it.
 */
public class EditProfileFragment extends Fragment
{
    /**
     * TAG is used for testing, to be displayed in the log
     */
    private static final String TAG = "EditProfileActivity";

    /**
     * Edit texts are used to display the users information in and so the user
     * can edit it
     */
    private EditText fullName, phoneNumber, addressOne, addressTwo, town, postCode;

    /**
     * Checkboxes are used to display the users current preference on what they
     * are most likely to be using the app for
     */
    private CheckBox checkBoxAdvertiser, checkBoxCourier;

    /**
     * Boolean values to store what the user is primarily using the app for
     */
    private boolean advertiser, courier;

    /**
     * Button used to confirm the data that the user is submitting is correct
     * and to be sent to the database
     */
    private Button saveProfile;

    /**
     * Store the users profile image URL and the userEmail
     * These cannot be edited on this page but they are stored to push
     *
     */
    private String profileImage, userEmail;
    private String user;

    /**
     * Variable for the reference of the database.
     */
    private DatabaseReference databaseReferenceUsersTable;

    /**
     * Progress Dialog that appears when a task is in progress.
     */
    private ProgressDialog progressDialog;


    public EditProfileFragment()
    {

    }

    public static EditProfileFragment newInstance()
    {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Method that runs on first launch.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        databaseConnections();
    }


    /**
     * Method that runs everytime the fragment is opened up.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        saveProfile = view.findViewById(R.id.buttonSaveProfile);

        fullName = view.findViewById(R.id.editTextFullName);
        phoneNumber = view.findViewById(R.id.editTextPhoneNumber);
        addressOne = view.findViewById(R.id.editTextAddress1);
        addressTwo = view.findViewById(R.id.editTextAddress2);
        town = view.findViewById(R.id.editTextTown);
        postCode = view.findViewById(R.id.editTextPostCode);
        checkBoxAdvertiser = view.findViewById(R.id.checkBoxAdvertiser);
        checkBoxCourier = view.findViewById(R.id.checkBoxCourier);

        databaseReferenceUsersTable.child(user).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String name = dataSnapshot.child("fullName").getValue(String.class);
                String number = dataSnapshot.child("phoneNumber").getValue(String.class);
                String address1 = dataSnapshot.child("addressOne").getValue(String.class);
                String address2 = dataSnapshot.child("addressTwo").getValue(String.class);
                String usersTown = dataSnapshot.child("town").getValue(String.class);
                String postalCode = dataSnapshot.child("postCode").getValue(String.class);
                profileImage = dataSnapshot.child("profileImage").getValue(String.class);
                boolean advertiser = dataSnapshot.child("advertiser").getValue(boolean.class);
                boolean courier = dataSnapshot.child("courier").getValue(boolean.class);
                userEmail = dataSnapshot.child("userEmail").getValue(String.class);

                Log.d(TAG, "Full Name: " + name);
                Log.d(TAG, "Phone Number: " + number);
                Log.d(TAG, "Address Line One: " + address1);
                Log.d(TAG, "Address Line Two: " + address2);
                Log.d(TAG, "Town: " + usersTown);
                Log.d(TAG, "PostCode: " + postalCode);
                Log.d(TAG, "ProfileImage: " + profileImage);
                Log.d(TAG, "Advertiser: " + advertiser);
                Log.d(TAG, "Courier: " + courier);

                fullName.setText(name);
                phoneNumber.setText(number);
                addressOne.setText(address1);
                addressTwo.setText(address2);
                town.setText(usersTown);
                postCode.setText(postalCode);

                if (advertiser && !courier)
                {
                    checkBoxAdvertiser.setChecked(true);
                    checkBoxCourier.setChecked(false);
                } else if (!advertiser && courier)
                {
                    checkBoxAdvertiser.setChecked(false);
                    checkBoxCourier.setChecked(true);
                } else if (advertiser && courier)
                {
                    checkBoxAdvertiser.setChecked(true);
                    checkBoxCourier.setChecked(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        saveProfile = view.findViewById(R.id.buttonSaveProfile);
        this.profileImage = profileImage;

        saveProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                progressDialog.setMessage("Saving Profile Please Wait...");
                saveUserInformation();
            }
        });

        return view;
    }

    /**
     * Establishing connections to Firebase Database, getting current user Id
     */
    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        databaseReferenceUsersTable = databaseConnections.getDatabaseReferenceUsers();
        databaseReferenceUsersTable.keepSynced(true);
        user = databaseConnections.getCurrentUser();
    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    /**
     *
     */
    private void saveUserInformation()
    {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Updating Information Please Wait...");
        progressDialog.show();

        String fullName = this.fullName.getText().toString().trim();
        String phoneNumber = this.phoneNumber.getText().toString().trim();
        String addressOne = this.addressOne.getText().toString().trim();
        String addressTwo = this.addressTwo.getText().toString().trim();
        String town = this.town.getText().toString().trim();
        String postCode = this.postCode.getText().toString().trim().toUpperCase();
        String userEmail = this.userEmail.trim();

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
                    addressTwo, town, postCode, advertiser, courier, profileImage, userEmail);

            setUserInformation(userInformation);
        } else if (!checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
        {
            advertiser = false;
            courier = true;
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, advertiser, courier, profileImage, userEmail);

            setUserInformation(userInformation);
        } else if (checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
        {
            advertiser = true;
            courier = true;
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, advertiser, courier, profileImage, userEmail);

            setUserInformation(userInformation);
        }

        progressDialog.dismiss();
        customToastMessage("Information Saved...");

        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, new ViewProfileFragment()).addToBackStack("tag").commit();
    }

    private void setUserInformation(UserInformation userInformation)
    {
        databaseReferenceUsersTable.child(user).setValue(userInformation);
    }

    private void customToastMessage(String message)
    {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

}