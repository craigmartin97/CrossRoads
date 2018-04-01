package com.kitkat.crossroads;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


public class MapFragment extends Fragment
{

    private static final String TAG = "MapFragment";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    private boolean locationPermissionGranted = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private GoogleMap gMap;

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
        View view =  inflater.inflate(R.layout.fragment_map, container, false);

        getLocationPermission();

        return view;
    }

    private void initMap()
    {
        Log.d(TAG, "initMap: initalizalizing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                Toast.makeText(getActivity(), "Map Is Ready And Loading...", Toast.LENGTH_SHORT).show();
                gMap = googleMap;
            }
        });

    }

    private void getLocationPermission()
    {
        Log.d(TAG, "Getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                locationPermissionGranted = true;
            }
        }
        else
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
                if(grantResults.length > 0)
                {
                    for(int i = 0; i < grantResults.length; i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            Log.d(TAG, "Permission Failed");
                            locationPermissionGranted = false;
                            return;
                        }
                    }

                    Log.d(TAG, "Permission Granted");
                    locationPermissionGranted = true;
                    // Initalize map
                    initMap();
                }
        }
    }
}
