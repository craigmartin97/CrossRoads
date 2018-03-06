package com.kitkat.crossroads.Jobs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kitkat.crossroads.R;

public class JobDetailsActivity extends AppCompatActivity {

    private TextView jobName, jobDescription, jobFrom, jobTo;
    private Button buttonBid;
    private EditText editTextBid;


    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);


        databaseReference = FirebaseDatabase.getInstance().getReference();


        Intent intent = getIntent();
        JobInformation jobInformation = (JobInformation)intent.getSerializableExtra("JobDetails");

        jobName = (TextView) findViewById(R.id.textViewJobName1);
        jobDescription = (TextView) findViewById(R.id.textViewJobDescription1);
        jobFrom = (TextView) findViewById(R.id.textViewJobFrom1);
        jobTo = (TextView) findViewById(R.id.textViewJobTo1);
        editTextBid = (EditText) findViewById(R.id.editTextBid);
        buttonBid = (Button) findViewById(R.id.buttonBid);

        jobName.setText(jobInformation.getJobName().toString());
        jobDescription.setText(jobInformation.getJobDescription().toString());
        jobFrom.setText(jobInformation.getJobFrom().toString());
        jobTo.setText(jobInformation.getJobTo().toString());


        buttonBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBidInformation();
                finish();
                startActivity(new Intent(getApplicationContext(), JobsActivity.class));
            }
        });


    }
    private void saveBidInformation(){
        Intent intent = getIntent();
        JobInformation jobInformation = (JobInformation)intent.getSerializableExtra("JobDetails");


        String userBid = editTextBid.getText().toString().trim();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        user.getUid();

        String userID = user.getUid();



        String jobID = jobInformation.getJobID().toString().trim();

        BidInformation bidInformation = new BidInformation(userID, userBid);

        databaseReference.child("Bids").child(jobID).push().setValue(bidInformation);

        Toast.makeText(this, "Bid Added!", Toast.LENGTH_SHORT).show();

    }
}
