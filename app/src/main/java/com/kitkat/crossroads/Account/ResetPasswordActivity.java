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
import com.google.firebase.auth.FirebaseAuth;
import com.kitkat.crossroads.R;

public class ResetPasswordActivity extends AppCompatActivity
{

    private EditText editTextEmailRecover;
    private Button buttonResetPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        editTextEmailRecover = (EditText) findViewById(R.id.editTextEmailReset);
        buttonResetPassword = (Button) findViewById(R.id.buttonResetPassword);
        progressDialog = new ProgressDialog(this);

        buttonResetPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                resetPassword();
            }
        });
    }

    private void resetPassword()
    {
        final String email = editTextEmailRecover.getText().toString().trim();

        progressDialog.setMessage("Registering User Please Wait");
        progressDialog.show();

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            customToastMessage("Email Sent To " + email);
                        } else
                        {
                            progressDialog.dismiss();
                            customToastMessage("An Error Has Occurred");
                        }
                    }
                });
    }

    private void customToastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
