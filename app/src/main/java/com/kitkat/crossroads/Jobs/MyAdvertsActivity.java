package com.kitkat.crossroads.Jobs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.R;

import java.util.ArrayList;

public class MyAdvertsActivity extends Activity
{

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DataSnapshot jobReference;
    private MyCustomAdapter mAdapter;
    private ArrayList<JobInformation> jobList = new ArrayList<JobInformation>();

    private ListView jobListView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_adverts);

        jobListView = (ListView) findViewById(R.id.jobListView1);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                jobReference = dataSnapshot.child("Jobs");

                Iterable<DataSnapshot> jobListSnapShot = jobReference.getChildren();

                mAdapter = new MyCustomAdapter();

                for (DataSnapshot ds : jobListSnapShot)
                {
                    JobInformation j = ds.getValue(JobInformation.class);
                    j.setJobID(ds.getKey());

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (j.getPosterID().equals(currentUser.getUid()))
                    {
                        jobList.add(j);

                    }

                    mAdapter.addArray(jobList);
                    jobListView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private class MyCustomAdapter extends BaseAdapter
    {
        private ArrayList<JobInformation> mData = new ArrayList();

        private LayoutInflater mInflater;

        public MyCustomAdapter()
        {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final JobInformation item)
        {
            mData.add(item);
        }

        public void addArray(final ArrayList<JobInformation> j)
        {
            mData = j;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer)
        {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer)
        {

        }

        @Override
        public int getCount()
        {
            return mData.size();
        }

        @Override
        public Object getItem(int position)
        {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }


        @Override
        public boolean hasStableIds()
        {
            return false;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            System.out.println("getView " + position + " " + convertView);
            GroupViewHolder holder;
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.job_info_list, null);
                holder = new GroupViewHolder();
                holder.textViewName = (TextView) convertView.findViewById(R.id.textName);
                holder.textViewFrom = (TextView) convertView.findViewById(R.id.textFrom);
                holder.textViewTo = (TextView) convertView.findViewById(R.id.textTo);
                holder.detailsButton = (Button) convertView.findViewById(R.id.detailsButton);
                convertView.setTag(holder);
            } else
            {
                holder = (GroupViewHolder) convertView.getTag();
            }

            holder.textViewName.setText(mData.get(position).getAdvertName());
            holder.textViewFrom.setText(mData.get(position).getJobType());
            holder.textViewTo.setText(mData.get(position).getJobSize());
            holder.detailsButton.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(MyAdvertsActivity.this, JobDetailsActivity.class);
                    intent.putExtra("JobDetails", mData.get(position));
                    startActivity(intent);

                }
            });
            return convertView;
        }

        @Override
        public boolean areAllItemsEnabled()
        {
            return false;
        }

        @Override
        public boolean isEmpty()
        {
            return false;
        }

        public class GroupViewHolder
        {
            public TextView textViewName;
            public TextView textViewFrom;
            public TextView textViewTo;
            public Button detailsButton;
        }

    }
}
