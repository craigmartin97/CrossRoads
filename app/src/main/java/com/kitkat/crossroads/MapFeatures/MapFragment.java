package com.kitkat.crossroads.MapFeatures;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
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
import com.google.android.gms.tasks.Task;
import com.kitkat.crossroads.ExternalClasses.WorkaroundMapFragment;
import com.kitkat.crossroads.Jobs.JobInformation;
import com.kitkat.crossroads.Jobs.PostAnAdvertFragment;
import com.kitkat.crossroads.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to display a Map to the user to select a location or use their current
 * location. The user can see information about that information as well such as the phone number,
 * website Uri, the name and full address.
 */
public class MapFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener
{

    /**
     * Tag used for Logs and debugging
     */
    private static final String TAG = "MapFragment";

    /**
     * Accessing the users locations, after they have gave permission
     */
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    /**
     * Boolean value to store the users permission
     */
    private boolean locationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    /**
     * Google map
     */
    private GoogleMap gMap;
    /**
     * Get the users current location their device is in
     */
    private FusedLocationProviderClient fusedLocationProviderClient;

    /**
     * Adapter, created by google, to display a list of potential locations as the user is typing
     */
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;

    /**
     * The main entry point for Google Play services integration
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Lat and Long Bounds that are used. This covers across the entire world
     */
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    /**
     * Storing the location details
     */
    private PlaceInformation placeInfo;

    /**
     * Marker to place a marker on the map
     */
    private Marker marker;

    /**
     * The default zoom to be used for the map
     */
    private static final float DEFAULT_ZOOM = 15;

    /**
     * Widgets that are found on the View, fragment_map
     */
    private AutoCompleteTextView editTextSearch;
    private ImageView imageViewGps;
    private ImageView imageViewInfo;
    private ImageView imageViewCheck;

    /**
     * Widgets found on the popup_accept_location
     */
    private TextView chooseLocationText;
    private Button yesButton;
    private Button noButton;

    private JobInformation jobInformation, jobInformation2;

    public MapFragment()
    {
        // Empty
    }

    /**
     * NOT USED At them moment, retained if we need to pass a bundle in, in the future or
     * set database connections
     *
     * @return - fragment
     */
    public static MapFragment newInstance()
    {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause()
    {
        try
        {
            super.onPause();
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        } catch (NullPointerException e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        placeInfo = new PlaceInformation();
        getViewsByIds(view);


        try
        {
            Bundle bundle = getArguments();
            if(bundle.getSerializable("JobInfo") != null)
            {
                jobInformation = (JobInformation) bundle.getSerializable("JobInfo");
            }
            if(bundle.getSerializable("JobInfoDel") != null)
            {
                jobInformation2 = (JobInformation) bundle.getSerializable("JobInfoDel");
                Toast.makeText(getActivity(), "HERE", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e)
        {
            Log.e(TAG, e.getMessage());
        }

        getLocationPermission();
        return view;
    }

    /**
     * Setting all of the widgets in the view to global variables for later use
     *
     * @param view
     */
    private void getViewsByIds(View view)
    {
        editTextSearch = view.findViewById(R.id.editTextSearch);
        imageViewGps = view.findViewById(R.id.ic_gps);
        imageViewInfo = view.findViewById(R.id.placeInfo);
        imageViewCheck = view.findViewById(R.id.check);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    /**
     * Initializing the map
     * Creates the GoogleApiClient to be able to find the locations and suggestions
     * All of the buttons, current location, information and selecting that location are all set
     * and created in this method
     */
    private void init()
    {
        Log.d(TAG, "Init: initializing");

        editTextSearch.setOnItemClickListener(mAutocompleteItemClickListener);
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient, LAT_LNG_BOUNDS, null);
        editTextSearch.setAdapter(placeAutocompleteAdapter);

        // Enter a location to search for
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
                    geoLocate();
                }
                return false;
            }
        });

        // Press the GPS image
        imageViewGps.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: Pressed GPS Image");
                // Get the current location and move the camera to that location
                getDeviceCurrentLocation();
            }
        });

        // Press the Info image
        imageViewInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: clicked place info");
                try
                {
                    // If the window is currently open
                    if (marker.isInfoWindowShown())
                    {
                        marker.hideInfoWindow();
                    } else
                    {
                        Log.d(TAG, "onCLick" + placeInfo.toString());
                        // Display the information in a new box
                        marker.showInfoWindow();
                    }
                } catch (NullPointerException e)
                {
                    Log.e(TAG, "onClick: Error" + e.getMessage());
                }
            }
        });

        // Press the check image
        imageViewCheck.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: Pressed The Tick Image");
                Toast.makeText(getActivity(), "Pressed Tick", Toast.LENGTH_SHORT).show();

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

                chooseLocationText = viewPopup.findViewById(R.id.selectLocationText);

                try
                {
                    // If their is information to display
                    if (placeInfo != null)
                    {
                        // Change the text in the alert dialog to the address
                        chooseLocationText.setText(placeInfo.getSubThoroughfare() + " " + placeInfo.getThoroughfare() + " " + placeInfo.getLocality() + " " + placeInfo.getPostCode());
                    }
                } catch (NullPointerException e)
                {
                    Toast.makeText(getActivity(), "Please Search For A Location First", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.getMessage());
                }

                // Setting widgets to variables
                yesButton = viewPopup.findViewById(R.id.yesButton);
                noButton = viewPopup.findViewById(R.id.noButton);

                yesButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        PostAnAdvertFragment postAnAdvertFragment = new PostAnAdvertFragment();
                        Bundle bundle = new Bundle();

                        if(jobInformation != null)
                        {
                            bundle.putSerializable("JobInfo", jobInformation);
                        }

                        if(jobInformation2 != null)
                        {
                            bundle.putSerializable("JobInfoDel", jobInformation2);
                        }

                        bundle.putSerializable("JobAddress", placeInfo);
                        postAnAdvertFragment.setArguments(bundle);

                        dialog.dismiss();

                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content, postAnAdvertFragment).commit();
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
        });

        hideKeyboard();
    }

    /**
     * Find a location that the user has entered and move the camera to that location
     */
    private void geoLocate()
    {
        Log.d(TAG, "geoLocate: geoLocating");
        String searchString = editTextSearch.getText().toString().trim();
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();

        try
        {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e)
        {
            Log.e(TAG, "geoLocate: IOException " + e.getMessage());
        }

        if (list.size() > 0)
        {
            Address address = list.get(0);
            Log.d(TAG, "Found A Location");

            if(address.getSubThoroughfare() != null)
            {
                placeInfo.setSubThoroughfare(address.getSubThoroughfare());
            }

            if(address.getSubThoroughfare() != null)
            {
                placeInfo.setThoroughfare(address.getThoroughfare());
            }
            else
            {
                placeInfo.setSubThoroughfare("");
            }

            if(address.getLocality() != null)
            {
                placeInfo.setLocality(address.getLocality());
            }
            else if(address.getSubLocality() != null)
            {
                placeInfo.setLocality(address.getSubLocality());
            }
            else if(address.getSubAdminArea() != null)
            {
                placeInfo.setLocality(address.getSubAdminArea());
            }
            else if(address.getAdminArea() != null)
            {
                placeInfo.setLocality(address.getAdminArea());
            }
            else
            {
                placeInfo.setLocality("");
            }

            placeInfo.setPostCode(address.getPostalCode());

            if(address.getPhone() != null)
            {
                placeInfo.setPhoneNumber(address.getPhone().toString());
            }
            else
            {
                placeInfo.setPhoneNumber("N/A");
            }

            if(address.getUrl() != null)
            {
                placeInfo.setWebsiteUrl(address.getUrl().toString());
            }
            else
            {
                placeInfo.setWebsiteUrl("N/A");
            }

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, placeInfo);
        } else
        {
            Toast.makeText(getActivity(), "Can't Find That Address, Try Again", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * Get the devices current location after the user has agreed to that permission
     */
    private void getDeviceCurrentLocation()
    {
        Log.d(TAG, "getDeviceCurrentLocation: Getting the devices current location");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try
        {
            if (locationPermissionGranted)
            {
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener()
                {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if (task.isSuccessful())
                        {
                            // Found Location
                            Log.d(TAG, "onComplete: Found Location");
                            Location devicesCurrentLocation = (Location) task.getResult();
                            List<Address> list = new ArrayList<>();
                            Geocoder geocoder = new Geocoder(getActivity());
                            try
                            {
                                list = geocoder.getFromLocation(devicesCurrentLocation.getLatitude(), devicesCurrentLocation.getLongitude(), 1);
                            } catch (IOException io)
                            {
                                Log.e(TAG, io.getMessage());
                            }

                            if (list.size() > 0)
                            {
                                Address address = list.get(0);

                                if(address.getSubThoroughfare() != null)
                                {
                                    placeInfo.setSubThoroughfare(address.getSubThoroughfare());
                                }

                                if(address.getSubThoroughfare() != null)
                                {
                                    placeInfo.setThoroughfare(address.getThoroughfare());
                                }
                                else
                                {
                                    placeInfo.setSubThoroughfare("");
                                }

                                if(address.getLocality() != null)
                                {
                                    placeInfo.setLocality(address.getLocality());
                                }
                                else if(address.getSubLocality() != null)
                                {
                                    placeInfo.setLocality(address.getSubLocality());
                                }
                                else if(address.getSubAdminArea() != null)
                                {
                                    placeInfo.setLocality(address.getSubAdminArea());
                                }
                                else if(address.getAdminArea() != null)
                                {
                                    placeInfo.setLocality(address.getAdminArea());
                                }
                                else
                                {
                                    placeInfo.setLocality("");
                                }

                                placeInfo.setPostCode(address.getPostalCode());

                                if(address.getPhone() != null)
                                {
                                    placeInfo.setPhoneNumber(address.getPhone().toString());
                                }
                                else
                                {
                                    placeInfo.setPhoneNumber("N/A");
                                }

                                if(address.getUrl() != null)
                                {
                                    placeInfo.setWebsiteUrl(address.getUrl().toString());
                                }
                                else
                                {
                                    placeInfo.setWebsiteUrl("N/A");
                                }

                                moveCamera(new LatLng(devicesCurrentLocation.getLatitude(), devicesCurrentLocation.getLongitude()), DEFAULT_ZOOM, placeInfo);
                            } else
                            {
                                Toast.makeText(getActivity(), "Could'nt Find Location, Please Try Again", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else
                        {
                            // Can't Find
                            Log.d(TAG, "onComplete: Can't Find Location");
                            Toast.makeText(getActivity(), "Unable To Find Your Current Location, Please Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e)
        {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    /**
     * Move the camera to a new location that the user has selected or the current locaiton
     *
     * @param latLng
     * @param zoom
     * @param placeInfo
     */
    private void moveCamera(LatLng latLng, float zoom, PlaceInformation placeInfo)
    {
        Log.d(TAG, "moveCamera: moving the camera lat: " + latLng.latitude + ",  long" + latLng.longitude);

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        gMap.clear();
        gMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getActivity()));

        if (placeInfo != null)
        {
            try
            {
                String details =
                        "Address: " + placeInfo.getSubThoroughfare() + " " + placeInfo.getThoroughfare() + " " + placeInfo.getLocality() + " " + placeInfo.getPostCode() + "\n" +
                                "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
                                "Website: " + placeInfo.getWebsiteUrl() + "\n";

                MarkerOptions options = new MarkerOptions().position(latLng).title(placeInfo.getSubThoroughfare() + " " + placeInfo.getThoroughfare() + " "
                        + placeInfo.getLocality() + " " + placeInfo.getPostCode()).snippet(details);
                marker = gMap.addMarker(options);
            } catch (NullPointerException e)
            {
                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage());
            }
        } else
        {
            gMap.addMarker(new MarkerOptions().position(latLng));
        }

        hideKeyboard();
    }

    /**
     * Create the inital map that the user will be displayed, they will be showed the
     * current location of their device upon creation.
     * If they havent agreed to the permission they will be returned to a blank fragment.
     */
    private void initMap()
    {
        Log.d(TAG, "initMap: initializing map");
        getChildFragmentManager().findFragmentById(R.id.map);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                Toast.makeText(getActivity(), "Map Is Ready And Loading...", Toast.LENGTH_SHORT).show();
                gMap = googleMap;

                if (locationPermissionGranted)
                {
                    getDeviceCurrentLocation();

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        return;
                    }

                    gMap.setMyLocationEnabled(true);
                    gMap.getUiSettings().setMyLocationButtonEnabled(false);

                    init();
                }
            }
        });
    }

    /**
     * Checking the users permission that they selected, accept or deny
     */
    private void getLocationPermission()
    {
        Log.d(TAG, "Getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                locationPermissionGranted = true;
                initMap();
            } else
            {
                // Denied
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else
        {
            // Denied
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        Log.d(TAG, "onRequestPermissionCalled");
        locationPermissionGranted = false;

        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0)
                {
                    for (int i = 0; i < grantResults.length; i++)
                    {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            Log.d(TAG, "Permission Failed");
                            locationPermissionGranted = false;
                            return;
                        }
                    }

                    Log.d(TAG, "Permission Granted");
                    locationPermissionGranted = true;
                    initMap();
                }
        }
    }

    private void hideKeyboard()
    {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
       Auto Complete API AutoComplete Suggestions
     */

    private AdapterView.OnItemClickListener mAutocompleteItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            hideKeyboard();

            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);

            placeResult.setResultCallback(mUpdatePlaceDetailsCallBack);
        }
    };

    /**
     * Setting the place location information
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallBack = new ResultCallback<PlaceBuffer>()
    {
        @Override
        public void onResult(@NonNull PlaceBuffer places)
        {
            if (!places.getStatus().isSuccess())
            {
                Log.d(TAG, "onResult: Places Query did not complete: " + places.getStatus().toString());
                // Prevent memory leak must release
                places.release();
                return;
            } else
            {
                final Place place = places.get(0);
                try
                {
                    List<Address> list = new ArrayList<>();
                    Geocoder geocoder = new Geocoder(getActivity());

                    try
                    {
                        list = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    } catch (IOException io)
                    {
                        Log.e(TAG, io.getMessage());
                    }

                    if (list.size() > 0)
                    {
                        Address address = list.get(0);
                        if(address.getSubThoroughfare() != null)
                        {
                            placeInfo.setSubThoroughfare(address.getSubThoroughfare());
                        }
                        else
                        {
                            placeInfo.setSubThoroughfare(place.getName().toString());
                        }


                        if(address.getThoroughfare() != null)
                        {
                            placeInfo.setThoroughfare(address.getThoroughfare());
                        }
                        else
                        {
                            placeInfo.setThoroughfare("");
                        }

                        if(address.getLocality() != null)
                        {
                            placeInfo.setLocality(address.getLocality());
                        }
                        else if(address.getSubLocality() != null)
                        {
                            placeInfo.setLocality(address.getSubLocality());
                        }
                        else if(address.getSubAdminArea() != null)
                        {
                            placeInfo.setLocality(address.getSubAdminArea());
                        }
                        else if(address.getAdminArea() != null)
                        {
                            placeInfo.setLocality(address.getAdminArea());
                        }
                        else
                        {
                            placeInfo.setLocality("");
                        }

                        placeInfo.setPostCode(address.getPostalCode());

                        if(address.getPhone() != null)
                        {
                            placeInfo.setPhoneNumber(place.getPhoneNumber().toString());
                        }
                        else
                        {
                            placeInfo.setPhoneNumber("N/A");
                        }

                        if(address.getUrl() != null)
                        {
                            placeInfo.setWebsiteUrl(place.getWebsiteUri().toString());
                        }
                        else
                        {
                            placeInfo.setWebsiteUrl("N/A");
                        }
                    }

                    Log.d(TAG, "onResult: " + placeInfo.toString());

                } catch (NullPointerException e)
                {
                    Log.e(TAG, "onResult: NullPointerException " + e.getMessage());
                }

                moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude), DEFAULT_ZOOM, placeInfo);
                editTextSearch.getText().clear();
                places.release();
            }
        }
    };
}
