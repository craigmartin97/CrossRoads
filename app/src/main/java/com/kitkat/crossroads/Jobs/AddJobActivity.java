package com.kitkat.crossroads.Jobs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kitkat.crossroads.Account.LoginActivity;
import com.kitkat.crossroads.R;

public class AddJobActivity extends AppCompatActivity
{

    private FirebaseAuth auth;

    private EditText editTextJobName;
    private EditText editTextJobDescription;
    private EditText editTextJobTo;
    private EditText editTextJobFrom;


    private Button buttonAddJob;

    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = auth.getCurrentUser();

        buttonAddJob = (Button) findViewById(R.id.buttonAddJob);

        editTextJobName = (EditText) findViewById(R.id.editTextAdName);
        editTextJobDescription = (EditText) findViewById(R.id.editTextAdDescription);
        editTextJobFrom = (EditText) findViewById(R.id.editTextJobFrom);
        editTextJobTo = (EditText) findViewById(R.id.editTextJobTo);

        buttonAddJob.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveJobInformation();
                finish();
                startActivity(new Intent(getApplicationContext(), JobsActivity.class));
            }
        });


    }

    private void saveJobInformation()
    {
        String jobName = editTextJobName.getText().toString().trim();
        String jobDescription = editTextJobDescription.getText().toString().trim();
        String jobFrom = editTextJobFrom.getText().toString().trim();
        String jobTo = editTextJobTo.getText().toString().trim();
        Boolean jobActive = true;
        String courierID = " ";

        FirebaseUser user = auth.getCurrentUser();

        String jobUserID = user.getUid().toString().trim();

        //JobInformation jobInformation = new JobInformation(jobName, jobDescription, jobTo, jobFrom, jobActive, jobUserID, courierID);


        //databaseReference.child("Jobs").push().setValue(jobInformation);

        Toast.makeText(this, "Job Added!", Toast.LENGTH_SHORT).show();


    }


}



