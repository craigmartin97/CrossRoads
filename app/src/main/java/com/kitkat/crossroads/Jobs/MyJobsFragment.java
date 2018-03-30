package com.kitkat.crossroads.Jobs;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

/**
 * MyJobsFragment displays all of the jobs associated with the current user signed in
 * It displays all of the users jobs they have Bid On, Accepted On and Completed.
 * It displays these in separate tab views.
 */
public class MyJobsFragment extends Fragment implements SearchView.OnQueryTextListener
{
    /**
     * FirebaseAuth, create a connection to the Firebase Authentication table
     */
    private FirebaseAuth auth;
    /**
     * DatabaseReference, create a connection to the Firebase Database table
     */
    private DatabaseReference databaseReference;
    /**
     * Used to get the children of the bids table
     */
    private DataSnapshot bidReference;
    /**
     * Used to get the children of the jobs table
     */
    private DataSnapshot jobReference;

    /**
     * ListViews are elements on the Fragments layout page
     * used to add users jobs into and display on the page
     */
    private ListView jobListViewBidOn, jobListViewMyAcJobs, jobListViewMyComJobs;

    /**
     * All of the users current jobs they have Bid On are stored in this ArrayList
     */
    private final ArrayList<JobInformation> jobList = new ArrayList<>();
    /**
     * All of the users current accepted, active jobs are stored in this ArrayList
     */
    private final ArrayList<JobInformation> jobListActive = new ArrayList<>();
    /**
     * All of the users previously completed jobs are stored in this ArrayList
     */
    private final ArrayList<JobInformation> jobListComplete = new ArrayList<>();
    /**
     * Stores the jobs key
     */
    private final ArrayList<String> jobListKey = new ArrayList<>();

    private SearchView jobSearchBidOn, jobSearchAccepted, jobSearchCompleted;
    private TabHost host;
    private String tabTag;

    private MyJobsFragment.MyCustomAdapter mAdapterBidOn, mAdapterAccepted, mAdapterCompleted;

    private TextView text;

    /**
     * OnCreate is called on the creation of Fragment to create a new
     * fragment.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setDatabaseConnections();
        tabTag = "Active";
    }

    /**
     * OnCreateView creates all of the graphical initialisations and sets all of the
     * actions of the fragment.
     * <p>
     * This method sets all of the Views elements, creates a new tab host
     * and creates all of the content for the list views.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_my_jobs, container, false);

        getViewsByIds(view);
        setTabHosts();
        createTabHost();

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                createDataSnapShots(dataSnapshot);
                clearLists();
                bidOnList();
                acceptedList();
                completeList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        setSearchOptions();
        return view;
    }

    /**
     * Method sets all of the fragments View elements to class variables
     *
     * @param view
     */
    private void getViewsByIds(View view)
    {
        host = view.findViewById(R.id.tabHost);

        jobListViewBidOn = view.findViewById(R.id.jobListViewBidOn);
        jobListViewMyAcJobs = view.findViewById(R.id.jobListViewMyActiveJobs);
        jobListViewMyComJobs = view.findViewById(R.id.jobListViewMyCompleteJobs);

        jobSearchBidOn = view.findViewById(R.id.searchViewBidOn);
        jobSearchAccepted = view.findViewById(R.id.searchViewAccepted);
        jobSearchCompleted = view.findViewById(R.id.searchViewCompletedJobs);
    }

    /**
     * Creating all of the database connections to firebase
     */
    private void setDatabaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        auth = databaseConnections.getAuth();
        databaseReference = databaseConnections.getDatabaseReference();
    }

    /**
     * Set all of the tab elements features.
     */
    private void setTabHosts()
    {
        host.setup();

        //Bid On Tab
        TabHost.TabSpec spec = host.newTabSpec("Bid On");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Bid On");
        host.addTab(spec);

        //Active Tab
        spec = host.newTabSpec("Active");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Accepted");
        host.addTab(spec);

        //Completed Tab
        spec = host.newTabSpec("Completed Jobs");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Completed");
        host.addTab(spec);
    }

    /**
     * Create all of the tabs and display on the fragment
     */
    private void createTabHost()
    {
        host.setCurrentTabByTag(tabTag);

        // Assigning the color for each tab
        for (int i = 0; i < host.getTabWidget().getChildCount(); i++)
        {
            TextView tv = host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#FFFFFF"));
        }

        // For the current selected tab
        host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundColor(Color.parseColor("#FFFFFF")); // selected
        TextView tv = host.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
        tv.setTextColor(Color.parseColor("#2bbc9b"));

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener()
        {
            @Override
            public void onTabChanged(String tabId)
            {
                for (int i = 0; i < host.getTabWidget().getChildCount(); i++)
                {
                    host.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#2bbc9b")); // unselected
                    TextView tv = host.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
                    tv.setTextColor(Color.parseColor("#FFFFFF"));
                }

                host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundColor(Color.parseColor("#FFFFFF")); // selected
                host.getTabWidget().getChildAt(host.getCurrentTab());
                TextView tv = host.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
                tv.setTextColor(Color.parseColor("#2bbc9b"));

                tabTag = host.getCurrentTabTag();
            }
        });
    }

    /**
     * Get the children of the Bids table
     *
     * @return bidReference
     */
    private Iterable<DataSnapshot> getBidListChildren()
    {
        return bidReference.getChildren();
    }

    /**
     * Get the children of the Jobs table
     *
     * @return jobReference
     */
    private Iterable<DataSnapshot> getJobListChildren()
    {
        return jobReference.getChildren();
    }

    /**
     * Get the Bid Information
     *
     * @param dataSnapshot
     * @return BidInformation
     */
    private BidInformation getBidInformation(DataSnapshot dataSnapshot)
    {
        return dataSnapshot.getValue(BidInformation.class);
    }

    /**
     * Get the Job Information
     *
     * @param dataSnapshot
     * @return JobInformation
     */
    private JobInformation getJobInformation(DataSnapshot dataSnapshot)
    {
        return dataSnapshot.getValue(JobInformation.class);
    }

    /**
     * Setting the search bars features
     */
    private void setSearchOptions()
    {
        jobSearchBidOn.setIconified(false);
        jobSearchAccepted.setIconified(false);
        jobSearchCompleted.setIconified(false);

        jobSearchBidOn.clearFocus();
        jobSearchAccepted.clearFocus();
        jobSearchCompleted.clearFocus();

        jobSearchBidOn.setOnQueryTextListener(this);
        jobSearchAccepted.setOnQueryTextListener(this);
        jobSearchCompleted.setOnQueryTextListener(this);
    }

    /**
     * Create a new Custom Adapter, when the user selects a job it will grab the data
     *
     * @param jobInformation
     * @return adapter
     */
    private MyJobsFragment.MyCustomAdapter createNewCustomAdapter(ArrayList<JobInformation> jobInformation)
    {
        MyJobsFragment.MyCustomAdapter adapter = new MyJobsFragment.MyCustomAdapter();
        adapter.addArray(jobInformation);
        return adapter;
    }

    /**
     * Assign class variables the Firebase tables
     *
     * @param dataSnapshot
     */
    private void createDataSnapShots(DataSnapshot dataSnapshot)
    {
        bidReference = dataSnapshot.child("Bids");
        jobReference = dataSnapshot.child("Jobs");
    }

    /**
     * Clear lists, to avoid duplication error
     */
    private void clearLists()
    {
        jobList.clear();
        jobListActive.clear();
        jobListComplete.clear();
    }

    /**
     * Get all the jobs I have currently bid on from the Firebase database
     */
    private void bidOnList()
    {
        final ArrayList<String> jobsListArray = new ArrayList<>();

        // Iterate through entire bids table
        for (DataSnapshot ds : getBidListChildren())
        {
            // Iterate through the actual bids information
            Iterable<DataSnapshot> bidsSnapShot = ds.getChildren();

            for (DataSnapshot ds1 : bidsSnapShot)
            {
                // if the User Id equals the current user added to a list
                if (getBidInformation(ds1).getUserID().equals(auth.getCurrentUser().getUid()))
                {
                    jobsListArray.add(ds.getKey());
                }
            }
        }

        // Go through the jobs table
        for (DataSnapshot ds3 : getJobListChildren())
        {
            /*
               If the job is in the jobsListArray previously and the status is Pending
                Add to the jobsList
            */
            if (jobsListArray.contains(ds3.getKey()) && getJobInformation(ds3).getJobStatus().equals("Pending"))
            {
                jobList.add(getJobInformation(ds3));
            }
        }

        // Display information in ListView
        final MyJobsFragment.MyCustomAdapter adapter = createNewCustomAdapter(jobList);
        mAdapterBidOn = adapter;
        jobListViewBidOn.setAdapter(adapter);

        // Press on the object and go view all the Job Information and Bids
        jobListViewBidOn.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                BidDetailsFragment bidDetailsFragment = new BidDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Job", adapter.mData.get(position));
                bidDetailsFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content, bidDetailsFragment).addToBackStack("tag").commit();
            }
        });
    }

    /**
     * Get the jobs that I have currently been accepted for and am actively doing
     */
    private void acceptedList()
    {
        // Go through the Jobs table
        for (DataSnapshot ds4 : getJobListChildren())
        {
            // If the status if active and the current users job
            if (getJobInformation(ds4).getJobStatus().equals("Active") && getJobInformation(ds4).getCourierID().equals(auth.getCurrentUser().getUid()))
            {
                // Add the key and information
                jobListKey.add(ds4.getKey());
                jobListActive.add(getJobInformation(ds4));
            }
        }

        // Display the Job in the ListView
        final MyJobsFragment.MyCustomAdapter adapterActiveJobs = createNewCustomAdapter(jobListActive);
        adapterActiveJobs.addKeyArray(jobListKey);
        mAdapterAccepted = adapterActiveJobs;
        jobListViewMyAcJobs.setAdapter(adapterActiveJobs);

        jobListViewMyAcJobs.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ActiveJobDetailsFragment activeJobDetailsFragment = new ActiveJobDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Job", adapterActiveJobs.mData.get(position));
                bundle.putSerializable("JobId", adapterActiveJobs.mDataKeys.get(position));
                activeJobDetailsFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content, activeJobDetailsFragment).addToBackStack(host.getCurrentTabTag()).commit();
            }
        });
    }

    /**
     * Get all the jobs that I have completed
     */
    private void completeList()
    {
        // Go through the Jobs table
        for (DataSnapshot ds5 : getJobListChildren())
        {
            // If the status if complete and the current users job
            if (getJobInformation(ds5).getJobStatus().equals("Complete") && getJobInformation(ds5).getCourierID().equals(auth.getCurrentUser().getUid()))
            {
                jobListComplete.add(getJobInformation(ds5));
            }
        }

        // Display in the ListView
        final MyJobsFragment.MyCustomAdapter adapterCompletedJobs = createNewCustomAdapter(jobListComplete);
        mAdapterCompleted = adapterCompletedJobs;
        jobListViewMyComJobs.setAdapter(adapterCompletedJobs);

        jobListViewMyComJobs.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                BidDetailsFragment bidDetailsFragment = new BidDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Job", adapterCompletedJobs.mData.get(position));
                bidDetailsFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content, bidDetailsFragment).addToBackStack("tag").commit();
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        mAdapterBidOn.filter(newText);
        mAdapterAccepted.filter(newText);
        mAdapterCompleted.filter(newText);
        return false;
    }

    public class MyCustomAdapter extends BaseAdapter
    {
        private ArrayList<JobInformation> mData = new ArrayList<>();
        private ArrayList<JobInformation> mDataOrig = new ArrayList<>();
        private ArrayList<String> mDataKeys = new ArrayList<>();

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

        public void addKeyArray(final ArrayList<String> k)
        {
            mDataKeys.clear();
            mDataKeys = k;
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
        public void registerDataSetObserver(DataSetObserver observer)
        {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer)
        {

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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            // Bid on holder
            MyJobsFragment.MyCustomAdapter.GroupViewHolderBidOn holderBidOn;
            // Accepted holder
            final MyJobsFragment.MyCustomAdapter.GroupViewHolderAccepted holderAccepted;
            // Completed holder
            MyJobsFragment.MyCustomAdapter.GroupViewHolderCompleted holderCompleted;

            if (convertView == null)
            {
                // Bid on
                if (host.getCurrentTab() == 0)
                {
                    convertView = mInflater.inflate(R.layout.job_info_bid_on, null);
                    holderBidOn = new MyJobsFragment.MyCustomAdapter.GroupViewHolderBidOn();

                    holderBidOn.textViewJobName = convertView.findViewById(R.id.textName);
                    holderBidOn.textViewJobDescription = convertView.findViewById(R.id.textDesc);

                    holderBidOn.textViewJobName.setText(mData.get(position).getAdvertName());
                    holderBidOn.textViewJobDescription.setText(mData.get(position).getAdvertDescription());

                    convertView.setTag(holderBidOn);
                }
                // Accepted
                else if (host.getCurrentTab() == 1)
                {
                    convertView = mInflater.inflate(R.layout.job_info_accepted, null);
                    holderAccepted = new MyJobsFragment.MyCustomAdapter.GroupViewHolderAccepted();

                    holderAccepted.textViewJobName = convertView.findViewById(R.id.textName);
                    holderAccepted.textViewDescription = convertView.findViewById(R.id.textDesc);

                    holderAccepted.textViewJobName.setText(mData.get(position).getAdvertName());
                    holderAccepted.textViewDescription.setText(mData.get(position).getAdvertDescription());

                    // TODO - Was going to add the the users bid into this here, however it's difficult as the bid isnt stored in the jobs table
                    // TODO - I tried to add it in, looped through the bids table and found the bids with the mDataKeys. But it always displayed the last value.

                    convertView.setTag(holderAccepted);
                }
                // Completed
                else if (host.getCurrentTab() == 2)
                {
                    convertView = mInflater.inflate(R.layout.job_info_list_completed, null);

                    holderCompleted = new MyJobsFragment.MyCustomAdapter.GroupViewHolderCompleted();

                    holderCompleted.textViewJobName = convertView.findViewById(R.id.textName);
                    holderCompleted.textViewJobName.setText(mData.get(position).getAdvertName());

                    convertView.setTag(holderCompleted);
                }
            } else
            {
                if (host.getCurrentTab() == 0)
                {
                    holderBidOn = (MyJobsFragment.MyCustomAdapter.GroupViewHolderBidOn) convertView.getTag();
                } else if (host.getCurrentTab() == 1)
                {
                    holderAccepted = (MyJobsFragment.MyCustomAdapter.GroupViewHolderAccepted) convertView.getTag();
                } else if (host.getCurrentTab() == 2)
                {
                    holderCompleted = (MyJobsFragment.MyCustomAdapter.GroupViewHolderCompleted) convertView.getTag();
                }
            }

            return convertView;
        }

        public class GroupViewHolderBidOn
        {
            public TextView textViewJobName;
            public TextView textViewJobDescription;
        }

        public class GroupViewHolderAccepted
        {
            public TextView textViewJobName;
            public TextView textViewDescription;
        }

        public class GroupViewHolderCompleted
        {
            public TextView textViewJobName;
        }

        public void filter(String charText)
        {
            ArrayList<JobInformation> jobs = new ArrayList<>();
            ArrayList<JobInformation> jA = new ArrayList<>();
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
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
