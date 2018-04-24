package com.kitkat.crossroads.Account;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.GenericMethods;
import com.kitkat.crossroads.R;

/**
 * This class allows the user to submit their email address to change their password.
 * The user receives an email to their email address from where they can reset their password
 */
public class ResetPasswordActivity extends AppCompatActivity
{
    /**
     * EditText, used so the user can enter their email address for reset
     */
    private EditText editTextEmailRecover;

    /**
     * Button, used so the user can confirm they want to reset their email
     */
    private Button buttonResetPassword;

    /**
     * Progress Dialog, used to inform the user the process of the reset password is taking place
     */
    private ProgressDialog progressDialog;

    /**
     * Creating a connection to an external class to get access to the FireBase database
     */
    private final DatabaseConnections databaseConnections = new DatabaseConnections();

    /**
     * Creating a connection to another class so it's methods can be accessed
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
        setContentView(R.layout.activity_reset_password);

        getViewsByIds();
        setOnClickListeners();
    }

    /**
     * Setting the onClick event for the Button.
     * When the button is pressed the resetPassword method is run and an email is sent to the email address
     */
    private void setOnClickListeners()
    {
        buttonResetPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                resetPassword();
            }
        });
    }

    /**
     * Sends an email to the email address typed into the EditText widget
     * If successful, the users email address will receive an email to be reset
     */
    private void resetPassword()
    {
        progressDialog.setMessage("Registering User Please Wait");
        progressDialog.show();

        databaseConnections.getAuth().sendPasswordResetEmail(getTextFromEmailRecoverWidget()).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                genericMethods.dismissDialog(progressDialog);

                // The email has been sent
                if (task.isSuccessful())
                {
                    customToastMessage("Email Sent To " + getTextFromEmailRecoverWidget());
                }
                // Unknown error has occurred
                else
                {
                    customToastMessage("An Error Has Occurred");
                }
            }
        });
    }

    /**
     * Get text from the email recover widget
     *
     * @return - Email Address entered
     */
    private String getTextFromEmailRecoverWidget()
    {
        return editTextEmailRecover.getText().toString().trim();
    }

    /**
     * Display a Toast message to the user
     *
     * @param message - Message to be displayed to the user
     */
    private void customToastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Get all of the widgets from the layout page
     */
    private void getViewsByIds()
    {
        progressDialog = new ProgressDialog(this);
        editTextEmailRecover = findViewById(R.id.editTextEmailReset);
        buttonResetPassword = findViewById(R.id.buttonResetPassword);
    }
}
