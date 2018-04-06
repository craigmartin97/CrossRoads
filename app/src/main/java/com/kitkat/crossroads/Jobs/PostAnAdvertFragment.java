package com.kitkat.crossroads.Jobs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kitkat.crossroads.Account.LoginActivity;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.ExifInterfaceImageRotater;
import com.kitkat.crossroads.ExternalClasses.ExpandableListAdapter;
import com.kitkat.crossroads.ExternalClasses.ListViewHeight;
import com.kitkat.crossroads.ExternalClasses.WorkaroundMapFragment;
import com.kitkat.crossroads.MapFeatures.MapFragment;
import com.kitkat.crossroads.MapFeatures.PlaceInformation;
import com.kitkat.crossroads.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PostAnAdvertFragment extends Fragment
{
    /**
     * Get the authentication to the Firebase Authentication area
     */
    private FirebaseAuth auth;

    /**
     * Get the CurrentUsers ID
     */
    private String user;

    /**
     * Get the reference to the Firebase Database
     */
    private DatabaseReference databaseReference;

    /**
     * Store the jobReference
     */
    private DataSnapshot jobReference;

    /**
     * Get the reference to the Firebase Storage area to store the Jobs Image In
     */
    private StorageReference storageReference;

    private static ImageView profileImage;
    private Uri imageUri;
    private static byte[] compressData;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog progressDialog;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private static final String TAG = "PostAnActivityFragment";

    private EditText editTextAdName, editTextAdDescription, editTextColDate, editTextColTime;
    private EditText editTextColAddL1, editTextColAddL2, editTextColAddTown, editTextColAddPostcode;
    private EditText editTextDelAddL1, editTextDelAddL2, editTextDelAddTown, editTextDelAddPostcode;
    private ExpandableListView expandableListView;
    private Spinner editTextJobSize, editTextJobType;
    private ScrollView scrollView;

    private Button buttonPostAd, buttonUploadImages;

    private static final int Error_Dialog_Request = 9001;

    private ArrayAdapter<CharSequence> adapter1, adapter2;

    private GoogleMap gMap;

    private ScrollView mScrollView;

    private View mView;

    public PostAnAdvertFragment()
    {
        // Required empty public constructor
    }

    public static PostAnAdvertFragment newInstance(String param1, String param2)
    {
        PostAnAdvertFragment fragment = new PostAnAdvertFragment();
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_post_an_advert, container, false);
        mView = view;

        if (gMap == null)
        {
            getChildFragmentManager().findFragmentById(R.id.map);
            SupportMapFragment mapFragment = (WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(GoogleMap googleMap)
                {
                    gMap = googleMap;
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    gMap.getUiSettings().setZoomControlsEnabled(true);

                    mScrollView = mView.findViewById(R.id.advertScrollView); //parent scrollview in xml, give your scrollview id value
                    ((WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                            .setListener(new WorkaroundMapFragment.OnTouchListener()
                            {
                                @Override
                                public void onTouch()
                                {
                                    mScrollView.requestDisallowInterceptTouchEvent(true);
                                }
                            });
                }
            });
        }

        getViewsByIds(view);

        if(isServicesOK())
        {
            init(view);
            initButton2(view);
        }

        createOnClickListeners();

        // Get map info
        try
        {
            Bundle bundle = getArguments();
            if(bundle.getSerializable("JobInfo") != null)
            {
                JobInformation jobInformation = (JobInformation) bundle.getSerializable("JobInfo");
                getJobInformation(jobInformation);

                editTextDelAddL1.setText(jobInformation.getDelL1());
                editTextDelAddL2.setText(jobInformation.getDelL2());
                editTextDelAddTown.setText(jobInformation.getDelTown());
                editTextDelAddPostcode.setText(jobInformation.getDelPostcode());

                if(bundle.getSerializable("JobAddress") != null)
                {
                    PlaceInformation address = (PlaceInformation) bundle.getSerializable("JobAddress");
                    editTextColAddL1.setText(address.getSubThoroughfare());
                    editTextColAddL2.setText(address.getThoroughfare());
                    editTextColAddTown.setText(address.getLocality());
                    editTextColAddPostcode.setText(address.getPostCode());
                }
            }

            if(bundle.getSerializable("JobInfoDel") != null)
            {
                JobInformation jobInformation = (JobInformation) bundle.getSerializable("JobInfoDel");
                getJobInformation(jobInformation);

                editTextColAddL1.setText(jobInformation.getColL1());
                editTextColAddL2.setText(jobInformation.getColL2());
                editTextColAddTown.setText(jobInformation.getColTown());
                editTextColAddPostcode.setText(jobInformation.getColPostcode());

                PlaceInformation address = (PlaceInformation) bundle.getSerializable("JobAddress");
                editTextDelAddL1.setText(address.getSubThoroughfare());
                editTextDelAddL2.setText(address.getThoroughfare());
                editTextDelAddTown.setText(address.getLocality());
                editTextDelAddPostcode.setText(address.getPostCode());
            }
        } catch(NullPointerException e)
        {
            Log.e(TAG, e.getMessage());
        }
        return view;
    }

    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        auth = databaseConnections.getAuth();
        user = databaseConnections.getCurrentUser();
        databaseReference = databaseConnections.getDatabaseReference();
        storageReference = databaseConnections.getStorageReference();

        if (auth.getCurrentUser() == null)
        {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    private void getViewsByIds(View view)
    {
        // Set the widgets to variables
        expandableListView = view.findViewById(R.id.expandable_list_view3);
        profileImage = view.findViewById(R.id.jobImage1);
        buttonPostAd = view.findViewById(R.id.buttonAddJob);
        buttonUploadImages = view.findViewById(R.id.buttonUploadImages);
        scrollView = view.findViewById(R.id.advertScrollView);
        editTextAdName = view.findViewById(R.id.editTextAdName);
        editTextAdDescription = view.findViewById(R.id.editTextAdDescription);
        editTextJobSize = view.findViewById(R.id.editTextJobSize);
        editTextJobType = view.findViewById(R.id.editTextJobType);
        editTextColDate = view.findViewById(R.id.editTextJobColDate);
        editTextColTime = view.findViewById(R.id.editTextJobColTime);
        editTextColAddL1 = view.findViewById(R.id.editTextJobColL1);
        editTextColAddL2 = view.findViewById(R.id.editTextJobColL2);
        editTextColAddTown = view.findViewById(R.id.editTextJobColTown);
        editTextColAddPostcode = view.findViewById(R.id.editTextJobColPostcode);
        editTextDelAddL1 = view.findViewById(R.id.editTextJobDelL1);
        editTextDelAddL2 = view.findViewById(R.id.editTextJobDelL2);
        editTextDelAddTown = view.findViewById(R.id.editTextJobDelTown);
        editTextDelAddPostcode = view.findViewById(R.id.editTextJobDelPostcode);

        // Create adapters for drop down lists
        adapter1 = createSpinnerAdapter(R.array.job_sizes);
        adapter2 = createSpinnerAdapter(R.array.job_types);
        editTextJobSize.setAdapter(adapter1);
        editTextJobType.setAdapter(adapter2);
    }

    private ArrayAdapter<CharSequence> createSpinnerAdapter(int arrayField)
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), arrayField, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void createOnClickListeners()
    {
        editTextColDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                month = month + 1;
                Log.d(TAG, "onDateSet: date: " + year + "/" + month + "/" + dayOfMonth);

                if (dayOfMonth >= 1 && dayOfMonth <= 9)
                {
                    String newDay = "0" + dayOfMonth;
                    editTextColDate.setText(newDay + "/" + month + "/" + year);
                }

                if (month >= 1 && month <= 9)
                {
                    String newMonth = "0" + month;
                    editTextColDate.setText(dayOfMonth + "/" + newMonth + "/" + year);
                }

                if (dayOfMonth >= 1 && dayOfMonth <= 9 && month >= 1 && month <= 9)
                {
                    String newDay = "0" + dayOfMonth;
                    String newMonth = "0" + month;
                    editTextColDate.setText(newDay + "/" + newMonth + "/" + year);
                }
                else
                {
                    editTextColDate.setText(dayOfMonth + "/" + month + "/" + year);
                }
            }
        };

        editTextColTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        editTextColTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        buttonPostAd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String enterTown = "Please Enter A Town";
                String enterAddress1 = "Please Enter An Address Line 1";
                String enterAddress2 = "Please Enter An Address Line 2";
                String enterPostCode = "Please Enter A Valid PostCode";

                if (TextUtils.isEmpty(getTextInAdNameWidget()))
                {
                    ifWidgetTextIsNull(editTextAdName, "Please Enter Advert Name!");
                }
                if (TextUtils.isEmpty(getTextInAdDescWidget()))
                {
                    ifWidgetTextIsNull(editTextAdDescription, "Please Enter Advert Description!");
                }
                if (TextUtils.isEmpty(getTextInColAd1Wiget()))
                {
                    ifWidgetTextIsNull(editTextColAddL1,enterAddress1);
                }
                if(TextUtils.isEmpty(getTextInColAd2Widget()))
                {
                    ifWidgetTextIsNull(editTextColAddL2, enterAddress2);
                }
                if (TextUtils.isEmpty(getTextInColTownWidget()))
                {
                    ifWidgetTextIsNull(editTextColAddTown, enterTown);
                }
                if ((!(getTextInColPostCodeWidget().toString().matches(getPostCodeRegex()))) || (TextUtils.isEmpty(getTextInColPostCodeWidget())))
                {
                    ifWidgetTextIsNull(editTextColAddPostcode, enterPostCode);
                }
                if (TextUtils.isEmpty(getTextInDelAd1Widget()))
                {
                    ifWidgetTextIsNull(editTextDelAddL1, enterAddress1);
                }
                if(TextUtils.isEmpty(getTextInDelAd2Widget()))
                {
                    ifWidgetTextIsNull(editTextDelAddL1, enterAddress2);
                }
                if (TextUtils.isEmpty(getTextInDelTownWidget()))
                {
                    ifWidgetTextIsNull(editTextDelAddTown, enterTown);
                }
                if ((!(getTextInDelPostCodeWidget().toString().matches(getPostCodeRegex()))) || (TextUtils.isEmpty(getTextInDelPostCodeWidget())))
                {
                    ifWidgetTextIsNull(editTextDelAddPostcode, enterPostCode);
                }
                else
                {
                    saveJobInformation();
                    newFragmentTransaction(new FindAJobFragment());
                }
            }
        });

        buttonUploadImages.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });
    }

    private void ifWidgetTextIsNull(EditText text, String message)
    {
        text.setText("");
        text.setHintTextColor(Color.RED);
        text.setHint(message);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    private void newFragmentTransaction(Fragment fragmentToTransferTo)
    {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragmentToTransferTo).addToBackStack("tag").commit();
    }

    private void getJobInformation(JobInformation jobInformation)
    {
        editTextAdName.setText(jobInformation.getAdvertName());
        editTextAdDescription.setText(jobInformation.getAdvertDescription());

        for(int i = 0; i < adapter1.getCount(); i++)
        {
            if(jobInformation.getJobSize().equals(adapter1.getItem(i)))
            {
                editTextJobSize.setSelection(i);
            }
        }

        for(int i = 0; i < adapter2.getCount(); i++)
        {
            if(jobInformation.getJobType().equals(adapter2.getItem(i)))
            {
                editTextJobType.setSelection(i);
            }
        }

        editTextColDate.setText(jobInformation.getCollectionDate());
        editTextColTime.setText(jobInformation.getCollectionTime());
    }

    private String getPostCodeRegex()
    {
        return "^([A-PR-UWYZ](([0-9](([0-9]|[A-HJKSTUW])?)?)|([A-HK-Y][0-9]([0-9]|[ABEHMNPRVWXY])?)) ?[0-9][ABD-HJLNP-UW-Z]{2})$";
    }

    private String getTextInAdNameWidget()
    {
        return editTextAdName.getText().toString().trim();
    }

    private String getTextInAdDescWidget()
    {
        return editTextAdDescription.getText().toString().trim();
    }

    private String getTextInJobSizeWidget()
    {
        return editTextJobSize.getSelectedItem().toString().trim();
    }

    private String getTextInJobTypeWidget()
    {
        return editTextJobType.getSelectedItem().toString().trim();
    }

    private String getTextInCollectionDateWidget()
    {
        return editTextColDate.getText().toString().trim();
    }

    private String getTextInCollectionTimeWidget()
    {
        return editTextColTime.getText().toString().trim();
    }

    private String getTextInColAd1Wiget()
    {
        return editTextColAddL1.getText().toString().trim();
    }

    private String getTextInColAd2Widget()
    {
        return editTextColAddL2.getText().toString().trim();
    }

    private String getTextInColTownWidget()
    {
        return editTextColAddTown.getText().toString().trim();
    }

    private String getTextInColPostCodeWidget()
    {
        return editTextColAddPostcode.getText().toString().trim();
    }

    private String getTextInDelAd1Widget()
    {
        return editTextDelAddL1.getText().toString().trim();
    }

    private String getTextInDelAd2Widget()
    {
        return editTextDelAddL2.getText().toString().trim();
    }

    private String getTextInDelTownWidget()
    {
        return editTextDelAddTown.getText().toString().trim();
    }

    private String getTextInDelPostCodeWidget()
    {
        return editTextDelAddPostcode.getText().toString().trim();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Redirect user to there gallery and get them to select an image
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Displaying Image...");
            progressDialog.show();

            imageUri = data.getData();
            final Uri uri = data.getData();

            try
            {
                ExifInterfaceImageRotater exifInterfaceImageRotater = new ExifInterfaceImageRotater();
                profileImage.setImageBitmap(exifInterfaceImageRotater.setUpImageTransfer(uri, getActivity().getContentResolver()));
                profileImage.buildDrawingCache();
                profileImage.getDrawingCache();
                Bitmap bitmap = profileImage.getDrawingCache();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                compressData = byteArrayOutputStream.toByteArray();
                progressDialog.dismiss();

            } catch (Exception e)
            {
                Log.e(TAG, "Error Uploading Image: " + e.getMessage());
            }
        }
    }

    private void saveJobInformation()
    {
        final StorageReference filePath = storageReference.child("Images").child(auth.getCurrentUser().getUid()).child(imageUri.getLastPathSegment());
        filePath.putBytes(compressData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(getActivity(), "Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed To Upload!", Toast.LENGTH_SHORT).show();
            }
        });

        String jobStatus = "Pending";

        final JobInformation jobInformation = new JobInformation(getTextInAdNameWidget(), getTextInAdDescWidget(), getTextInJobSizeWidget(), getTextInJobTypeWidget(), user.trim(),
                " ", getTextInCollectionDateWidget(), getTextInCollectionTimeWidget(), getTextInColAd1Wiget()
                , getTextInColAd2Widget(), getTextInColTownWidget(), getTextInColPostCodeWidget()
                , getTextInDelAd1Widget(), getTextInDelAd2Widget(), getTextInDelTownWidget(), getTextInDelPostCodeWidget(), jobStatus);


        databaseReference.child("Jobs").push().setValue(jobInformation);

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                jobReference = dataSnapshot.child("Jobs");

                Iterable<DataSnapshot> jobListSnapShot = jobReference.getChildren();

                for (DataSnapshot ds : jobListSnapShot)
                {
                    JobInformation j = ds.getValue(JobInformation.class);
                    if(j.equals(jobInformation))
                    {
                        databaseReference.child("Jobs").child(ds.getKey()).child("jobID").setValue(ds.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        Toast.makeText(getActivity(), "Job Added!", Toast.LENGTH_SHORT).show();
    }

    private void init(View view)
    {
        final Button mapButton = view.findViewById(R.id.buttonMap);
        mapButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MapFragment mapFragment = new MapFragment();
                Bundle bundle = new Bundle();

                // job info obj
                final JobInformation jobInformation = new JobInformation(getTextInAdNameWidget(), getTextInAdDescWidget(), getTextInJobSizeWidget(), getTextInJobTypeWidget(), " ",
                        " ", getTextInCollectionDateWidget(), getTextInCollectionTimeWidget(), getTextInColAd1Wiget(), getTextInColAd2Widget()
                        , getTextInColTownWidget(), getTextInColPostCodeWidget(), getTextInDelAd1Widget(),
                        getTextInDelAd2Widget(), getTextInDelTownWidget(), getTextInDelPostCodeWidget(), " ");

                bundle.putSerializable("JobInfo", jobInformation);
                mapFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, mapFragment).commit();
            }
        });
    }

    private void initButton2(View view)
    {
        final Button mapButton = view.findViewById(R.id.buttonMap2);
        mapButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MapFragment mapFragment = new MapFragment();
                Bundle bundle = new Bundle();

                final JobInformation jobInformation = new JobInformation(getTextInAdNameWidget(), getTextInAdDescWidget(), getTextInJobSizeWidget(), getTextInJobTypeWidget(), " ",
                        " ", getTextInCollectionDateWidget(), getTextInCollectionTimeWidget(), getTextInColAd1Wiget(), getTextInColAd2Widget()
                        , getTextInColTownWidget(), getTextInColPostCodeWidget(), getTextInDelAd1Widget(),
                        getTextInDelAd2Widget(), getTextInDelTownWidget(), getTextInDelPostCodeWidget(), " ");

                bundle.putSerializable("JobInfoDel", jobInformation);
                mapFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, mapFragment).commit();
            }
        });
    }


    public boolean isServicesOK()
    {
        Log.d(TAG, "IsServicesOK: checking google services version: ");
        int avaliable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if(avaliable == ConnectionResult.SUCCESS)
        {
            // Can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services Is Working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(avaliable))
        {
            Log.d(TAG, "isServicesOK: An error has occured but it can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), avaliable, Error_Dialog_Request);
            dialog.show();
        }
        else
        {
            Log.d(TAG, "isServicesError: Google Play Services Isnt Working, Unable To Resolve");
        }
        return false;
    }
}
