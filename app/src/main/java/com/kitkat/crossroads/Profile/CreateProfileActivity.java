package com.kitkat.crossroads.Profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.kitkat.crossroads.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.felipecsl.gifimageview.library.GifHeaderParser.TAG;

public class CreateProfileActivity extends AppCompatActivity
{
    private EditText fullName, phoneNumber, addressOne, addressTwo, town, postCode;
    private CheckBox checkBoxAdvertiser, checkBoxCourier;
    private boolean advertiser, courier;
    private Button saveProfile, uploadProfileImage;
    private static ImageView profileImage;

    private FirebaseAuth auth;
    private DatabaseReference myRef;
    private StorageReference storageReference;
    private String user, userEmail;

    private DatabaseConnections databaseConnections = new DatabaseConnections();

    private static final int REQUEST_CODE = 200;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog progressDialog;
    private Uri imageUri;
    private static byte[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        getViewByIds();
        setDatabaseConnections();

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
                if(verifyPermissions()) {

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_INTENT);

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Permissions Denied", Toast.LENGTH_SHORT).show();
                    verifyPermissions();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Displaying Image...");
            progressDialog.show();

            imageUri = data.getData();
            final Uri uri = data.getData();
            setUpImageTransfer(uri);
        }
    }

    public void setUpImageTransfer(Uri uri)
    {
        dismissDialog();
        try
        {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);
            modifyOrientation(bitmap, inputStream);
        } catch (IOException e)
        {
            e.getStackTrace();
        }
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, InputStream image_absolute_path) throws IOException
    {
        android.support.media.ExifInterface exifInterface = new android.support.media.ExifInterface(image_absolute_path);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);
            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        profileImage.setImageBitmap(bitmap1);

        profileImage.buildDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        data = byteArrayOutputStream.toByteArray();
        return bitmap1;
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical)
    {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void saveUserInformation()
    {
        String fullName = this.fullName.getText().toString().trim();
        String phoneNumber = this.phoneNumber.getText().toString().trim();
        String addressOne = this.addressOne.getText().toString().trim();
        String addressTwo = this.addressTwo.getText().toString().trim();
        String town = this.town.getText().toString().trim();
        String postCode = this.postCode.getText().toString().trim().toUpperCase();
        String userEmail = this.userEmail.toString().trim();

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
                    addressTwo, town, postCode, advertiser, courier, userEmail, null);

            setUserInformation(userInformation);
        } else if (!checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
        {
            advertiser = false;
            courier = true;
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, advertiser, courier, userEmail, null);

            setUserInformation(userInformation);
        } else if (checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
        {
            advertiser = true;
            courier = true;
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, advertiser, courier, userEmail, null);

            setUserInformation(userInformation);
        }

        uploadUsersProfileImage();
        databaseVerification();
        startActivity(new Intent(CreateProfileActivity.this, LoginActivity.class));
    }

    private void setUserInformation(UserInformation userInformation)
    {
        FirebaseUser user = auth.getCurrentUser();
        myRef.child("Users").child(user.getUid()).setValue(userInformation);
    }

    private void databaseVerification()
    {
        FirebaseUser userEmail = FirebaseAuth.getInstance().getCurrentUser();
        userEmail.sendEmailVerification();
        FirebaseAuth.getInstance().signOut();
    }

    private void getViewByIds()
    {
        profileImage = (ImageView) findViewById(R.id.profileImage);
        uploadProfileImage = (Button) findViewById(R.id.buttonUploadProfileImage);
        saveProfile = (Button) findViewById(R.id.buttonSaveProfile);
        fullName = (EditText) findViewById(R.id.editTextFullName);
        phoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        addressOne = (EditText) findViewById(R.id.editTextAddress1);
        addressTwo = (EditText) findViewById(R.id.editTextAddress2);
        town = (EditText) findViewById(R.id.editTextTown);
        postCode = (EditText) findViewById(R.id.editTextPostCode);
        checkBoxAdvertiser = (CheckBox) findViewById(R.id.checkBoxAdvertiser);
        checkBoxCourier = (CheckBox) findViewById(R.id.checkBoxCourier);
    }

    private void setDatabaseConnections()
    {
        auth = databaseConnections.getAuth();
        myRef = databaseConnections.getDatabaseReference();
        user = databaseConnections.getCurrentUser();
        storageReference = databaseConnections.getStorageReference();

        if (auth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void uploadUsersProfileImage()
    {
        final StorageReference filePath = storageReference.child("Images").child(user).child(imageUri.getLastPathSegment());
        filePath.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                customToastMessage("Profile Image Uploaded Successfully");
                Uri downloadUri = taskSnapshot.getDownloadUrl();
                myRef.child("Users").child(user).child("profileImage").setValue(downloadUri.toString());
                dismissDialog();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                dismissDialog();
                customToastMessage("Failed To Upload Image, Try Again");
            }
        });
    }

    private void customToastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void dismissDialog()
    {
        progressDialog.dismiss();
    }

    private boolean verifyPermissions()
    {
        Log.d(TAG, "Verifying user Phone permissions");
        String[] phonePermissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if(ContextCompat.checkSelfPermission(this, phonePermissions[0]) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, phonePermissions[1]) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            ActivityCompat.requestPermissions(this, phonePermissions, REQUEST_CODE);
            return false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] phonePermissions, @NonNull int[] grantResults)
    {
        verifyPermissions();
    }

}