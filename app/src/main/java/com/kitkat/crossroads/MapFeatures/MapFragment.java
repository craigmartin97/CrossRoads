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
import com.kitkat.crossroads.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener
{

    private static final String TAG = "MapFragment";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    private boolean locationPermissionGranted = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private GoogleMap gMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private PlaceInfo placeInfo;
    private Marker marker;

    private static final float DEFAULT_ZOOM = 15;

    // widgets
    private AutoCompleteTextView editTextSearch;
    private ImageView imageViewGps;
    private ImageView imageViewInfo;
    private ImageView imageViewCheck;
    private TextView chooseLocationText;
    private Button yesButton;
    private Button noButton;

    public MapFragment()
    {
        // Empty
    }

    public static MapFragment newInstance()
    {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        getViewsByIds(view);
        getLocationPermission();
        return view;
    }

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

    private void init()
    {
        Log.d(TAG, "Init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        editTextSearch.setOnItemClickListener(mAutocompleteItemClickListener);
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient, LAT_LNG_BOUNDS, null);
        editTextSearch.setAdapter(placeAutocompleteAdapter);

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
                    geoLocate();
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
                getDeviceCurrentLocation();
            }
        });

        imageViewInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: clicked place info");
                try
                {
                    if (marker.isInfoWindowShown())
                    {
                        marker.hideInfoWindow();
                    } else
                    {
                        Log.d(TAG, "onCLick" + placeInfo.toString());
                        marker.showInfoWindow();
                    }
                } catch (NullPointerException e)
                {
                    Log.e(TAG, "onClick: Error" + e.getMessage());
                }
            }
        });

        imageViewCheck.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: Pressed The Tick Image");
                Toast.makeText(getActivity(), "Pressed Tick", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                View viewPopup = getLayoutInflater().inflate(R.layout.popup_confirm_location, null);

                chooseLocationText = viewPopup.findViewById(R.id.selectLocationText);
                yesButton = viewPopup.findViewById(R.id.yesButton);
                noButton = viewPopup.findViewById(R.id.noButton);

                alertDialog.setTitle("Choose Location?");
                alertDialog.setView(viewPopup);
                final AlertDialog dialog = alertDialog.create();
                dialog.show();

                chooseLocationText = viewPopup.findViewById(R.id.selectLocationText);

                try
                {
                    if (placeInfo != null)
                    {
                        chooseLocationText.setText(placeInfo.getName() + ", " + placeInfo.getAddress().toString());
                    }
                } catch (NullPointerException e)
                {
                    Toast.makeText(getActivity(), "Please Search For A Location First", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.getMessage());
                }

                yesButton = viewPopup.findViewById(R.id.yesButton);
                noButton = viewPopup.findViewById(R.id.noButton);

                yesButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Toast.makeText(getActivity(), "Pressed The Yes Button", Toast.LENGTH_SHORT).show();
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

            placeInfo = new PlaceInfo();
            placeInfo.setAddress(address.getAddressLine(0));
            placeInfo.setName(address.getFeatureName().toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    placeInfo);
        } else
        {
            Toast.makeText(getActivity(), "Can't Find That Address, Try Again", Toast.LENGTH_SHORT).show();
            return;
        }
    }

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
                                placeInfo = new PlaceInfo();
                                placeInfo.setAddress(address.getAddressLine(0));
                                placeInfo.setName(address.getFeatureName().toString());
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

    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo)
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
                        "Address: " + placeInfo.getAddress() + "\n" +
                                "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
                                "Website: " + placeInfo.getWebsiteUri() + "\n" +
                                "Price Rating: " + placeInfo.getRating() + "\n";

                MarkerOptions options = new MarkerOptions().position(latLng).title(placeInfo.getName()).snippet(details);
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

//    private void moveCamera(LatLng latLng, float zoom, String title)
//    {
//        Log.d(TAG, "moveCamera: moving the camera lat: " + latLng.latitude + ",  long" + latLng.longitude);
//        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
//
//        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
//        gMap.addMarker(options);
//
//        hideKeyboard();
//    }

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
            }
        });
    }

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
                    placeInfo = new PlaceInfo();
                    placeInfo.setName(place.getName().toString());
                    placeInfo.setAddress(place.getAddress().toString());
                    placeInfo.setPhoneNumber(place.getPhoneNumber().toString());
                    placeInfo.setRating(place.getRating());
                    placeInfo.setWebsiteUri(place.getWebsiteUri());
                    placeInfo.setAttributions(place.getAttributions().toString());
                    placeInfo.setId(place.getId());
                    placeInfo.setLatLng(place.getLatLng());

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
