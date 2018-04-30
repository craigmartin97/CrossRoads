package com.kitkat.crossroads.Jobs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import com.kitkat.crossroads.ExternalClasses.ExifInterfaceImageRotate;
import com.kitkat.crossroads.ExternalClasses.GenericMethods;
import com.kitkat.crossroads.ExternalClasses.Map;
import com.kitkat.crossroads.ExternalClasses.WorkaroundMapFragment;
import com.kitkat.crossroads.MainActivity.CrossRoadsMainActivity;
import com.kitkat.crossroads.MainActivity.CrossRoads;
import android.content.pm.PackageManager;
import android.util.Log;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.kitkat.crossroads.MapFeatures.PlaceAutocompleteAdapter;
import com.kitkat.crossroads.MapFeatures.PlaceInformation;
import com.kitkat.crossroads.MyAdverts.MyAdvertsFragment;
import com.kitkat.crossroads.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.felipecsl.gifimageview.library.GifHeaderParser.TAG;

public class PostAnAdvertFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener
{
    /**
     * Get the authentication to the FireBase Authentication area
     */
    private FirebaseAuth auth;

    /**
     * Get the CurrentUsers ID
     */
    private String user;

    /**
     * Get the reference to the FireBase Database Users table
     */
    private DatabaseReference databaseReferenceUsersTable;

    /**
     * Get the reference to the FireBase Database Jobs table
     */
    private DatabaseReference databaseReferenceJobsTable;

    /**
     * Store the jobReference
     */
    private DataSnapshot jobReference;

    /**
     * Get the reference to the FireBase Storage area to store the Jobs Image In
     */
    private StorageReference storageReference;

    private ImageView profileImage;
    private Uri imageUri;
    private static byte[] compressData;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog progressDialog;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private static final String TAG = "PostAnActivityFragment";

    private EditText editTextAdName, editTextAdDescription, editTextColDate, editTextColTime;
    private EditText editTextColAddL1, editTextColAddL2, editTextColAddTown, editTextColAddPostcode;
    private EditText editTextDelAddL1, editTextDelAddL2, editTextDelAddTown, editTextDelAddPostcode;
    private Spinner editTextJobSize, editTextJobType;
    private ScrollView scrollView;

    private Button buttonPostAd, buttonUploadImages, buttonMap1, buttonMap2;
    private LinearLayout linLayout1, linLayout2;
    private RelativeLayout relLayout2, relLayout3;

    private static final int Error_Dialog_Request = 9001;

    private JobInformation jobInformation;
    private String jobIdKey;

    private CheckBox myAddressCheckBox1, myAddressCheckBox2;

    /**
     * Widgets that are found on the View, fragment_map
     */
    private AutoCompleteTextView editTextSearch, editTextSearch2;
    private ImageView imageViewGps, imageViewGps2;
    private ImageView imageViewCheck, imageViewCheck2;

    /**
     * Widgets found on the popup_accept_location
     */
    private TextView chooseLocationText;
    private Button yesButton;
    private Button noButton;

    private Map map1, map2;
    private SupportMapFragment mapFragment, mapFragment2;
    private View layoutView;

    /**
     * Lat and Long Bounds that are used. This covers across the entire world
     */
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private GoogleApiClient mGoogleApiClient1;

    private final GenericMethods genericMethods = new GenericMethods();

    private static final int REQUEST_CODE = 3;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        databaseConnections();
        ((CrossRoadsMainActivity)getActivity()).wifiCheck();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_post_an_advert, container, false);

        layoutView = view;
        getViewsByIds(view);
        // Collection Map
        map1 = new Map();
        map2 = new Map();

        ArrayAdapter<CharSequence> adapter1 = createSpinnerAdapter(R.array.job_sizes);
        ArrayAdapter<CharSequence> adapter2 = createSpinnerAdapter(R.array.job_types);

        getBundleInformation();
        if (jobInformation != null)
        {
            editTextAdName.setText(jobInformation.getAdvertName());
            editTextAdDescription.setText(jobInformation.getAdvertDescription());

            for (int i = 0; i < adapter1.getCount(); i++)
            {
                if (jobInformation.getJobSize().contentEquals(Objects.requireNonNull(adapter1.getItem(i))))
                {
                    editTextJobSize.setSelection(i);
                }
            }

            for (int i = 0; i < adapter2.getCount(); i++)
            {
                if (jobInformation.getJobType().contentEquals(Objects.requireNonNull(adapter2.getItem(i))))
                {
                    editTextJobType.setSelection(i);
                }
            }

            editTextColDate.setText(jobInformation.getCollectionDate());
            editTextColTime.setText(jobInformation.getCollectionTime());
            editTextColAddL1.setText(jobInformation.getColL1());
            editTextColAddL2.setText(jobInformation.getColL2());
            editTextColAddTown.setText(jobInformation.getColTown());
            editTextColAddPostcode.setText(jobInformation.getColPostcode());
            editTextDelAddL1.setText(jobInformation.getDelL1());
            editTextDelAddL2.setText(jobInformation.getDelL2());
            editTextDelAddTown.setText(jobInformation.getDelTown());
            editTextDelAddPostcode.setText(jobInformation.getDelTown());
            Picasso.get().load(jobInformation.getJobImage()).into(profileImage);
            setJobImageHeight();
        }

        boolean result = ((CrossRoadsMainActivity) getActivity()).getLocationPermissionGranted();
        if (result)
        {
            // Create google api client, so user has pre-set options to select.
            /*
                The main entry point for Google Play services integration
            */
            mGoogleApiClient1 = new GoogleApiClient
                    .Builder(getActivity())
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(getActivity(), 0, this)
                    .build();

            PlaceAutocompleteAdapter placeAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient1, LAT_LNG_BOUNDS, null);
            map1.setPlaceAutocompleteAdapter(placeAutocompleteAdapter);
            map1.setmGoogleApiClient1(mGoogleApiClient1);

            map2.setPlaceAutocompleteAdapter(placeAutocompleteAdapter);
            map2.setmGoogleApiClient1(mGoogleApiClient1);

            editTextSearch.setOnItemClickListener(map1.mAutocompleteItemClickListener);
            editTextSearch2.setOnItemClickListener(map2.mAutocompleteItemClickListener);

            editTextSearch.setAdapter(placeAutocompleteAdapter);
            editTextSearch2.setAdapter(placeAutocompleteAdapter);

            // Collection Map
            mapFragment = (WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(GoogleMap googleMap)
                {
                    map1.getLocationPermission(getActivity().getApplicationContext(), mapFragment, getActivity(), view, getChildFragmentManager());
                }
            });

            // Delivery Map
            mapFragment2 = (WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.map2);
            mapFragment2.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(GoogleMap googleMap)
                {
                    map2.getLocationPermission(getActivity().getApplicationContext(), mapFragment2, getActivity(), view, getChildFragmentManager());
                }
            });

            if (isServicesOK())
            {
                mapOnClickListeners();
                relLayout2.setVisibility(View.GONE);
                linLayout1.setVisibility(View.GONE);
                editTextSearch.setVisibility(View.GONE);
                imageViewGps.setVisibility(View.GONE);
                imageViewCheck.setVisibility(View.GONE);

                relLayout3.setVisibility(View.GONE);
                linLayout2.setVisibility(View.GONE);
                editTextSearch2.setVisibility(View.GONE);
                imageViewGps2.setVisibility(View.GONE);
                imageViewCheck2.setVisibility(View.GONE);
            }
        } else
        {
            RelativeLayout relativeLayout = view.findViewById(R.id.relLayout3);
            relativeLayout.setVisibility(View.GONE);
            relativeLayout.getLayoutParams().height = 0;

            RelativeLayout relativeLayout1 = view.findViewById(R.id.relLayout2);
            relativeLayout1.setVisibility(View.GONE);
            relativeLayout1.getLayoutParams().height = 0;

            buttonMap1.setVisibility(View.GONE);
            buttonMap2.setVisibility(View.GONE);
        }

        createOnClickListeners();
        return view;
    }

    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        auth = databaseConnections.getAuth();
        user = databaseConnections.getCurrentUser();
        databaseReferenceUsersTable = databaseConnections.getDatabaseReferenceUsers();
        databaseReferenceJobsTable = databaseConnections.getDatabaseReferenceJobs();
        databaseReferenceUsersTable.keepSynced(true);
        databaseReferenceJobsTable.keepSynced(true);
        storageReference = databaseConnections.getStorageReference();

        if (auth.getCurrentUser() == null)
        {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    private void getViewsByIds(View view)
    {
        // Set the widgets to variables
        profileImage = view.findViewById(R.id.jobImage1);
        profileImage.setVisibility(View.GONE);

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

        buttonMap1 = view.findViewById(R.id.buttonMap1);
        buttonMap2 = view.findViewById(R.id.buttonMap2);

        myAddressCheckBox1 = view.findViewById(R.id.checkBoxMyAddress1);
        myAddressCheckBox2 = view.findViewById(R.id.checkBoxMyAddress2);

        linLayout1 = view.findViewById(R.id.mapLin);
        linLayout2 = view.findViewById(R.id.mapLin2);
        relLayout2 = view.findViewById(R.id.relLayout2);
        relLayout3 = view.findViewById(R.id.relLayout3);

        // Create adapters for drop down lists
        ArrayAdapter<CharSequence> adapter1 = createSpinnerAdapter(R.array.job_sizes);
        ArrayAdapter<CharSequence> adapter2 = createSpinnerAdapter(R.array.job_types);
        editTextJobSize.setAdapter(adapter1);
        editTextJobType.setAdapter(adapter2);

        // Widgets for the map
        editTextSearch = view.findViewById(R.id.editTextSearch);
        imageViewGps = view.findViewById(R.id.ic_gps);
        imageViewCheck = view.findViewById(R.id.check);

        editTextSearch2 = view.findViewById(R.id.editTextSearch2);
        imageViewGps2 = view.findViewById(R.id.ic_gps2);
        imageViewCheck2 = view.findViewById(R.id.check2);
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
                        R.style.datepicker,
                        dateSetListener,
                        year, month, day);
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
                } else
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
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), R.style.datepicker, new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        editTextColTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.show();
            }
        });

        buttonPostAd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Posting a new ad
                if (jobIdKey == null)
                {
                    checkWidgetsContainText();
                    saveJobInformation();
                }
                // Editing an ad
                else
                {
                    checkWidgetsContainText();
                    saveEditJob();
                    newFragmentTransaction(new MyAdvertsFragment());
                }
            }
        });

        buttonUploadImages.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (verifyPermissions())
                {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    View mView = getLayoutInflater().inflate(R.layout.popup_image_chooser, null);

                    alertDialog.setTitle("Upload Image With");
                    alertDialog.setView(mView);
                    final AlertDialog dialog = alertDialog.create();
                    dialog.show();

                    Button gallery = mView.findViewById(R.id.gallery);
                    Button camera = mView.findViewById(R.id.camera);

                    if (!hasCamera())
                    {
                        camera.setEnabled(false);
                    } else
                    {
                        camera.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                                dialog.dismiss();

                            }
                        });
                    }

                    gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, GALLERY_INTENT);
                            dialog.dismiss();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getContext(), "Permissions Denied", Toast.LENGTH_SHORT).show();
                    verifyPermissions();
                }
            }
        });

        buttonMap1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (linLayout1.getVisibility() == View.GONE)
                {
                    relLayout2.setVisibility(View.VISIBLE);
                    linLayout1.setVisibility(View.VISIBLE);
                    editTextSearch.setVisibility(View.VISIBLE);
                    imageViewGps.setVisibility(View.VISIBLE);
                    imageViewCheck.setVisibility(View.VISIBLE);
                } else
                {
                    relLayout2.setVisibility(View.GONE);
                    linLayout1.setVisibility(View.GONE);
                    editTextSearch.setVisibility(View.GONE);
                    imageViewGps.setVisibility(View.GONE);
                    imageViewCheck.setVisibility(View.GONE);
                }
            }
        });

        buttonMap2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (linLayout2.getVisibility() == View.GONE)
                {
                    relLayout3.setVisibility(View.VISIBLE);
                    linLayout2.setVisibility(View.VISIBLE);
                    editTextSearch2.setVisibility(View.VISIBLE);
                    imageViewGps2.setVisibility(View.VISIBLE);
                    imageViewCheck2.setVisibility(View.VISIBLE);
                } else
                {
                    relLayout3.setVisibility(View.GONE);
                    linLayout2.setVisibility(View.GONE);
                    editTextSearch2.setVisibility(View.GONE);
                    imageViewGps2.setVisibility(View.GONE);
                    imageViewCheck2.setVisibility(View.GONE);
                }
            }
        });
        myAddressCheckBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (myAddressCheckBox1.isChecked())
                {
                    databaseReferenceUsersTable.child(user).addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            try
                            {
                                editTextColAddL1.setText(dataSnapshot.child("addressOne").getValue(String.class));
                                editTextColAddL2.setText(dataSnapshot.child("addressTwo").getValue(String.class));
                                editTextColAddTown.setText(dataSnapshot.child("town").getValue(String.class));
                                editTextColAddPostcode.setText(dataSnapshot.child("postCode").getValue(String.class));
                            } catch (NullPointerException e)
                            {
                                Log.d(TAG, e.getMessage());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    });
                } else
                {
                    try
                    {
                        editTextColAddL1.setText(null);
                        editTextColAddL2.setText(null);
                        editTextColAddTown.setText(null);
                        editTextColAddPostcode.setText(null);
                    } catch (NullPointerException e)
                    {
                        Log.d(TAG, e.getMessage());
                    }
                }
            }
        });

        myAddressCheckBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (myAddressCheckBox2.isChecked())
                {
                    databaseReferenceUsersTable.child(user).addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            try
                            {
                                editTextDelAddL1.setText(dataSnapshot.child("addressOne").getValue(String.class));
                                editTextDelAddL2.setText(dataSnapshot.child("addressTwo").getValue(String.class));
                                editTextDelAddTown.setText(dataSnapshot.child("town").getValue(String.class));
                                editTextDelAddPostcode.setText(dataSnapshot.child("postCode").getValue(String.class));
                            } catch (NullPointerException e)
                            {
                                Log.d(TAG, e.getMessage());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    });
                } else
                {
                    try
                    {
                        editTextDelAddL1.setText(null);
                        editTextDelAddL2.setText(null);
                        editTextDelAddTown.setText(null);
                        editTextDelAddPostcode.setText(null);
                    } catch (NullPointerException e)
                    {
                        Log.d(TAG, e.getMessage());
                    }
                }
            }
        });
    }

    private void checkWidgetsContainText()
    {
        String enterTown = "Please Enter A Town";
        String enterAddress1 = "Please Enter An Address Line 1";
        String enterAddress2 = "Please Enter An Address Line 2";
        String enterPostCode = "Please Enter A Valid PostCode";

        if (TextUtils.isEmpty(getTextInAdNameWidget()))
        {
            ifWidgetTextIsNull(editTextAdName, "Please Enter Advert Name!");
            return;
        }
        if (TextUtils.isEmpty(getTextInAdDescWidget()))
        {
            ifWidgetTextIsNull(editTextAdDescription, "Please Enter Advert Description!");
            return;
        }
        if (TextUtils.isEmpty(getTextInCollectionDateWidget()))
        {
            ifWidgetTextIsNull(editTextColDate, "Please Enter Collection Date!");
            return;
        }
        if (TextUtils.isEmpty(getTextInCollectionDateWidget()))
        {
            ifWidgetTextIsNull(editTextColTime, "Please Enter Collection Time!");
            return;
        }
        if (TextUtils.isEmpty(getTextInColAd1Widget()))
        {
            ifWidgetTextIsNull(editTextColAddL1, enterAddress1);
            return;
        }
        if (TextUtils.isEmpty(getTextInColAd2Widget()))
        {
            ifWidgetTextIsNull(editTextColAddL2, enterAddress2);
            return;
        }
        if (TextUtils.isEmpty(getTextInColTownWidget()))
        {
            ifWidgetTextIsNull(editTextColAddTown, enterTown);
            return;
        }
        if ((!(getTextInColPostCodeWidget().matches(getPostCodeRegex()))) || (TextUtils.isEmpty(getTextInColPostCodeWidget())))
        {
            ifWidgetTextIsNull(editTextColAddPostcode, enterPostCode);
            return;
        }
        if (TextUtils.isEmpty(getTextInDelAd1Widget()))
        {
            ifWidgetTextIsNull(editTextDelAddL1, enterAddress1);
            return;
        }
        if (TextUtils.isEmpty(getTextInDelAd2Widget()))
        {
            ifWidgetTextIsNull(editTextDelAddL1, enterAddress2);
            return;
        }
        if (TextUtils.isEmpty(getTextInDelTownWidget()))
        {
            ifWidgetTextIsNull(editTextDelAddTown, enterTown);
            return;
        }
        if ((!(getTextInDelPostCodeWidget().matches(getPostCodeRegex()))) || (TextUtils.isEmpty(getTextInDelPostCodeWidget())))
        {
            ifWidgetTextIsNull(editTextDelAddPostcode, enterPostCode);
        }
    }

    private void getBundleInformation()
    {
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            jobInformation = (JobInformation) bundle.getSerializable("JobInfo");
            jobIdKey = (String) bundle.getSerializable("JobIdKey");
        }
    }

    private void mapOnClickListeners()
    {
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN ||
                        keyEvent.getAction() == KeyEvent.KEYCODE_ENTER)
                {
                    // Find that location
                    map1.geoLocate(editTextSearch.getText().toString().trim());
                    editTextSearch.getText().clear();
                    map1.hideKeyboard(getActivity());
                }
                return false;
            }
        });

        imageViewGps.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: Pressed GPS Image");
                map1.getLocationPermission(getActivity().getApplicationContext(), mapFragment, getActivity(), layoutView, getChildFragmentManager());
                map1.hideKeyboard(getActivity());
            }
        });

        // Press the check image
        imageViewCheck.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Create a new Alert dialog for the user to interact with
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                // Inflate layout, and get widgets
                View viewPopup = getLayoutInflater().inflate(R.layout.popup_creator, null);
                chooseLocationText = viewPopup.findViewById(R.id.textViewCustomText);
                yesButton = viewPopup.findViewById(R.id.yesButton);
                noButton = viewPopup.findViewById(R.id.noButton);

                // Show the dialog and edit info in it
                alertDialog.setTitle("Choose Location?");
                alertDialog.setView(viewPopup);
                final AlertDialog dialog = alertDialog.create();
                dialog.show();

                try
                {
                    final PlaceInformation pInfo = map1.getPlaceInfo();

                    if (pInfo.getPlaceName() == null || pInfo.getPlaceAddressLineOne() == null || pInfo.getPlaceAddressLineTwo() == null
                            || pInfo.getPlacePostCode() == null)
                    {
                        Toast.makeText(getActivity(), "Bad Location Choose Another", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                    // Change the text in the alert dialog to the address
                    map1.hideKeyboard(getActivity());
                    String placeName = null;
                    if (pInfo.getPlaceName() != null)
                    {
                        placeName = pInfo.getPlaceName() + " ";
                    }
                    String addressOne = null;
                    if (pInfo.getPlaceAddressLineOne() != null)
                    {
                        addressOne = pInfo.getPlaceAddressLineOne() + " ";
                    }
                    String addressTwo = null;
                    if (pInfo.getPlaceAddressLineTwo() != null || !pInfo.getPlaceAddressLineTwo().equals(""))
                    {
                        addressTwo = pInfo.getPlaceAddressLineTwo() + " ";
                    }
                    String postCode = null;
                    if (pInfo.getPlacePostCode() != null)
                    {
                        postCode = pInfo.getPlacePostCode();
                    }

                    chooseLocationText.setText(placeName + addressOne + addressTwo + postCode);


                    yesButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            if (pInfo.getPlaceName() != null)
                            {
                                editTextColAddL1.setText(pInfo.getPlaceName());
                            }
                            if (pInfo.getPlaceAddressLineOne() != null)
                            {
                                editTextColAddL2.setText(pInfo.getPlaceAddressLineOne());
                            }
                            if (pInfo.getPlaceAddressLineTwo() != null)
                            {
                                editTextColAddTown.setText(pInfo.getPlaceAddressLineTwo());
                            }
                            if (pInfo.getPlacePostCode() != null)
                            {
                                editTextColAddPostcode.setText(pInfo.getPlacePostCode());
                            }
                            dialog.dismiss();
                        }
                    });

                    noButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            dialog.dismiss();
                        }
                    });

                } catch (NullPointerException e)
                {
                    genericMethods.customToastMessage("Please Search For A Location First", getActivity());
                    Log.e(TAG, e.getMessage());
                }
            }
        });
        // Map 2 for Delivery
        editTextSearch2.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN ||
                        keyEvent.getAction() == KeyEvent.KEYCODE_ENTER)
                {
                    // Find that location
                    map2.geoLocate(editTextSearch2.getText().toString().trim());
                    editTextSearch2.getText().clear();
                    map2.hideKeyboard(getActivity());
                }
                return false;
            }
        });

        imageViewGps2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: Pressed GPS Image");
                map2.getLocationPermission(getActivity().getApplicationContext(), mapFragment2, getActivity(), layoutView, getChildFragmentManager());
                map2.hideKeyboard(getActivity());
            }
        });

        // Press the check image
        imageViewCheck2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Create a new Alert dialog for the user to interact with
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                // Inflate layout, and get widgets
                View viewPopup = getLayoutInflater().inflate(R.layout.popup_creator, null);
                chooseLocationText = viewPopup.findViewById(R.id.textViewCustomText);
                yesButton = viewPopup.findViewById(R.id.yesButton);
                noButton = viewPopup.findViewById(R.id.noButton);

                // Show the dialog and edit info in it
                alertDialog.setTitle("Choose Location?");
                alertDialog.setView(viewPopup);
                final AlertDialog dialog = alertDialog.create();
                dialog.show();

                try
                {
                    final PlaceInformation pInfo = map2.getPlaceInfo();

                    if (pInfo != null)
                    {
                        // Change the text in the alert dialog to the address
                        map2.hideKeyboard(getActivity());
                        String placeName = null;
                        if (pInfo.getPlaceName() != null)
                        {
                            placeName = pInfo.getPlaceName() + " ";
                        }
                        String addressOne = null;
                        if (pInfo.getPlaceAddressLineOne() != null)
                        {
                            addressOne = pInfo.getPlaceAddressLineOne() + " ";
                        }
                        String addressTwo = null;
                        if (pInfo.getPlaceAddressLineTwo() != null || !pInfo.getPlaceAddressLineTwo().equals(""))
                        {
                            addressTwo = pInfo.getPlaceAddressLineTwo() + " ";
                        }
                        String postCode = null;
                        if (pInfo.getPlacePostCode() != null)
                        {
                            postCode = pInfo.getPlacePostCode();
                        }

                        chooseLocationText.setText(placeName + addressOne + addressTwo + postCode);

                        yesButton.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                if (pInfo.getPlaceName() != null)
                                {
                                    editTextColAddL1.setText(pInfo.getPlaceName());
                                }
                                if (pInfo.getPlaceAddressLineOne() != null)
                                {
                                    editTextColAddL2.setText(pInfo.getPlaceAddressLineOne());
                                }
                                if (pInfo.getPlaceAddressLineTwo() != null)
                                {
                                    editTextColAddTown.setText(pInfo.getPlaceAddressLineTwo());
                                }
                                if (pInfo.getPlacePostCode() != null)
                                {
                                    editTextColAddPostcode.setText(pInfo.getPlacePostCode());
                                }
                                dialog.dismiss();
                            }
                        });

                        noButton.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                dialog.dismiss();
                            }
                        });

                    }
                } catch (NullPointerException e)
                {
                    genericMethods.customToastMessage("Please Search For A Location First", getActivity());
                    Log.e(TAG, e.getMessage());
                }
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

    private String getTextInColAd1Widget()
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

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Displaying Image...");
        progressDialog.show();
        final ExifInterfaceImageRotate exifInterfaceImageRotate = new ExifInterfaceImageRotate();

        // Redirect user to there gallery and get them to select an image
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            imageUri = data.getData();
            final Uri uri = data.getData();

            try
            {
                profileImage.setImageBitmap(exifInterfaceImageRotate.setUpImageTransfer(uri, getActivity().getContentResolver()));
                compressBitMapForStorage();
                setJobImageHeight();

            } catch (Exception e)
            {
                Log.e(TAG, "Error Uploading Image: " + e.getMessage());
            }
        }
        // Redirect them to the Camera
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            imageUri = data.getData();
            Bitmap photo = (Bitmap) (extras != null ? extras.get("data") : null);
            profileImage.setImageBitmap(photo);
            compressBitMapForStorage();
            setJobImageHeight();
        }

        progressDialog.dismiss();
    }

    private void saveJobInformation()
    {
        final String key = databaseReferenceJobsTable.push().getKey();

        if (imageUri != null)
        {
            progressDialog.setMessage("Uploading Job Please Wait");
            progressDialog.create();
            progressDialog.show();
            final StorageReference filePath = storageReference.child("JobImages").child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child(key).child(imageUri.getLastPathSegment());
            filePath.putBytes(compressData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    genericMethods.customToastMessage("Uploaded Successfully!", getActivity());

                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    String jobStatus = "Pending";

                    assert (downloadUri) != null;
                    databaseReferenceJobsTable.child(key).setValue(setJobInformation(jobStatus, downloadUri));

                    progressDialog.dismiss();
                    newFragmentTransaction(new FindAJobFragment());
                    genericMethods.customToastMessage("Job Uploaded Successfully", getActivity());

                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    genericMethods.customToastMessage("Failed To Upload!", getActivity());
                    progressDialog.dismiss();
                }
            });
        } else
        {
            genericMethods.customToastMessage("You must upload an image", getActivity());
        }
    }

    private void saveEditJob()
    {

        final StorageReference filePath = storageReference.child("JobImages").child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child(jobIdKey).child(imageUri.getLastPathSegment());
        filePath.putBytes(compressData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Uri downloadUri = taskSnapshot.getDownloadUrl();
                String jobStatus = "Pending";

                assert (downloadUri) != null;
                databaseReferenceJobsTable.child(jobIdKey).setValue(setJobInformation(jobStatus, downloadUri));
                genericMethods.customToastMessage("Uploaded Successfully!", getActivity());
            }
        });
    }

    private JobInformation setJobInformation(String jobStatus, Uri downloadUri)
    {

        return new JobInformation(getTextInAdNameWidget(), getTextInAdDescWidget(), getTextInJobSizeWidget(), getTextInJobTypeWidget(), user.trim(),
                " ", getTextInCollectionDateWidget(), getTextInCollectionTimeWidget(), getTextInColAd1Widget()
                , getTextInColAd2Widget(), getTextInColTownWidget(), getTextInColPostCodeWidget()
                , getTextInDelAd1Widget(), getTextInDelAd2Widget(), getTextInDelTownWidget(), getTextInDelPostCodeWidget(), jobStatus, downloadUri.toString());
    }

    private boolean isServicesOK()
    {
        Log.d(TAG, "IsServicesOK: checking google services version: ");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if (available == ConnectionResult.SUCCESS)
        {
            // Can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services Is Working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            Log.d(TAG, "isServicesOK: An error has occurred but it can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, Error_Dialog_Request);
            dialog.show();
        } else
        {
            Log.d(TAG, "isServicesError: Google Play Services Isn't Working, Unable To Resolve");
        }
        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean hasCamera()
    {
        return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void setJobImageHeight()
    {
        profileImage.setVisibility(View.VISIBLE);
//        profileImage.getLayoutParams().height = 115;
    }

    private void compressBitMapForStorage()
    {
        profileImage.buildDrawingCache();
        profileImage.getDrawingCache();
        BitmapDrawable drawable = (BitmapDrawable) profileImage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        compressData = byteArrayOutputStream.toByteArray();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (((CrossRoadsMainActivity) getActivity()).getLocationPermissionGranted())
        {
            mGoogleApiClient1.stopAutoManage(getActivity());
            mGoogleApiClient1.disconnect();
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        if (((CrossRoadsMainActivity) getActivity()).getLocationPermissionGranted())
        {
            mGoogleApiClient1.stopAutoManage(getActivity());
            mGoogleApiClient1.disconnect();
        }

    }


    private boolean verifyPermissions()
    {
        Log.d(TAG, "Verifying user Phone permissions");
        String[] phonePermissions = {
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if(ContextCompat.checkSelfPermission(getActivity(), phonePermissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), phonePermissions[1]) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), phonePermissions, REQUEST_CODE);
            return false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] phonePermissions, @NonNull int[] grantResults)
    {
        verifyPermissions();
    }
}
