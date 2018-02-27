package com.kitkat.crossroads;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class JobDetailsActivity extends AppCompatActivity {

    private TextView jobName, jobDescription, jobFrom, jobTo;
    private Button buttonBid;
    private EditText editTextBid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        Intent intent = getIntent();
        JobInformation jobInformation = (JobInformation)intent.getSerializableExtra("JobDetails");

        jobName = (TextView) findViewById(R.id.textViewJobName1);
        jobDescription = (TextView) findViewById(R.id.textViewJobDescription1);
        jobFrom = (TextView) findViewById(R.id.textViewJobFrom1);
        jobTo = (TextView) findViewById(R.id.textViewJobTo1);

        jobName.setText(jobInformation.getJobName().toString());
        jobDescription.setText(jobInformation.getJobDescription().toString());
        jobFrom.setText(jobInformation.getJobFrom().toString());
        jobTo.setText(jobInformation.getJobTo().toString());



    }
}
