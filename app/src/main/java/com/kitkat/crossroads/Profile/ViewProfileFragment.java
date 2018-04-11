package com.kitkat.crossroads.Profile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kitkat.crossroads.ExternalClasses.CircleTransformation;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.ExpandableListAdapter;
import com.kitkat.crossroads.ExternalClasses.ListViewHeight;
import com.kitkat.crossroads.R;
import com.kitkat.crossroads.Jobs.UserBidInformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.icu.lang.UProperty.INT_START;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewProfileFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final String TAG = "ViewProfileActivity";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private DataSnapshot reviewReference;
    private RatingBar userRatingBar;

    private TextView fullName, phoneNumber, addressOne, addressTwo, town, postCode;
    private CheckBox checkBoxAdvertiser, checkBoxCourier;

    private ImageView profileImageUri;

    private String passedUserID;

    private String profileImage;

    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private List<String> list;
    private HashMap<String, List<String>> listHashMap;

    public ViewProfileFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewProfileFragment newInstance(String param1, String param2)
    {
        ViewProfileFragment fragment = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);

        final ArrayList<ReviewInformation> reviewListArray = new ArrayList<>();

        DatabaseConnections databaseConnections = new DatabaseConnections();
        String user = databaseConnections.getCurrentUser();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = mFirebaseDatabase.getReference();

        fullName = (TextView) view.findViewById(R.id.textViewName);
        phoneNumber = (TextView) view.findViewById(R.id.textViewPhoneNumber);
        addressOne = (TextView) view.findViewById(R.id.textViewAddressOne);
        addressTwo = (TextView) view.findViewById(R.id.textViewAddressTwo);
        town = (TextView) view.findViewById(R.id.textViewTown);
        postCode = (TextView) view.findViewById(R.id.textViewPostCode);
        checkBoxAdvertiser = (CheckBox) view.findViewById(R.id.checkBoxAdvertiser);
        checkBoxCourier = (CheckBox) view.findViewById(R.id.checkBoxCourier);
        profileImageUri = (ImageView) view.findViewById(R.id.profileImage);
        expandableListView = view.findViewById(R.id.expandable_list_view);
        userRatingBar = (RatingBar) view.findViewById(R.id.UserRatingsBar);

        databaseReference.child("Ratings").child(user).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                long totalRating = 0;
                long counter = 0;

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    long rating = ds.child("startReview").getValue(long.class);

                    totalRating += rating;
                    counter++;

                    totalRating = totalRating / counter;

                    int usersRating = Math.round(totalRating);
                    userRatingBar.setNumStars(usersRating);
                    Drawable drawable = userRatingBar.getProgressDrawable();
                    drawable.setColorFilter(Color.parseColor("#cece63"), PorterDuff.Mode.SRC_ATOP);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        addReviews();

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


        Bundle bundle = this.getArguments();

        if (bundle != null)
        {
            final UserBidInformation userBidInformation = (UserBidInformation) bundle.getSerializable("ViewProfile");
            passedUserID = userBidInformation.getUserID();
        }

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = mFirebaseDatabase.getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else
                {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
            }
        };

        if (passedUserID != null)
        {
            databaseReference.child(passedUserID).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
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

                    Log.d(TAG, "Full Name: " + name);
                    Log.d(TAG, "Phone Number: " + number);
                    Log.d(TAG, "Address Line One: " + address1);
                    Log.d(TAG, "Address Line Two: " + address2);
                    Log.d(TAG, "Town: " + usersTown);
                    Log.d(TAG, "PostCode: " + postalCode);
                    Log.d(TAG, "ProfileImage: " + profileImage);
                    Log.d(TAG, "Advertiser: " + advertiser);
                    Log.d(TAG, "Courier: " + courier);

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
                    ;
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        } else
        {
            databaseReference.child(user).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    String name = dataSnapshot.child("fullName").getValue(String.class);
                    String number = dataSnapshot.child("phoneNumber").getValue(String.class);
                    String address1 = dataSnapshot.child("addressOne").getValue(String.class);
                    String address2 = dataSnapshot.child("addressTwo").getValue(String.class);
                    String usersTown = dataSnapshot.child("town").getValue(String.class);
                    String postalCode = dataSnapshot.child("postCode").getValue(String.class);
                    profileImage = dataSnapshot.child("profileImage").getValue(String.class);
                    boolean advertiser = dataSnapshot.child("advertiser").getValue(boolean.class);
                    boolean courier = dataSnapshot.child("courier").getValue(boolean.class);

                    Log.d(TAG, "Full Name: " + name);
                    Log.d(TAG, "Phone Number: " + number);
                    Log.d(TAG, "Address Line One: " + address1);
                    Log.d(TAG, "Address Line Two: " + address2);
                    Log.d(TAG, "Town: " + usersTown);
                    Log.d(TAG, "PostCode: " + postalCode);
                    Log.d(TAG, "ProfileImage: " + profileImage);
                    Log.d(TAG, "Advertiser: " + advertiser);
                    Log.d(TAG, "Courier: " + courier);

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
                    Picasso.get().load(profileImage).resize(350,500).into(image);

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Adding information into Expandable list collection information
     */
    private void addReviews()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        String user = databaseConnections.getCurrentUser();
        final DatabaseReference databaseReference = databaseConnections.getDatabaseReference();

        list = new ArrayList<>();
        listHashMap = new HashMap<>();

        list.add("Reviews");

        final List<String> collectionInfo = new ArrayList<>();

        databaseReference.child("Ratings").child(user).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
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

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


    }


    private void toastMessage(String message)
    {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}