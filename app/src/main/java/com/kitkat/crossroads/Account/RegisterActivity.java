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
import com.kitkat.crossroads.ExternalClasses.GenericMethods;
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
     * Storing the users email and password
     */
    private String email, password;

    /**
     * Accessing methods from the GenericMethods class, by creating an instance of the class
     */
    private final GenericMethods genericMethods = new GenericMethods();

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
    }

    /**
     * Assigning all widgets in layout file to class variables in this activity.
     */
    private void getViewByIds()
    {
        progressDialog = new ProgressDialog(this, R.style.datepicker);
        buttonRegister = findViewById(R.id.buttonRegister);
        editTextEmail = findViewById(R.id.editTextEmailLogin);
        editTextPassword = findViewById(R.id.editTextPasswordLogin);
        editTextConfirmPassword = findViewById(R.id.editTextPasswordConfirmLogin);
        checkBox = findViewById(R.id.checkBox);
        textViewSignUp = findViewById(R.id.textViewSignIn);
        textViewTermsAndConditionsAndPrivacyPolicy = findViewById(R.id.textViewTermsAndConditionsAndPrivacyPolicy);
    }

    /**
     * Setting the on click listeners for the widgets in the activity.
     * Button register allows the user to confirm there details.
     * TextView sign up allows the user to go to the login screen.
     * TextViewT&C's allows the user to download a pdf of the terms and conditions.
     */
    private void setOnClickListeners()
    {
        // Register the new user with details
        buttonRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                registerUser();
            }
        });

        // Takes user to logging activity
        textViewSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        // Downloads the T&C's
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

    /**
     * This method registers the user into the FireBase database
     * Creates a new progress dialog and display to the user. Then registers the user via
     * their email and password.
     */
    private void registerUser()
    {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // email is too short or empty
        if (TextUtils.isEmpty(email))
        {
            customToastMessage("Please Enter An Email Address");
            return;
        }

        // password to short or empty
        if (TextUtils.isEmpty(password) || password.length() < 8)
        {
            customToastMessage("Please Enter A Password With 6 Or More Characters");
            return;
        }

        // confirm password to short or empty
        if (TextUtils.isEmpty(confirmPassword) || confirmPassword.length() < 8)
        {
            customToastMessage("Please Confirm Your Password With 8 Or More Characters");
            return;
        }

        // Passwords don't contain number, letter and capital
        if (!password.matches(checkPasswordCombo()) && !confirmPassword.matches(checkPasswordCombo()))
        {
            customToastMessage("Passwords Must Have Numbers, Upper and Lowercase's");
            return;
        }

        // Passwords don't match each other
        if (!password.matches(confirmPassword) && !confirmPassword.matches(password))
        {
            customToastMessage("Passwords Do Not Match");
            return;
        }

        // If user has agreed to T&C's
        if (checkBox.isChecked())
        {
            progressDialog.setMessage("Registering User Please Wait...");
            progressDialog.show();

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            // Can successfully register user
                            if (task.isSuccessful())
                            {
                                boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                                if (isNewUser)
                                {
                                    genericMethods.dismissDialog(progressDialog);
                                    startActivity(new Intent(RegisterActivity.this, CreateProfileActivity.class));
                                }
                                // Already a user
                                else if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                {
                                    genericMethods.dismissDialog(progressDialog);
                                    customToastMessage("Could Not Register. User with this email already exist. Please Login.");
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                }
                                // Unknown error
                                else
                                {
                                    genericMethods.dismissDialog(progressDialog);
                                    customToastMessage("Couldn't Register, Please Try Again");
                                    finish();
                                }
                            }
                        }
                    });
        } else
        {
            customToastMessage("You Must Agree To Our Terms & Conditions");
        }
    }

    /**
     * Displays a Toast message to the user
     *
     * @param message - The message to be displayed to the user
     */
    private void customToastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Check that all of the user information, email and password, match our requirements
     * They must provide an email, password and then confirm password.
     * Passwords must contain letters, numbers, and an uppercase
     */
    private void userInformationValidation()
    {

    }

    /**
     * Regex used to check the users email contains, uppercase, number and letters
     *
     * @return - Regex string that password must equal for user to register account
     */
    private String checkPasswordCombo()
    {
        return "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{4,}$";
    }
}