package com.kitkat.crossroads.Profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.GenericMethods;
import com.kitkat.crossroads.R;

/**
 * The EditProfileFragment is used to edit the users personal information
 * The user is displayed with all of their information that they entered about themselves.
 * They can then edit this information and submit it.
 */
public class EditProfileFragment extends Fragment
{
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
     * Button used to confirm the data that the user is submitting is correct
     * and to be sent to the database
     */
    private Button saveProfile;

    /**
     * Store the users profile image URL and the userEmail
     * These cannot be edited on this page but they are stored to push
     */
    private String profileImage, userEmail;

    /**
     * Storing the current users unique id
     */
    private String user;

    /**
     * Accessing the FireBase database under the Users table
     */
    private DatabaseReference databaseReferenceUsersTable;

    /**
     * Accessing the methods in class Generic Methods
     */
    private final GenericMethods genericMethods = new GenericMethods();

    public EditProfileFragment()
    {

    }

    /**
     * This method is called when the upload image fragment is displayed. It creates all of the
     * widgets and functionality that the user can do in the activity.
     *
     * @param savedInstanceState Bundle: If the fragment is being recreated from a previous saved state, this is the state.
     *                           This value may be null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        databaseConnections();
    }

    /**
     * @param inflater           Instantiates a layout XML file into its corresponding view Objects
     * @param container          A view used to contain other views, in this case, the view fragment_edit_profile
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     *                           This value may be null.
     * @return Returns inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        getViewByIds(view);
        getUsersInformationFromDatabase();
        createOnClickListnerSave();
        return view;
    }

    /**
     * Connecting to the FireBase database.
     * Accessing the Users table and keeping it synced as well as getting and
     * storing the users unique id
     */
    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        databaseReferenceUsersTable = databaseConnections.getDatabaseReferenceUsers();
        databaseReferenceUsersTable.keepSynced(true);
        user = databaseConnections.getCurrentUser();
    }

    /**
     * Storing all of the widgets in the layout file to variables in the fragment
     *
     * @param view View: The layout file that is being accessed
     */
    private void getViewByIds(View view)
    {
        fullName = view.findViewById(R.id.editTextFullName);
        phoneNumber = view.findViewById(R.id.editTextPhoneNumber);
        addressOne = view.findViewById(R.id.editTextAddress1);
        addressTwo = view.findViewById(R.id.editTextAddress2);
        town = view.findViewById(R.id.editTextTown);
        postCode = view.findViewById(R.id.editTextPostCode);
        saveProfile = view.findViewById(R.id.buttonSaveProfile);
        checkBoxAdvertiser = view.findViewById(R.id.checkBoxAdvertiser);
        checkBoxCourier = view.findViewById(R.id.checkBoxCourier);
    }

    /**
     * Reading from the FireBase database all of the current users personal information.
     * Then displaying them in the edit text boxes for the user to edit.
     */
    private void getUsersInformationFromDatabase()
    {
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

                fullName.setText(name);
                phoneNumber.setText(number);
                addressOne.setText(address1);
                addressTwo.setText(address2);
                town.setText(usersTown);
                postCode.setText(postalCode);

                genericMethods.checkUserPreference(advertiser, courier, checkBoxAdvertiser, checkBoxCourier);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    /**
     * Onclick operations for Save Profile Button
     * Calls the method to store the users information in the database
     */
    private void createOnClickListnerSave()
    {
        saveProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveUserInformation();
            }
        });
    }

    /**
     * Checks if all of the fields have text in, then creates a new user information object
     * and overrides it in the FireBase database
     */
    private void saveUserInformation()
    {
        // new progress dialog
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Updating Information Please Wait...");
        progressDialog.create();
        progressDialog.show();

        // get all text
        String fullName = this.fullName.getText().toString().trim();
        String phoneNumber = this.phoneNumber.getText().toString().trim();
        String addressOne = this.addressOne.getText().toString().trim();
        String addressTwo = this.addressTwo.getText().toString().trim();
        String town = this.town.getText().toString().trim();
        String postCode = this.postCode.getText().toString().trim().toUpperCase();
        String userEmail = this.userEmail.trim();

        if (TextUtils.isEmpty(fullName))
        {
            genericMethods.customToastMessage("Please Enter Your Phone Number", getActivity());
            return;
        }
        if (TextUtils.isEmpty(phoneNumber))
        {
            genericMethods.customToastMessage("Please Enter A Phone Number", getActivity());
            return;
        }
        if (TextUtils.isEmpty(addressOne))
        {
            genericMethods.customToastMessage("Please Enter Your House Number & Street", getActivity());
            return;
        }
        if (TextUtils.isEmpty(addressTwo))
        {
            genericMethods.customToastMessage("Please Enter Your Second Address Line", getActivity());
            return;
        }
        if (TextUtils.isEmpty(town))
        {
            genericMethods.customToastMessage("Please Enter Your Town", getActivity());
            return;
        }
        if (TextUtils.isEmpty(postCode))
        {
            genericMethods.customToastMessage("Please Enter Your PostCode", getActivity());
            return;
        }

        if (fullName.length() < 4)
        {
            genericMethods.customToastMessage("Your Full Name Must Be Greater Than Four Characters", getActivity());
            return;
        }

        if (phoneNumber.length() != 11)
        {
            genericMethods.customToastMessage("Your Phone Number Must Be 11 Numbers Long", getActivity());
            return;
        }

        if (!postCode.matches("^(?=.*[A-Z])(?=.*[0-9])[A-Z0-9 ]+$"))
        {
            genericMethods.customToastMessage("PostCode Must Have Numbers and Letters", getActivity());
            return;
        }


        // check which boxes have been ticked
        if (checkBoxAdvertiser.isChecked() && !checkBoxCourier.isChecked())
        {
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, true, false, profileImage, userEmail);

            setUserInformation(userInformation);
        } else if (!checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
        {
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, false, true, profileImage, userEmail);

            setUserInformation(userInformation);
        } else if (checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
        {
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, true, true, profileImage, userEmail);

            setUserInformation(userInformation);
        }

        genericMethods.dismissDialog(progressDialog);
        genericMethods.customToastMessage("Information Saved...", getActivity());
    }

    /**
     * Post and send the user information to the database under the user table, under the current
     * users unique id
     *
     * @param userInformation UserInformation: UserInformation object with the user details stored in
     */
    private void setUserInformation(UserInformation userInformation)
    {
        databaseReferenceUsersTable.child(user).setValue(userInformation);
    }

    /**
     * onAttach             onAttach is called when a fragment is first attached to its context
     * onCreate can be called only after the fragment is attached
     *
     * @param context Allows access to application specific resources and classes, also
     *                supports application-level operations such as receiving intents, launching activities
     */
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    /**
     * When the fragment is no longer attached to the activity, set the listener to null
     */
    @Override
    public void onDetach()
    {
        super.onDetach();
    }
}