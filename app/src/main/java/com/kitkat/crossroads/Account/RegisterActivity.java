package com.kitkat.crossroads.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kitkat.crossroads.ExternalClasses.GenericMethods;
import com.kitkat.crossroads.MainActivity.CrossRoadsMainActivity;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.Profile.CreateProfileActivity;
import com.kitkat.crossroads.R;

/**
 * Register Activity allows the user to register for an account if they are not already
 * an account holder. The user enters their email address and password that they wish to use.
 */
public class RegisterActivity extends AppCompatActivity
{
    /**
     * Button widget, when the user presses this button the process of registering for an account
     * will begin
     */
    private Button buttonRegister;

    /**
     * EditText widgets, so the user can enter their register information
     * editTextEmail - User can enter their email address
     * editTextPassword - User can enter a password they wish
     * editTextConfirmPassword - User can re-enter their password for confirmation
     */
    private EditText editTextEmail, editTextPassword, editTextConfirmPassword;

    /**
     * Checkbox widget, the user must check the checkbox and agree to our T&C's so they can
     * create an account
     */
    private CheckBox checkBox;

    /**
     * TextView widgets, are used to allow the user to login to their account if they already have an account
     * or download our T&C's
     */
    private TextView textViewSignUp, textViewTermsAndConditionsAndPrivacyPolicy;

    /**
     * A progress dialog so the user knows the process is happening and they need to wait for a process to finish
     */
    private ProgressDialog progressDialog;

    /**
     * Storing the reference to the FireBase Authentication area, so users can access their accounts
     */
    private FirebaseAuth auth;

    /**
     * Storing the reference to the FireBase Database area, so users information can be stored
     */
    private DatabaseReference databaseReference;

    /**
     * Storing the users email and password
     */
    private String email, password;

    /**
     * Accessing methods from the GenericMethods class, by creating an instance of the class
     */
    private GenericMethods genericMethods = new GenericMethods();

    /**
     * This method is called when the activity login is displayed to the user. It creates all of the
     * widgets and functionality that the user can do in the activity.
     *
     * @param savedInstanceState - if the activity needs to be recreated it can be passed back
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseConnections();
        getViewByIds();
        setOnClickListeners();
    }

    /**
     * Creates all of the connections to FireBase that are necessary.
     * databaseReference, is user to store a new token under the usersID
     * auth, is used to create a connection to verify the user
     */
    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        auth = databaseConnections.getAuth();
        databaseReference = databaseConnections.getDatabaseReference();

        if (auth.getCurrentUser() != null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), CrossRoadsMainActivity.class));
        }
    }

    /**
     * Assigning all widgets in layout file to class variables in this activity.
     */
    private void getViewByIds()
    {
        progressDialog = new ProgressDialog(this);
        buttonRegister = findViewById(R.id.buttonRegister);
        editTextEmail = findViewById(R.id.editTextEmailLogin);
        editTextPassword = findViewById(R.id.editTextPasswordLogin);
        editTextConfirmPassword = findViewById(R.id.editTextPasswordConfirmLogin);
        checkBox = findViewById(R.id.checkBox);
        textViewSignUp = findViewById(R.id.textViewSignIn);
        textViewTermsAndConditionsAndPrivacyPolicy = findViewById(R.id.textViewTermsAndConditionsAndPrivacyPolicy);
    }

    /**
     * Setting the on click listeners f
     */
    private void setOnClickListeners()
    {
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

            auth.createUserWithEmailAndPassword(email, password)
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
                                    genericMethods.dismissDialog(progressDialog);
                                    databaseReference.child("Users").child(auth.getCurrentUser().getUid()).child("notifToken").setValue(FirebaseInstanceId.getInstance().getToken());
                                    startActivity(new Intent(RegisterActivity.this, CreateProfileActivity.class));
                                } else if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                {
                                    genericMethods.dismissDialog(progressDialog);
                                    DatabaseConnections databaseConnections = new DatabaseConnections();
                                    customToastMessage("Could Not Register. User with this email already exist. Please Login.");
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();

                                } else
                                {
                                    genericMethods.dismissDialog(progressDialog);
                                    customToastMessage("Couldn't Register, Please Try Again");
                                    finish();
                                }
                            }
                        }
                    });
        }
    }

    private void customToastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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