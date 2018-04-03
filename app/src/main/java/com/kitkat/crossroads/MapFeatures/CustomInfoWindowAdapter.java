package com.kitkat.crossroads.MapFeatures;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.kitkat.crossroads.R;

/**
 * Created by craig on 02/04/18.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
{
    private final View window;
    private Context context;
    private static final String TAG = "CustomInfoWindowAdaper";

    public CustomInfoWindowAdapter(Context context)
    {
        this.context = context;
        window = LayoutInflater.from(context).inflate(R.layout.custom_info_window_map, null);
    }

    private void renderWindowText(Marker marker, View view)
    {
        try
        {
            String title = marker.getSnippet();
            TextView textViewTitle = view.findViewById(R.id.title);

            if(!title.equals(""))
            {
                textViewTitle.setText(title);
            }
        } catch(NullPointerException e)
        {
            Log.e(TAG, "NullPointerException: " + e.getMessage());
            Toast.makeText(context, "Can't get the information for this location at this time", Toast.LENGTH_SHORT).show();
        }

        try
        {
            String snippet = marker.getSnippet();
            TextView textViewSnippet = view.findViewById(R.id.snippet);

            if(!snippet.equals(""))
            {
                textViewSnippet.setText(snippet);
            }
        } catch (NullPointerException e)
        {
            Log.e(TAG, "NullPointer Exception: " + e.getMessage());
            Toast.makeText(context, "Can't get the information for this location at this time", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
        renderWindowText(marker, window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        renderWindowText(marker, window);
        return window;
    }
}
