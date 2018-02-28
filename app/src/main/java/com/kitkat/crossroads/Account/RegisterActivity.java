package com.kitkat.crossroads.Account;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.kitkat.crossroads.Profile.CreateProfileActivity;
import com.kitkat.crossroads.Jobs.JobsActivity;
import com.kitkat.crossroads.R;

public class RegisterActivity extends AppCompatActivity {

    // WHERE ON THE DEVELOP BRANCH
    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private CheckBox checkBox;
    private TextView textViewSignUp;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private void registerUser() {
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // email is too short
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter An Email Address", Toast.LENGTH_SHORT).show();
            return;
        }

        // password to short
        if (TextUtils.isEmpty(password) || password.length() < 8) {
            Toast.makeText(this, "Please Enter A Password With 6 Or More Characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(confirmPassword) || confirmPassword.length() < 8)
        {
            Toast.makeText(this, "Please Confirm Your Password With 6 Or More Characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{4,}$") && !confirmPassword.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{4,}$"))
        {
            Toast.makeText(this, "Passwords Must Have Numbers, Upper and Lowercase's", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.matches(confirmPassword) && !confirmPassword.matches(password))
        {
            Toast.makeText(this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkBox.isChecked()) {
            //if validation is ok, show progress bar
            progressDialog.setMessage("Registering User Please Wait");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Registered Sucessfully", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), JobsActivity.class));

                            } 
                            else if(task.getException() instanceof FirebaseAuthUserCollisionException)
                            {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Could Not Register. User with this email already exist. Please Login.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();

                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Could Not Register. Please check your details and try " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), CreateProfileActivity.class));
        }

        progressDialog = new ProgressDialog(this);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextEmail = (EditText) findViewById(R.id.editTextEmailLogin);
        editTextPassword = (EditText) findViewById(R.id.editTextPasswordLogin);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextPasswordConfirmLogin);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        textViewSignUp = (TextView) findViewById(R.id.textViewSignIn);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    }
}
