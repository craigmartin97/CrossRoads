package com.kitkat.crossroads.MapFeatures;

import java.io.Serializable;

/**
 * Created by craig on 03/04/18.
 */

public class PlaceInformation implements Serializable
{
    private String placeName;
    private String placeAddressLineOne;
    private String placeAddressLineTwo;
    private String placePostCode;

    public PlaceInformation()
    {

    }

    public PlaceInformation(String placeName, String placeAddressLineOne, String placeAddressLineTwo, String placePostCode)
    {
        this.placeName = placeName;
        this.placeAddressLineOne = placeAddressLineOne;
        this.placeAddressLineTwo = placeAddressLineTwo;
        this.placePostCode = placePostCode;
    }

    public String getPlaceName()
    {
        return placeName;
    }

    public void setPlaceName(String placeName)
    {
        this.placeName = placeName;
    }

    public String getPlaceAddressLineOne()
    {
        return placeAddressLineOne;
    }

    public void setPlaceAddressLineOne(String placeAddressLineOne)
    {
        this.placeAddressLineOne = placeAddressLineOne;
    }

    public String getPlaceAddressLineTwo()
    {
        return placeAddressLineTwo;
    }

    public void setPlaceAddressLineTwo(String placeAddressLineTwo)
    {
        this.placeAddressLineTwo = placeAddressLineTwo;
    }

    public String getPlacePostCode()
    {
        return placePostCode;
    }

    public void setPlacePostCode(String placePostCode)
    {
        this.placePostCode = placePostCode;
    }
}
