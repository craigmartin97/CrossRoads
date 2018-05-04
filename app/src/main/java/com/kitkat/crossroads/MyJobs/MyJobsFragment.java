package com.kitkat.crossroads.MyJobs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.DatabaseReferences;
import com.kitkat.crossroads.ExternalClasses.GenericMethods;
import com.kitkat.crossroads.ExternalClasses.MyCustomAdapterForTabViews;
import com.kitkat.crossroads.Jobs.JobInformation;
import com.kitkat.crossroads.Jobs.UserBidInformation;
import com.kitkat.crossroads.MainActivity.CrossRoadsMainActivity;
import com.kitkat.crossroads.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    private final ArrayList<String> jobListKeyActive = new ArrayList<>();
    private final ArrayList<String> jobListKeyComplete = new ArrayList<>();

    /**
     * Search options to search for a job
     */
    private SearchView jobSearchBidOn, jobSearchAccepted, jobSearchCompleted;
    private TabHost host;
    private String tabTag;

    /**
     * CustomAdapter to allow
     */
    private MyCustomAdapterForTabViews mAdapterBidOn, mAdapterAccepted, mAdapterCompleted;

    /**
     * Establish database connection to the FireBase database tables
     */
    private DatabaseReferences databaseReferences = new DatabaseReferences();

    /**
     * Accessing the generic methods
     */
    private GenericMethods genericMethods = new GenericMethods();

    /**
     * OnCreate is called on the creation of Fragment to create a new
     * fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     *                           This value may be null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setDatabaseConnections();
        Bundle bundle = getArguments();

        if (bundle != null)
        {
            String tag = bundle.getString("tabView");
            if (tag != null)
            {
                tabTag = tag;
            }
        } else
        {
            tabTag = "Active";
        }

        ((CrossRoadsMainActivity) getActivity()).wifiCheck();
    }

    /**
     * @param inflater           Instantiates a layout XML file into its corresponding view Objects
     * @param container          A view used to contain other views, in this case, the view fragment_my_jobs
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     *                           This value may be null.
     * @return Returns inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_my_jobs, container, false);

        Bundle bundle = getArguments();

        if (bundle != null)
        {
            String tag = bundle.getString("tabView");
            if (tag != null)
            {
                tabTag = tag;
            }
        }


        getViewsByIds(view);
        setTabHosts();

        // Get data from database
        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                createDataSnapShots(dataSnapshot);
                clearLists();
                try
                {
                    bidOnList();
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
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
     * @param view page to be inflated
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
        databaseReference.keepSynced(true);
    }

    /**
     * Set all of the tab elements features.
     */
    private void setTabHosts()
    {
        //Bid On Tab
        genericMethods.setupTabHost(host, R.id.tab2, "Bid On");

        //Active Tab
        genericMethods.setupTabHost(host, R.id.tab1, "Active");

        //Completed Tab
        genericMethods.setupTabHost(host, R.id.tab3, "Completed");

        // Create the colors and styling of the tab host
        genericMethods.createTabHost(host, tabTag);
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
        return databaseReferences.getTableChildren(jobReference);
    }

    /**
     * Get the Bid Information
     *
     * @param dataSnapshot required to retrieve data from the database
     * @return UserBidInformation
     */
    private UserBidInformation getBidInformation(DataSnapshot dataSnapshot)
    {
        return dataSnapshot.getValue(UserBidInformation.class);
    }

    /**
     * @param dataSnapshot required to retrieve data from the database
     * @return JobInformation       Get the Job Information
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
     * Assign class variables the Firebase tables
     *
     * @param dataSnapshot required to retrieve data from the database
     */
    private void createDataSnapShots(DataSnapshot dataSnapshot)
    {
        DatabaseReferences databaseReferences = new DatabaseReferences();
        bidReference = databaseReferences.getTableReference(dataSnapshot, "Bids");
        jobReference = databaseReferences.getTableReference(dataSnapshot, "Jobs");
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

    /**
     * Get all the jobs I have currently bid on from the Firebase database
     */
    private void bidOnList() throws ParseException
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
                    boolean active = ds1.child("active").getValue(boolean.class);
                    if (active)
                    {
                        jobsListArray.add(ds.getKey());
                    }
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
                Date sdf = new SimpleDateFormat("dd/MM/yyyy").parse(genericMethods.getJobInformation(ds3).getCollectionDate());

                if (new Date().before(sdf))
                {
                    jobListKey.add(ds3.getKey());
                    jobList.add(getJobInformation(ds3));
                }
            }
        }

        // Display information in ListView
        mAdapterBidOn = new MyCustomAdapterForTabViews(getActivity(), isAdded(), host, getLayoutInflater(), getFragmentManager());
        mAdapterBidOn.addKeyArray(jobListKey);
        mAdapterBidOn.addArray(jobList);

        jobListViewBidOn.setAdapter(mAdapterBidOn);
        System.out.println("HEY");

        // Press on the object and go view all the Job Information and Bids
        jobListViewBidOn.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                BidOnJobsFragment bidOnJobsFragment = new BidOnJobsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Job", mAdapterBidOn.mData.get(position));
                bundle.putSerializable("JobId", mAdapterBidOn.mDataKeys.get(position));
                bidOnJobsFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content, bidOnJobsFragment).addToBackStack("tag").commit();
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
                jobListKeyActive.add(ds4.getKey());
                jobListActive.add(getJobInformation(ds4));
            }
        }

        // Display the Job in the ListView
        mAdapterAccepted = new MyCustomAdapterForTabViews(getActivity(), isAdded(), host, getLayoutInflater(), getFragmentManager());
        mAdapterAccepted.addKeyArray(jobListKeyActive);
        mAdapterAccepted.addArray(jobListActive);

        jobListViewMyAcJobs.setAdapter(mAdapterAccepted);

        jobListViewMyAcJobs.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ActiveJobDetailsFragment activeJobDetailsFragment = new ActiveJobDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Job", mAdapterAccepted.mData.get(position));
                bundle.putSerializable("JobId", mAdapterAccepted.mDataKeys.get(position));
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
        for (final DataSnapshot ds5 : getJobListChildren())
        {
            // If the status if active and the current users job
            if (getJobInformation(ds5).getJobStatus().equals("Complete") && getJobInformation(ds5).getCourierID().equals(auth.getCurrentUser().getUid()))
            {
                // Add the key and information
                jobListKeyComplete.add(ds5.getKey());
                jobListComplete.add(getJobInformation(ds5));
            }
        }

        // Display in the ListView
        mAdapterCompleted = new MyCustomAdapterForTabViews(getActivity(), isAdded(), host, getLayoutInflater(), getFragmentManager());
        mAdapterCompleted.addKeyArray(jobListKeyComplete);
        mAdapterCompleted.addArray(jobListComplete);

        jobListViewMyComJobs.setAdapter(mAdapterCompleted);

        jobListViewMyComJobs.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                CompletedJobsFragment completedJobsFragment = new CompletedJobsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Job", mAdapterCompleted.mData.get(position));
                bundle.putSerializable("JobId", mAdapterCompleted.mDataKeys.get(position));
                completedJobsFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content, completedJobsFragment).addToBackStack("tag").commit();
            }
        });
    }

    /**
     * Called when the user submits the query.
     *
     * @param query String: the query text that is to be submitted
     * @return boolean onQueryTextSubmit (String query)
     */
    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText String: the new content of the query text field.
     * @return boolean onQueryTextChange (String newText)
     */
    @Override
    public boolean onQueryTextChange(String newText)
    {
        mAdapterBidOn.filter(newText);
        mAdapterAccepted.filter(newText);
        mAdapterCompleted.filter(newText);
        return false;
    }

    /**
     * Finds which item has been selected
     *
     * @param item - the item that has been selected
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
