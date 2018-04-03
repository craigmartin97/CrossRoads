package com.kitkat.crossroads.MapFeatures;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by craig on 01/04/18.
 */

public class PlaceInfo
{

    private String name;
    private String address;
    private String phoneNumber;
    private Uri websiteUri;
    private String id;
    private LatLng latLng;
    private float rating;
    private String attributions;

    public PlaceInfo()
    {

    }

    public PlaceInfo(String name, String address, String phoneNumber, Uri websiteUri, String id, LatLng latLng, float rating, String attributions)
    {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.websiteUri = websiteUri;
        this.id = id;
        this.latLng = latLng;
        this.rating = rating;
        this.attributions = attributions;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public Uri getWebsiteUri()
    {
        return websiteUri;
    }

    public void setWebsiteUri(Uri websiteUri)
    {
        this.websiteUri = websiteUri;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public LatLng getLatLng()
    {
        return latLng;
    }

    public void setLatLng(LatLng latLng)
    {
        this.latLng = latLng;
    }

    public float getRating()
    {
        return rating;
    }

    public void setRating(float rating)
    {
        this.rating = rating;
    }

    public String getAttributions()
    {
        return attributions;
    }

    public void setAttributions(String attributions)
    {
        this.attributions = attributions;
    }

    @Override
    public String toString()
    {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", websiteUri=" + websiteUri +
                ", id='" + id + '\'' +
                ", latLng=" + latLng +
                ", rating=" + rating +
                ", attributions='" + attributions + '\'' +
                '}';
    }
}
