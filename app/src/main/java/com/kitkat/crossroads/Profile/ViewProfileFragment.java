package com.kitkat.crossroads.Profile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.kitkat.crossroads.ExternalClasses.ListViewHeight;
import com.kitkat.crossroads.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewProfileFragment extends Fragment
{
    private OnFragmentInteractionListener mListener;

    private static final String TAG = "ViewProfileActivity";

    /**
     * Assigning database connection to firebase database
     */
    private DatabaseReference databaseReference;

    /**
     * Storing the current users Id
     */
    private String user;

    /**
     * Creating variables to store the widgets
     */
    private RatingBar userRatingBar;
    private TextView fullName, phoneNumber, addressOne, addressTwo, town, postCode, textViewNoRating;
    private CheckBox checkBoxAdvertiser, checkBoxCourier;
    private ImageView profileImageUri;

    /**
     * CourierId is a variable passed in if the bundle is not null
     * profileImage is the users profileImage to be displayed from FirebaseStorage
     */
    private String courierId, profileImage;

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
        // Required empty public constructor
    }

    public static ViewProfileFragment newInstance()
    {
        ViewProfileFragment fragment = new ViewProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        databaseConnections();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);

        getViewsByIds(view);
        getBundleInformation();

        getUsersStarRating();
        addReviews();

        if (listHashMap.size() != 0)
        {
            try
            {
                adapter = new ExpandableListAdapter(getActivity(), list, listHashMap);
                expandableListView.setAdapter(adapter);

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

        if (courierId != null)
        {
            databaseReference.child("Users").child(courierId).addValueEventListener(new ValueEventListener()
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
        } else
        {
            databaseReference.child("Users").child(user).addValueEventListener(new ValueEventListener()
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

            profileImageUri.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    final AlertDialog.Builder profileImageDialog = new AlertDialog.Builder(getActivity());
                    View viewPopUpImage = getLayoutInflater().inflate(R.layout.popup_profile_image, null);

                    profileImageDialog.setTitle("Profile Image");
                    profileImageDialog.setView(viewPopUpImage);
                    final AlertDialog alertDialog = profileImageDialog.create();
                    alertDialog.show();

                    ImageView image = viewPopUpImage.findViewById(R.id.profileImage);
                    Picasso.get().load(profileImage).resize(350, 500).into(image);

                    Button cancelButton = viewPopUpImage.findViewById(R.id.cancelButton);

                    cancelButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            alertDialog.cancel();
                        }
                    });
                }
            });
        }
        return view;
    }

    /**
     * Establishing connections to Firebase Database, getting current user Id
     */
    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        databaseReference = databaseConnections.getDatabaseReference();
        user = databaseConnections.getCurrentUser();
    }

    /**
     * Assigning variables to the relevant widgets in the layout
     *
     * @param view - the layout page associated with the Fragment
     */
    private void getViewsByIds(View view)
    {
        fullName = view.findViewById(R.id.textViewName);
        phoneNumber = view.findViewById(R.id.textViewPhoneNumber);
        addressOne = view.findViewById(R.id.textViewAddressOne);
        addressTwo = view.findViewById(R.id.textViewAddressTwo);
        town = view.findViewById(R.id.textViewTown);
        postCode = view.findViewById(R.id.textViewPostCode);
        checkBoxAdvertiser = view.findViewById(R.id.checkBoxAdvertiser);
        checkBoxCourier = view.findViewById(R.id.checkBoxCourier);
        profileImageUri = view.findViewById(R.id.profileImage);
        expandableListView = view.findViewById(R.id.expandable_list_view);
        userRatingBar = view.findViewById(R.id.UserRatingsBar);
        textViewNoRating = view.findViewById(R.id.ratingNoFeedback);
    }

    /**
     * Getting the bundle information that could have been passed into the Fragment
     */
    private void getBundleInformation()
    {
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            // Assign the courier Id to the String passed across
            courierId = (String) bundle.getSerializable("courierId");
        }
    }

    /**
     * Get the user star rating
     */
    private void getUsersStarRating()
    {
        if (courierId != null)
        {
            databaseReference.child("Ratings").child(courierId).addValueEventListener(new ValueEventListener()
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
        } else
        {
            databaseReference.child("Ratings").child(user).addValueEventListener(new ValueEventListener()
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
     * Logic to assign the amount of stars required for the courier or the user
     *
     * @param dataSnapshot - Snapshot of the database table Ratings, with an id
     */
    private void assignStars(DataSnapshot dataSnapshot)
    {
        long totalRating = 0;
        long counter = 0;
        // Iterate through entire bids table
        if (dataSnapshot.hasChildren())
        {
            for (DataSnapshot ds : dataSnapshot.getChildren())
            {
                long rating = ds.child("starReview").getValue(long.class);

                totalRating += rating;
                counter++;

                totalRating = totalRating / counter;

                int usersRating = Math.round(totalRating);
                userRatingBar.setNumStars(usersRating);
                Drawable drawable = userRatingBar.getProgressDrawable();
                drawable.setColorFilter(Color.parseColor("#cece63"), PorterDuff.Mode.SRC_ATOP);
            }
        } else
        {
            textViewNoRating.setText("No Ratings For User");
            textViewNoRating.setVisibility(View.VISIBLE);
            userRatingBar.setVisibility(View.GONE);
        }
    }

    /**
     * Adding information into Expandable list collection information for the users reviews
     */
    private void addReviews()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        String user = databaseConnections.getCurrentUser();
        final DatabaseReference databaseReference = databaseConnections.getDatabaseReference();

        list = new ArrayList<>();
        listHashMap = new HashMap<>();
        final List<String> collectionInfo = new ArrayList<>();

        list.add("Reviews");

        if (courierId != null)
        {
            databaseReference.child("Ratings").child(courierId).addValueEventListener(new ValueEventListener()
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
        } else
        {
            databaseReference.child("Ratings").child(user).addValueEventListener(new ValueEventListener()
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
     * Logic for the reviews that are to be added
     *
     * @param dataSnapshot   - Snapshot of the Ratings table, with an id
     * @param collectionInfo - List that the information is to be added to
     */
    private void assignReviews(DataSnapshot dataSnapshot, final List<String> collectionInfo)
    {
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            final String review = ds.child("review").getValue(String.class);
            String key = dataSnapshot.getKey();

            databaseReference.child("Users").child(key).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot data)
                {
                    String fullName = data.child("fullName").getValue(String.class);
                    collectionInfo.add(review + " - " + fullName);
                    listHashMap.put(list.get(0), collectionInfo);
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }
    }

    private void addUserInformation(DataSnapshot dataSnapshot)
    {
        String name = dataSnapshot.child("fullName").getValue(String.class);
        String number = dataSnapshot.child("phoneNumber").getValue(String.class);
        String address1 = dataSnapshot.child("addressOne").getValue(String.class);
        String address2 = dataSnapshot.child("addressTwo").getValue(String.class);
        String usersTown = dataSnapshot.child("town").getValue(String.class);
        String postalCode = dataSnapshot.child("postCode").getValue(String.class);
        String profileImage = dataSnapshot.child("profileImage").getValue(String.class);
        boolean advertiser = dataSnapshot.child("advertiser").getValue(boolean.class);
        boolean courier = dataSnapshot.child("courier").getValue(boolean.class);

        fullName.setText(name);
        phoneNumber.setText(number);
        addressOne.setText(address1);
        addressTwo.setText(address2);
        town.setText(usersTown);
        postCode.setText(postalCode);

        if (advertiser == true && courier == false)
        {
            checkBoxAdvertiser.setChecked(true);
            checkBoxCourier.setChecked(false);
        } else if (advertiser == false && courier == true)
        {
            checkBoxAdvertiser.setChecked(false);
            checkBoxCourier.setChecked(true);
        } else if (advertiser == true && courier == true)
        {
            checkBoxAdvertiser.setChecked(true);
            checkBoxCourier.setChecked(true);
        }
        Picasso.get().load(profileImage).resize(350, 350).transform(new CircleTransformation()).into(profileImageUri);
    }

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
            Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }
}