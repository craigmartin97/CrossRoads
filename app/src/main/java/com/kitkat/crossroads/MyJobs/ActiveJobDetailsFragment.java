package com.kitkat.crossroads.MyJobs;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.ExpandableListAdapter;
import com.kitkat.crossroads.ExternalClasses.ListViewHeight;
import com.kitkat.crossroads.Jobs.JobInformation;
import com.kitkat.crossroads.R;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActiveJobDetailsFragment extends Fragment
{
    private DatabaseReference databaseReferenceJobsTable;

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
        View view = inflater.inflate(R.layout.fragment_active_job_details, container, false);

        getViewsByIds(view);
        JobInformation jobInformation = getBundleInformation();
        getJobInformationFromBundle(jobInformation);
        addItemsCollection();
        addItemsDelivery();
        addItemsJobInformation();
        createExpandableListViews();
        setupSignaturePad();
        return view;
    }

    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        databaseReferenceJobsTable = databaseConnections.getDatabaseReferenceJobs();
        databaseReferenceJobsTable.keepSynced(true);
        storageReference = databaseConnections.getStorageReference();
    }

    private void getViewsByIds(View view)
    {
        textViewJobName1 = view.findViewById(R.id.textViewJobName1);
        textViewDescription1 = view.findViewById(R.id.textViewJobDescription1);
        expandableListView = view.findViewById(R.id.expandable_list_view);
        expandableListView2 = view.findViewById(R.id.expandable_list_view2);
        expandableListView3 = view.findViewById(R.id.expandable_list_view3);
        mSignaturePad = view.findViewById(R.id.signature_pad);
        mClearButton = view.findViewById(R.id.clear_button);
        mJobCompleteButton = view.findViewById(R.id.job_complete_button);
        jobImageAccepted = view.findViewById(R.id.jobImageAccepted);
    }

    private JobInformation getBundleInformation()
    {
        final Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            jobId = (String) bundle.getSerializable(getString(R.string.job_id));
            return (JobInformation) bundle.getSerializable(getString(R.string.job_key));
        }
        else
        {
            return null;
        }
    }

    private void getJobInformationFromBundle(JobInformation jobInformation)
    {
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
    }

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

    private void createExpandableListViews()
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

    private void setupSignaturePad()
    {
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener()
        {
            @Override
            public void onStartSigning()
            {

            }

            @Override
            public void onSigned()
            {
                mJobCompleteButton.setEnabled(true);
                mClearButton.setEnabled(true);
                mJobCompleteButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mClearButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }

            @Override
            public void onClear()
            {
                mJobCompleteButton.setEnabled(false);
                mClearButton.setEnabled(false);
                mJobCompleteButton.setBackgroundColor(getResources().getColor(R.color.white));
                mClearButton.setBackgroundColor(getResources().getColor(R.color.white));
                mJobCompleteButton.setTextColor(getResources().getColor(R.color.white));
                mClearButton.setTextColor(getResources().getColor(R.color.white));
            }
        });

        mClearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mSignaturePad.clear();
            }
        });

        mJobCompleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();

                final StorageReference filePath = storageReference.child("Jobs").child(jobId).child("Signature/collectionSignature.jpg");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = filePath.putBytes(data);

                uploadTask.addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(getActivity(), R.string.unsuccessful, Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        Toast.makeText(getActivity(), R.string.success, Toast.LENGTH_SHORT).show();
                        databaseReferenceJobsTable.child(jobId).child(getString(R.string.job_status_table)).setValue(getString(R.string.complete));
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.content, new MyJobsFragment()).addToBackStack(null).commit();
                    }
                });
            }
        });
    }
}

