package com.kitkat.crossroads;

import android.content.Context;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.Jobs.BidInformation;
import com.kitkat.crossroads.Jobs.MyAdvertsActivity;
import com.kitkat.crossroads.Profile.ViewProfileFragment;
import com.kitkat.crossroads.UserBidInformation;
import com.kitkat.crossroads.Jobs.JobInformation;
import com.kitkat.crossroads.Profile.UserInformation;

import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JobBidsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JobBidsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobBidsFragment extends Fragment implements SearchView.OnQueryTextListener
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth auth;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private DataSnapshot bidReference;
    private DataSnapshot jobReference;
    private DataSnapshot usersReference;

    private String jobId;
    private String jobBidId;
    private String usersId;
    private static final String TAG = "JobsBidsFragment";
    private String name;

    private JobBidsFragment.MyCustomAdapter mAdapter;

    private ArrayList<UserBidInformation> jobList = new ArrayList<>();

    private ListView jobListView;

    private SearchView jobSearch;

    private OnFragmentInteractionListener mListener;

    private TextView textViewJobName1, textViewDescription1, textViewJobSize1,
            textViewJobType1, textViewJobColDate1, textViewJobColTime1,
            textViewFromAddress, textViewFromTown, textViewFromPostcode,
            textViewToAddress, textViewToTown, textViewToPostcode;

    public JobBidsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JobBidsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobBidsFragment newInstance(String param1, String param2)
    {
        JobBidsFragment fragment = new JobBidsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_job_bids, container, false);

        database = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        databaseReference = database.getReference();

        Bundle bundle = this.getArguments();
        final JobInformation jobInformation = (JobInformation) bundle.getSerializable("JobId");
        jobId = jobInformation.getJobID();

        jobListView = view.findViewById(R.id.jobListView1);

        textViewJobName1 = view.findViewById(R.id.textViewJobName1);
        textViewDescription1 = view.findViewById(R.id.textViewJobDescription1);
        textViewJobSize1 = view.findViewById(R.id.textViewJobSize1);
        textViewJobType1 = view.findViewById(R.id.textViewJobType1);
        textViewJobColDate1 = view.findViewById(R.id.textViewJobColDate1);
        textViewJobColTime1 = view.findViewById(R.id.textViewJobColTime1);


        textViewFromAddress = view.findViewById(R.id.textViewFromAddress);
        textViewFromTown = view.findViewById(R.id.textViewFromTown);
        textViewFromPostcode = view.findViewById(R.id.textViewFromPostcode);

        textViewToAddress = view.findViewById(R.id.textViewToAddress);
        textViewToTown = view.findViewById(R.id.textViewToTown);
        textViewToPostcode = view.findViewById(R.id.textViewJobToPostcode);

        myRef.child("Jobs").child(jobId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String name = dataSnapshot.child("advertName").getValue(String.class);
                String description = dataSnapshot.child("advertDescription").getValue(String.class);
                String jobSize = dataSnapshot.child("jobSize").getValue(String.class);
                String jobType = dataSnapshot.child("jobType").getValue(String.class);
                String collectionDate = dataSnapshot.child("collectionDate").getValue(String.class);
                String collectionTime = dataSnapshot.child("collectionTime").getValue(String.class);

                String fromAddress = dataSnapshot.child("colL1").getValue(String.class);
                String fromAddressLine2 = dataSnapshot.child("colL2").getValue(String.class);
                String fromTown = dataSnapshot.child("colTown").getValue(String.class);
                String fromPostcode = dataSnapshot.child("colPostcode").getValue(String.class);

                String toAddress = dataSnapshot.child("delL1").getValue(String.class);
                String toAddressLine2 = dataSnapshot.child("delL2").getValue(String.class);
                String toTown = dataSnapshot.child("delTown").getValue(String.class);
                String toPostcode = dataSnapshot.child("delPostcode").getValue(String.class);

                Log.d(TAG, "Job Name: " + name);
                Log.d(TAG, "Description: " + description);
                Log.d(TAG, "Job Size: " + jobSize);
                Log.d(TAG, "Job Type: " + jobType);
                Log.d(TAG, "Collection Date: " + collectionDate);
                Log.d(TAG, "Collection Time: " + collectionTime);

                Log.d(TAG, "From Address: " + fromAddress);
                Log.d(TAG, "From Address Line 2: " + fromAddressLine2);
                Log.d(TAG, "From Town: " + fromTown);
                Log.d(TAG, "From PostCode: " + fromPostcode);

                Log.d(TAG, "To Address: " + toAddress);
                Log.d(TAG, "To Address Line 2: " + toAddressLine2);
                Log.d(TAG, "To Town: " + toTown);
                Log.d(TAG, "To PostCode: " + toPostcode);

                textViewJobName1.setText(name);
                textViewDescription1.setText(description);
                textViewJobSize1.setText(jobSize);
                textViewJobType1.setText(jobType);
                textViewJobColDate1.setText(collectionDate);
                textViewJobColTime1.setText(collectionTime);

                textViewFromAddress.setText(fromAddress + ", " + fromAddressLine2);
                textViewFromTown.setText(fromTown);
                textViewFromPostcode.setText(fromPostcode);

                textViewToAddress.setText(toAddress + ", " + toAddressLine2);
                textViewToTown.setText(toTown);
                textViewToPostcode.setText(toPostcode);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                bidReference = dataSnapshot.child("Bids");
                jobReference = dataSnapshot.child("Jobs");
                usersReference = dataSnapshot.child("Users");

                Iterable<DataSnapshot> bidListSnapShot = bidReference.getChildren();

                mAdapter = new JobBidsFragment.MyCustomAdapter();


                for (DataSnapshot ds : bidListSnapShot)
                {
                    Iterable<DataSnapshot> bidsSnapShot = ds.getChildren();
                    jobBidId = ds.getKey();

                    if (jobId.equals(ds.getKey()))
                    {
                        for (DataSnapshot ds1 : bidsSnapShot)
                        {
                            final UserBidInformation bid = ds1.getValue(UserBidInformation.class);
                            String usersBid = bid.getUserBid();
                            String userID = bid.getUserID();
                            String id = bid.getJobID();

                            myRef.child("Users").child(userID).addValueEventListener(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(DataSnapshot thisDataSnapshot)
                                {
                                    String name = thisDataSnapshot.child("fullName").getValue(String.class);
                                    bid.setFullName(name);
                                    bid.getFullName();
                                    jobList.add(bid);

                                    mAdapter.addArray(jobList);
                                    jobListView.setAdapter(mAdapter);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError)
                                {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


        return view;
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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
            System.out.println("getView " + position + " " + convertView);
            JobBidsFragment.MyCustomAdapter.GroupViewHolder holder;
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.job_info_list_bid, null);
                holder = new JobBidsFragment.MyCustomAdapter.GroupViewHolder();
                holder.textViewName = convertView.findViewById(R.id.textName);
                holder.textViewBid = convertView.findViewById(R.id.textBid);
                holder.textViewRating = convertView.findViewById(R.id.textRating);
                holder.acceptBidButton = convertView.findViewById(R.id.acceptBidButton);
                convertView.setTag(holder);
            } else
            {
                holder = (JobBidsFragment.MyCustomAdapter.GroupViewHolder) convertView.getTag();
            }

            holder.textViewName.setText(mData.get(position).getFullName());
            holder.textViewBid.setText(mData.get(position).getUserBid());
            holder.textViewRating.setText(mData.get(position).getUserID());

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

                    TextView text = (TextView) mView.findViewById(R.id.acceptBidText);
                    Button yesButton = (Button) mView.findViewById(R.id.yesButton);
                    Button noButton = (Button) mView.findViewById(R.id.noButton);

                        /*accept users bid
                            - get bidder's ID and set to CourierID
                            - change Job Status to Active
                            - notify users of selection
                        */

                    yesButton.setOnClickListener(new View.OnClickListener()
                    {


                        @Override
                        public void onClick(View v)
                        {
                            dialog.cancel();

                            View mView = getLayoutInflater().inflate(R.layout.popup_bid_accepted, null);

                            myRef.child("Jobs").child(jobId).child("courierID").setValue(mData.get(position).getUserID());
                            myRef.child("Jobs").child(jobId).child("jobStatus").setValue("Active");

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
            public TextView textViewRating;
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
}
