package com.kitkat.crossroads;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kitkat.crossroads.Profile.UserInformation;
import com.kitkat.crossroads.Profile.ViewProfileFragment;
import java.io.IOException;
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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private FirebaseAuth auth;
    private EditText fullName, phoneNumber, addressOne, addressTwo, town, postCode;
    private CheckBox checkBoxAdvertiser, checkBoxCourier;
    private boolean advertiser, courier;
    private Button saveProfile, uploadProfileImage;
    private ImageView profileImage;

    private static final int GALLERY_INTENT = 2;
    private ProgressDialog progressDialog;

    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private StorageReference storageReference;


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

//        textViewDateOfBirth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar calendar = Calendar.getInstance();
//                int year = calendar.get(Calendar.YEAR);
//                int month = calendar.get(Calendar.MONTH);
//                int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//                DatePickerDialog dialog = new DatePickerDialog(
//                        getActivity(),
//                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
//                        dateSetListener,
//                        year,month,day);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.show();
//            }
//        });
//
//        dateSetListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//
//                month = month + 1;
//                Log.d(TAG, "onDateSet: date: " + year + "/" + month + "/" + dayOfMonth);
//
//                if(dayOfMonth >= 1 && dayOfMonth <= 9)
//                {
//                    String newDay = "0" + dayOfMonth;
//                    textViewDateOfBirth.setText(newDay + "/" + month + "/" + year);
//                }
//
//                if(month >= 1 && month <= 9)
//                {
//                    String newMonth = "0" + month;
//                    textViewDateOfBirth.setText(dayOfMonth + "/" + newMonth + "/" + year);
//                }
//
//                if(dayOfMonth >= 1 && dayOfMonth <= 9 && month >= 1 && month <= 9)
//                {
//                    String newDay = "0" + dayOfMonth;
//                    String newMonth = "0" + month;
//                    textViewDateOfBirth.setText(newDay + "/" + newMonth + "/" + year);
//                }
//                else
//                {
//                    textViewDateOfBirth.setText(dayOfMonth + "/" + month + "/" + year);
//                }
//            }
//        };

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
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        FirebaseUser user = auth.getCurrentUser();

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            progressDialog.setMessage("Uploading Image Please Wait...");
            progressDialog.show();

            final Uri uri = data.getData();
            final StorageReference filePath = storageReference.child("Images").child(user.getUid()).child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    progressDialog.dismiss();
                    try
                    {
                        Toast.makeText(getActivity(), "Uploaded Successfully!", Toast.LENGTH_SHORT).show();

                        //////////////////NAV//////////////////

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        Bitmap bitmap2 = bitmap.createScaledBitmap(bitmap, 200,200,true);
                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap2);
                        roundedBitmapDrawable.setCircular(true);
                        //profileImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 250,250,true));
                        profileImage.setImageDrawable(roundedBitmapDrawable);


                        ////////////////////////////////////////

                        ViewProfileFragment viewProfileFragment = new ViewProfileFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("ProfileImage",bitmap);
                        viewProfileFragment.setArguments(bundle);
                        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content, viewProfileFragment).commit();

                    } catch(IOException e)
                    {
                        Toast.makeText(getActivity(), "Unexpected Error Has Occurred", Toast.LENGTH_SHORT).show();
                    }
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
        String postCode = this.postCode.getText().toString().trim();

        if (checkBoxAdvertiser.isChecked() && !checkBoxCourier.isChecked())
        {
            advertiser = true;
            courier = false;
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, advertiser, courier);

            setUserInformation(userInformation);
        }
        else if (!checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
        {
            advertiser = false;
            courier = true;
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, advertiser, courier);

            setUserInformation(userInformation);
        }
        else if (checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
        {
            advertiser = true;
            courier = true;
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, advertiser, courier);

            setUserInformation(userInformation);
        }

        Toast.makeText(getActivity(), "Information Saved...", Toast.LENGTH_SHORT).show();

        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, new ViewProfileFragment()).commit();
    }

    private void setUserInformation(UserInformation userInformation)
    {
        FirebaseUser user = auth.getCurrentUser();
        myRef.child("Users").child(user.getUid()).setValue(userInformation);
    }
}
