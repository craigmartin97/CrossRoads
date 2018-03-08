package com.kitkat.crossroads;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kitkat.crossroads.Account.LoginActivity;
import com.kitkat.crossroads.Profile.CreateProfileActivity;
import com.kitkat.crossroads.Profile.UserInformation;
import com.kitkat.crossroads.Profile.ViewProfileFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

    private FirebaseAuth auth;
    private EditText editTextName;
    private EditText editTextPhoneNumber;
    private EditText editTextPostalAddress;
    private TextView textViewDateOfBirth;
    private ImageView profileImage;

    private static final int GALLERY_INTENT = 2;

    private Button buttonSaveProfile;
    private Button buttonUploadImage;

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
        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        editTextName = (EditText) view.findViewById(R.id.editTextName);
        editTextPhoneNumber = (EditText) view.findViewById(R.id.editTextPhoneNumber);
        editTextPostalAddress = (EditText) view.findViewById(R.id.editTextPostalAddress);
        textViewDateOfBirth = (TextView) view.findViewById(R.id.textViewDateOfBirth);

        buttonUploadImage = (Button) view.findViewById(R.id.buttonUploadImage);
        buttonSaveProfile = (Button) view.findViewById(R.id.buttonSaveProfile);

        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);



        ImageView profileImage = (ImageView) headerView.findViewById(R.id.navigationImage);

        this.profileImage = profileImage;

        progressDialog = new ProgressDialog(getActivity());

        buttonUploadImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });

        buttonSaveProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveUserInformation();
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
        String name = editTextName.getText().toString().trim();
        String address = editTextPostalAddress.getText().toString().trim();
        String dateOfBirth = textViewDateOfBirth.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();

        UserInformation userInformation = new UserInformation(name, address, dateOfBirth, phoneNumber);

        FirebaseUser user = auth.getCurrentUser();

        myRef.child("users").child(user.getUid()).setValue(userInformation);


        Toast.makeText(getActivity(), "Information Saved...", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(getActivity(), ViewProfileFragment.class));
    }
}
