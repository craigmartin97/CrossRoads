package com.kitkat.crossroads.Jobs;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.ExpandableListAdapter;
import com.kitkat.crossroads.ExternalClasses.GenericMethods;
import com.kitkat.crossroads.ExternalClasses.ListViewHeight;
import com.kitkat.crossroads.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PendingAdverts extends Fragment implements SearchView.OnQueryTextListener
{
    /**
     * Creating connection to the Firebase Database
     */
    private DatabaseReference databaseReference;

    /**
     * Snapshot of the Bids table
     */
    private DataSnapshot bidReference;

    /**
     * Strings to store the job information in
     */
    private String jobId, colDate, colTime, colAddress, colTown, colPostcode, delAddress, delTown, delPostcode, jobType, jobSize, courierId;

    /**
     * Adapter to be able to select a bid
     */
    private PendingAdverts.MyCustomAdapter mAdapter;

    /**
     * List to store all of the the users bids
     */
    private ArrayList<UserBidInformation> jobList = new ArrayList<>();

    /**
     * List view to display the information
     */
    private ListView jobListView;

    private SearchView jobSearch;

    private OnFragmentInteractionListener mListener;

    /**
     * Display the jobs name and description in the header for the job
     */
    private TextView textViewJobName1, textViewDescription1;

    /**
     * Expandable list views to store the information and display upon users request
     */
    private ExpandableListView expandableListView, expandableListView2, expandableListView3;

    /**
     * Adaptres to create the expandable list views
     */
    private ExpandableListAdapter adapter, adapter2, adapter3;

    /**
     * Lists to hold the Jobs information
     */
    private List<String> list, list2, list3;
    private HashMap<String, List<String>> listHashMap, listHashMap2, listHashMap3;

    /**
     * Display the image on the page
     */
    private ImageView jobImagePending;

    public PendingAdverts()
    {
        // Required empty public constructor
    }

    public static PendingAdverts newInstance(String param1, String param2)
    {
        PendingAdverts fragment = new PendingAdverts();
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
        View view = inflater.inflate(R.layout.fragment_job_bids, container, false);

        getViewsByIds(view);
        JobInformation jobInformation = getBundleInformation();

        getJobInformationFromBundle(jobInformation);
        setInformationInHeaderWidgets(jobInformation);

        addItemsCollection();
        addItemsDelivery();
        addItemsJobInformation();

        setExpandableListAdapters();

        displayUsersBidsOnAd();

        return view;
    }

    /**
     * Add the collection information into a new list and into the expandable list view
     */
    private void addItemsCollection()
    {
        list = new ArrayList<>();
        listHashMap = new HashMap<>();

        list.add("Collection Information");

        List<String> collectionInfo = new ArrayList<>();
        collectionInfo.add(colDate);
        collectionInfo.add(colTime);
        collectionInfo.add(colAddress);
        collectionInfo.add(colTown);
        collectionInfo.add(colPostcode);

        listHashMap.put(list.get(0), collectionInfo);
    }

    /**
     * Add the delivery information into a new list and into an expandable list view
     */
    private void addItemsDelivery()
    {
        list2 = new ArrayList<>();
        listHashMap2 = new HashMap<>();

        list2.add("Delivery Information");

        List<String> deliveryInfo = new ArrayList<>();
        deliveryInfo.add(delAddress);
        deliveryInfo.add(delTown);
        deliveryInfo.add(delPostcode);

        listHashMap2.put(list2.get(0), deliveryInfo);
    }

    /**
     * Add the job information into a new list and into an expandable list view
     */
    private void addItemsJobInformation()
    {
        list3 = new ArrayList<>();
        listHashMap3 = new HashMap<>();

        list3.add("Job Information");

        List<String> jobInformation = new ArrayList<>();
        jobInformation.add(jobSize);
        jobInformation.add(jobType);

        listHashMap3.put(list3.get(0), jobInformation);
    }

    /**
     * Create database connections to access the Firebase Database
     */
    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        databaseReference = databaseConnections.getDatabaseReference();
    }

    /**
     * Setting all of the weidgets on the layout page to variables for future use and be able to access them
     *
     * @param view
     */
    private void getViewsByIds(View view)
    {
        expandableListView = view.findViewById(R.id.expandable_list_view);
        expandableListView2 = view.findViewById(R.id.expandable_list_view2);
        expandableListView3 = view.findViewById(R.id.expandable_list_view3);
        textViewJobName1 = view.findViewById(R.id.textViewJobName1);
        textViewDescription1 = view.findViewById(R.id.textViewJobDescription1);
        jobListView = view.findViewById(R.id.jobListView1);
        jobImagePending = view.findViewById(R.id.jobImagePending);
    }

    /**
     * Getting all arguments from the bundle that was passed across
     *
     * @return jobInformation
     */
    private JobInformation getBundleInformation()
    {
        Bundle bundle = getArguments();
        jobId = (String) bundle.getSerializable("JobKeyId");
        return (JobInformation) bundle.getSerializable("JobId");
    }

    /**
     * Set all of the job information to variable to add to the expandable lists
     *
     * @param jobInformation
     */
    private void getJobInformationFromBundle(JobInformation jobInformation)
    {
        colDate = jobInformation.getCollectionDate().toString();
        colTime = jobInformation.getCollectionTime().toString();
        colAddress = jobInformation.getColL1().toString() + ", " + jobInformation.getColL2().toString();
        colTown = jobInformation.getColTown().toString();
        colPostcode = jobInformation.getColPostcode().toString();
        delAddress = jobInformation.getDelL1().toString() + ", " + jobInformation.getDelL2().toString();
        delTown = jobInformation.getDelTown().toString();
        delPostcode = jobInformation.getDelPostcode().toString();
        jobType = jobInformation.getJobType().toString();
        jobSize = jobInformation.getJobSize().toString();
        courierId = jobInformation.getCourierID().toString();
    }

    /**
     * Setting job information in the header of the page such as name, description and the image
     *
     * @param jobInformation
     */
    private void setInformationInHeaderWidgets(JobInformation jobInformation)
    {
        textViewJobName1.setText(jobInformation.getAdvertName());
        textViewDescription1.setText(jobInformation.getAdvertDescription());
        Picasso.get().load(jobInformation.getJobImage()).fit().into(jobImagePending);
    }

    /**
     * Creating the expandable list views and setting their heights
     */
    private void setExpandableListAdapters()
    {
        adapter = new ExpandableListAdapter(getActivity(), list, listHashMap);
        adapter2 = new ExpandableListAdapter(getActivity(), list2, listHashMap2);
        adapter3 = new ExpandableListAdapter(getActivity(), list3, listHashMap3);

        expandableListView.setAdapter(adapter);
        expandableListView2.setAdapter(adapter2);
        expandableListView3.setAdapter(adapter3);

        final ListViewHeight listViewHeight = new ListViewHeight();

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
            {
                listViewHeight.setExpandableListViewHeight(parent, groupPosition);
                return false;
            }
        });

        expandableListView2.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
            {
                listViewHeight.setExpandableListViewHeight(parent, groupPosition);
                return false;
            }
        });

        expandableListView3.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
            {
                listViewHeight.setExpandableListViewHeight(parent, groupPosition);
                return false;
            }
        });
    }

    /**
     * Display all of the bids received on the advert and allow the user to accepta  bid
     */
    private void displayUsersBidsOnAd()
    {
        databaseReference.child("Bids").child(jobId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    final UserBidInformation bid = ds.getValue(UserBidInformation.class);
                    bid.setJobID(jobId);
                    jobList.add(bid);
                }

                PendingAdverts.MyCustomAdapter myCustomAdapter = new MyCustomAdapter();
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
            final PendingAdverts.MyCustomAdapter.GroupViewHolder holder;
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.job_info_list_bid, null);
                holder = new PendingAdverts.MyCustomAdapter.GroupViewHolder();

                holder.textViewName = convertView.findViewById(R.id.textName);
                holder.textViewBid = convertView.findViewById(R.id.textBid);
                holder.ratingBarSeeFeedback = convertView.findViewById(R.id.ratingBarSeeFeedback);
                holder.acceptBidButton = convertView.findViewById(R.id.acceptBidButton);
                convertView.setTag(holder);
            } else
            {
                holder = (PendingAdverts.MyCustomAdapter.GroupViewHolder) convertView.getTag();
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

                            databaseReference.child("Jobs").child(jobId).child("courierID").setValue(mData.get(position).getUserID());
                            databaseReference.child("Jobs").child(jobId).child("jobStatus").setValue("Active");

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
            Toast.makeText(getActivity(), "TOASTY", Toast.LENGTH_SHORT).show();
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


    // TODO: Rename method, update argument and hook method into UI event
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

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        String text = newText;
        mAdapter.filter(text);

        return false;
    }

    interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}