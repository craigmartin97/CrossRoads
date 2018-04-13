package com.kitkat.crossroads.Jobs;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.kitkat.crossroads.ExternalClasses.GenericMethods;
import com.kitkat.crossroads.ExternalClasses.Map;
import com.kitkat.crossroads.ExternalClasses.WorkaroundMapFragment;
import com.kitkat.crossroads.MainActivity.CrossRoads;
import com.kitkat.crossroads.MapFeatures.CustomInfoWindowAdapter;
import com.kitkat.crossroads.MapFeatures.PlaceAutocompleteAdapter;
import com.kitkat.crossroads.MapFeatures.PlaceInformation;
import com.kitkat.crossroads.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PostAnAdvertFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener
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

    private Button buttonPostAd, buttonUploadImages;

    private static final int Error_Dialog_Request = 9001;

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

    private GenericMethods genericMethods = new GenericMethods();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        databaseConnections();
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

        boolean result = ((CrossRoads) getActivity()).getLocationPermissionGranted();
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
            }
        } else
        {
            RelativeLayout relativeLayout = view.findViewById(R.id.relLayout3);
            relativeLayout.setVisibility(View.GONE);
            relativeLayout.getLayoutParams().height = 0;

            RelativeLayout relativeLayout1 = view.findViewById(R.id.relLayout2);
            relativeLayout1.setVisibility(View.GONE);
            relativeLayout1.getLayoutParams().height = 0;
        }

        createOnClickListeners();
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
        profileImage = view.findViewById(R.id.jobImage1);
        profileImage.getLayoutParams().height = 0;

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
                    ifWidgetTextIsNull(editTextColAddL1, enterAddress1);
                }
                if (TextUtils.isEmpty(getTextInColAd2Widget()))
                {
                    ifWidgetTextIsNull(editTextColAddL2, enterAddress2);
                }
                if (TextUtils.isEmpty(getTextInColTownWidget()))
                {
                    ifWidgetTextIsNull(editTextColAddTown, enterTown);
                }
                if ((!(getTextInColPostCodeWidget().matches(getPostCodeRegex()))) || (TextUtils.isEmpty(getTextInColPostCodeWidget())))
                {
                    ifWidgetTextIsNull(editTextColAddPostcode, enterPostCode);
                }
                if (TextUtils.isEmpty(getTextInDelAd1Widget()))
                {
                    ifWidgetTextIsNull(editTextDelAddL1, enterAddress1);
                }
                if (TextUtils.isEmpty(getTextInDelAd2Widget()))
                {
                    ifWidgetTextIsNull(editTextDelAddL1, enterAddress2);
                }
                if (TextUtils.isEmpty(getTextInDelTownWidget()))
                {
                    ifWidgetTextIsNull(editTextDelAddTown, enterTown);
                }
                if ((!(getTextInDelPostCodeWidget().matches(getPostCodeRegex()))) || (TextUtils.isEmpty(getTextInDelPostCodeWidget())))
                {
                    ifWidgetTextIsNull(editTextDelAddPostcode, enterPostCode);
                } else
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
                        }
                    });
                }

                gallery.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, GALLERY_INTENT);
                        dialog.dismiss();
                    }
                });
            }
        });
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
                View viewPopup = getLayoutInflater().inflate(R.layout.popup_confirm_location, null);
                chooseLocationText = viewPopup.findViewById(R.id.selectLocationText);
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

                    if (pInfo != null)
                    {
                        // Change the text in the alert dialog to the address
                        map1.hideKeyboard(getActivity());
                        chooseLocationText.setText(pInfo.getSubThoroughfare() + " " + pInfo.getThoroughfare() + " " + pInfo.getLocality() + " " + pInfo.getPostCode());

                        yesButton.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                editTextColAddL1.setText(pInfo.getSubThoroughfare());
                                editTextColAddL2.setText(pInfo.getThoroughfare());
                                editTextColAddTown.setText(pInfo.getLocality());
                                editTextColAddPostcode.setText(pInfo.getPostCode());
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
                View viewPopup = getLayoutInflater().inflate(R.layout.popup_confirm_location, null);
                chooseLocationText = viewPopup.findViewById(R.id.selectLocationText);
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
                        chooseLocationText.setText(pInfo.getSubThoroughfare() + " " + pInfo.getThoroughfare() + " " + pInfo.getLocality() + " " + pInfo.getPostCode());

                        yesButton.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                editTextDelAddL1.setText(pInfo.getSubThoroughfare());
                                editTextDelAddL2.setText(pInfo.getThoroughfare());
                                editTextDelAddTown.setText(pInfo.getLocality());
                                editTextDelAddPostcode.setText(pInfo.getPostCode());
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

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Displaying Image...");
        progressDialog.show();
        final ExifInterfaceImageRotater exifInterfaceImageRotater = new ExifInterfaceImageRotater();

        // Redirect user to there gallery and get them to select an image
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            imageUri = data.getData();
            final Uri uri = data.getData();

            try
            {
                profileImage.setImageBitmap(exifInterfaceImageRotater.setUpImageTransfer(uri, getActivity().getContentResolver()));
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
            Bitmap photo = (Bitmap) extras.get("data");
            profileImage.setImageBitmap(photo);
            compressBitMapForStorage();
            setJobImageHeight();
        }

        progressDialog.dismiss();
    }

    private void saveJobInformation()
    {
        final String key = databaseReference.child("Jobs").push().getKey();

        final StorageReference filePath = storageReference.child("JobImages").child(auth.getCurrentUser().getUid()).child(key).child(imageUri.getLastPathSegment());
        filePath.putBytes(compressData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                genericMethods.customToastMessage("Uploaded Successfully!", getActivity());

                Uri downloadUri = taskSnapshot.getDownloadUrl();

                String jobStatus = "Pending";

                final JobInformation jobInformation = new JobInformation(getTextInAdNameWidget(), getTextInAdDescWidget(), getTextInJobSizeWidget(), getTextInJobTypeWidget(), user.trim(),
                        " ", getTextInCollectionDateWidget(), getTextInCollectionTimeWidget(), getTextInColAd1Wiget()
                        , getTextInColAd2Widget(), getTextInColTownWidget(), getTextInColPostCodeWidget()
                        , getTextInDelAd1Widget(), getTextInDelAd2Widget(), getTextInDelTownWidget(), getTextInDelPostCodeWidget(), jobStatus, downloadUri.toString());

                databaseReference.child("Jobs").child(key).setValue(jobInformation);
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
                            if (j.equals(jobInformation))
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
                progressDialog.dismiss();

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

        genericMethods.customToastMessage("Job Uploaded Successfully", getActivity());
    }

    private boolean isServicesOK()
    {
        Log.d(TAG, "IsServicesOK: checking google services version: ");
        int avaliable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if (avaliable == ConnectionResult.SUCCESS)
        {
            // Can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services Is Working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(avaliable))
        {
            Log.d(TAG, "isServicesOK: An error has occured but it can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), avaliable, Error_Dialog_Request);
            dialog.show();
        } else
        {
            Log.d(TAG, "isServicesError: Google Play Services Isnt Working, Unable To Resolve");
        }
        return false;
    }

    private boolean hasCamera()
    {
        return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void setJobImageHeight()
    {
        profileImage.setVisibility(View.VISIBLE);
        profileImage.getLayoutParams().height = 115;
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
    public void onPause()
    {
        super.onPause();

    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (((CrossRoads) getActivity()).getLocationPermissionGranted())
        {
            mGoogleApiClient1.stopAutoManage(getActivity());
            mGoogleApiClient1.disconnect();
        }

    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        if (((CrossRoads) getActivity()).getLocationPermissionGranted())
        {
            mGoogleApiClient1.stopAutoManage(getActivity());
            mGoogleApiClient1.disconnect();
        }

    }
}
