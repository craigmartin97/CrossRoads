package com.kitkat.crossroads.MyAdverts;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.GenericMethods;
import com.kitkat.crossroads.Jobs.JobInformation;
import com.kitkat.crossroads.Jobs.UserBidInformation;
import com.kitkat.crossroads.Profile.ViewProfileFragment;
import com.kitkat.crossroads.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class ActiveBidsFragment extends Fragment
{
    private OnFragmentInteractionListener mListener;

    /**
     * Getting a reference to the firebase datbase area
     */
    private DatabaseReference databaseReference;

    /**
     * Getting the current user who is sign in id
     */
    private String user;

    /**
     * List view to display the information
     */
    private ListView jobListView;

    /**
     * List to store all of the the users bids
     */
    private ArrayList<UserBidInformation> jobList = new ArrayList<>();

    /**
     * Adapter to be able to select a bid
     */
    private ActiveBidsFragment.MyCustomAdapter mAdapter;

    public ActiveBidsFragment()
    {
        // Required empty public constructor
    }

    public static ActiveBidsFragment newInstance(String param1, String param2)
    {
        ActiveBidsFragment fragment = new ActiveBidsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        databaseConnections();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_active_bids, container, false);
        getViewByIds(view);
        displayUsersBidsOnAd();
        return view;
    }

    /**
     * Accessing the Firebase Database to get information and upload new information from
     */
    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        databaseReference = databaseConnections.getDatabaseReference();
        user = databaseConnections.getCurrentUser();
    }

    private void getViewByIds(View view)
    {
        jobListView = view.findViewById(R.id.jobListView1);
    }

    /**
     * Getting all arguments from the bundle that was passed across
     *
     * @return jobInformation
     */
    private String getBundleInformation()
    {
        Bundle bundle = getArguments();
        return (String) bundle.getSerializable("JobId");
    }

    /**
     * Display all of the bids received on the advert and allow the user to accept bid
     */
    private void displayUsersBidsOnAd()
    {
        final String jobId = getBundleInformation();
        databaseReference.child("Bids").child(jobId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    final UserBidInformation bid = ds.getValue(UserBidInformation.class);
                    bid.setJobID(jobId);
                    jobList.add(bid);
                }

                MyCustomAdapter myCustomAdapter = new MyCustomAdapter();
                myCustomAdapter.addArray(jobList);
                jobListView.setAdapter(myCustomAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    public class MyCustomAdapter extends BaseAdapter
    {

        private ArrayList<UserBidInformation> mData = new ArrayList();
        private ArrayList<UserBidInformation> mDataOrig = new ArrayList();

        private LayoutInflater mInflater;

        public MyCustomAdapter()
        {
            if (isAdded())
            {
                mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
        }

        public void addItem(final UserBidInformation item)
        {
            mData.add(item);
            mDataOrig.add(item);
        }


        public void addArray(final ArrayList<UserBidInformation> j)
        {
            mData = j;
            mDataOrig = j;
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
            final MyCustomAdapter.GroupViewHolder holder;
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.job_info_list_bid, null);
                holder = new MyCustomAdapter.GroupViewHolder();

                holder.textViewName = convertView.findViewById(R.id.textName);
                holder.textViewBid = convertView.findViewById(R.id.textBid);
                holder.ratingBarSeeFeedback = convertView.findViewById(R.id.ratingBarSeeFeedback);
                holder.acceptBidButton = convertView.findViewById(R.id.acceptBidButton);

                holder.textViewName.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Toast.makeText(getActivity(), "PRESSED", Toast.LENGTH_SHORT).show();
                        ViewProfileFragment viewProfileFragment = new ViewProfileFragment();
                        GenericMethods genericMethods = new GenericMethods();
                        viewProfileFragment.setArguments(genericMethods.createNewBundleStrings("courierId", mData.get(position).getUserID()));
                        genericMethods.beginTransactionToFragment(getFragmentManager(), viewProfileFragment);
                    }
                });
                convertView.setTag(holder);
            } else
            {
                holder = (MyCustomAdapter.GroupViewHolder) convertView.getTag();
            }

            holder.textViewBid.setText(mData.get(position).getUserBid());

            databaseReference.child("Ratings").child(mData.get(position).getUserID()).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    long totalRating = 0;
                    long counter = 0;
                    // Iterate through entire bids table
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        long rating = ds.child("starReview").getValue(long.class);


                        totalRating += rating;
                        counter++;

                        totalRating = totalRating / counter;

                        int usersRating = Math.round(totalRating);
                        holder.ratingBarSeeFeedback.setNumStars(usersRating);
                        holder.ratingBarSeeFeedback.getNumStars();
                        Drawable drawable = holder.ratingBarSeeFeedback.getProgressDrawable();
                        drawable.setColorFilter(Color.parseColor("#cece63"), PorterDuff.Mode.SRC_ATOP);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });

            holder.acceptBidButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    View mView = getLayoutInflater().inflate(R.layout.popup_accept_bid, null);

                    alertDialog.setTitle("Accept Bid?");
                    alertDialog.setView(mView);
                    final AlertDialog dialog = alertDialog.create();
                    dialog.show();

                    Button yesButton = (Button) mView.findViewById(R.id.yesButton);
                    Button noButton = (Button) mView.findViewById(R.id.noButton);

                    yesButton.setOnClickListener(new View.OnClickListener()
                    {


                        @Override
                        public void onClick(View v)
                        {
                            dialog.cancel();

                            View mView = getLayoutInflater().inflate(R.layout.popup_bid_accepted, null);

                            databaseReference.child("Jobs").child(getBundleInformation()).child("courierID").setValue(mData.get(position).getUserID());
                            databaseReference.child("Jobs").child(getBundleInformation()).child("jobStatus").setValue("Active");
                            FirebaseFirestore dbs = FirebaseFirestore.getInstance();
                            Map<String, Object> notificationMessage = new HashMap<>();
                            String message = "Your bid has been accepted!";
                            String jobID = mData.get(position).getJobID();
                            notificationMessage.put("message", message);
                            notificationMessage.put("Job", jobID);
                            notificationMessage.put("From", user);
                            databaseReference.child("Notifications").child(mData.get(position).getUserID()).push().setValue(notificationMessage);
                            alertDialog.setTitle("Bid Accepted");
                            alertDialog.setView(mView);
                            final AlertDialog dialog = alertDialog.create();
                            dialog.show();

                            TextView text = (TextView) mView.findViewById(R.id.bidAccepted);
                            Button okButton = (Button) mView.findViewById(R.id.okButton);

                            okButton.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    dialog.cancel();
                                }
                            });

                        }
                    });

                    noButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            dialog.cancel();
                        }
                    });
                }
            });

            holder.textViewName.setText(mData.get(position).getFullName());
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
            public TextView textViewBid;
            public RatingBar ratingBarSeeFeedback;
            public Button acceptBidButton;
        }

        public void filter(String charText)
        {

            ArrayList<UserBidInformation> jobs = new ArrayList<UserBidInformation>();
            ArrayList<UserBidInformation> jA = new ArrayList<UserBidInformation>();
            charText = charText.toLowerCase(Locale.getDefault());

            if (charText.length() == 0)
            {
                mData = mDataOrig;
            } else
            {

                for (UserBidInformation j : mDataOrig)
                {
                    if (j.getWholeString().toLowerCase(Locale.getDefault()).contains(charText))
                    {
                        jobs.add(j);
                        jA.add(j);
                    } else
                    {
                        jA.add(j);
                    }
                }
                mData.clear();
                mData = jobs;
                mDataOrig = jA;
            }

            notifyDataSetChanged();
        }
    }

    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }
}
