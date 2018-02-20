package com.kitkat.crossroads;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

public class JobsActivity extends ListActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DataSnapshot jobReference;


    private MyCustomAdapter mAdapter;

    private ArrayList<JobInformation> jobList = new ArrayList<JobInformation>();

    private ListView jobListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                jobReference = dataSnapshot.child("Jobs");

                Iterable<DataSnapshot> jobListSnapShot = jobReference.getChildren();

                mAdapter = new MyCustomAdapter();

                for (DataSnapshot ds : jobListSnapShot) {
                    JobInformation j = ds.getValue(JobInformation.class);

                    jobList.add(j);

                    mAdapter.addItem("Job: " + j.getJobName() + " From: " + j.getJobFrom() + " To: " + j.getJobTo());

                }

                setListAdapter(mAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private class MyCustomAdapter extends BaseAdapter {

        private ArrayList mData = new ArrayList();
        private LayoutInflater mInflater;

        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final String item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position).toString();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            System.out.println("getView " + position + " " + convertView);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.job_info_list, null);
                holder = new ViewHolder();
                holder.textView = (TextView)convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.textView.setText(mData.get(position).toString());
            return convertView;
        }

    }

    public static class ViewHolder {
        public TextView textView;
    }
}




