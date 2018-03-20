package com.kitkat.crossroads;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kitkat.crossroads.Jobs.JobInformation;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActiveJobDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActiveJobDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActiveJobDetailsFragment extends Fragment {


    private FirebaseAuth auth;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private DataSnapshot bidReference;
    private DataSnapshot jobReference;
    private DataSnapshot usersReference;
    private StorageReference storageReference;


    private String jobId;
    private String jobBidId;
    private String usersId;
    private static final String TAG = "ActJobDetailsFragment";
    private String name;

    private ActiveJobDetailsFragment.MyCustomAdapter mAdapter;

    private ArrayList<UserBidInformation> jobList = new ArrayList<>();

    private ListView jobListView;

    private SearchView jobSearch;

    private SignaturePad mSignaturePad;

    private Button mJobCompleteButton, mClearButton;

    private TextView textViewJobName1, textViewDescription1, textViewJobSize1,
            textViewJobType1, textViewJobColDate1, textViewJobColTime1,
            textViewFromAddress, textViewFromTown, textViewFromPostcode,
            textViewToAddress, textViewToTown, textViewToPostcode;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ActiveJobDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActiveJobDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActiveJobDetailsFragment newInstance(String param1, String param2) {
        ActiveJobDetailsFragment fragment = new ActiveJobDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_active_job_details, container, false);

        database = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        databaseReference = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        Bundle bundle = this.getArguments();
        final JobInformation jobInformation = (JobInformation) bundle.getSerializable("Job");
        jobId = jobInformation.getJobID();

        jobListView = view.findViewById(R.id.jobListView1);

        textViewJobName1 = view.findViewById(R.id.textViewJobName1);
        textViewDescription1 = view.findViewById(R.id.textViewJobDescription1);
        textViewJobSize1 = view.findViewById(R.id.textViewJobSize1);
        textViewJobType1 = view.findViewById(R.id.textViewJobType1);
        textViewJobColDate1 = view.findViewById(R.id.textViewJobColDate1);
        textViewJobColTime1 = view.findViewById(R.id.textViewJobColTime1);


        textViewFromAddress = view.findViewById(R.id.textViewFromAddress);
        textViewFromTown = view.findViewById(R.id.textViewFromTown);
        textViewFromPostcode = view.findViewById(R.id.textViewFromPostcode);

        textViewToAddress = view.findViewById(R.id.textViewToAddress);
        textViewToTown = view.findViewById(R.id.textViewToTown);
        textViewToPostcode = view.findViewById(R.id.textViewJobToPostcode);

        textViewJobName1.setText(jobInformation.getAdvertName());
        textViewDescription1.setText(jobInformation.getAdvertDescription());
        textViewJobSize1.setText(jobInformation.getJobSize());
        textViewJobType1.setText(jobInformation.getJobType());
        textViewJobColDate1.setText(jobInformation.getCollectionDate());
        textViewJobColTime1.setText(jobInformation.getCollectionTime());

        textViewFromAddress.setText(jobInformation.getColL1() + ", " + jobInformation.getColL2());
        textViewFromTown.setText(jobInformation.getColTown());
        textViewFromPostcode.setText(jobInformation.getColPostcode());

        textViewToAddress.setText(jobInformation.getDelL1() + ", " + jobInformation.getDelL2());
        textViewToTown.setText(jobInformation.getDelTown());
        textViewToPostcode.setText(jobInformation.getDelPostcode());

        mSignaturePad = (SignaturePad) view.findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                mJobCompleteButton.setEnabled(true);
                mClearButton.setEnabled(true);
                mJobCompleteButton.setBackgroundColor(Color.parseColor("#2bbc9b"));
                mClearButton.setBackgroundColor(Color.parseColor("#2bbc9b"));


            }

            @Override
            public void onClear() {
                mJobCompleteButton.setEnabled(false);
                mClearButton.setEnabled(false);
                mJobCompleteButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mClearButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mJobCompleteButton.setTextColor(Color.parseColor("#FFFFFF"));
                mClearButton.setTextColor(Color.parseColor("#FFFFFF"));


            }
        });

        mClearButton = (Button) view.findViewById(R.id.clear_button);
        mJobCompleteButton = (Button) view.findViewById(R.id.job_complete_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mJobCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();

                String jobID = jobInformation.getJobID();

                final StorageReference filePath = storageReference.child("Jobs").child(jobID).child("Signature/collectionSignature.jpg");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = filePath.putBytes(data);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Unsuccessful!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        myRef.child("Jobs").child(jobInformation.getJobID()).child("jobStatus").setValue("Complete");


                    }
                });

            }
        });





        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {

        }
    }

    @Override
    public void onDetach() {
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public class MyCustomAdapter extends BaseAdapter
    {

        private ArrayList<UserBidInformation> mData = new ArrayList();
        private ArrayList<UserBidInformation> mDataOrig = new ArrayList();

        private LayoutInflater mInflater;

        public MyCustomAdapter()
        {

            if (isAdded())
            {
                mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
        }

        public void addItem(final UserBidInformation item)
        {
            mData.add(item);
            mDataOrig.add(item);
        }


        public void addArray(final ArrayList<UserBidInformation> j)
        {
            mData = j;
            mDataOrig = j;
        }


        @Override
        public void registerDataSetObserver(DataSetObserver observer)
        {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer)
        {

        }

        @Override
        public int getCount()
        {
            return mData.size();
        }

        @Override
        public Object getItem(int position)
        {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public boolean hasStableIds()
        {
            return false;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            System.out.println("getView " + position + " " + convertView);
            ActiveJobDetailsFragment.MyCustomAdapter.GroupViewHolder holder;
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.job_info_list_bid, null);
                holder = new ActiveJobDetailsFragment.MyCustomAdapter.GroupViewHolder();
                holder.textViewName = convertView.findViewById(R.id.textName);
                holder.textViewBid = convertView.findViewById(R.id.textBid);
                holder.textViewRating = convertView.findViewById(R.id.textRating);
                holder.acceptBidButton = convertView.findViewById(R.id.acceptBidButton);
                convertView.setTag(holder);
            } else
            {
                holder = (ActiveJobDetailsFragment.MyCustomAdapter.GroupViewHolder) convertView.getTag();
            }

            holder.textViewName.setText(mData.get(position).getFullName());
            holder.textViewBid.setText(mData.get(position).getUserBid());
            holder.textViewRating.setText(mData.get(position).getUserID());

            holder.acceptBidButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    View mView = getLayoutInflater().inflate(R.layout.popup_accept_bid, null);

                    alertDialog.setTitle("Accept Bid?");
                    alertDialog.setView(mView);
                    final AlertDialog dialog = alertDialog.create();
                    dialog.show();

                    TextView text = (TextView) mView.findViewById(R.id.acceptBidText);
                    Button yesButton = (Button) mView.findViewById(R.id.yesButton);
                    Button noButton = (Button) mView.findViewById(R.id.noButton);

                        /*accept users bid
                            - get bidder's ID and set to CourierID
                            - change Job Status to Active
                            - notify users of selection
                        */

                    yesButton.setOnClickListener(new View.OnClickListener()
                    {


                        @Override
                        public void onClick(View v)
                        {
                            dialog.cancel();

                            View mView = getLayoutInflater().inflate(R.layout.popup_bid_accepted, null);

                            myRef.child("Jobs").child(jobId).child("courierID").setValue(mData.get(position).getUserID());
                            myRef.child("Jobs").child(jobId).child("jobStatus").setValue("Active");

                            alertDialog.setTitle("Bid Accepted");
                            alertDialog.setView(mView);
                            final AlertDialog dialog = alertDialog.create();
                            dialog.show();

                            TextView text = (TextView) mView.findViewById(R.id.bidAccepted);
                            Button okButton = (Button) mView.findViewById(R.id.okButton);

                            okButton.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    dialog.cancel();
                                }
                            });

                        }
                    });

                    noButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            dialog.cancel();
                        }
                    });
                }
            });
            return convertView;
        }

        @Override
        public boolean areAllItemsEnabled()
        {
            return false;
        }

        @Override
        public boolean isEmpty()
        {
            return false;
        }

        public class GroupViewHolder
        {
            public TextView textViewName;
            public TextView textViewBid;
            public TextView textViewRating;
            public Button acceptBidButton;
        }

        public void filter(String charText)
        {

            ArrayList<UserBidInformation> jobs = new ArrayList<UserBidInformation>();
            ArrayList<UserBidInformation> jA = new ArrayList<UserBidInformation>();
            charText = charText.toLowerCase(Locale.getDefault());

            if (charText.length() == 0)
            {
                mData = mDataOrig;
            } else
            {

                for (UserBidInformation j : mDataOrig)
                {
                    if (j.getWholeString().toLowerCase(Locale.getDefault()).contains(charText))
                    {
                        jobs.add(j);
                        jA.add(j);
                    } else
                    {
                        jA.add(j);
                    }
                }
                mData.clear();
                mData = jobs;
                mDataOrig = jA;
            }

            notifyDataSetChanged();
        }
    }
}

