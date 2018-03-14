package com.kitkat.crossroads;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.squareup.picasso.Picasso;
import java.io.IOException;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadImageFragment extends Fragment
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
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private StorageReference storageReference;
    private FirebaseUser user;

    private ImageView profileImage;
    private Uri imageUri;

    private static final int GALLERY_INTENT = 2;

    private ProgressDialog progressDialog;

    public UploadImageFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadImageFragment newInstance(String param1, String param2)
    {
        UploadImageFragment fragment = new UploadImageFragment();
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

        View view = inflater.inflate(R.layout.fragment_upload_image, container, false);

        // Creating firebase links etc
        database = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = auth.getCurrentUser();

        // Setting buttons
        profileImage = (ImageView) view.findViewById(R.id.imageViewProfileImage);
        ImageView rotateLeft = (ImageView) view.findViewById(R.id.imageRotateLeft);
        ImageView rotateRight = (ImageView) view.findViewById(R.id.imageRotateRight);
        Button uploadProfileImage = (Button) view.findViewById(R.id.buttonUploadProfileImage);
        Button saveProfileImage = (Button) view.findViewById(R.id.buttonSaveProfileImage);

        // Rotate Left is a button
        rotateLeft.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(profileImage != null)
                {
                    // Rotate image 90
                    Picasso.get().load(imageUri).rotate(90).into(profileImage);
                }
            }
        });

        // Rotate Right is a button
        rotateRight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(profileImage != null)
                {
                    // Rotate image -90
                    Picasso.get().load(imageUri).rotate(-90).into(profileImage);
                }
            }
        });

        uploadProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Send user to gallery
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        // Save image to storage area
        saveProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                progressDialog.setMessage("Uploading Image Please Wait...");
                progressDialog.show();

                final StorageReference filePath = storageReference.child("Images").child(user.getUid()).child(imageUri.getLastPathSegment());
                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        Toast.makeText(getActivity(), "Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                        Uri downloadUri = taskSnapshot.getDownloadUrl();

                        // Save image uri in the Firebase database under the usersID
                        myRef.child("Users").child(user.getUid()).child("profileImage").setValue(downloadUri.toString());
                        progressDialog.dismiss();
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
        });

        return view;
    }

    // Get image data and display on page
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Displaying Image...");
            progressDialog.show();

            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profileImage);

            progressDialog.dismiss();
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
}
