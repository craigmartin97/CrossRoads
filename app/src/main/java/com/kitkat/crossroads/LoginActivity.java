package com.kitkat.crossroads;

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

public class LoginActivity extends AppCompatActivity
{
    private EditText inputEmail;
    private EditText inputPassword;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private Button btnLogin;
    private TextView signUp;
    private TextView resetPassword;


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

        auth = FirebaseAuth.getInstance();
//        if(auth.getCurrentUser() != null)
//        {
//            Intent intent = new Intent(LoginActivity.this, CreateProfileActivity.class);
//            startActivity(intent);
//            finish();
//        }

        signUp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();


                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter an email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter a password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Logging In Please Wait");
                progressDialog.show();

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        progressDialog.dismiss();
                        if(task.isSuccessful() && user.isEmailVerified() == true)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Logged In Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), CreateProfileActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else if(user.isEmailVerified() == false)
                        {
                            Toast.makeText(getApplicationContext(), "You Must Verify Your Email Address. Check Your Mail", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Please Re-enter your details and try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}


