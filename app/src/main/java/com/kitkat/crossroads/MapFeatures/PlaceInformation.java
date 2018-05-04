package com.kitkat.crossroads.MapFeatures;

import java.io.Serializable;

/**
 * PlaceInformation creates a new object to store all of the information about
 * the current location that has been selected
 */
public class PlaceInformation implements Serializable
{
    public String placeName;
    public String placeAddressLineOne;
    public String placeAddressLineTwo;
    public String placePostCode;

    public PlaceInformation()
    {

    }

    /**
     * Create a new PlaceInformation object
     *
     * @param placeName           String: name of the current location
     * @param placeAddressLineOne String: First address line of the current location
     * @param placeAddressLineTwo String: Second address line of the current location
     * @param placePostCode       String: PostCode of the selected location
     */
    public PlaceInformation(String placeName, String placeAddressLineOne, String placeAddressLineTwo, String placePostCode)
    {
        this.placeName = placeName;
        this.placeAddressLineOne = placeAddressLineOne;
        this.placeAddressLineTwo = placeAddressLineTwo;
        this.placePostCode = placePostCode;
    }

    /**
     * Get the name of the location
     *
     * @return String: name of location
     */
    public String getPlaceName()
    {
        return placeName;
    }

    /**
     * Change the name of the current location
     *
     * @param placeName String: name of location
     */
    public void setPlaceName(String placeName)
    {
        this.placeName = placeName;
    }

    /**
     * Get the name of the first part of the address line
     *
     * @return String: name of first part of address
     */
    public String getPlaceAddressLineOne()
    {
        return placeAddressLineOne;
    }

    /**
     * Assign a new value to the address line one
     *
     * @param placeAddressLineOne String: address line name
     */
    public void setPlaceAddressLineOne(String placeAddressLineOne)
    {
        this.placeAddressLineOne = placeAddressLineOne;
    }

    /**
     * Get the address line of place two
     *
     * @return String: address line two
     */
    public String getPlaceAddressLineTwo()
    {
        return placeAddressLineTwo;
    }

    /**
     * Assign a new value to the address line two
     *
     * @param placeAddressLineTwo String: address line two
     */
    public void setPlaceAddressLineTwo(String placeAddressLineTwo)
    {
        this.placeAddressLineTwo = placeAddressLineTwo;
    }

    /**
     * Get the postcode of the address
     *
     * @return String: postcode
     */
    public String getPlacePostCode()
    {
        return placePostCode;
    }

    /**
     * Assign a value to the postCode
     *
     * @param placePostCode String: postCode name
     */
    public void setPlacePostCode(String placePostCode)
    {
        this.placePostCode = placePostCode;
    }
}