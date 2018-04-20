package com.kitkat.crossroads.MyJobs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.ExpandableListAdapter;
import com.kitkat.crossroads.ExternalClasses.ListViewHeight;
import com.kitkat.crossroads.Jobs.JobInformation;
import com.kitkat.crossroads.Jobs.PostAnAdvertFragment;
import com.kitkat.crossroads.Payment.ConfigPaypal;
import com.kitkat.crossroads.R;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ActiveJobDetailsFragment extends Fragment {

    private View view;

    private DatabaseReference databaseReference;

    private StorageReference storageReference;

    private String colDate, colTime, colAddress, colTown, colPostcode, delAddress, delTown, delPostcode, jobType, jobSize, jobId;

    private ExpandableListView expandableListView, expandableListView2, expandableListView3;

    private ExpandableListAdapter adapter, adapter2, adapter3;

    private List<String> list, list2, list3;

    private HashMap<String, List<String>> listHashMap, listHashMap2, listHashMap3;

    private SignaturePad mSignaturePad;

    private Button mJobCompleteButton, mClearButton;

    private TextView textViewJobName1, textViewDescription1;

    private ImageView jobImageAccepted;
    private String bid, courierId;
    private JobInformation jobInformation;

//    public static final int PAYPAL_REQUEST_CODE = 7171;
//    public static final PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId("jonsnow123@yahoo.co.uk"); // Test Mode


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseConnections();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_active_job_details, container, false);

        textViewJobName1 = view.findViewById(R.id.textViewJobName1);
        textViewDescription1 = view.findViewById(R.id.textViewJobDescription1);

        expandableListView = view.findViewById(R.id.expandable_list_view);
        expandableListView2 = view.findViewById(R.id.expandable_list_view2);
        expandableListView3 = view.findViewById(R.id.expandable_list_view3);

        mSignaturePad = view.findViewById(R.id.signature_pad);

        mClearButton = view.findViewById(R.id.clear_button);
        mJobCompleteButton = view.findViewById(R.id.job_complete_button);
        jobImageAccepted = view.findViewById(R.id.jobImageAccepted);

        final Bundle bundle = this.getArguments();
        jobInformation = (JobInformation) bundle.getSerializable("Job");
        jobId = (String) bundle.getSerializable("JobId");
        courierId = (String) ((JobInformation) bundle.getSerializable("Job")).getCourierID();


        Picasso.get().load(jobInformation.getJobImage()).fit().into(jobImageAccepted);

        textViewJobName1.setText(jobInformation.getAdvertName());
        textViewDescription1.setText(jobInformation.getAdvertDescription());

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

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
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

                final StorageReference filePath = storageReference.child("Jobs").child(jobId).child("Signature/collectionSignature.jpg");

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
                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                        databaseReference.child("Jobs").child(jobId).child("jobStatus").setValue("Complete");
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.content, new MyJobsFragment()).addToBackStack(null).commit();
                        //processPayment();
                    }
                });
            }
        });

        return view;
    }

//    private void processPayment()
//    {
//        try
//        {
//            databaseReference.child("Bids").child(jobId).child(courierId).addValueEventListener(new ValueEventListener()
//            {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot)
//                {
//                    bid = dataSnapshot.child("userBid").getValue(String.class);
//
//                    BigDecimal decimal = new BigDecimal(Double.parseDouble(bid));
//                    decimal = decimal.setScale(2, RoundingMode.CEILING);
//
//                    PayPalPayment payPalPayment = new PayPalPayment(decimal, "GBP"
//                            , "Pay CrossRoads Commission", PayPalPayment.PAYMENT_INTENT_SALE);
//
//                    Intent intent = new Intent(getActivity(), PaymentActivity.class);
//                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
//                    startActivityForResult(intent, PAYPAL_REQUEST_CODE);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError)
//                {
//
//                }
//            });
//        } catch(Exception e)
//        {
//            Log.e("Can't Find Bid Error", e.getMessage());
//        }
//
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        if (requestCode == PAYPAL_REQUEST_CODE)
//        {
//            if (resultCode == RESULT_OK)
//            {
//                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
//                if (confirmation != null)
//                {
//                    try
//                    {
//                        databaseReference.child("Jobs").child(jobId).child("jobStatus").setValue("Complete");
//                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                        fragmentTransaction.replace(R.id.content, new MyJobsFragment()).addToBackStack(null).commit();
//
////                        databaseReference.child("Jobs").child(jobId).child("jobStatus").setValue("Complete");
////                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
////                        fragmentTransaction.replace(R.id.content, new PostAnAdvertFragment()).commit();
//
//                        String paymentDetails = confirmation.toJSONObject().toString(4);
//                        JSONObject jsonObject = new JSONObject(paymentDetails);
//                        JSONObject jsonObject1 = jsonObject.getJSONObject("response");
//
//                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
//                        View mView = getLayoutInflater().inflate(R.layout.popup_payment_successful, null);
//
//                        alertDialog.setTitle("Payment Successful");
//                        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                dialog.dismiss();
//                            }
//                        });
//                        alertDialog.setView(mView);
//                        final AlertDialog dialog = alertDialog.create();
//                        dialog.show();
//
//                        TextView textViewId = mView.findViewById(R.id.textId);
//                        TextView textViewAmount = mView.findViewById(R.id.textAmount);
//                        TextView textViewStatus = mView.findViewById(R.id.textStatus);
//
//                        textViewStatus.setText(jsonObject1.getString("state"));
//                        textViewAmount.setText("£" + bid);
//                        textViewId.setText(jsonObject1.getString("id"));
//                    } catch (JSONException e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//            } else if (resultCode == Activity.RESULT_CANCELED)
//            {
//                Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
//            }
//        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
//        {
//            Toast.makeText(getActivity(), "Invalid", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        databaseReference = databaseConnections.getDatabaseReference();
        storageReference = databaseConnections.getStorageReference();
    }

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
}

