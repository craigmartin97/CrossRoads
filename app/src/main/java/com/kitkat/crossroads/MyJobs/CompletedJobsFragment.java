package com.kitkat.crossroads.MyJobs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.ExpandableListAdapter;
import com.kitkat.crossroads.ExternalClasses.ListViewHeight;
import com.kitkat.crossroads.Jobs.JobInformation;
import com.kitkat.crossroads.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * This class displays the job information for a job they have just bid on.
 * They can also view their bid they made and edit that bid.
 * They can also delete the bid from here as well.
 */
public class CompletedJobsFragment extends Fragment
{
    /**
     * Text Views to display the jobs name and description
     */
    private TextView jobName, jobDescription, textViewUsersBid;

    /**
     * ImageView, to store and display the Jobs Image
     */
    private ImageView jobImageCompleted;
    private ProgressBar progressBar;

    /**
     * Strings to store the jobs information passed in by a bundle
     */
    private String colDate, colTime, colAddress, colTown, colPostcode, delAddress, delTown, delPostcode, jobType, jobSize;

    /**
     * Expandable list views to store the job information in
     */
    private ExpandableListView expandableListView, expandableListView2, expandableListView3;

    /**
     * Adapters to process and handle the data in the expandable list views
     */
    private ExpandableListAdapter adapter, adapter2, adapter3;

    /**
     * Lists to store the information in
     */
    private List<String> list, list2, list3;
    private HashMap<String, List<String>> listHashMap, listHashMap2, listHashMap3;

    /**
     * Creating variable to store the connection to the Firebase Database
     */
    private DatabaseReference databaseReferenceBidsTable;

    /**
     * Getting the current users Id
     */
    private String user;

    private String jobId;

    /**
     * * This method is called when CompletedJobsFragment is displayed. It creates all of the
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
    }

    /**
     * @param inflater           Instantiates a layout XML file into its corresponding view Objects
     * @param container          A view used to contain other views, in this case, the view fragment_my_completed_jobs
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     *                           This value may be null.
     * @return Returns inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_my_completed_jobs, container, false);

        getViewsByIds(view);
        final JobInformation jobInformation = getBundleInformation();

        setJobInformationDetails(jobInformation);
        getJobInformationFromBundle(jobInformation);

        addItemsCollection();
        addItemsDelivery();
        addItemsJobInformation();
        createExpandableListViews();

        return view;
    }

    /**
     * Establishing FireBase connections to access data
     */
    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        databaseReferenceBidsTable = databaseConnections.getDatabaseReferenceBids();
        databaseReferenceBidsTable.keepSynced(true);
        user = databaseConnections.getCurrentUser();
    }

    /**
     * Get all of the layout pages content, such as TextViews and the Expandable Lists
     *
     * @param view - page to be inflated
     */
    private void getViewsByIds(View view)
    {
        jobName = view.findViewById(R.id.textViewJobName1);
        jobDescription = view.findViewById(R.id.textViewJobDescription1);
        jobImageCompleted = view.findViewById(R.id.jobImageCompleted);
        textViewUsersBid = view.findViewById(R.id.textViewAcceptedBid);
        progressBar = view.findViewById(R.id.progressBar);

        expandableListView = view.findViewById(R.id.expandable_list_view);
        expandableListView2 = view.findViewById(R.id.expandable_list_view2);
        expandableListView3 = view.findViewById(R.id.expandable_list_view3);
    }

    /**
     * Setting all of the information from the bundle that has been passed across to the TextViews
     * And storing them in strings for future use
     *
     * @param jobInformation - Information passed from a bundle that contains that Job Information
     */
    private void setJobInformationDetails(JobInformation jobInformation)
    {
        // Setting text in the TextViews
        jobName.setText(jobInformation.getAdvertName());
        jobDescription.setText(jobInformation.getAdvertDescription());
        Picasso.get().load(jobInformation.getJobImage()).fit().into(jobImageCompleted, new Callback()
        {
            @Override
            public void onSuccess()
            {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e)
            {

            }
        });

        databaseReferenceBidsTable.child(jobId).child(user).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String userBid = dataSnapshot.child(getString(R.string.user_bid_table)).getValue(String.class);
                textViewUsersBid.setText("Agreed Fee:       £" + userBid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    /**
     * Assigning all the data from the passed bundle into variables for later use
     *
     * @param jobInformation JobInformation: Object that contains all data about a job
     */
    private void getJobInformationFromBundle(JobInformation jobInformation)
    {
        // Storing information in variables for later use
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
    }

    /**
     * Getting all arguments from the bundle that was passed across
     *
     * @return jobInformation
     */
    private JobInformation getBundleInformation()
    {
        Bundle bundle = getArguments();
        jobId = (String) bundle.getSerializable(getString(R.string.job_id));
        return (JobInformation) bundle.getSerializable(getString(R.string.job_key));
    }

    /**
     * Adding information into Expandable list collection information
     */
    private void addItemsCollection()
    {
        list = new ArrayList<>();
        listHashMap = new HashMap<>();

        list.add(getString(R.string.collection_information));

        List<String> collectionInfo = new ArrayList<>();
        collectionInfo.add(colDate);
        collectionInfo.add(colTime);
        collectionInfo.add(colAddress);
        collectionInfo.add(colTown);
        collectionInfo.add(colPostcode);

        listHashMap.put(list.get(0), collectionInfo);
    }

    /**
     * Adding information into Expandable list delivery
     */
    private void addItemsDelivery()
    {
        list2 = new ArrayList<>();
        listHashMap2 = new HashMap<>();

        list2.add(getString(R.string.delivery_information));

        List<String> deliveryInfo = new ArrayList<>();
        deliveryInfo.add(delAddress);
        deliveryInfo.add(delTown);
        deliveryInfo.add(delPostcode);

        listHashMap2.put(list2.get(0), deliveryInfo);
    }

    /**
     * Adding information into Expandable list job information
     */
    private void addItemsJobInformation()
    {
        list3 = new ArrayList<>();
        listHashMap3 = new HashMap<>();

        list3.add(getString(R.string.job_information));

        List<String> jobInformation = new ArrayList<>();
        jobInformation.add(jobSize);
        jobInformation.add(jobType);

        listHashMap3.put(list3.get(0), jobInformation);
    }

    /**
     * Creating all expandable list views and creating the heights of each to wrap the content
     */
    private void createExpandableListViews()
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
}
