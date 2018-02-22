package com.kitkat.crossroads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class JobsActivity extends Activity {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DataSnapshot jobReference;


    private MyCustomAdapter mAdapter;

    private ArrayList<JobInformation> jobList = new ArrayList<JobInformation>();

    private ExpandableListView jobListView;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);


        jobListView = (ExpandableListView) findViewById(R.id.jobListView12345);


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

                    mAdapter.addItem(j);

                }



                jobListView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });


    }


    private class MyCustomAdapter implements ExpandableListAdapter {

        private ArrayList<JobInformation> mData = new ArrayList();

        private LayoutInflater mInflater;

        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final JobInformation item) {
            mData.add(item);

        }



        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getGroupCount() {
            return mData.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mData.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mData.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return groupPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            System.out.println("getView " + groupPosition + " " + convertView);
            GroupViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.job_info_list, null);
                holder = new GroupViewHolder();
                holder.textViewName = (TextView)convertView.findViewById(R.id.textName);
                holder.textViewFrom = (TextView)convertView.findViewById(R.id.textFrom);
                holder.textViewTo = (TextView)convertView.findViewById(R.id.textTo);
                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder)convertView.getTag();
            }
            holder.textViewName.setText(mData.get(groupPosition).getJobName());
            holder.textViewFrom.setText(mData.get(groupPosition).getJobFrom());
            holder.textViewTo.setText(mData.get(groupPosition).getJobTo());
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder;
            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.job_expanded_view, null);
                holder = new ChildViewHolder();
                holder.textViewName = (TextView)convertView.findViewById(R.id.textViewJobName);
                holder.textViewDesc = (TextView)convertView.findViewById(R.id.textViewJobDesc);
                holder.textViewFrom = (TextView)convertView.findViewById(R.id.textViewJobFrom);
                holder.textViewTo = (TextView)convertView.findViewById(R.id.textViewJobTo);
                convertView.setTag(holder);
            } else {
                holder = (ChildViewHolder)convertView.getTag();
            }

            holder.textViewName.setText(mData.get(groupPosition).getJobName());
            holder.textViewDesc.setText(mData.get(groupPosition).getJobDescription());
            holder.textViewFrom.setText(mData.get(groupPosition).getJobFrom());
            holder.textViewTo.setText(mData.get(groupPosition).getJobTo());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void onGroupExpanded(int groupPosition) {

        }

        @Override
        public void onGroupCollapsed(int groupPosition) {

        }

        @Override
        public long getCombinedChildId(long groupId, long childId) {
            return 0;
        }

        @Override
        public long getCombinedGroupId(long groupId) {
            return 0;
        }
    }

    public static class GroupViewHolder {
        public TextView textViewName;
        public TextView textViewFrom;
        public TextView textViewTo;
    }


    public static class ChildViewHolder {
        public TextView textViewName;
        public TextView textViewDesc;
        public TextView textViewFrom;
        public TextView textViewTo;
    }
}

