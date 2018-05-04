package com.kitkat.crossroads.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kitkat.crossroads.EnumClasses.DatabaseEntryNames;
import com.kitkat.crossroads.ExternalClasses.GenericMethods;
import com.kitkat.crossroads.MainActivity.CrossRoadsMainActivity;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.R;

import java.util.Objects;

/**
 * This class is used so users can login to their accounts. The users must enter their email address and password.
 * This is then checked in the FireBase Authentication area to ensure they are a user. If they are a sign up user
 * they are logged into their account. Otherwise they are asked to create an account.
 */
public class LoginActivity extends AppCompatActivity
{
    /**
     * Storing the reference to the FireBase Authentication area, so users can access their accounts
     */
    private FirebaseAuth auth;

    /**
     * Storing the reference to the FireBase Database area, so users information can be stored
     */
    private DatabaseReference databaseReferenceUsers;

    /**
     * EditText widgets, so the user can enter their email address and password for verification
     */
    private EditText inputEmail, inputPassword;

    /**
     * Creating a new progress dialog, to display to the user, so the knows the process of logging in
     * is taking place.
     */
    private ProgressDialog progressDialog;

    /**
     * Button widget, when the user has entered their information they can press the button
     * to trigger the verification process and login.
     */
    private Button btnLogin;

    /**
     * TextView widgets
     * signUp - will redirect the user to create a profile if they don't have an account.
     * resetPassword - will allow the user to enter their email address, if they have forgot their password.
     */
    private TextView signUp, resetPassword;

    /**
     * Accessing methods from the generic methods, were CustomToast and DialogDismiss can be accessed from.
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
        setContentView(R.layout.activity_login);

        getViewByIds();
        databaseConnections();
        setOnClickListeners();
    }

    /**
     * Creates all of the connections to FireBase that are necessary.
     * databaseReferenceUsers, is user to store a new token under the usersID
     * auth, is used to create a connection to verify the user
     */
    private void databaseConnections()
    {
        // Establishing a connection to the DatabaseConnections class to retrieve the FireBase connections.
        DatabaseConnections databaseConnections = new DatabaseConnections();
        databaseReferenceUsers = databaseConnections.getDatabaseReferenceUsers();
        databaseReferenceUsers.keepSynced(true);
        auth = databaseConnections.getAuth();

        // If there is already a user signed in
        if (auth.getCurrentUser() != null)
        {
            Intent intent = new Intent(LoginActivity.this, CrossRoadsMainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Assigning all widgets in layout file to class variables in this activity.
     */
    private void getViewByIds()
    {
        progressDialog = new ProgressDialog(this, R.style.datepicker);
        inputEmail = findViewById(R.id.editTextEmailLogin);
        inputPassword = findViewById(R.id.editTextPasswordLogin);
        signUp = findViewById(R.id.textViewSignUp);
        resetPassword = findViewById(R.id.textViewResetPassword);
        btnLogin = findViewById(R.id.buttonSignIn);
    }

    /**
     * Set the onClick events for the widgets in the activity.
     * Allowing the user to login, register or reset their password
     */
    private void setOnClickListeners()
    {
        // allow user to sign up, move to register activity
        signUp.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // allow user to reset their password, move to ResetPassword activity
        resetPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        // allow user to login to their account
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // Check email box if its empty, return back to page
                if (TextUtils.isEmpty(getTextFromEmailWidget()))
                {
                    customToastMessage("Please enter an email address!");
                    return;
                }

                // Check password is empty, if so, return to page
                if (TextUtils.isEmpty(getTextFromPasswordWidget()))
                {
                    customToastMessage("Please Enter A Password");
                    return;
                }

                progressDialog.setMessage("Logging In Please Wait...");
                progressDialog.show();

                auth.signInWithEmailAndPassword(getTextFromEmailWidget(), getTextFromPasswordWidget()).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {

                        // if the user failed authentication
                        if (!task.isSuccessful())
                        {
                            genericMethods.dismissDialog(progressDialog);
                            customToastMessage("Please Check Your Details And Try Again");
                        } else
                        {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            genericMethods.dismissDialog(progressDialog);
                            // successfully logged in
                            if (task.isSuccessful() && (user != null && user.isEmailVerified()))
                            {
                                genericMethods.dismissDialog(progressDialog);
                                databaseReferenceUsers.child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child(DatabaseEntryNames.notifToken.name()).setValue(FirebaseInstanceId.getInstance().getToken());
                                startActivity(new Intent(getApplicationContext(), CrossRoadsMainActivity.class));
                                finish();
                            }
                            // email not verified, must login to their email and accept
                            else if (!(user != null && user.isEmailVerified()))
                            {
                                genericMethods.dismissDialog(progressDialog);
                                customToastMessage("You Must Verify Your Email Address Before Logging In. Please Check Your Email.");
                            }
                            // Unexpected error happened
                            else
                            {
                                genericMethods.dismissDialog(progressDialog);
                                customToastMessage("Please Re-enter Your Details And Try Again");
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * Retrieves the text the user has entered from the email widget
     *
     * @return - users email
     */
    private String getTextFromEmailWidget()
    {
        return inputEmail.getText().toString().trim();
    }

    /**
     * Retrieves the text the user has entered from the password widget
     *
     * @return - users password
     */
    private String getTextFromPasswordWidget()
    {
        return inputPassword.getText().toString().trim();
    }

    /**
     * Creating a custom toast message for all Activities to access
     *
     * @param message - Text to be displayed to the user
     */
    private void customToastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}