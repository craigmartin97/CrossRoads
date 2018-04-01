package com.kitkat.crossroads;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class GoogleMapsFragment extends Fragment
{

    private static final String TAG = "GoogleMaps";
    private static final int Error_Dialog_Request = 9001;

    public GoogleMapsFragment()
    {
        // Required empty public constructor
    }

    public static GoogleMapsFragment newInstance()
    {
        GoogleMapsFragment fragment = new GoogleMapsFragment();
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
        View view =  inflater.inflate(R.layout.fragment_google_maps, container, false);

        if(isServicesOK())
        {
            init(view);
        }
        return view;
    }

    private void init(View view)
    {
        Button mapButton = view.findViewById(R.id.buttonMap);
        mapButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

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
