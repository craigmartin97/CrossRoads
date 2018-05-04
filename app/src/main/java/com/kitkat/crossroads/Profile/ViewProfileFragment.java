package com.kitkat.crossroads.Profile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.ExternalClasses.CircleTransformation;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.ExpandableListAdapter;
import com.kitkat.crossroads.ExternalClasses.GenericMethods;
import com.kitkat.crossroads.ExternalClasses.ListViewHeight;
import com.kitkat.crossroads.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The view profile fragment allows the user to view their own profile details.
 * It shows the users phone number, email address, full name, address and profile image.
 * This class can also show the user the couriers information that they have accepted minus their address information.
 */
public class ViewProfileFragment extends Fragment
{
    /**
     * Assigning database connection to Firebase database
     */
    private DatabaseReference databaseReferenceUsersTable;

    /**
     * Accessing the database ratings table
     */
    private DatabaseReference databaseReferenceRatingsTable;

    /**
     * Storing the current users Id
     */
    private String user;

    /**
     * Creating variables to store the widgets
     */
    private RatingBar userRatingBar;
    private TextView fullName, phoneNumber, addressOne, addressTwo, town, postCode, textViewNoRating, textViewEmail, textViewUserPreference;
    private ImageView profileImageUri;

    /**
     * CourierId is a variable passed in if the bundle is not null
     * profileImage is the users profileImage to be displayed from FirebaseStorage
     */
    private String courierId;

    /**
     * Creating an expandable list view to display the data in
     */
    private ExpandableListView expandableListView;

    /**
     * Create an adapter to create the expandable list view
     */
    private ExpandableListAdapter adapter;

    /**
     * Lists to add the data into to add into the Expandable list view
     */
    private List<String> list;
    private HashMap<String, List<String>> listHashMap;

    public ViewProfileFragment()
    {

    }

    /**
     * This method is called when the activity login is displayed to the user. It creates all of the
     * widgets and functionality that the user can do in the activity.
     *
     * @param savedInstanceState - if the activity needs to be recreated it can be passed back
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
     * @param inflater           LayoutInflater: The LayoutInflater object that can be used to inflate any views in the fragment
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

        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);

        getViewsByIds(view);
        getBundleInformation();

        getUsersStarRating();
        addReviews();

        // If its a couriers profile to be viewed
        if (courierId != null)
        {
            databaseReferenceUsersTable.child(courierId).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    addUserInformation(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }
        // Users own profile
        else
        {
            databaseReferenceUsersTable.child(user).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    addUserInformation(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });

        }
        return view;
    }

    /**
     * Establishing connections to FireBase Database, and getting current user Id
     * Connects to the users table and the ratings table
     */
    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        databaseReferenceUsersTable = databaseConnections.getDatabaseReferenceUsers();
        databaseReferenceRatingsTable = databaseConnections.getDatabaseReferenceRatings();
        databaseReferenceUsersTable.keepSynced(true);
        databaseReferenceRatingsTable.keepSynced(true);
        user = databaseConnections.getCurrentUser();
    }

    /**
     * Assigning variables to the relevant widgets in the layout
     *
     * @param view View: the layout page associated with the Fragment
     */
    private void getViewsByIds(View view)
    {
        fullName = view.findViewById(R.id.textViewName);
        phoneNumber = view.findViewById(R.id.textViewPhoneNumber);
        addressOne = view.findViewById(R.id.textViewAddressOne);
        addressTwo = view.findViewById(R.id.textViewAddressTwo);
        town = view.findViewById(R.id.textViewTown);
        postCode = view.findViewById(R.id.textViewPostCode);
        textViewUserPreference = view.findViewById(R.id.userPreference);
        profileImageUri = view.findViewById(R.id.profileImage);
        expandableListView = view.findViewById(R.id.expandable_list_view);
        userRatingBar = view.findViewById(R.id.UserRatingsBar);
        textViewNoRating = view.findViewById(R.id.ratingNoFeedback);
        textViewEmail = view.findViewById(R.id.textViewEmail);
    }

    /**
     * Getting the bundle information that could have been passed into the Fragment
     * If a bundle has been passed in, the courierId won't be null as it's a
     * couriers profile being viewed
     */
    private void getBundleInformation()
    {
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            courierId = (String) bundle.getSerializable("courierId");
        }
    }

    /**
     * Get the user star rating, if the bundle wasn't null it'll get
     * the couriers star rating. Otherwise it'll get the current users rating
     */
    private void getUsersStarRating()
    {
        // Couriers stars
        if (courierId != null)
        {
            databaseReferenceRatingsTable.child(courierId).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    assignStars(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }
        // Current users stars
        else
        {
            databaseReferenceRatingsTable.child(user).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    assignStars(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }
    }

    /**
     * Logic to assign the amount of stars required for the user/courier or the user
     * Goes through the ratings table under the couriers Id and get all of the ratings
     * then divides by the amount of reviews that have been left.
     * If their is no reviews the ratings bar is removed
     *
     * @param dataSnapshot DataSnapShot: Snapshot of the database table Ratings, with an id
     */
    private void assignStars(DataSnapshot dataSnapshot)
    {
        long totalRating = 0;
        long counter = 0;

        // If the user has a review
        if (dataSnapshot.hasChildren())
        {
            for (DataSnapshot ds : dataSnapshot.getChildren())
            {
                if (ds.child("starReview").exists())
                {
                    long rating = ds.child("starReview").getValue(long.class);
                    totalRating += rating;
                    counter++;

                    totalRating = totalRating / counter;
                }
            }

            // Round the star ratings, display and make gold
            int usersRating = Math.round(totalRating);
            userRatingBar.setNumStars(usersRating);
            Drawable drawable = userRatingBar.getProgressDrawable();
            drawable.setColorFilter(Color.parseColor("#cece63"), PorterDuff.Mode.SRC_ATOP);
        }
        // Otherwise, display so there are no stars visible and text saying no ratings
        else
        {
            textViewNoRating.setText(R.string.no_ratings);
            textViewNoRating.setVisibility(View.VISIBLE);
            userRatingBar.setVisibility(View.GONE);
        }

    }

    /**
     * Adding information into Expandable list collection information for the users reviews
     * this information is displayable to the user in a list
     */
    private void addReviews()
    {
        list = new ArrayList<>();
        listHashMap = new HashMap<>();
        final List<String> collectionInfo = new ArrayList<>();

        list.add("Reviews");

        // Couriers information
        if (courierId != null)
        {
            databaseReferenceRatingsTable.child(courierId).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    assignReviews(dataSnapshot, collectionInfo);
                    addressOne.setVisibility(View.GONE);
                    addressTwo.setVisibility(View.GONE);
                    postCode.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }
        // Current users information
        else
        {
            databaseReferenceRatingsTable.child(user).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    assignReviews(dataSnapshot, collectionInfo);
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }
    }

    /**
     * Logic for the reviews that are to be added, displays all of the
     * reviews in a list view
     *
     * @param dataSnapshot   DataSnapShot: Snapshot of the Ratings table, with an id
     * @param collectionInfo List<String>: List that the information is to be added to
     */
    private void assignReviews(DataSnapshot dataSnapshot, final List<String> collectionInfo)
    {
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            final String review = ds.child("review").getValue(String.class);

            collectionInfo.add(review);
            listHashMap.put(list.get(0), collectionInfo);

            if (listHashMap.size() != 0)
            {
                try
                {
                    adapter = new ExpandableListAdapter(getActivity(), list, listHashMap);
                    expandableListView.setAdapter(adapter);

                    textViewNoRating.setVisibility(View.GONE);
                    expandableListView.setVisibility(View.VISIBLE);

                    expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
                    {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
                        {
                            ListViewHeight listViewHeight = new ListViewHeight();
                            listViewHeight.setExpandableListViewHeight(parent, groupPosition);
                            return false;
                        }
                    });
                } catch (NullPointerException e)
                {
                    Toast.makeText(getActivity(), "Can't Display Reviews At This Time", Toast.LENGTH_SHORT).show();
                    Log.d("ExpandListError: ", e.getMessage());
                }
            } else
            {
                expandableListView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Getting all of the data from the FireBase database and displaying in
     * the fragment. Gets all data such as the fullName, address etc to be displayed.
     *
     * @param dataSnapshot DataSnapShot: snapshot of the database
     */
    private void addUserInformation(DataSnapshot dataSnapshot)
    {
        String name = dataSnapshot.child("fullName").getValue(String.class);
        String number = dataSnapshot.child("phoneNumber").getValue(String.class);
        String address1 = dataSnapshot.child("addressOne").getValue(String.class);
        String address2 = dataSnapshot.child("addressTwo").getValue(String.class);
        String usersTown = dataSnapshot.child("town").getValue(String.class);
        String postalCode = dataSnapshot.child("postCode").getValue(String.class);
        String profileImage = dataSnapshot.child("profileImage").getValue(String.class);
        String email = dataSnapshot.child("userEmail").getValue(String.class);
        boolean advertiser = dataSnapshot.child("advertiser").getValue(boolean.class);
        boolean courier = dataSnapshot.child("courier").getValue(boolean.class);

        fullName.setText(name);
        phoneNumber.setText(number);
        addressOne.setText(address1);
        addressTwo.setText(address2);
        town.setText(usersTown);
        postCode.setText(postalCode);
        textViewEmail.setText(email);


        if (advertiser && !courier)
        {
            textViewUserPreference.setText("Advertiser!");
        } else if (!advertiser && courier)
        {
            textViewUserPreference.setText("Courier!");
        } else
        {
            textViewUserPreference.setText("Advertiser and Courier!");
        }
        Picasso.get().load(profileImage).resize(350, 350).transform(new CircleTransformation()).into(profileImageUri);
    }

    /**
     * onAttach             onAttach is called when a fragment is first attached to its context
     * onCreate can be called only after the fragment is attached
     *
     * @param context Context: Allows access to application specific resources and classes, also
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