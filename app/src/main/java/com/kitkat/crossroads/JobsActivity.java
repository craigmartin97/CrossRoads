package com.kitkat.crossroads;

import android.app.ListActivity;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DataSnapshot jobReference;


    private ArrayList<JobInformation> jobList = new ArrayList<JobInformation>();

    private ListView jobListView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

        jobListView = (ListView) findViewById(R.id.listJobs);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


               jobReference = dataSnapshot.child("Jobs");

               Iterable<DataSnapshot> jobListSnapShot = jobReference.getChildren();

                for (DataSnapshot ds : jobListSnapShot)
                {
                    JobInformation j = ds.getValue(JobInformation.class);

                    jobList.add(j);
                }

                JobAdapter adapter = new JobAdapter(getBaseContext(), jobList);

                jobListView.setAdapter(adapter);





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void jobData(DataSnapshot dataSnapshot) {



    }


}
