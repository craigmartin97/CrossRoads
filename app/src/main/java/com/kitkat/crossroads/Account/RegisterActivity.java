package com.kitkat.crossroads.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kitkat.crossroads.CrossRoads;
import com.kitkat.crossroads.Profile.CreateProfileActivity;
import com.kitkat.crossroads.R;
import com.kitkat.crossroads.TermsAndConditions;


public class
RegisterActivity extends AppCompatActivity
{

    private Button buttonRegister;
    private EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private CheckBox checkBox;
    private TextView textViewSignUp, textViewTermsAndConditionsAndPrivacyPolicy;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setDatabaseConnections();
        getViewByIds();
        buttonRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                registerUser();
            }
        });

        textViewSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        textViewTermsAndConditionsAndPrivacyPolicy.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                String url = "https://firebasestorage.googleapis.com/v0/b/crossroads-b1198.appspot.com/o/TermsConditions%2FTermsAndConditions.pdf?alt=media&token=694fc922-5a1a-4ee6-b130-af3f226263fc";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
    }


    private void registerUser()
    {
        userInformationValidation();

        if (checkBox.isChecked())
        {
            //if validation is ok, show progress bar
            progressDialog.setMessage("Registering User Please Wait...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                                if (isNewUser == true)
                                {
                                    dismissDialog();
                                    databaseReference.child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("notifToken").setValue(FirebaseInstanceId.getInstance().getToken());
                                    startActivity(new Intent(RegisterActivity.this, CreateProfileActivity.class));
                                } else if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                {
                                    dismissDialog();
                                    customToastMessage("Could Not Register. User with this email already exist. Please Login.");
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();

                                } else
                                {
                                    dismissDialog();
                                    customToastMessage("Couldn't Register, Please Try Again");
                                    finish();
                                }
                            }
                        }
                    });
        }
    }

    private void dismissDialog()
    {
        progressDialog.dismiss();
    }

    private void customToastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void getViewByIds()
    {
        progressDialog = new ProgressDialog(this);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextEmail = (EditText) findViewById(R.id.editTextEmailLogin);
        editTextPassword = (EditText) findViewById(R.id.editTextPasswordLogin);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextPasswordConfirmLogin);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        textViewSignUp = (TextView) findViewById(R.id.textViewSignIn);
        textViewTermsAndConditionsAndPrivacyPolicy = (TextView) findViewById(R.id.textViewTermsAndConditionsAndPrivacyPolicy);
    }

    private void setDatabaseConnections()
    {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (firebaseAuth.getCurrentUser() != null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), CrossRoads.class));
        }
    }

    private void userInformationValidation()
    {
        email = editTextEmail.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        final String confirmPassword = editTextConfirmPassword.getText().toString().trim();


        // email is too short
        if (TextUtils.isEmpty(email))
        {
            customToastMessage("Please Enter An Email Address");
            return;
        }

        // password to short
        if (TextUtils.isEmpty(password) || password.length() < 8)
        {
            customToastMessage("Please Enter A Password With 6 Or More Characters");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword) || confirmPassword.length() < 8)
        {
            customToastMessage("Please Confirm Your Password With 8 Or More Characters");
            return;
        }

        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{4,}$") && !confirmPassword.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{4,}$"))
        {
            customToastMessage("Passwords Must Have Numbers, Upper and Lowercase's");
            return;
        }

        if (!password.matches(confirmPassword) && !confirmPassword.matches(password))
        {
            customToastMessage("Passwords Do Not Match");
            return;
        }
    }
}