package com.kitkat.crossroads;

import android.content.Context;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.kitkat.crossroads.Jobs.JobInformation;

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
    private DatabaseReference databaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener authStateListener;

    private DataSnapshot bidReference;
    private DataSnapshot jobReference;
    private DataSnapshot usersReference;

    private String jobId;
    private String usersId;
    private static final String TAG = "JobsBidsFragment";
    private String name;

    private JobBidsFragment.MyCustomAdapter mAdapter;

    private ArrayList<BidInformation> jobList = new ArrayList<>();

    private ListView jobListView;

    private SearchView jobSearch;

    private OnFragmentInteractionListener mListener;

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
        Bundle bundle = this.getArguments();
        final JobInformation jobInformation =  (JobInformation) bundle.getSerializable("JobId");
        jobId = jobInformation.getJobID();


        jobListView = view.findViewById(R.id.jobListView1);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Users");

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                bidReference = dataSnapshot.child("Bids");
                jobReference = dataSnapshot.child("Jobs");
                usersReference = dataSnapshot.child("Users");

                Iterable<DataSnapshot> bidListSnapShot = bidReference.getChildren();
                Iterable<DataSnapshot> jobListSnapShot = jobReference.getChildren();
                Iterable<DataSnapshot> userListSnapShot = usersReference.getChildren();

                mAdapter = new JobBidsFragment.MyCustomAdapter();

                for(DataSnapshot ds : bidListSnapShot)
                {
                    Iterable<DataSnapshot> bidsSnapShot = ds.getChildren();

                    if(jobId.equals(ds.getKey()))
                    {
                        for(DataSnapshot ds1 : bidsSnapShot)
                        {
                            BidInformation bid = ds1.getValue(BidInformation.class);
                            bid.getUserBid();
                            usersId = bid.getUserID();
//                            myRef = myRef.getRef().child(usersId);
//
//                            myRef.addValueEventListener(new ValueEventListener()
//                            {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot)
//                                {
//                                    name = dataSnapshot.child("fullName").getValue(String.class);
////                                    String number = dataSnapshot.child("phoneNumber").getValue(String.class);
////                                    String address1 = dataSnapshot.child("addressOne").getValue(String.class);
////                                    String address2 = dataSnapshot.child("addressTwo").getValue(String.class);
////                                    String usersTown = dataSnapshot.child("town").getValue(String.class);
////                                    String postalCode = dataSnapshot.child("postCode").getValue(String.class);
////                                    String profileImage = dataSnapshot.child("profileImage").getValue(String.class);
////                                    //boolean advertiser = dataSnapshot.child("advertiser").getValue(boolean.class);
////                                    //boolean courier = dataSnapshot.child("courier").getValue(boolean.class);
//
//                                    Log.d(TAG, "Full Name: " + name);
////                                    Log.d(TAG, "Phone Number: " + number);
////                                    Log.d(TAG, "Address Line One: " + address1);
////                                    Log.d(TAG, "Address Line Two: " + address2);
////                                    Log.d(TAG, "Town: " + usersTown);
////                                    Log.d(TAG, "PostCode: " + postalCode);
////                                    Log.d(TAG, "ProfileImage: " + profileImage);
//                                    //Log.d(TAG, "Advertiser: " + advertiser);
//                                    //Log.d(TAG, "Courier: " + courier);
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError)
//                                {
//
//                                }
//                            });

                            jobList.add(bid);
                        }
                    }
                }

                mAdapter.addArray(jobList);
                jobListView.setAdapter(mAdapter);
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

        private ArrayList<BidInformation> mData = new ArrayList();
        private ArrayList<BidInformation> mDataOrig = new ArrayList();

        private LayoutInflater mInflater;

        public MyCustomAdapter()
        {

            if (isAdded())
            {
                mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
        }

        public void addItem(final BidInformation item)
        {
            mData.add(item);
            mDataOrig.add(item);
        }


        public void addArray(final ArrayList<BidInformation> j)
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
                holder.detailsButton = convertView.findViewById(R.id.detailsButton);
                convertView.setTag(holder);
            } else
            {
                holder = (JobBidsFragment.MyCustomAdapter.GroupViewHolder) convertView.getTag();
            }

            holder.textViewName.setText(name);
            holder.textViewBid.setText(mData.get(position).getUserBid());
            holder.textViewRating.setText(mData.get(position).getUserID());
            holder.detailsButton.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {

                    JobDetailsFragment jobDetailsFragment = new JobDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Job", mData.get(position));
                    jobDetailsFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content, jobDetailsFragment).commit();
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
            public Button detailsButton;
        }

        public void filter(String charText)
        {

            ArrayList<BidInformation> jobs = new ArrayList<BidInformation>();
            ArrayList<BidInformation> jA = new ArrayList<BidInformation>();
            charText = charText.toLowerCase(Locale.getDefault());

            if (charText.length() == 0)
            {
                mData = mDataOrig;
            } else
            {

                for (BidInformation j : mDataOrig)
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
