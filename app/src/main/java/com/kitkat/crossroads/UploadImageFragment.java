package com.kitkat.crossroads;

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

    //TODO - fragment listeners
    private OnFragmentInteractionListener mListener;

    /**
     * used in Firebase database connections
     */
    private DatabaseReference databaseReferenceUsersTable;
    private StorageReference storageReference;
    private String user;

    private static ImageView profileImage;
    //address of an image
    private Uri imageUri;

    //used in conjunction with putBytes which returns an UploadTask where we can monitor whether or not the upload was successful
    private static byte[] compressData;

    //code for Gallery Intent, compared with requestCode
    private static final int GALLERY_INTENT = 2;

    //request code for phone permissions
    private final static int REQUEST_CODE = 400;

    //progress dialog used to notify users of image upload status
    private ProgressDialog progressDialog;

    //progress bar for image uploads
    private ProgressBar progressBar;

    public UploadImageFragment()
    {
        // Required empty public constructor
    }

    /**
     * TODO - 'unused' method?
     * @return      returns fragment
     */
    public static UploadImageFragment newInstance()
    {
        UploadImageFragment fragment = new UploadImageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * This method is called when the upload image fragment is displayed. It creates all of the
     * widgets and functionality that the user can do in the activity.
     *
     * @param savedInstanceState -If the fragment is being recreated from a previous saved state, this is the state.
     *                           This value may be null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        databaseConnections();
    }

    /**
     * @param inflater              Instantiates a layout XML file into its corresponding view Objects
     * @param container             A view used to contain other views, in this case, the view fragment_upload_image
     * @param savedInstanceState    If the fragment is being re-created from a previous saved state, this is the state.
     *                              This value may be null.
     * @return                      Returns inflated view
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_upload_image, container, false);

        //Set widgets in the inflated view to variables within this class
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

        //set onClick operations for the Upload Profile Image button
        uploadProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //ensure the permissions for out of app functions have been granted
                if(verifyPermissions()) {

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_INTENT);
                }
                else
                {
                    //prompt the user for permissions
                    Toast.makeText(getContext(), "Permissions Denied", Toast.LENGTH_SHORT).show();
                    verifyPermissions();
                }
            }
        });

        //set onClick operations for the Save Profile Image button
        saveProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //check the image Uri is valid
                if (imageUri != null)
                {
                    //notify user upload is in progress
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Uploading Image Please Wait...");
                    progressDialog.show();

                    //get location of image Uri and set onSuccess/Failure listeners
                    final StorageReference filePath = storageReference.child("Images").child(user).child(imageUri.getLastPathSegment());
                    filePath.putBytes(compressData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            //notify user upload was successful
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
                            //notify user upload failed
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

    /**
     * Establishes connections to the firebase database
     */
    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        storageReference = databaseConnections.getStorageReference();
        databaseReferenceUsersTable = databaseConnections.getDatabaseReferenceUsers();
        databaseReferenceUsersTable.keepSynced(true);
        user = databaseConnections.getCurrentUser();
    }

    /**
     *
     * @param requestCode       The request code passed to startActivityForResult(...)
     * @param resultCode        The result code, either RESULT_OK or RESULT_CANCELED
     * @param data              An intent that carries data, in this case its used to get the image Uri
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Redirect user to their gallery and get them to select an image
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            //notify user of upload status
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Displaying Image...");
            progressDialog.show();

            imageUri = data.getData();
            final Uri uri = data.getData();

            try
            {
                //ExifInterfaceImageRotate ensures the image selected is uploaded in the correct orientation
                ExifInterfaceImageRotate exifInterfaceImageRotate = new ExifInterfaceImageRotate();
                profileImage.setImageBitmap(exifInterfaceImageRotate.setUpImageTransfer(uri, getActivity().getContentResolver()));
                profileImage.buildDrawingCache();
                profileImage.getDrawingCache();
                Bitmap bitmap = profileImage.getDrawingCache();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //compress data to make image upload more efficient
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
            //permissions were denied, so prompt the user again
            Toast.makeText(getContext(), "Permissions Denied", Toast.LENGTH_SHORT).show();
            verifyPermissions();
        }
    }

    /**
     *
     * TODO - 'unused' method?
     */
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }


    /**onAttach             onAttach is called when a fragment is first attached to its context
     *                      onCreate can be called only after the fragment is attached
     *
     * @param context       Allows access to application specific resources and classes, also
     *                      supports application-level operations such as receiving intents, launching activities
     */
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

    /**
     * When the fragment is no longer attached to the activity, set the listener to null
     */
    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     *TODO
     */
    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }

    /**
     *Verify the user has given the app permissions to use out of app functions
     *
     * @return - returns true if permissions have been allowed
     */
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

    /**
     * @param requestCode           The request code passed in requestPermissions(...)
     * @param phonePermissions      An array which stores the requested permissions (can never be null)
     * @param grantResults          The results of the corresponding permissions, either PERMISSION_GRANTED or PERMISSION_DENIED
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] phonePermissions, @NonNull int[] grantResults)
    {
        verifyPermissions();
    }
}
