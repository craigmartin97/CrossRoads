package com.kitkat.crossroads.Profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kitkat.crossroads.Account.LoginActivity;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.ExifInterfaceImageRotate;
import com.kitkat.crossroads.R;

import java.io.ByteArrayOutputStream;

/**
 * Class is used to create a new user profile. After the user has registered for an account
 * they are displayed with the create profile activity.
 * The user inputs all of their data such as their name, phone number etc and they can upload a profile
 * picture as well if they choose. Otherwise a default profile outline will be used
 */
public class CreateProfileActivity extends AppCompatActivity
{
    /**
     * Widgets used in the layout file
     */
    private EditText fullName, phoneNumber, addressOne, addressTwo, town, postCode;
    private CheckBox checkBoxAdvertiser, checkBoxCourier;
    private Button saveProfile, uploadProfileImage;
    private ImageView profileImage;

    /**
     * Storing the current authenticated user
     */
    private FirebaseAuth auth;

    /**
     * Storing connection to the FireBase database
     */
    private DatabaseReference databaseReference;

    /**
     * Storing connection to the FireBase Storage area
     */
    private StorageReference storageReference;

    /**
     * Storing the current users unique id
     */
    private String user;

    /**
     * Accessing methods from DatabaseConnections class to access the database connections
     */
    private final DatabaseConnections databaseConnections = new DatabaseConnections();

    /**
     * Request code to check it was the gallery selected
     */
    private static final int REQUEST_CODE = 200;

    /**
     * Code to move to the gallery intent
     */
    private static final int GALLERY_INTENT = 2;

    /**
     * Creating a new progress dialog, to indicate the progress of the submission
     */
    private ProgressDialog progressDialog;

    /**
     * Storing the imageUri, which is the image selected
     */
    private Uri imageUri;

    /**
     * Storing the compressed version of the image to push to the FireBase storage area
     */
    private static byte[] compressData;


    /**
     * This method is called when CreateProfile is displayed. It creates all of the
     * widgets and functionality that the user can do in the activity.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        getViewByIds();
        databaseConnections();

        //When pressed saved users profile
        saveProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveUserInformation();
            }
        });

        // Sends user to gallery after accepting permissions
        uploadProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ContextCompat.checkSelfPermission(CreateProfileActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    createGalleryIntent();
                } else
                {
                    requestStoragePermission();
                }
            }
        });
    }

    /**
     * @param requestCode The request code passed to startActivityForResult, in this case GALLERY_INTENT
     * @param resultCode  The result code, either RESULT_OK or RESULT_CANCELED
     * @param data        An intent that carries data, in this case its used to get the image Uri
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // If request code is the gallery and request is ok
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Displaying Image...");
            progressDialog.show();

            imageUri = data.getData();
            final Uri uri = data.getData();

            // Rotating the image, and displaying
            ExifInterfaceImageRotate exifInterfaceImageRotate = new ExifInterfaceImageRotate();
            profileImage.setImageBitmap(exifInterfaceImageRotate.setUpImageTransfer(uri, getContentResolver()));
            profileImage.buildDrawingCache();
            profileImage.getDrawingCache();
            Bitmap bitmap = profileImage.getDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            compressData = byteArrayOutputStream.toByteArray();
            progressDialog.dismiss();
        }
    }

    /**
     * Validates user entry and sets userInfo variables to the data entered by the user
     */
    private void saveUserInformation()
    {
        String fullName = this.fullName.getText().toString().trim();
        String phoneNumber = this.phoneNumber.getText().toString().trim();
        String addressOne = this.addressOne.getText().toString().trim();
        String addressTwo = this.addressTwo.getText().toString().trim();
        String town = this.town.getText().toString().trim();
        String postCode = this.postCode.getText().toString().trim().toUpperCase();
        String userEmail = auth.getCurrentUser().getEmail();

        // all checks below to see if user information is correct
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

        if (profileImage.getDrawable() == null)
        {
            customToastMessage("You Must Upload A Profile Image");
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

        // check is actually postcode
        if (!postCode.matches("^(?=.*[A-Z])(?=.*[0-9])[A-Z0-9 ]+$"))
        {
            customToastMessage("Post Code Must Have Numbers and Letters");
            return;
        }

        if (imageUri != null)
        {
            preferenceCheck(fullName, phoneNumber, addressOne, addressTwo, town, postCode, userEmail);
            databaseVerification();
            startActivityToLogin();
        } else

        {
            preferenceCheck(fullName, phoneNumber, addressOne, addressTwo, town, postCode, userEmail);
            databaseVerification();
            startActivityToLogin();
        }
    }

    /**
     * Send account verification email to user
     */
    private void databaseVerification()
    {
        FirebaseUser userEmail = FirebaseAuth.getInstance().getCurrentUser();
        assert userEmail != null;
        userEmail.sendEmailVerification();
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Set widgets in the inflated view to variables within this class
     */
    private void getViewByIds()
    {
        profileImage = findViewById(R.id.profileImage);
        uploadProfileImage = findViewById(R.id.buttonUploadProfileImage);
        saveProfile = findViewById(R.id.buttonSaveProfile);
        fullName = findViewById(R.id.editTextFullName);
        phoneNumber = findViewById(R.id.editTextPhoneNumber);
        addressOne = findViewById(R.id.editTextAddress1);
        addressTwo = findViewById(R.id.editTextAddress2);
        town = findViewById(R.id.editTextTown);
        postCode = findViewById(R.id.editTextPostCode);
        checkBoxAdvertiser = findViewById(R.id.checkBoxAdvertiser);
        checkBoxCourier = findViewById(R.id.checkBoxCourier);
    }

    /**
     * Establishes connections to the FireBase database
     */
    private void databaseConnections()
    {
        auth = databaseConnections.getAuth();
        databaseReference = databaseConnections.getDatabaseReference();
        user = databaseConnections.getCurrentUser();
        storageReference = databaseConnections.getStorageReference();
    }

    /**
     * @param userInformation instance of UserInformation with all the profile data ready to upload
     */
    private void uploadUsersProfile(final UserInformation userInformation)
    {
        if (imageUri != null)
        {
            final StorageReference filePath = storageReference.child("Images").child(user).child(imageUri.getLastPathSegment());
            filePath.putBytes(compressData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    customToastMessage("Profile Image Uploaded Successfully");
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    userInformation.setProfileImage(downloadUri.toString());
                    databaseReference.child("Users").child(user).setValue(userInformation);
                    dismissDialog();
                }
            }).addOnFailureListener(new OnFailureListener()

            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    System.out.println(e.getMessage());
                    dismissDialog();
                    customToastMessage("Failed To Upload Image, Try Again");
                }
            });
        } else
        {
            String imageUrl = "https://firebasestorage.googleapis.com/v0/b/crossroads-b1198.appspot.com/o/default_image.jpg?alt=media&token=4f5aff1d-ed72-4c18-80a7-4da71982730b";
            userInformation.setProfileImage(imageUrl);
            databaseReference.child("Users").child(user).setValue(userInformation);
        }
    }

    /**
     * Allows us to display custom messages to the user
     *
     * @param message String value of toast message
     */
    private void customToastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * dismisses dialog
     */
    private void dismissDialog()
    {
        progressDialog.dismiss();
    }

    /**
     * Builds gallery intent to send user to the gallery of phone
     */
    private void createGalleryIntent()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    /**
     * Requests permission for external storage
     */
    private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    /**
     * @param requestCode  The request code passed in requestPermissions
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
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Changes startup pages based on whether user is predominantly an Advertiser or a Courier.
     * <p>
     * The following are values that will be saved in the database as the user's Profile Data:
     *
     * @param fullName
     * @param phoneNumber
     * @param addressOne
     * @param addressTwo
     * @param town
     * @param postCode
     * @param userEmail
     */
    private void preferenceCheck(String fullName, String phoneNumber, String addressOne, String addressTwo, String town, String postCode, String userEmail)
    {
        if (checkBoxAdvertiser.isChecked() && !checkBoxCourier.isChecked())
        {
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, true, false, null, userEmail);

            uploadUsersProfile(userInformation);
        } else if (!checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
        {
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, false, true, null, userEmail);

            uploadUsersProfile(userInformation);
        } else if (checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
        {
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, true, true, null, userEmail);

            uploadUsersProfile(userInformation);
        }
    }

    /**
     * Start a login activity
     */
    private void startActivityToLogin()
    {
        startActivity(new Intent(CreateProfileActivity.this, LoginActivity.class));
    }

}