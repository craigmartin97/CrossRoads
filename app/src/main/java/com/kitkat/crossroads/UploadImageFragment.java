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
import android.support.v4.app.FragmentTransaction;
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
import com.kitkat.crossroads.ExternalClasses.GenericMethods;
import com.kitkat.crossroads.Profile.CreateProfileActivity;
import com.kitkat.crossroads.Profile.ViewProfileFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

/**
 * UploadImageFragment allows the user to upload a new profile image.
 * The user must select their current profile image in the navigation header to
 * make the change. The users image will then be stored in the FireBase storage area
 * and the URL stored under the users account information in FireBase database
 */
public class UploadImageFragment extends Fragment
{

    /**
     * Used to connection to the FireBase database, users table
     */
    private DatabaseReference databaseReferenceUsersTable;

    /**
     * Connecting to FireBase Storage area
     */
    private StorageReference storageReference;

    /**
     * Storing the current user unique id
     */
    private String user;

    /**
     * Widget element to store and hold the users profile image
     */
    private ImageView profileImage;

    /**
     * Uri address of an image that has been uploaded
     * from the users phone
     */
    private Uri imageUri;

    /**
     * used in conjunction with putBytes which returns an UploadTask where we can monitor whether or not the upload was successful
     * CompressData is used to store the data from the image that has been compressed smaller
     */
    private static byte[] compressData;

    /**
     * code for Gallery Intent, compared with requestCode
     */
    private static final int GALLERY_INTENT = 2;

    /**
     * request code for phone permissions
     */
    private final static int REQUEST_CODE = 400;

    /**
     * progress dialog used to notify users that image is uploading
     */
    private ProgressDialog progressDialog;

    /**
     * progress bar for image uploads, used to let user know image is loading
     */
    private ProgressBar progressBar;

    /**
     * Buttons used to upload a new image and save that image upon
     * users request
     */
    private Button saveProfileImage, uploadProfileImage;

    /**
     * Accessing methods in the generic methods class
     */
    private GenericMethods genericMethods = new GenericMethods();

    public UploadImageFragment()
    {
        // Required empty public constructor
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
     * @param inflater           Instantiates a layout XML file into its corresponding view Objects
     * @param container          A view used to contain other views, in this case, the view fragment_upload_image
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     *                           This value may be null.
     * @return Returns inflated view
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_upload_image, container, false);
        getViewsByIds(view);

        // Getting the users current profile image from the database
        databaseReferenceUsersTable.child(user).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // Display the image
                final String profileImageURL = dataSnapshot.child(getString(R.string.profile_image_table)).getValue(String.class);
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
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    createGalleryIntent();
                } else
                {
                    requestStoragePermission();
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
                    progressDialog = new ProgressDialog(getActivity(), R.style.datepicker);
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
                            genericMethods.customToastMessage("Uploaded Successfully", getActivity());
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            assert downloadUri != null;
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
                            genericMethods.customToastMessage("Failed To Upload", getActivity());
                        }
                    });
                } else
                {
                    genericMethods.customToastMessage("Can't Upload The Same Image", getActivity());
                }
            }
        });

        return view;
    }

    /**
     * Establishes connections to the FireBase database
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
     * Creates the gallery intent, this is called if a user has accepted permissions
     */
    private void createGalleryIntent()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    /**
     * Setting all of the widgets in the layout
     * file to variables in the fragment
     */
    private void getViewsByIds(View view)
    {
        profileImage = view.findViewById(R.id.imageViewProfileImage);
        uploadProfileImage = view.findViewById(R.id.buttonUploadProfileImage);
        saveProfileImage = view.findViewById(R.id.buttonSaveProfileImage);
        progressBar = view.findViewById(R.id.progressBar);
    }

    /**
     * @param requestCode The request code passed to startActivityForResult(...)
     * @param resultCode  The result code, either RESULT_OK or RESULT_CANCELED
     * @param data        An intent that carries data, in this case its used to get the image Uri
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
        } else
        {
            //permissions were denied, so prompt the user again
            customToastDenied();
        }
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

    /**
     * Prompts the user to accept or deny out-of-app permissions (External Storage/Gallery)
     */
    private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    /**
     * @param requestCode  The request code passed in
     * @param permissions  An array which stores the requested permissions (can never be null)
     * @param grantResults The results of the corresponding permissions, either PERMISSION_GRANTED or PERMISSION_DENIED
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                createGalleryIntent();
            } else
            {
                customToastDenied();
            }
        }
    }

    private void customToastDenied()
    {
        genericMethods.customToastMessage("Permission Denied", getActivity());
    }
}
