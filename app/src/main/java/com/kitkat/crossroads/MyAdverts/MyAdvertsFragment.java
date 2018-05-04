package com.kitkat.crossroads.MyAdverts;

import android.content.Context;
import android.net.Uri;
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
import com.kitkat.crossroads.MainActivity.CrossRoadsMainActivity;
import com.kitkat.crossroads.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * My adverts fragment sets up the tab host for pending, active and completed adverts.
 * All of the pending, active and completed adverts for the user are read from the database and
 * added into each tab view.
 */
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

    private MyCustomAdapterForTabViews mAdapter, mAdapterActiveJobs, mAdapterCompleteJobs;

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

    /**
     * * This method is called when the MyAdverts tab is displayed. It creates all of the
     * widgets and functionality that the user can do in the activity.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     *                           This value may be null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        databaseConnections();
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
     * @param container          A view used to contain other views, in this case, the view fragment_my_adverts
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     *                           This value may be null.
     * @return Returns inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_my_adverts, container, false);

        Bundle bundle = getArguments();

        if (bundle != null)
        {
            String tag = bundle.getString("tabView");
            if (tag != null)
            {
                tabTag = tag;
            }
        }

        getViewByIds(view);
        setTabHost();

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                createDataSnapShots(dataSnapshot);
                clearLists();
                try
                {
                    pendingList();
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
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


    /**
     * Establishes connections to the FireBase database
     */
    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        auth = databaseConnections.getAuth();
        databaseReference = databaseConnections.getDatabaseReference();
        databaseReference.keepSynced(true);
        user = databaseConnections.getCurrentUser();
    }

    /**
     * Set widgets in the inflated view to variables within this class
     *
     * @param view gets the job Status tab (pending, active, completed)
     */
    private void getViewByIds(View view)
    {
        host = view.findViewById(R.id.tabHost);
        jobListViewPending = view.findViewById(R.id.jobListViewPending);
        jobListViewActive = view.findViewById(R.id.jobListViewMyActiveJobs);
        jobListViewComplete = view.findViewById(R.id.jobListViewMyCompleteJobs);
        jobSearch = view.findViewById(R.id.searchViewPendingJobs);
    }

    /**
     * Sets titles of Job Status tabs at bottom of the screen
     */
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
     * @param dataSnapshot dataSnapshot used to iterate through the database
     */
    private void createDataSnapShots(DataSnapshot dataSnapshot)
    {
        jobReference = databaseReferences.getTableReference(dataSnapshot, "Jobs");
    }

    /**
     * @return Returns all jobs in the database that match the specified jobReference
     */
    private Iterable<DataSnapshot> getJobListChildren()
    {
        return databaseReferences.getTableChildren(jobReference);
    }

    /**
     * Retrieves all adverts the user has posted with the "Pending" job status
     *
     * @throws ParseException Signals that an error has been reached unexpectedly while parsing.
     */
    private void pendingList() throws ParseException
    {
        for (DataSnapshot ds : getJobListChildren())
        {
            if (genericMethods.getJobInformation(ds).getPosterID().equals(user) && genericMethods.getJobInformation(ds).getJobStatus().equals("Pending"))
            {
                Date sdf = new SimpleDateFormat("dd/MM/yyyy").parse(genericMethods.getJobInformation(ds).getCollectionDate());

                if (new Date().before(sdf))
                {
                    jobListKey.add(ds.getKey());
                    jobList.add(genericMethods.getJobInformation(ds));
                }
            }
        }

        mAdapter = new MyCustomAdapterForTabViews(getActivity(), isAdded(), host, getLayoutInflater(), getFragmentManager());
        mAdapter.addKeyArray(jobListKey);
        mAdapter.addArray(jobList);

        jobListViewPending.setAdapter(mAdapter);
        jobListViewPending.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                PendingAdverts pendingAdverts = new PendingAdverts();
                createBundleToTransfer(pendingAdverts, mAdapter, position);
            }
        });
    }

    /**
     * Retrieves all adverts the user has posted with the "Active" job status
     */
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

        mAdapterActiveJobs = new MyCustomAdapterForTabViews(getActivity(), isAdded(), host, getLayoutInflater(), getFragmentManager());
        mAdapterActiveJobs.addArray(jobListActive);
        mAdapterActiveJobs.addKeyArray(jobListKeyActive);

        jobListViewActive.setAdapter(mAdapterActiveJobs);
        jobListViewActive.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                ActiveAdverts activeAdverts = new ActiveAdverts();
                createBundleToTransfer(activeAdverts, mAdapterActiveJobs, position);
            }
        });
    }

    /**
     * Retrieves all adverts the user has posted with the "Pending" job status
     */
    private void completedAdverts()
    {
        for (DataSnapshot ds : getJobListChildren())
        {
            if (genericMethods.getJobInformation(ds).getPosterID().equals(user) && genericMethods.getJobInformation(ds).getJobStatus().equals(getString(R.string.complete)))
            {
                jobListKeyComplete.add(ds.getKey());
                jobListComplete.add(genericMethods.getJobInformation(ds));
            }

            mAdapterCompleteJobs = new MyCustomAdapterForTabViews(getActivity(), isAdded(), host, getLayoutInflater(), getFragmentManager());
            mAdapterCompleteJobs.addKeyArray(jobListKeyComplete);
            mAdapterCompleteJobs.addArray(jobListComplete);

            jobListViewComplete.setAdapter(mAdapterCompleteJobs);
            jobListViewComplete.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                {
                    CompletedAdverts completedAdverts = new CompletedAdverts();
                    createBundleToTransfer(completedAdverts, mAdapterCompleteJobs, position);
                }
            });
        }
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
     * Creates a new bundle to transfer onto another fragment
     *
     * @param fragment - fragment that the user is transferring to
     * @param adapter  - the array list data that is being transferred
     * @param position - the position of the data that is to be transferred
     */
    public void createBundleToTransfer(Fragment fragment, MyCustomAdapterForTabViews adapter, int position)
    {
        // Creating bundle and adding information to transfer into it
        Bundle bundle = new Bundle();
        bundle.putSerializable("JobId", adapter.mData.get(position));
        bundle.putSerializable("JobKeyId", adapter.mDataKeys.get(position));
        fragment.setArguments(bundle);

        // Transfer to new fragment
        genericMethods.beginTransactionToFragment(getFragmentManager(), fragment);
    }

    /**
     * Find which item has been selected
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /**
     * onAttach             onAttach is called when a fragment is first attached to its context
     * onCreate can be called only after the fragment is attached
     *
     * @param context Allows access to application specific resources and classes, also
     *                supports application-level operations such as receiving intents, launching activities
     */
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

    /**
     * onDetatch
     * When the fragment is no longer attached to the activity, set the listener to null
     */
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

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText String: the new content of the query text field.
     * @return boolean onQueryTextChange (String newText)
     */
    @Override
    public boolean onQueryTextChange(String newText)
    {
        String text = newText;
        mAdapter.filter(text);
        mAdapterActiveJobs.filter(text);
        mAdapterCompleteJobs.filter(text);

        return false;
    }

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }
}
