package com.kitkat.crossroads.Profile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kitkat.crossroads.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "EditProfileActivity";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private DatePickerDialog.OnDateSetListener dateSetListener;


    private EditText fullName, phoneNumber, addressOne, addressTwo, town, postCode;
    private CheckBox checkBoxAdvertiser, checkBoxCourier;
    private boolean advertiser, courier;
    private Button saveProfile, uploadProfileImage;
    private ImageView profileImage;

    private static final int GALLERY_INTENT = 2;
    private ProgressDialog progressDialog;

    private FirebaseAuth auth;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private StorageReference storageReference;
    private StorageReference filePath;

    public EditProfileFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2)
    {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        database = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseUser user = auth.getCurrentUser();

        saveProfile = (Button) view.findViewById(R.id.buttonSaveProfile);

        fullName = (EditText) view.findViewById(R.id.editTextFullName);
        phoneNumber = (EditText) view.findViewById(R.id.editTextPhoneNumber);
        addressOne = (EditText) view.findViewById(R.id.editTextAddress1);
        addressTwo = (EditText) view.findViewById(R.id.editTextAddress2);
        town = (EditText) view.findViewById(R.id.editTextTown);
        postCode = (EditText) view.findViewById(R.id.editTextPostCode);
        checkBoxAdvertiser = (CheckBox) view.findViewById(R.id.checkBoxAdvertiser);
        checkBoxCourier = (CheckBox) view.findViewById(R.id.checkBoxCourier);

        myRef.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener()
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
                String profileImage = dataSnapshot.child("profileImage").getValue(String.class);
                boolean advertiser = dataSnapshot.child("advertiser").getValue(boolean.class);
                boolean courier = dataSnapshot.child("courier").getValue(boolean.class);

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

                if (advertiser == true && courier == false)
                {
                    checkBoxAdvertiser.setChecked(true);
                    checkBoxCourier.setChecked(false);
                } else if (advertiser == false && courier == true)
                {
                    checkBoxAdvertiser.setChecked(false);
                    checkBoxCourier.setChecked(true);
                } else if (advertiser == true && courier == true)
                {
                    checkBoxAdvertiser.setChecked(true);
                    checkBoxCourier.setChecked(true);
                }
                //  Picasso.get().load(profileImage).rotate(90).resize(350,350).transform(new CircleTransformation()).into(profileImageUri);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        saveProfile = (Button) view.findViewById(R.id.buttonSaveProfile);
        uploadProfileImage = (Button) view.findViewById(R.id.buttonUploadImage);

        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        ImageView profileImage = (ImageView) headerView.findViewById(R.id.navigationImage);

        this.profileImage = profileImage;

        progressDialog = new ProgressDialog(getActivity());

        saveProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveUserInformation();
            }
        });

        uploadProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // intent to gallery area
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        final FirebaseUser user = auth.getCurrentUser();

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {

            progressDialog.setMessage("Uploading Image Please Wait...");
            progressDialog.show();

            final Uri uri = data.getData();

            final StorageReference filePath = storageReference.child("Images").child(user.getUid()).child(uri.getLastPathSegment());
            this.filePath = filePath;

            // Put the file in the Firebase Storage Area
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    // Saving the URL under the "Users" table, under the "Users ID" In the Firebase Database to later retrieve it
                    myRef.child("Users").child(user.getUid()).child("profileImage").setValue(downloadUri.toString());
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Failed To Upload!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
                    addressTwo, town, postCode, advertiser, courier, filePath);

            setUserInformation(userInformation);
        } else if (!checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
        {
            advertiser = false;
            courier = true;
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, advertiser, courier, filePath);

            setUserInformation(userInformation);
        } else if (checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
        {
            advertiser = true;
            courier = true;
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, advertiser, courier, filePath);

            setUserInformation(userInformation);
        }

        customToastMessage("Information Saved...");

        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, new ViewProfileFragment()).addToBackStack("tag").commit();
    }

    private void setUserInformation(UserInformation userInformation)
    {
        FirebaseUser user = auth.getCurrentUser();
        myRef.child("Users").child(user.getUid()).setValue(userInformation);
    }

    private void customToastMessage(String message)
    {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}