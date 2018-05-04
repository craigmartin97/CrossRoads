package com.kitkat.crossroads.Jobs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.EnumClasses.DatabaseEntryNames;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.MyJobs.MyJobsFragment;
import com.kitkat.crossroads.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * JobsDetailsFragment displays all of the information about the job that the user may want to bid on
 * The user can see all of the information except the address line one, address line two and postcode of the job
 * for privacy reasons. The user can also bid on job here once.
 */
public class JobDetailsFragment extends Fragment
{
    /**
     * TextView widgets to store the widgets from the View in
     */
    private TextView jobName, jobDescription, jobSize, jobType, jobColDate, jobColTime, jobFrom, jobTo;

    /**
     * Button to submit the bid
     */
    private Button buttonBid;

    /**
     * EditText to enter a bid
     */
    private EditText editTextBid;

    /**
     * Connection to the FireBase Bids table
     */
    private DatabaseReference databaseReferenceBidsTable;

    /**
     * Connection to the FireBase Users table
     */
    private DatabaseReference databaseReferenceUsersTable;

    /**
     * Storing the users Id and the jobId
     */
    private String user, jobId;

    private ImageView jobImageDetails;
    private ProgressBar progressBar;

    public JobDetailsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Called to do initial creation of a fragment.
     * This is called after onAttach(Activity) and before onCreateView(LayoutInflater, ViewGroup, Bundle),
     * but is not called if the fragment instance is retained across Activity re-creation (see setRetainInstance(boolean)).
     *
     * @param savedInstanceState Bundle: If the fragment is being re-created from a previous saved state, this is the state.
     *                           This value may be null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        databaseConnections();
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once they know their view hierarchy has been completely created.
     * The fragment's view hierarchy is not however attached to its parent at this point.
     *
     * @param inflater           LayoutInflater: The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container          ViewGroup: If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState Bundle: If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return - Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_job_details, container, false);

        getViewsByIds(view);
        final JobInformation jobInformation = getBundleInformation();
        jobId = jobInformation.getJobID();
        setJobInformationText(Objects.requireNonNull(jobInformation));
        checkIfUserBidOn();
        setOnClickListeners();

        return view;
    }

    /**
     * Creates connections to FireBase database and gets the current users Id
     */
    private void databaseConnections()
    {
        DatabaseConnections databaseConnectionsClass = new DatabaseConnections();
        databaseReferenceBidsTable = databaseConnectionsClass.getDatabaseReferenceBids();
        databaseReferenceUsersTable = databaseConnectionsClass.getDatabaseReferenceUsers();
        databaseReferenceBidsTable.keepSynced(true);
        databaseReferenceUsersTable.keepSynced(true);
        user = databaseConnectionsClass.getCurrentUser();
    }

    /**
     * Assigning the widgets in the layout file to variables in the fragment
     *
     * @param view View: the layout that has been inflated
     */
    private void getViewsByIds(View view)
    {
        jobName = view.findViewById(R.id.textViewJobName1);
        jobDescription = view.findViewById(R.id.textViewJobDescription1);
        jobSize = view.findViewById(R.id.textViewJobSize1);
        jobType = view.findViewById(R.id.textViewJobType1);
        jobColDate = view.findViewById(R.id.textViewJobColDate1);
        jobColTime = view.findViewById(R.id.textViewJobColTime1);
        jobFrom = view.findViewById(R.id.textViewJobFrom1);
        jobTo = view.findViewById(R.id.textViewJobTo1);
        editTextBid = view.findViewById(R.id.editTextBid);
        buttonBid = view.findViewById(R.id.buttonBid);
        jobImageDetails = view.findViewById(R.id.jobImageDetails);
        progressBar = view.findViewById(R.id.progressBar2);
    }

    /**
     * Gets the bundle that has been passed from a previous fragment and returns it
     * otherwise it returns null
     *
     * @return - JobInformation: returns a jobInformation object
     */
    private JobInformation getBundleInformation()
    {
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            return (JobInformation) bundle.getSerializable("Job");
        } else
        {
            return null;
        }
    }

    /**
     * Set the onClick listeners for the widgets in the layout
     */
    private void setOnClickListeners()
    {
        // User presses bid on job
        buttonBid.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (TextUtils.isEmpty(editTextBid.getText()))
                {
                    editTextBid.setHint("Please enter a bid!");
                    editTextBid.setHintTextColor(Color.RED);
                } else
                {
                    saveBidInformation();
                }
            }
        });
    }

    /**
     * Set the widgets in the view with text from the jobInformation object passed across
     *
     * @param jobInformation jobInformation: Object that has been passed from the bundle
     */
    private void setJobInformationText(JobInformation jobInformation)
    {
        jobName.setText(jobInformation.getAdvertName());
        jobDescription.setText(jobInformation.getAdvertDescription());
        jobSize.setText(jobInformation.getJobSize());
        jobType.setText(jobInformation.getJobType());
        jobColDate.setText(jobInformation.getCollectionDate());
        jobColTime.setText(jobInformation.getCollectionTime());
        jobFrom.setText(jobInformation.getColTown());
        jobTo.setText(jobInformation.getDelTown());
        Picasso.get().load(jobInformation.getJobImage()).into(jobImageDetails, new Callback()
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
     * Get the users bids from the editText widget and converts it to a money, numeric value
     * to be stored in the FireBase Database
     */
    private void saveBidInformation()
    {
        String userBid = editTextBid.getText().toString().trim();
        BigDecimal decimal = new BigDecimal(userBid);
        decimal = decimal.setScale(2, RoundingMode.CEILING);
        final String jobID = Objects.requireNonNull(getBundleInformation()).getJobID().trim();
        saveBidInDatabase(user, jobID, decimal.toString());
    }

    /**
     * Posts the users bid into the FireBase database under the Bids table, jobId and then the users id
     * Then moves back to the MainActivity
     *
     * @param userID  String: the current users id who is bidding on the job
     * @param jobID   String: the job id the user is bidding on
     * @param userBid String: the bid that they have entered
     */
    private void saveBidInDatabase(final String userID, final String jobID, final String userBid)
    {
        databaseReferenceUsersTable.child(userID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String fullName = dataSnapshot.child(DatabaseEntryNames.fullName.name()).getValue(String.class);
                UserBidInformation userBidInformation = new UserBidInformation(fullName, userBid, userID, true);
                databaseReferenceBidsTable.child(jobID).child(userID).setValue(userBidInformation);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle newBundle = new Bundle();
                newBundle.putString("tabView", "Bid On");
                MyJobsFragment myJobsFragment = new MyJobsFragment();
                myJobsFragment.setArguments(newBundle);
                fragmentTransaction.replace(R.id.content, myJobsFragment).addToBackStack("tag").commit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    /**
     * Checks if the user has already bid on the job, if they have they are unable to
     * enter another bid for that job and must edit their bid on MyJobs, Pending tab
     */
    private void checkIfUserBidOn()
    {
        databaseReferenceBidsTable.child(jobId).child(user).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    buttonBid.setVisibility(View.GONE);
                    editTextBid.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

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
    }

    /**
     * When the fragment is no longer attached to the activity, set the listener to null
     */
    @Override
    public void onDetach()
    {
        super.onDetach();
    }
}