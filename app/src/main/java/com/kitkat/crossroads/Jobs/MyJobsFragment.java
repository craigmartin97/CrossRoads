package com.kitkat.crossroads;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.Jobs.ActiveJobDetailsFragment;
import com.kitkat.crossroads.Jobs.BidDetailsFragment;
import com.kitkat.crossroads.Jobs.BidInformation;
import com.kitkat.crossroads.Jobs.BidInformation;
import com.kitkat.crossroads.Jobs.JobInformation;

import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyJobsFragment
 * .OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyJobsFragment
 * #newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyJobsFragment extends Fragment implements SearchView.OnQueryTextListener
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DataSnapshot bidReference;
    private DataSnapshot jobReference;

    private ArrayList<String> userBidId = new ArrayList<String>();
    private ArrayList<String> userJobId = new ArrayList<String>();

    private MyJobsFragment.MyCustomAdapter mAdapter, mAdapterActiveJobs, mAdapterCompleteJobs;

    private ArrayList<JobInformation> jobList = new ArrayList<>();
    private ArrayList<JobInformation> jobListActive = new ArrayList<>();
    private ArrayList<JobInformation> jobListComplete = new ArrayList<>();


    private ListView jobListView, jobListViewMyAcJobs, jobListViewMyComJobs;

    private SearchView jobSearch;

    private TabHost host;

    private TabHost tabHost;

    public MyJobsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyJobsFragment
     * .
     */
    // TODO: Rename and change types and number of parameters
    public static MyJobsFragment newInstance(String param1, String param2)
    {
        MyJobsFragment fragment = new MyJobsFragment();
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
        final View view = inflater.inflate(R.layout.fragment_my_jobs, container, false);


        host = (TabHost) view.findViewById(R.id.tabHost);
        host.setup();


        //Tab 2
        TabHost.TabSpec spec = host.newTabSpec("Active");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Active");
        host.addTab(spec);

        //Tab 1
        spec = host.newTabSpec("Bid On");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Bid On");
        host.addTab(spec);


        //Tab 3
        spec = host.newTabSpec("Completed Jobs");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Completed");
        host.addTab(spec);

        for (int i = 0; i < host.getTabWidget().getChildCount(); i++)
        {
            TextView tv = (TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#FFFFFF"));
        }

        host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundColor(Color.parseColor("#FFFFFF")); // selected
        TextView tv = (TextView) host.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
        tv.setTextColor(Color.parseColor("#2bbc9b"));

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener()
        {

            @Override
            public void onTabChanged(String tabId)
            {

                for (int i = 0; i < host.getTabWidget().getChildCount(); i++)
                {
                    host.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#2bbc9b")); // unselected
                    TextView tv = (TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
                    tv.setTextColor(Color.parseColor("#FFFFFF"));
                }

                host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundColor(Color.parseColor("#FFFFFF")); // selected
                TextView tv = (TextView) host.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
                tv.setTextColor(Color.parseColor("#2bbc9b"));

            }
        });


        jobListView = view.findViewById(R.id.jobListView1);
        jobListViewMyAcJobs = view.findViewById(R.id.jobListViewMyActiveJobs);
        jobListViewMyComJobs = view.findViewById(R.id.jobListViewMyCompleteJobs);

        final ArrayList<String> jobsListArray = new ArrayList<>();

        final ArrayList<String> activeJobsListArray = new ArrayList<>();


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                bidReference = dataSnapshot.child("Bids");
                jobReference = dataSnapshot.child("Jobs");

                Iterable<DataSnapshot> bidListSnapShot = bidReference.getChildren();
                Iterable<DataSnapshot> jobListSnapShot = jobReference.getChildren();

                mAdapter = new MyCustomAdapter();
                mAdapterActiveJobs = new MyCustomAdapter();
                mAdapterCompleteJobs = new MyCustomAdapter();

                for (DataSnapshot ds : bidListSnapShot)
                {

                    Iterable<DataSnapshot> bidsSnapShot = ds.getChildren();


                    for (DataSnapshot ds1 : bidsSnapShot)
                    {
                        BidInformation bid = ds1.getValue(BidInformation.class);


                        if (bid.getUserID().equals(auth.getCurrentUser().getUid()))
                        {
                            jobsListArray.add(ds.getKey());
                        }
                    }
                }

                for (DataSnapshot ds3 : jobListSnapShot)
                {
                    if (jobsListArray.contains(ds3.getKey()))
                    {
                        JobInformation j = ds3.getValue(JobInformation.class);
                        if(j.getJobStatus().equals("Pending")) {
                            jobList.add(j);
                        }
                    }

                }
                mAdapter.addArray(jobList);
                jobListView.setAdapter(mAdapter);

                jobListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        BidDetailsFragment bidDetailsFragment = new BidDetailsFragment();

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Job", mAdapter.mData.get(position));
                        bidDetailsFragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content, bidDetailsFragment).addToBackStack("tag").commit();

                    }
                });

                Iterable<DataSnapshot> activeJobListSnapShot = jobReference.getChildren();


                for (DataSnapshot ds4 : activeJobListSnapShot)
                {
                    JobInformation j = ds4.getValue(JobInformation.class);
                    if(j.getJobStatus().equals("Active") && j.getCourierID().equals(auth.getCurrentUser().getUid()))
                    {
                        jobListActive.add(j);
                    }

                }
                mAdapterActiveJobs.addArray(jobListActive);
                jobListViewMyAcJobs.setAdapter(mAdapterActiveJobs);

                jobListViewMyAcJobs.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        ActiveJobDetailsFragment activeJobDetailsFragment = new ActiveJobDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Job", mAdapterActiveJobs.mData.get(position));
                        activeJobDetailsFragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content, activeJobDetailsFragment).addToBackStack(host.getCurrentTabTag()).commit();

                    }
                });


                Iterable<DataSnapshot> completeJobListSnapShot = jobReference.getChildren();


                for (DataSnapshot ds5 : completeJobListSnapShot)
                {
                    JobInformation j = ds5.getValue(JobInformation.class);
                    if(j.getJobStatus().equals("Complete") && j.getCourierID().equals(auth.getCurrentUser().getUid()))
                    {
                        jobListComplete.add(j);
                    }

                }
                mAdapterCompleteJobs.addArray(jobListComplete);
                jobListViewMyComJobs.setAdapter(mAdapterCompleteJobs);


            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        jobSearch = (SearchView) view.findViewById(R.id.searchViewJob);
        jobSearch.setIconified(false);
        jobSearch.clearFocus();

        jobSearch.setOnQueryTextListener(this);

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

        private ArrayList<JobInformation> mData = new ArrayList();
        private ArrayList<JobInformation> mDataOrig = new ArrayList();

        private LayoutInflater mInflater;

        public MyCustomAdapter()
        {

            if (isAdded())
            {
                mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
        }

        public void addItem(final JobInformation item)
        {
            mData.add(item);
            mDataOrig.add(item);
        }


        public void addArray(final ArrayList<JobInformation> j)
        {
            mData.clear();
            mDataOrig.clear();
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
            MyJobsFragment.MyCustomAdapter.GroupViewHolder holder;
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.job_info_list, null);
                holder = new MyJobsFragment.MyCustomAdapter.GroupViewHolder();
                holder.textViewName = convertView.findViewById(R.id.textName);
                holder.textViewFrom = convertView.findViewById(R.id.textFrom);
                holder.textViewTo = convertView.findViewById(R.id.textTo);
                //holder.detailsButton = convertView.findViewById(R.id.detailsButton);
                convertView.setTag(holder);
            } else
            {
                holder = (MyJobsFragment.MyCustomAdapter.GroupViewHolder) convertView.getTag();
            }

            holder.textViewName.setText(mData.get(position).getAdvertName());
            holder.textViewFrom.setText(mData.get(position).getColL1());
            holder.textViewTo.setText(mData.get(position).getDelL1());
//            holder.detailsButton.setOnClickListener(new View.OnClickListener()
//            {
//
//                @Override
//                public void onClick(View v)
//                {
//
//                    BidDetailsFragment bidDetailsFragment = new BidDetailsFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("Job", mData.get(position));
//                    bidDetailsFragment.setArguments(bundle);
//                    FragmentManager fragmentManager = getFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.content, bidDetailsFragment).addToBackStack(host.getCurrentTabTag()).commit();
//                }
//            });
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

        public void filter(String charText)
        {

            ArrayList<JobInformation> jobs = new ArrayList<JobInformation>();
            ArrayList<JobInformation> jA = new ArrayList<JobInformation>();
            charText = charText.toLowerCase(Locale.getDefault());

            if (charText.length() == 0)
            {
                mData = mDataOrig;
            } else
            {

                for (JobInformation j : mDataOrig)
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}