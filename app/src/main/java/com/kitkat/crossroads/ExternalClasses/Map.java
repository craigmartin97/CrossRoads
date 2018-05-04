package com.kitkat.crossroads.ExternalClasses;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ScrollView;
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
import com.kitkat.crossroads.MapFeatures.CustomInfoWindowAdapter;
import com.kitkat.crossroads.MapFeatures.PlaceAutocompleteAdapter;
import com.kitkat.crossroads.MapFeatures.PlaceInformation;
import com.kitkat.crossroads.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Map class gets the current location of the user, displays a map on the page
 * The user is able to view their current location and find other locations anywhere in the world
 */
public class Map implements GoogleApiClient.OnConnectionFailedListener
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
    private GoogleMap gMap, gMap2;
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
    private GoogleApiClient mGoogleApiClient1;

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

    private ScrollView mScrollView1, mScrollView2;
    private SupportMapFragment mapFragment;
    private FragmentActivity fragmentActivity;
    private View view;
    private FragmentManager fragmentManager;

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
        hideKeyboard();
    }

    /**
     * Find a location that the user has entered and move the camera to that location
     */
    public void geoLocate(String searchLocation)
    {
        Log.d(TAG, "geoLocate: geoLocating");
        Geocoder geocoder = new Geocoder(fragmentActivity);
        List<Address> list = new ArrayList<>();

        try
        {
            list = geocoder.getFromLocationName(searchLocation, 1);
        } catch (IOException e)
        {
            Log.e(TAG, "geoLocate: IOException " + e.getMessage());
        }

        if (list.size() > 0)
        {
            Address address = list.get(0);
            Log.d(TAG, "Found A Location");

            if(address.getAddressLine(0) != null)
            {
                String addressLine = address.getAddressLine(0).substring(0, address.getAddressLine(0).indexOf(","));
                placeInfo.setPlaceName(addressLine);
            }

            if(address.getSubAdminArea() != null)
            {
                placeInfo.setPlaceAddressLineOne(address.getSubAdminArea());
            }

            if(address.getLocality() != null && !address.getSubAdminArea().equals(address.getLocality()))
            {
                placeInfo.setPlaceAddressLineTwo(address.getLocality());
            }
            else
            {
                placeInfo.setPlaceAddressLineTwo("");
            }

            if(address.getPostalCode() != null)
            {
                placeInfo.setPlacePostCode(address.getPostalCode());
            }

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, placeInfo);
        } else
        {
            Toast.makeText(fragmentActivity, "Can't Find That Address, Try Again", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * Get the devices current location after the user has agreed to that permission
     */
    public void getDeviceCurrentLocation()
    {
        Log.d(TAG, "getDeviceCurrentLocation: Getting the devices current location");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(fragmentActivity);
        placeInfo = new PlaceInformation();
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
                            Geocoder geocoder = new Geocoder(fragmentActivity, Locale.ENGLISH);

                            try
                            {
                                if (devicesCurrentLocation != null)
                                {
                                    list = geocoder.getFromLocation(devicesCurrentLocation.getLatitude(), devicesCurrentLocation.getLongitude(), 1);
                                } else
                                {
                                    Toast.makeText(fragmentActivity, "Unable To Get Location", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException io)
                            {
                                Log.e(TAG, io.getMessage());
                            }

                            if (list.size() > 0)
                            {
                                Address address = list.get(0);

                                if(address.getAddressLine(0) != null)
                                {
                                    String addressLine = address.getAddressLine(0).substring(0, address.getAddressLine(0).indexOf(","));
                                    placeInfo.setPlaceName(addressLine);
                                }

                                if(address.getSubAdminArea() != null)
                                {
                                    placeInfo.setPlaceAddressLineOne(address.getSubAdminArea());
                                }

                                if(address.getLocality() != null && !address.getSubAdminArea().equals(address.getLocality()))
                                {
                                    placeInfo.setPlaceAddressLineTwo(address.getLocality());
                                }
                                else
                                {
                                    placeInfo.setPlaceAddressLineTwo("");
                                }

                                if(address.getPostalCode() != null)
                                {
                                    placeInfo.setPlacePostCode(address.getPostalCode());
                                }

                                moveCamera(new LatLng(devicesCurrentLocation.getLatitude(), devicesCurrentLocation.getLongitude()), DEFAULT_ZOOM, placeInfo);
                            } else
                            {
                                return;
                            }
                        } else
                        {
                            // Can't Find
                            Log.d(TAG, "onComplete: Can't Find Location");
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
        gMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(fragmentActivity));

        if (placeInfo != null)
        {
            try
            {
                String details =
                        "Address: " + placeInfo.getPlaceName() + ", " + placeInfo.getPlaceAddressLineOne() + ", " + placeInfo.getPlaceAddressLineTwo() + ", " + placeInfo.getPlacePostCode() + "\n";

                MarkerOptions options = new MarkerOptions().position(latLng).title(placeInfo.getPlaceName() + ", " + placeInfo.getPlaceAddressLineOne() + ", "
                        + placeInfo.getPlaceAddressLineTwo() + ", " + placeInfo.getPlacePostCode()).snippet(details);
                marker = gMap.addMarker(options);
                hideKeyboard(fragmentActivity);
            } catch (NullPointerException e)
            {
                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage());
            }
        } else
        {
            gMap.addMarker(new MarkerOptions().position(latLng));
        }

        hideKeyboard(fragmentActivity);
    }

    /**
     * Create the inital map that the user will be displayed, they will be showed the
     * current location of their device upon creation.
     * If they havent agreed to the permission they will be returned to a blank fragment.
     */
    private void initMap()
    {
        Log.d(TAG, "initMap: initializing map");

        mapFragment.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                gMap = googleMap;
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                gMap.getUiSettings().setZoomControlsEnabled(true);

                mScrollView1 = view.findViewById(R.id.advertScrollView);
                ((WorkaroundMapFragment) fragmentManager.findFragmentById(R.id.map))
                        .setListener(new WorkaroundMapFragment.OnTouchListener()
                        {
                            @Override
                            public void onTouch()
                            {
                                mScrollView1.requestDisallowInterceptTouchEvent(true);
                            }
                        });

                mScrollView2 = view.findViewById(R.id.advertScrollView);
                ((WorkaroundMapFragment) fragmentManager.findFragmentById(R.id.map2))
                        .setListener(new WorkaroundMapFragment.OnTouchListener()
                        {
                            @Override
                            public void onTouch()
                            {
                                mScrollView2.requestDisallowInterceptTouchEvent(true);
                            }
                        });

                if (locationPermissionGranted)
                {
                    getDeviceCurrentLocation();

                    if (ActivityCompat.checkSelfPermission(fragmentActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(fragmentActivity,
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
    public void getLocationPermission(Context context, SupportMapFragment mapFragmentGiven, FragmentActivity fragmentActivityGiven, View viewGiven, FragmentManager fragmentManagerGiven)
    {
        Log.d(TAG, "Getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(context,
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if (ContextCompat.checkSelfPermission(context,
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                this.locationPermissionGranted = true;
                mapFragment = mapFragmentGiven;
                fragmentActivity = fragmentActivityGiven;
                view = viewGiven;
                fragmentManager = fragmentManagerGiven;
                initMap();
            } else
            {
                // Denied
                ActivityCompat.requestPermissions(fragmentActivity, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else
        {
            //Denied
            ActivityCompat.requestPermissions(fragmentActivity, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     *
     */
    private void hideKeyboard()
    {
        fragmentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void hideKeyboard(FragmentActivity fragmentActivity)
    {
        fragmentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     Auto Complete API AutoComplete Suggestions
     */
    public void setPlaceAutocompleteAdapter(PlaceAutocompleteAdapter placeAutocompleteAdapter)
    {
        this.placeAutocompleteAdapter = placeAutocompleteAdapter;
    }

    public void setmGoogleApiClient1(GoogleApiClient googleApiClient1)
    {
        this.mGoogleApiClient1 = googleApiClient1;
    }

    public AdapterView.OnItemClickListener mAutocompleteItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            hideKeyboard();
            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient1, placeId);
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
                    Geocoder geocoder = new Geocoder(fragmentActivity);

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

                        if(address.getAddressLine(0) != null)
                        {
                            String addressLine = address.getAddressLine(0).substring(0, address.getAddressLine(0).indexOf(","));
                            placeInfo.setPlaceName(addressLine);
                        }

                        if(address.getSubAdminArea() != null)
                        {
                            placeInfo.setPlaceAddressLineOne(address.getSubAdminArea());
                        }

                        if(address.getLocality() != null && !address.getSubAdminArea().equals(address.getLocality()))
                        {
                            placeInfo.setPlaceAddressLineTwo(address.getLocality());
                        }
                        else
                        {
                            placeInfo.setPlaceAddressLineTwo("");
                        }

                        if(address.getPostalCode() != null)
                        {
                            placeInfo.setPlacePostCode(address.getPostalCode());
                        }
                    }

                    Log.d(TAG, "onResult: " + placeInfo.toString());

                } catch (NullPointerException e)
                {
                    Log.e(TAG, "onResult: NullPointerException " + e.getMessage());
                }

                moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude), DEFAULT_ZOOM, placeInfo);
                places.release();
            }
        }
    };


    public PlaceInformation getPlaceInfo()
    {
        if (placeInfo != null)
        {
            return placeInfo;
        } else
        {
            return null;
        }
    }
}