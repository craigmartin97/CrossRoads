package com.kitkat.crossroads;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.ExifInterfaceImageRotate;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class UploadImageFragment extends Fragment
{
    private OnFragmentInteractionListener mListener;

    private DatabaseReference databaseReferenceUsersTable;
    private StorageReference storageReference;
    private String user;

    private static ImageView profileImage;
    private Uri imageUri;
    private static byte[] compressData;

    private static final int GALLERY_INTENT = 2;
    private final static int REQUEST_CODE = 400;


    private ProgressDialog progressDialog;

    private ProgressBar progressBar;

    public UploadImageFragment()
    {
        // Required empty public constructor
    }

    public static UploadImageFragment newInstance()
    {
        UploadImageFragment fragment = new UploadImageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        databaseConnections();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_upload_image, container, false);

        // Setting buttons
        profileImage = (ImageView) view.findViewById(R.id.imageViewProfileImage);
        Button uploadProfileImage = (Button) view.findViewById(R.id.buttonUploadProfileImage);
        Button saveProfileImage = (Button) view.findViewById(R.id.buttonSaveProfileImage);
        progressBar = view.findViewById(R.id.progressBar);

        databaseReferenceUsersTable.child(user).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                final String profileImageURL = dataSnapshot.child("profileImage").getValue(String.class);
                Picasso.get().load(profileImageURL).into(profileImage, new Callback()
                {
                    @Override
                    public void onSuccess()
                    {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e)
                    {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        uploadProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(verifyPermissions()) {

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_INTENT);
                }
                else
                {
                    Toast.makeText(getContext(), "Permissions Denied", Toast.LENGTH_SHORT).show();
                    verifyPermissions();
                }
            }
        });

        saveProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (imageUri != null)
                {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Uploading Image Please Wait...");
                    progressDialog.show();

                    final StorageReference filePath = storageReference.child("Images").child(user).child(imageUri.getLastPathSegment());
                    filePath.putBytes(compressData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            Toast.makeText(getActivity(), "Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            databaseReferenceUsersTable.child(user).child("profileImage").setValue(downloadUri.toString());
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
                } else
                {
                    Toast.makeText(getActivity(), "Can't Upload Same Image", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        return view;
    }

    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        storageReference = databaseConnections.getStorageReference();
        databaseReferenceUsersTable = databaseConnections.getDatabaseReferenceUsers();
        databaseReferenceUsersTable.keepSynced(true);
        user = databaseConnections.getCurrentUser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Redirect user to there gallery and get them to select an image
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Displaying Image...");
            progressDialog.show();

            imageUri = data.getData();
            final Uri uri = data.getData();

            try
            {
                ExifInterfaceImageRotate exifInterfaceImageRotate = new ExifInterfaceImageRotate();
                profileImage.setImageBitmap(exifInterfaceImageRotate.setUpImageTransfer(uri, getActivity().getContentResolver()));
                profileImage.buildDrawingCache();
                profileImage.getDrawingCache();
                Bitmap bitmap = profileImage.getDrawingCache();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                compressData = byteArrayOutputStream.toByteArray();
                progressDialog.dismiss();
            } catch (Exception e)
            {
                Log.e("Error Uploading Image: ", e.getMessage());
            }
        }
        else
        {
            Toast.makeText(getContext(), "Permissions Denied", Toast.LENGTH_SHORT).show();
            verifyPermissions();
        }
    }


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

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }

    private boolean verifyPermissions()
    {
        Log.d(TAG, "Verifying user Phone permissions");
        String[] phonePermissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if(ContextCompat.checkSelfPermission(getContext(), phonePermissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), phonePermissions[1]) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), phonePermissions, REQUEST_CODE);
            return false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] phonePermissions, @NonNull int[] grantResults)
    {
        verifyPermissions();
    }
}
