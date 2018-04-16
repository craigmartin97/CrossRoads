package com.kitkat.crossroads.MyAdverts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.ExpandableListAdapter;
import com.kitkat.crossroads.ExternalClasses.ListViewHeight;
import com.kitkat.crossroads.Jobs.JobInformation;
import com.kitkat.crossroads.R;
import com.kitkat.crossroads.Ratings.RatingsAndReviews;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * This class displays the job information for a job they have just bid on.
 * They can also view their bid they made and edit that bid.
 * They can also delete the bid from here as well.
 */
public class CompletedAdverts extends Fragment
{
    /**
     * Text Views to display the jobs name and description
     */
    private TextView jobName, jobDescription, textViewUsersBid, leaveFeedback;

    /**
     * ImageView, to store and display the Jobs Image
     */
    private ImageView jobImageCompleted;

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
    private DatabaseReference databaseReference;

    /**
     * Getting the current users Id
     */
    private String user;

    private String jobId;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DatabaseConnections databaseConnections = new DatabaseConnections();
        databaseReference = databaseConnections.getDatabaseReference();
        user = databaseConnections.getCurrentUser();
    }

    /**
     * Method displays and renders the content to the user
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_completed_adverts, container, false);

        getViewsByIds(view);
        final JobInformation jobInformation = getBundleInformation();

        setJobInformationDetails(jobInformation);

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

        leaveFeedbackPressed();

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
        jobImageCompleted = (ImageView) view.findViewById(R.id.jobImageCompleted);
        textViewUsersBid = (TextView) view.findViewById(R.id.textViewAcceptedBid);
        leaveFeedback = (TextView) view.findViewById(R.id.textViewLeaveFeedback);

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
        String courierId = jobInformation.getCourierID();
        Picasso.get().load(jobInformation.getJobImage()).fit().into(jobImageCompleted);

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
     * If the leave feedback text has been pressed, display the popup to leave feedback
     */
    private void leaveFeedbackPressed()
    {
        leaveFeedback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                View mView = getLayoutInflater().inflate(R.layout.popup_leave_feedback, null);

                alertDialog.setTitle("Leave Feedback");
                alertDialog.setView(mView);
                final AlertDialog dialog = alertDialog.create();
                dialog.show();

                final TextView leaveFeedback = (TextView) mView.findViewById(R.id.editTextLeaveFeedback);
                final RatingBar ratingBar = (RatingBar) mView.findViewById(R.id.ratingBarFeedback);
                Button submitButton = (Button) mView.findViewById(R.id.submitButton);
                Button cancelButton = (Button) mView.findViewById(R.id.cancelButton);


                submitButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        JobInformation jobInformation = getBundleInformation();
                        String feedback = leaveFeedback.getText().toString().trim();
                        float rating = ratingBar.getRating();
                        RatingsAndReviews ratingsAndReviews = new RatingsAndReviews(rating, feedback);

                        databaseReference.child("Ratings").child(jobInformation.getCourierID()).child(jobId).setValue(ratingsAndReviews);
                        Toast.makeText(getActivity(), "Pressed Submit!", Toast.LENGTH_SHORT).show();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog.cancel();
                    }
                });
            }
        });
    }
}
