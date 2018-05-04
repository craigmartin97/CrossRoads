package com.kitkat.crossroads.MyAdverts;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.kitkat.crossroads.ExternalClasses.ExpandableListAdapter;
import com.kitkat.crossroads.ExternalClasses.GenericMethods;
import com.kitkat.crossroads.ExternalClasses.ListViewHeight;
import com.kitkat.crossroads.Jobs.JobInformation;
import com.kitkat.crossroads.Jobs.UserBidInformation;
import com.kitkat.crossroads.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Pending adverts fragment shows the current user all of the active, pending adverts they have.
 * These are jobs they have uploaded and are waiting to receive bids, or accept a bid
 */
public class PendingAdverts extends Fragment
{
    /**
     * Strings to store the job information in
     */
    private String jobId, colDate, colTime, colAddress, colTown, colPostcode, delAddress, delTown, delPostcode, jobType, jobSize, courierId;

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

    private ProgressBar progressBar;

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

    private Button viewActiveBids;

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

    /**
     * * This method is called when [...] is displayed. It creates all of the
     * widgets and functionality that the user can do in the activity.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    /**
     * @param inflater           Instantiates a layout XML file into its corresponding view Objects
     * @param container          A view used to contain other views, in this case, the view fragment_job_bids
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     *                           This value may be null.
     * @return Returns inflated view
     */
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
        setBidsOnClickListener();
        return view;
    }

    /**
     * Add the collection information into a new list and into the expandable list view
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
     * Add the delivery information into a new list and into an expandable list view
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
     * Add the job information into a new list and into an expandable list view
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
     * Setting all of the widgets on the layout page to variables for future use and be able to access them
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
        jobImagePending = view.findViewById(R.id.jobImagePending);
        progressBar = view.findViewById(R.id.progressBar);
        viewActiveBids = view.findViewById(R.id.buttonViewActiveBids);
    }

    /**
     * Getting all arguments from the bundle that was passed across
     *
     * @return jobInformation
     */
    private JobInformation getBundleInformation()
    {
        Bundle bundle = getArguments();
        jobId = (String) bundle.getSerializable(getString(R.string.job_key_id));
        return (JobInformation) bundle.getSerializable(getString(R.string.job_id));
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
        Picasso.get().load(jobInformation.getJobImage()).fit().into(jobImagePending, new Callback()
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
     * onClick operations for viewActiveBids
     */
    private void setBidsOnClickListener()
    {
        viewActiveBids.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GenericMethods genericMethods = new GenericMethods();
                ActiveBidsFragment activeBidsFragment = new ActiveBidsFragment();
                activeBidsFragment.setArguments(genericMethods.createNewBundleStrings("JobId", jobId));
                genericMethods.beginTransactionToFragment(getFragmentManager(), activeBidsFragment);
            }
        });
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

    interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }

}