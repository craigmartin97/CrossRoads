package com.kitkat.crossroads.MapFeatures;

/**
 * Created by craig on 03/04/18.
 */

public class PlaceInformation
{
    private String subThoroughfare;
    private String thoroughfare;
    private String locality;
    private String postCode;
    private String phoneNumber;
    private String websiteUrl;

    public PlaceInformation()
    {

    }

    public PlaceInformation(String subThoroughfare, String thoroughfare, String locality, String postCode, String phoneNumber, String websiteUrl)
    {
        this.subThoroughfare = subThoroughfare;
        this.thoroughfare = thoroughfare;
        this.locality = locality;
        this.postCode = postCode;
        this.phoneNumber = phoneNumber;
        this.websiteUrl = websiteUrl;
    }

    public String getSubThoroughfare()
    {
        return subThoroughfare;
    }

    public void setSubThoroughfare(String subThoroughfare)
    {
        this.subThoroughfare = subThoroughfare;
    }

    public String getThoroughfare()
    {
        return thoroughfare;
    }

    public void setThoroughfare(String thoroughfare)
    {
        this.thoroughfare = thoroughfare;
    }

    public String getLocality()
    {
        return locality;
    }

    public void setLocality(String locality)
    {
        this.locality = locality;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsiteUrl()
    {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl)
    {
        this.websiteUrl = websiteUrl;
    }
}
