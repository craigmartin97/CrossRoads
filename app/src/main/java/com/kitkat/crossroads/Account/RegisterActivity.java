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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.kitkat.crossroads.HomeActivity;
import com.kitkat.crossroads.Profile.CreateProfileActivity;
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

    private StorageReference storageReference;

    private TextView textViewTermsAndConditions;
    private TextView textViewPrivacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

//        storageReference = FirebaseStorage.getInstance().getReference().child("TermsConditions/TermsAndConditions.pdf");
//        StorageReference termsAndConditionsFile= storageReference.child("TermsConditions/TermsAndConditions.pdf");

        if(firebaseAuth.getCurrentUser() != null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        progressDialog = new ProgressDialog(this);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextEmail = (EditText) findViewById(R.id.editTextEmailLogin);
        editTextPassword = (EditText) findViewById(R.id.editTextPasswordLogin);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextPasswordConfirmLogin);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        textViewSignUp = (TextView) findViewById(R.id.textViewSignIn);

        textViewTermsAndConditions = (TextView) findViewById(R.id.textViewTermsAndConditions);
        textViewPrivacyPolicy = (TextView) findViewById(R.id.textViewPrivacyPolicy);

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


//        textViewTermsAndConditions.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try
//                {
//                    final File localFile = File.createTempFile("TermsAndConditions", "pdf");
//                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                            Uri path = Uri.fromFile(localFile);
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                            intent.setDataAndType(path, "application/pdf");
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
//                        }
//                    });
//                }
//                catch (Exception e)
//                {
//
//                }
//            }
//        });

    }


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
                                boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                                if(isNewUser == true)
                                {
                                    progressDialog.dismiss();
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    user.sendEmailVerification();
                                    Toast.makeText(RegisterActivity.this, "Please Enter You're Details", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, CreateProfileActivity.class));
                                }
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
                                Toast.makeText(RegisterActivity.this, "Couldn't Register, Please Try Again", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
        }
    }
}
