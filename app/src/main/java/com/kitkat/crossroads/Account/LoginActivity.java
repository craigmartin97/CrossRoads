package com.kitkat.crossroads.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.CrossRoads;
import com.kitkat.crossroads.PostAnAdvertFragment;
import com.kitkat.crossroads.Profile.ViewProfileFragment;
import com.kitkat.crossroads.R;

public class LoginActivity extends AppCompatActivity
{
    private EditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;
    public DatabaseReference myRef;
    public FirebaseDatabase mFirebaseDatabase;
    private ProgressDialog progressDialog;
    private Button btnLogin;
    private TextView signUp, resetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.editTextEmailLogin);
        inputPassword = (EditText) findViewById(R.id.editTextPasswordLogin);
        progressDialog = new ProgressDialog(this);
        signUp = (TextView) findViewById(R.id.textViewSignUp);
        resetPassword = (TextView) findViewById(R.id.textViewResetPassword);
        btnLogin = (Button) findViewById(R.id.buttonSignIn);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Users");
        FirebaseUser user = mAuth.getCurrentUser();

        getCurrentUser();

        signUp.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email))
                {
                    customToastMessage("Please enter an email address!");
                    return;
                }

                if (TextUtils.isEmpty(password))
                {
                    customToastMessage("Please Enter A Password");
                    return;
                }

                progressDialog.setMessage("Logging In Please Wait...");
                progressDialog.show();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {

                        if (!task.isSuccessful())
                        {
                            dismissDialog();
                            customToastMessage("Please Check Your Details And Try Again");
                        } else
                        {
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            dismissDialog();
                            if (task.isSuccessful() && user.isEmailVerified() == true)
                            {
                                dismissDialog();
                                startActivity(new Intent(getApplicationContext(), CrossRoads.class));
                                finish();
                            } else if (user.isEmailVerified() == false)
                            {
                                dismissDialog();
                                customToastMessage("You Must Verify Your Email Address Before Logging In. Please Check Your Email.");
                            } else
                            {
                                dismissDialog();
                                customToastMessage("Please Re-enter Your Details And Try Again");
                            }
                        }
                    }
                });

            }
        });
    }

    private void getCurrentUser()
    {
        if (mAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(LoginActivity.this, CrossRoads.class);
            startActivity(intent);
            finish();
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
}
