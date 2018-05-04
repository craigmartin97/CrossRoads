package com.kitkat.crossroads.MyAdverts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.ExpandableListAdapter;
import com.kitkat.crossroads.ExternalClasses.GenericMethods;
import com.kitkat.crossroads.ExternalClasses.ListViewHeight;
import com.kitkat.crossroads.Jobs.JobInformation;
import com.kitkat.crossroads.Profile.ViewProfileFragment;
import com.kitkat.crossroads.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.content.Intent;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.felipecsl.gifimageview.library.GifHeaderParser.TAG;

/**
 * Active adverts allows user to view all of the details for their active adverts.
 * They can press on an item in the ListView and it will bring up all of the details for that job
 */
public class ActiveAdverts extends Fragment
{
    /**
     * Text Views to display the jobs name and description
     */
    private TextView jobName, jobDescription, textViewUsersBid;

    /**
     * Button, when pressed, takes user to the couriers profile to view
     */
    private Button buttonViewCourierProfile, buttonEmailCourier, buttonCallCourier;

    /**
     * ImageView for the JobsImage
     */
    private ImageView jobImageActive;

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
     * Variable to store the current users Id
     */
    private ProgressBar progressBar;

    /**
     * Creating variable to store the connection to the Firebase Database
     */
    private DatabaseReference databaseReference;

    /**
     * Access the jobId the user pressed on
     */
    private String jobId;

    /**
     * the couriers id who is completing the job
     */
    private String courierId;

    private final static int REQUEST_CODE = 100;

    /**
     * @param savedInstanceState If the fragment is being recreated from a previous saved state, this is the state.
     *                           This value may be null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DatabaseConnections databaseConnections = new DatabaseConnections();
        databaseReference = databaseConnections.getDatabaseReference();
    }

    /**
     * Method displays and renders the content to the user
     * /**
     *
     * @param inflater           Instantiates a layout XML file into its corresponding view Objects
     * @param container          A view used to contain other views, in this case, the view fragment_active_adverts
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     *                           This value may be null.
     * @return Returns inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_active_adverts, container, false);

        getViewsByIds(view);
        final JobInformation jobInformation = getBundleInformation();
        courierId = jobInformation.getCourierID();

        setJobInformationDetails(jobInformation);

        setButtonViewCourierProfile();
        setButtonEmailCourier();
        setButtonCallCourier();

        addItemsCollection();
        addItemsDelivery();
        addItemsJobInformation();

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

        return view;
    }

    /**
     * Get all of the layout pages content, such as TextViews and the Expandable Lists
     *
     * @param view - page to be inflated
     */
    private void getViewsByIds(View view)
    {
        jobName = (TextView) view.findViewById(R.id.textViewJobName1);
        jobDescription = (TextView) view.findViewById(R.id.textViewJobDescription1);
        jobImageActive = (ImageView) view.findViewById(R.id.jobImageActive);
        textViewUsersBid = (TextView) view.findViewById(R.id.textViewAcceptedBid);
        buttonViewCourierProfile = view.findViewById(R.id.buttonViewCourierProfile);
        buttonEmailCourier = view.findViewById(R.id.buttonEmailCourier);
        buttonCallCourier = view.findViewById(R.id.buttonCallCourier);
        progressBar = view.findViewById(R.id.progressBar);
        expandableListView = view.findViewById(R.id.expandable_list_view);
        expandableListView2 = view.findViewById(R.id.expandable_list_view2);
        expandableListView3 = view.findViewById(R.id.expandable_list_view3);
    }

    /**
     * Setting the on click listener for the button to that the user to the courier profile
     */
    private void setButtonViewCourierProfile()
    {
        buttonViewCourierProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ViewProfileFragment viewProfileFragment = new ViewProfileFragment();
                GenericMethods genericMethods = new GenericMethods();
                viewProfileFragment.setArguments(genericMethods.createNewBundleStrings("courierId", getBundleInformation().getCourierID()));
                genericMethods.beginTransactionToFragment(getFragmentManager(), viewProfileFragment);
            }
        });
    }

    /**
     * Sets onClick operations for Email Courier Button
     */
    private void setButtonEmailCourier()
    {
        buttonEmailCourier.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                databaseReference.child("Users").child(courierId).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        String userEmail = dataSnapshot.child("userEmail").getValue(String.class);
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", userEmail, null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "CrossRoads Job");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                        startActivity(Intent.createChooser(emailIntent, "Send Email"));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }
        });
    }

    /**
     * Sets onClick operations for Call Courier button
     */
    private void setButtonCallCourier()
    {

        buttonCallCourier.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                databaseReference.child("Users").child(courierId).addValueEventListener(new ValueEventListener()
                {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        /**
                         * Ensure the app has the required permissions before we start a new Intent
                         */
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                        {
                            String phoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + phoneNumber));
                            startActivity(callIntent);
                        } else
                        {
                            /**
                             * if permissions are denied, prompt the user again
                             */
                            requestPhonePermissions();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }
        });

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
        Picasso.get().load(jobInformation.getJobImage()).fit().into(jobImageActive, new Callback()
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

        /*
         *Set the users accepted bid
         */
        databaseReference.child("Bids").child(jobId).child(courierId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String userBid = dataSnapshot.child("userBid").getValue(String.class);
                textViewUsersBid.setText("Agreed Fee:       £" + userBid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        // Storing information in variables for later use
        jobType = jobInformation.getJobType().toString();
        jobSize = jobInformation.getJobSize().toString();

        colDate = jobInformation.getCollectionDate().toString();
        colTime = jobInformation.getCollectionTime().toString();

        colAddress = jobInformation.getColL1().toString() + ", " + jobInformation.getColL2().toString();
        colTown = jobInformation.getColTown().toString();
        colPostcode = jobInformation.getColPostcode().toString();

        delAddress = jobInformation.getDelL1().toString() + ", " + jobInformation.getDelL2().toString();
        delTown = jobInformation.getDelTown().toString();
        delPostcode = jobInformation.getDelPostcode().toString();
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
     * Adding information into Expandable list collection information
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
     * Adding information into Expandable list delivery
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
     * Adding information into Expandable list job information
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
     * request phone permissions
     */
    private void requestPhonePermissions()
    {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
    }

    /**
     * Called after permissions are requested.
     *
     * @param requestCode  the requested permissions, in this case we need CALL_PHONE
     * @param permissions  the array in which the permissions are held
     * @param grantResults the result of the permission request, if equal to PERMISSION_GRANTED the corresponding intent will be constructed
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                setButtonCallCourier();
            } else
            {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}