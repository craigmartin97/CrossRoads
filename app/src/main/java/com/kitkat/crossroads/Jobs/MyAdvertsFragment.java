package com.kitkat.crossroads.Jobs;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.DatabaseReferences;
import com.kitkat.crossroads.ExternalClasses.GenericMethods;
import com.kitkat.crossroads.ExternalClasses.MyCustomAdapter;
import com.kitkat.crossroads.R;

import java.util.ArrayList;
import java.util.Locale;

public class MyAdvertsFragment extends Fragment implements SearchView.OnQueryTextListener
{
    /**
     * To detach and attach fragment
     */
    private OnFragmentInteractionListener mListener;
    /**
     * Create connection to Firebase Authentication
     */
    private FirebaseAuth auth;

    /**
     * Create reference to access the Firebase database
     */
    private DatabaseReference databaseReference;

    /**
     * Get the current users unique ID
     */
    private String user;

    /**
     * Create a snapshot of the Firebase Database, for the jobs table
     */
    private DataSnapshot jobReference;

//    private MyAdvertsFragment.MyCustomAdapter mAdapter,
//            mAdapterActiveJobs,
//            mAdapterCompleteJobs;

    private com.kitkat.crossroads.ExternalClasses.MyCustomAdapter mAdapter2;

    private ArrayList<JobInformation> jobList = new ArrayList<>();
    private ArrayList<JobInformation> jobListActive = new ArrayList<>();
    private ArrayList<JobInformation> jobListComplete = new ArrayList<>();

    /**
     * Stores the jobs key
     */
    private final ArrayList<String> jobListKey = new ArrayList<>();
    private final ArrayList<String> jobListKeyActive = new ArrayList<>();
    private final ArrayList<String> jobListKeyComplete = new ArrayList<>();

    private ListView jobListViewPending, jobListViewActive, jobListViewComplete;

    private SearchView jobSearch;

    private TabHost host;
    private String tabTag;

    private GenericMethods genericMethods = new GenericMethods();

    private DatabaseReferences databaseReferences = new DatabaseReferences();

    public MyAdvertsFragment()
    {
        // Required empty public constructor
    }

    public static MyAdvertsFragment newInstance()
    {
        MyAdvertsFragment fragment = new MyAdvertsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        databaseConnections();
        tabTag = "Active";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_my_adverts, container, false);

        getViewByIds(view);
        setTabHost();


        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                createDataSnapShots(dataSnapshot);
                clearLists();
                pendingList();
                activeList();
                completedAdverts();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        jobSearch.setIconified(false);
        jobSearch.clearFocus();
        jobSearch.setOnQueryTextListener(this);

        return view;

    }

    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        auth = databaseConnections.getAuth();
        databaseReference = databaseConnections.getDatabaseReference();
        user = databaseConnections.getCurrentUser();
    }

    private void getViewByIds(View view)
    {
        host = (TabHost) view.findViewById(R.id.tabHost);
        jobListViewPending = (ListView) view.findViewById(R.id.jobListViewPending);
        jobListViewActive = view.findViewById(R.id.jobListViewMyActiveJobs);
        jobListViewComplete = view.findViewById(R.id.jobListViewMyCompleteJobs);
        jobSearch = (SearchView) view.findViewById(R.id.searchViewPendingJobs);
    }

    private void setTabHost()
    {
        //Tab 1
        genericMethods.setupTabHost(host, R.id.tab1, "Pending");

        //Tab 2
        genericMethods.setupTabHost(host, R.id.tab2, "Active");

        //Tab 3
        genericMethods.setupTabHost(host, R.id.tab3, "Completed");

        // Create the colors and styling of the tab host
        genericMethods.createTabHost(host, tabTag);
    }

    /**
     * Assign class variables the Firebase tables
     *
     * @param dataSnapshot
     */
    private void createDataSnapShots(DataSnapshot dataSnapshot)
    {
        jobReference = databaseReferences.getTableReference(dataSnapshot, "Jobs");
    }

    private Iterable<DataSnapshot> getJobListChildren()
    {
        return databaseReferences.getTableChildren(jobReference);
    }

    /**
     * Create a new Custom Adapter, when the user selects a job it will grab the data
     *
     * @param jobInformation
     * @return adapter
     */
//    private MyAdvertsFragment.MyCustomAdapter createNewCustomAdapter(ArrayList<JobInformation> jobInformation)
//    {
//        MyAdvertsFragment.MyCustomAdapter adapter = new MyAdvertsFragment.MyCustomAdapter();
//        adapter.addArray(jobInformation);
//        return adapter;
//    }

    private void pendingList()
    {
//        for (DataSnapshot ds : getJobListChildren())
//        {
//            if (genericMethods.getJobInformation(ds).getPosterID().equals(user) && genericMethods.getJobInformation(ds).getJobStatus().equals("Pending"))
//            {
//                jobListKey.add(ds.getKey());
//                jobList.add(genericMethods.getJobInformation(ds));
//            }
//        }
//
//        mAdapter = new MyAdvertsFragment.MyCustomAdapter();
//        mAdapter.addKeyArray(jobListKey);
//        mAdapter.addArray(jobList);
//
//        jobListViewPending.setAdapter(mAdapter);
//        jobListViewPending.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//            {
//                PendingAdverts pendingAdverts = new PendingAdverts();
//                createBundleToTransfer(pendingAdverts, mAdapter, position);
//            }
//        });
    }

    private void activeList()
    {
        for (DataSnapshot ds : getJobListChildren())
        {
            if (genericMethods.getJobInformation(ds).getPosterID().equals(user) && genericMethods.getJobInformation(ds).getJobStatus().equals("Active"))
            {
                jobListKeyActive.add(ds.getKey());
                jobListActive.add(genericMethods.getJobInformation(ds));
            }
        }

        mAdapter2 = new com.kitkat.crossroads.ExternalClasses.MyCustomAdapter(getActivity(), isAdded(), host);
        mAdapter2.addArray(jobListActive);
        mAdapter2.addKeyArray(jobListKeyActive);

        jobListViewActive.setAdapter(mAdapter2);
        jobListViewActive.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                ActiveAdverts activeAdverts = new ActiveAdverts();
                createBundleToTransfer(activeAdverts, mAdapter2, position);
            }
        });
    }

    private void completedAdverts()
    {
//        for (DataSnapshot ds : getJobListChildren())
//        {
//            if (genericMethods.getJobInformation(ds).getPosterID().equals(user) && genericMethods.getJobInformation(ds).getJobStatus().equals("Complete"))
//            {
//                jobListKeyComplete.add(ds.getKey());
//                jobListComplete.add(genericMethods.getJobInformation(ds));
//            }
//
//            mAdapterCompleteJobs = new MyAdvertsFragment.MyCustomAdapter();
//            mAdapterCompleteJobs.addKeyArray(jobListKeyComplete);
//            mAdapterCompleteJobs.addArray(jobListComplete);
//
//            jobListViewComplete.setAdapter(mAdapterCompleteJobs);
//            jobListViewComplete.setOnItemClickListener(new AdapterView.OnItemClickListener()
//            {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
//                {
//                    CompletedAdverts completedAdverts = new CompletedAdverts();
//                    createBundleToTransfer(completedAdverts, mAdapterCompleteJobs, position);
//                }
//            });
//        }
    }

    /**
     * Clear lists, to avoid duplication error
     */
    private void clearLists()
    {
        genericMethods.clearLists(jobList);
        genericMethods.clearLists(jobListActive);
        genericMethods.clearLists(jobListComplete);
    }

    public void createBundleToTransfer(Fragment fragment, com.kitkat.crossroads.ExternalClasses.MyCustomAdapter adapter, int position)
    {
        // Creating bundle and adding information to transfer into it
        Bundle bundle = new Bundle();
        bundle.putSerializable("JobId", adapter.mData.get(position));
        bundle.putSerializable("JobKeyId", adapter.mDataKeys.get(position));
        fragment.setArguments(bundle);

        // Transfer to new fragment
        genericMethods.beginTransactionToFragment(getFragmentManager(), fragment);
    }


//    public class MyCustomAdapter extends BaseAdapter
//    {
//        private ArrayList<JobInformation> mData = new ArrayList<>();
//        private ArrayList<JobInformation> mDataOrig = new ArrayList<>();
//        private ArrayList<String> mDataKeys = new ArrayList<>();
//
//        private LayoutInflater mInflater;
//
//        public MyCustomAdapter()
//        {
//            if (isAdded())
//            {
//                mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            }
//        }
//
//        public void addItem(final JobInformation item)
//        {
//            mData.add(item);
//            mDataOrig.add(item);
//        }
//
//
//        public void addArray(final ArrayList<JobInformation> j)
//        {
//            mData.clear();
//            mDataOrig.clear();
//            mData = j;
//            mDataOrig = j;
//        }
//
//        public void addKeyArray(final ArrayList<String> k)
//        {
//            mDataKeys.clear();
//            mDataKeys = k;
//        }
//
//        @Override
//        public int getCount()
//        {
//            return mData.size();
//        }
//
//        @Override
//        public Object getItem(int position)
//        {
//            return mData.get(position);
//        }
//
//        @Override
//        public long getItemId(int position)
//        {
//            return 0;
//        }
//
//        @Override
//        public boolean hasStableIds()
//        {
//            return false;
//        }
//
//        @Override
//        public void registerDataSetObserver(DataSetObserver observer)
//        {
//
//        }
//
//        @Override
//        public void unregisterDataSetObserver(DataSetObserver observer)
//        {
//
//        }
//
//        @Override
//        public boolean areAllItemsEnabled()
//        {
//            return false;
//        }
//
//        @Override
//        public boolean isEmpty()
//        {
//            return false;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent)
//        {
//            // Bid on holder
//            MyAdvertsFragment.MyCustomAdapter.GroupViewHolderBidOn holderBidOn;
//            // Accepted holder
//            final MyAdvertsFragment.MyCustomAdapter.GroupViewHolderAccepted holderAccepted;
//            // Completed holder
//            MyAdvertsFragment.MyCustomAdapter.GroupViewHolderCompleted holderCompleted;
//
//            if (convertView == null)
//            {
//                // Bid on
//                if (host.getCurrentTab() == 0)
//                {
//                    convertView = mInflater.inflate(R.layout.job_info_bid_on, null);
//                    holderBidOn = new MyAdvertsFragment.MyCustomAdapter.GroupViewHolderBidOn();
//
//                    holderBidOn.textViewJobName = convertView.findViewById(R.id.textName);
//                    holderBidOn.textViewJobDescription = convertView.findViewById(R.id.textDesc);
//                    holderBidOn.textViewAddressFrom = convertView.findViewById(R.id.textAddressFrom);
//                    holderBidOn.textViewAddressTo = convertView.findViewById(R.id.textAddressTo);
//
//                    holderBidOn.textViewJobName.setText(mData.get(position).getAdvertName());
//                    holderBidOn.textViewJobDescription.setText(mData.get(position).getAdvertDescription());
//                    holderBidOn.textViewAddressFrom.setText(mData.get(position).getColL1() + ", " + mData.get(position).getColTown() + ", " + mData.get(position).getColPostcode());
//                    holderBidOn.textViewAddressTo.setText(mData.get(position).getDelL1() + ", " + mData.get(position).getDelPostcode() + ", " + mData.get(position).getDelPostcode());
//
//                    convertView.setTag(holderBidOn);
//                }
//                // Accepted
//                else if (host.getCurrentTab() == 1)
//                {
//                    convertView = mInflater.inflate(R.layout.job_info_accepted, null);
//                    holderAccepted = new MyAdvertsFragment.MyCustomAdapter.GroupViewHolderAccepted();
//
//                    holderAccepted.textViewJobName = convertView.findViewById(R.id.textName);
//                    holderAccepted.textViewDescription = convertView.findViewById(R.id.textDesc);
//                    holderAccepted.textViewAddressFrom = convertView.findViewById(R.id.textAddressFrom);
//                    holderAccepted.textViewAddressTo = convertView.findViewById(R.id.textAddressTo);
//
//                    holderAccepted.textViewJobName.setText(mData.get(position).getAdvertName());
//                    holderAccepted.textViewDescription.setText(mData.get(position).getAdvertDescription());
//                    holderAccepted.textViewAddressFrom.setText(mData.get(position).getColL1() + ", " + mData.get(position).getColTown() + ", " + mData.get(position).getColPostcode());
//                    holderAccepted.textViewAddressTo.setText(mData.get(position).getDelL1() + ", " + mData.get(position).getDelPostcode() + ", " + mData.get(position).getDelPostcode());
//
//                    convertView.setTag(holderAccepted);
//                }
//                // Completed
//                else if (host.getCurrentTab() == 2)
//                {
//                    convertView = mInflater.inflate(R.layout.job_info_list_completed, null);
//
//                    holderCompleted = new MyAdvertsFragment.MyCustomAdapter.GroupViewHolderCompleted();
//
//                    holderCompleted.textViewJobName = convertView.findViewById(R.id.textName);
//                    holderCompleted.textViewJobName.setText(mData.get(position).getAdvertName());
//
//                    convertView.setTag(holderCompleted);
//                }
//            } else
//            {
//                if (host.getCurrentTab() == 0)
//                {
//                    holderBidOn = (MyAdvertsFragment.MyCustomAdapter.GroupViewHolderBidOn) convertView.getTag();
//                } else if (host.getCurrentTab() == 1)
//                {
//                    holderAccepted = (MyAdvertsFragment.MyCustomAdapter.GroupViewHolderAccepted) convertView.getTag();
//                } else if (host.getCurrentTab() == 2)
//                {
//                    holderCompleted = (MyAdvertsFragment.MyCustomAdapter.GroupViewHolderCompleted) convertView.getTag();
//                }
//            }
//
//            return convertView;
//        }
//
//        public class GroupViewHolderBidOn
//        {
//            public TextView textViewJobName;
//            public TextView textViewJobDescription;
//            public TextView textViewAddressFrom;
//            public TextView textViewAddressTo;
//        }
//
//        public class GroupViewHolderAccepted
//        {
//            public TextView textViewJobName;
//            public TextView textViewDescription;
//            public TextView textViewAddressFrom;
//            public TextView textViewAddressTo;
//        }
//
//        public class GroupViewHolderCompleted
//        {
//            public TextView textViewJobName;
//        }
//
//        public void filter(String charText)
//        {
//            ArrayList<JobInformation> jobs = new ArrayList<>();
//            ArrayList<JobInformation> jA = new ArrayList<>();
//            charText = charText.toLowerCase(Locale.getDefault());
//
//            if (charText.length() == 0)
//            {
//                mData = mDataOrig;
//            } else
//            {
//                for (JobInformation j : mDataOrig)
//                {
//                    if (j.getWholeString().toLowerCase(Locale.getDefault()).contains(charText))
//                    {
//                        jobs.add(j);
//                        jA.add(j);
//                    } else
//                    {
//                        jA.add(j);
//                    }
//                }
//                mData.clear();
//                mData = jobs;
//                mDataOrig = jA;
//            }
//
//            notifyDataSetChanged();
//        }
//    }

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
        mAdapter2.filter(text);

        return false;
    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
