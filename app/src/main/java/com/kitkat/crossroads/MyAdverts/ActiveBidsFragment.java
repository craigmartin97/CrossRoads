package com.kitkat.crossroads.MyAdverts;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.EnumClasses.JobStatus;
import com.kitkat.crossroads.Jobs.PostAnAdvertFragment;
import com.kitkat.crossroads.Payment.ConfigPaypal;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.Jobs.UserBidInformation;
import com.kitkat.crossroads.R;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Active Jobs Details displays all of the active bids that the user has received on their job advert
 * The user can see all bids, which include the bidders name, average rating and fee.
 * The user can go to accept the bid and pay through paypal to complete the transaction.
 */
public class ActiveBidsFragment extends Fragment
{
    private OnFragmentInteractionListener mListener;

    /**
     * Establishing connection to FireBase database, bids table
     */
    private DatabaseReference databaseReferenceBidsTable;

    /**
     * Establishing connection to FireBase database, ratings table
     */
    private DatabaseReference databaseReferenceRatingsTable;

    /**
     * Establishing connection to FireBase database, jobs table
     */
    private DatabaseReference databaseReferenceJobsTable;

    /**
     * Getting the current user who is sign in id
     */
    private String user;

    /**
     * List view to display the information
     */
    private ListView jobListView;

    /**
     * List to store all of the the users bids
     */
    private ArrayList<UserBidInformation> jobList = new ArrayList<>();
    private double commisionAmount;
    private double totalAmount;

    private int pos;

    public static final int PAYPAL_REQUEST_CODE = 7171;
    public static final PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(ConfigPaypal.PAYPAL_CLIENT_ID); // Test Mode

    public ActiveBidsFragment()
    {
        // Required empty public constructor
    }

    public static ActiveBidsFragment newInstance()
    {
        ActiveBidsFragment fragment = new ActiveBidsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param savedInstanceState -If the fragment is being recreated from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        databaseConnections();
    }

    /**
     * /**
     *
     * @param inflater           Instantiates a layout XML file into its corresponding view Objects
     * @param container          A view used to contain other views, in this case, the view fragment_active_bids
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     *                           This value may be null.
     * @return Returns inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_active_bids, container, false);
        getViewByIds(view);
        displayUsersBidsOnAd();

        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(intent);

        return view;
    }

    /**
     * Called when the view previously created by onCreateView(LayoutInflater, ViewGroup, Bundle) has been detached from the fragment.
     * The next time the fragment needs to be displayed, a new view will be created. This is called after onStop() and before onDestroy().
     * It is called regardless of whether onCreateView(LayoutInflater, ViewGroup, Bundle)
     * returned a non-null view. Internally it is called after the view's state has been saved but before it has been removed from its parent.
     */
    @Override
    public void onDestroyView()
    {
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroyView();
    }

    /**
     * Called when the fragment is no longer in use. This is called after onStop() and before onDetach()
     */
    @Override
    public void onDestroy()
    {
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroy();
    }

    /**
     * Accessing the Firebase Database to get information and upload new information from
     */
    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        databaseReferenceBidsTable = databaseConnections.getDatabaseReferenceBids();
        databaseReferenceRatingsTable = databaseConnections.getDatabaseReferenceRatings();
        databaseReferenceJobsTable = databaseConnections.getDatabaseReferenceJobs();
        databaseReferenceBidsTable.keepSynced(true);
        databaseReferenceRatingsTable.keepSynced(true);
        databaseReferenceJobsTable.keepSynced(true);
        user = databaseConnections.getCurrentUser();
    }

    private void getViewByIds(View view)
    {
        jobListView = view.findViewById(R.id.jobListView1);
    }

    /**
     * Getting all arguments from the bundle that was passed across
     *
     * @return jobInformation
     */
    private String getBundleInformation()
    {
        Bundle bundle = getArguments();
        return (String) bundle.getSerializable("JobId");
    }

    /**
     * Display all of the bids received on the advert and allow the user to accept bid
     */
    private void displayUsersBidsOnAd()
    {
        final String jobId = getBundleInformation();

        databaseReferenceBidsTable.child(jobId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot)
            {
                for (final DataSnapshot ds : dataSnapshot.getChildren())
                {
                    boolean active = ds.child(getString(R.string.active)).getValue(boolean.class);
                    if (active)
                    {
                        final UserBidInformation bid = ds.getValue(UserBidInformation.class);
                        bid.setJobID(jobId);
                        jobList.add(bid);
                    }
                }

                final MyCustomAdapter myCustomAdapter = new MyCustomAdapter();
                myCustomAdapter.addArray(jobList);
                jobListView.setAdapter(myCustomAdapter);

                jobListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                    {
                        pos = position;
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        final View mView = getLayoutInflater().inflate(R.layout.popup_accept_user_bid, null);

                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View titleView = inflater.inflate(R.layout.popup_style, null);
                        TextView title = titleView.findViewById(R.id.title);
                        title.setText("Accept Bid");
                        title.setTypeface(null, Typeface.BOLD);
                        alertDialog.setCustomTitle(titleView);
                        alertDialog.setView(mView);
                        final AlertDialog dialog = alertDialog.create();
                        dialog.show();

                        TextView textViewName = mView.findViewById(R.id.textName);
                        final RatingBar ratingBar = mView.findViewById(R.id.ratingBarSeeFeedback);
                        TextView textViewBid = mView.findViewById(R.id.textBid);
                        TextView textViewCommission = mView.findViewById(R.id.textCommission);
                        TextView textViewTotal = mView.findViewById(R.id.textTotal);
                        Button payPal = mView.findViewById(R.id.acceptBidButton);

                        textViewName.setText(jobList.get(position).getFullName());
                        BigDecimal decimal = new BigDecimal(Double.parseDouble(jobList.get(position).getUserBid()));
                        decimal = decimal.setScale(2, RoundingMode.CEILING);
                        textViewBid.setText("£" + decimal);

                        totalAmount = decimal.longValue();

                        if (totalAmount < 20.00 || totalAmount < 20.0 || totalAmount < 20)
                        {
                            commisionAmount = 1.00;
                            decimal = new BigDecimal(1.00);
                            decimal = decimal.setScale(2, RoundingMode.CEILING);
                            textViewCommission.setText("£" + decimal);
                        } else
                        {
                            double commission = (double) (decimal.doubleValue() * 0.05);
                            Math.round(commission);
                            decimal = new BigDecimal(commission);
                            decimal = decimal.setScale(2, RoundingMode.CEILING);
                            commisionAmount = decimal.doubleValue();
                            textViewCommission.setText("£" + decimal);
                        }

                        totalAmount = totalAmount + commisionAmount;
                        BigDecimal decimal1 = new BigDecimal(totalAmount).setScale(2, RoundingMode.CEILING);
                        textViewTotal.setText("£" + decimal1);

                        databaseReferenceRatingsTable.child(jobList.get(position).getUserID()).addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                long totalRating = 0;
                                long counter = 0;
                                // Iterate through entire bids table
                                for (DataSnapshot ds : dataSnapshot.getChildren())
                                {
                                    long rating = ds.child(getString(R.string.star_review_table)).getValue(long.class);

                                    totalRating += rating;
                                    counter++;

                                    totalRating = totalRating / counter;

                                    int usersRating = Math.round(totalRating);
                                    ratingBar.setNumStars(usersRating);
                                    ratingBar.getNumStars();
                                    Drawable drawable = ratingBar.getProgressDrawable();
                                    drawable.setColorFilter(Color.parseColor("#cece63"), PorterDuff.Mode.SRC_ATOP);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {

                            }
                        });

                        databaseReferenceRatingsTable.child(jobList.get(position).getUserID()).addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                long totalRating = 0;
                                long counter = 0;
                                // Iterate through entire bids table
                                if (dataSnapshot.hasChildren())
                                {
                                    for (DataSnapshot ds : dataSnapshot.getChildren())
                                    {
                                        long rating = ds.child(getString(R.string.star_review_table)).getValue(long.class);

                                        totalRating += rating;
                                        counter++;

                                        totalRating = totalRating / counter;

                                        int usersRating = Math.round(totalRating);
                                        ratingBar.setNumStars(usersRating);
                                        ratingBar.getNumStars();
                                        Drawable drawable = ratingBar.getProgressDrawable();
                                        drawable.setColorFilter(Color.parseColor("#cece63"), PorterDuff.Mode.SRC_ATOP);
                                    }
                                } else
                                {
                                    TextView textViewNoReview = mView.findViewById(R.id.textViewNoReview);
                                    textViewNoReview.setText("No Ratings For User");
                                    textViewNoReview.setVisibility(View.VISIBLE);
                                    ratingBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {

                            }
                        });

                        payPal.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                dialog.dismiss();
                                processPayment();
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    /**
     * handles functionality and creates intent for the in-app Paypal payment service
     */
    private void processPayment()
    {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(totalAmount), "GBP"
                , "Pay CrossRoadsMainActivity Commission", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    /**
     * @param requestCode The request code passed to startActivityForResult. (PAYPAL_REQUEST_CODE)
     * @param resultCode  The result code, either RESULT_OK or RESULT_CANCELED
     * @param data        An intent that carries data, in this case its used to get the image Uri
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PAYPAL_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null)
                {
                    try
                    {
                        databaseReferenceJobsTable.child(getBundleInformation()).child(getString(R.string.courier_id_table)).setValue(jobList.get(pos).getUserID());
                        databaseReferenceJobsTable.child(getBundleInformation()).child(getString(R.string.job_status_table)).setValue(JobStatus.Active.name());
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.content, new PostAnAdvertFragment()).commit();

                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetails);
                        JSONObject jsonObject1 = jsonObject.getJSONObject(getString(R.string.response));

                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
                        View mView = getLayoutInflater().inflate(R.layout.popup_payment_successful, null);

                        LayoutInflater inflater = getLayoutInflater();
                        View titleView = inflater.inflate(R.layout.popup_style, null);
                        TextView title = titleView.findViewById(R.id.title);
                        title.setText("Payment Successful");
                        title.setTypeface(null, Typeface.BOLD);
                        alertDialog.setCustomTitle(titleView);

                        alertDialog.setNegativeButton(R.string.close, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.setView(mView);
                        final AlertDialog dialog = alertDialog.create();
                        dialog.show();

                        TextView textViewId = mView.findViewById(R.id.textId);
                        TextView textViewAmount = mView.findViewById(R.id.textAmount);
                        TextView textViewStatus = mView.findViewById(R.id.textStatus);

                        textViewStatus.setText(jsonObject1.getString("state"));
                        textViewAmount.setText("£" + totalAmount);
                        textViewId.setText(jsonObject1.getString("id"));

                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        Bundle newBundle = new Bundle();
                        newBundle.putString("tabView", "Active");
                        MyAdvertsFragment myAdvertsFragment = new MyAdvertsFragment();
                        myAdvertsFragment.setArguments(newBundle);
                        fragmentTransaction.replace(R.id.content, myAdvertsFragment).addToBackStack("tag").commit();

                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
            {
                Toast.makeText(getActivity(), getString(R.string.cancel), Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
        {
            Toast.makeText(getActivity(), "Invalid", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Adapter to populate a view from an ArrayList is the ArrayAdapter
     */
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
            final MyCustomAdapter.GroupViewHolder holder;
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.job_info_list_bid, null);
                holder = new MyCustomAdapter.GroupViewHolder();

                holder.textViewName = convertView.findViewById(R.id.textName);
                holder.textViewBid = convertView.findViewById(R.id.textBid);
                holder.ratingBarSeeFeedback = convertView.findViewById(R.id.ratingBarSeeFeedback);
                holder.ratingNoFeedback = convertView.findViewById(R.id.ratingNoFeedback);
                holder.ratingNoFeedback.setVisibility(View.GONE);

                convertView.setTag(holder);
            } else
            {
                holder = (MyCustomAdapter.GroupViewHolder) convertView.getTag();
            }

            double userBid = Double.parseDouble(mData.get(position).getUserBid());
            BigDecimal decimal = new BigDecimal(userBid);
            decimal = decimal.setScale(2, RoundingMode.CEILING);

            holder.textViewBid.setText("£" + decimal);

            databaseReferenceRatingsTable.child(mData.get(position).getUserID()).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    long totalRating = 0;
                    long counter = 0;
                    // Iterate through entire bids table
                    if (dataSnapshot.hasChildren())
                    {
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            long rating = ds.child(getString(R.string.star_review_table)).getValue(long.class);

                            totalRating += rating;
                            counter++;

                            totalRating = totalRating / counter;

                            int usersRating = Math.round(totalRating);
                            holder.ratingBarSeeFeedback.setNumStars(usersRating);
                            holder.ratingBarSeeFeedback.getNumStars();
                            Drawable drawable = holder.ratingBarSeeFeedback.getProgressDrawable();
                            drawable.setColorFilter(Color.parseColor("#cece63"), PorterDuff.Mode.SRC_ATOP);
                        }
                    } else
                    {
                        holder.ratingNoFeedback.setText("No Ratings For User");
                        holder.ratingNoFeedback.setVisibility(View.VISIBLE);
                        holder.ratingBarSeeFeedback.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });

            holder.textViewName.setText(mData.get(position).getFullName());
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
            public RatingBar ratingBarSeeFeedback;
            public TextView ratingNoFeedback;
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

    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }


    /**
     * onAttach is called when a fragment is first attached to its context
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

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }
}
