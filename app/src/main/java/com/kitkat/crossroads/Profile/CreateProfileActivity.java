package com.kitkat.crossroads.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
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

public class CreateProfileActivity extends AppCompatActivity
{
    private EditText fullName, phoneNumber, addressOne, addressTwo, town, postCode;
    private CheckBox checkBoxAdvertiser, checkBoxCourier;
    private Button saveProfile, uploadProfileImage;
    private ImageView profileImage;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String user;

    private final DatabaseConnections databaseConnections = new DatabaseConnections();

    private static final int GALLERY_INTENT = 2;
    private ProgressDialog progressDialog;
    private Uri imageUri;
    private static byte[] compressData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        getViewByIds();
        databaseConnections();

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
                startActivityForResult(intent, GALLERY_INTENT);
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

    private void saveUserInformation()
    {
        String fullName = this.fullName.getText().toString().trim();
        String phoneNumber = this.phoneNumber.getText().toString().trim();
        String addressOne = this.addressOne.getText().toString().trim();
        String addressTwo = this.addressTwo.getText().toString().trim();
        String town = this.town.getText().toString().trim();
        String postCode = this.postCode.getText().toString().trim().toUpperCase();
        String userEmail = auth.getCurrentUser().getEmail();

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

        if(imageUri != null)
        {
            boolean advertiser;
            boolean courier;
            if (checkBoxAdvertiser.isChecked() && !checkBoxCourier.isChecked())
            {
                advertiser = true;
                courier = false;
                UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                        addressTwo, town, postCode, true, false, null, userEmail);

                uploadUsersProfile(userInformation);
            } else if (!checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
            {
                advertiser = false;
                courier = true;
                UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                        addressTwo, town, postCode, false, true, null, userEmail);

                uploadUsersProfile(userInformation);
            } else if (checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
            {
                advertiser = true;
                courier = true;
                UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                        addressTwo, town, postCode, true, true, null, userEmail);

                uploadUsersProfile(userInformation);
            }
        }
        else
        {
            Toast.makeText(this, "Must Upload A Profile Image", Toast.LENGTH_SHORT).show();
        }

        databaseVerification();
        startActivity(new Intent(CreateProfileActivity.this, LoginActivity.class));
    }

    private void databaseVerification()
    {
        FirebaseUser userEmail = FirebaseAuth.getInstance().getCurrentUser();
        assert userEmail != null;
        userEmail.sendEmailVerification();
        FirebaseAuth.getInstance().signOut();
    }

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

    private void databaseConnections()
    {
        auth = databaseConnections.getAuth();
        databaseReference = databaseConnections.getDatabaseReference();
        user = databaseConnections.getCurrentUser();
        storageReference = databaseConnections.getStorageReference();
    }

    private void uploadUsersProfile(final UserInformation userInformation)
    {
        System.out.println(imageUri.getLastPathSegment());
        final StorageReference filePath = storageReference.child("Images").child(user).child(imageUri.getLastPathSegment());
        filePath.putBytes(compressData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                customToastMessage("Profile Image Uploaded Successfully");
                Uri downloadUri = taskSnapshot.getDownloadUrl();
                assert downloadUri != null;
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
    }

    private void customToastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void dismissDialog()
    {
        progressDialog.dismiss();
    }
}