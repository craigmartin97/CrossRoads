package com.kitkat.crossroads.Profile;

import android.net.Uri;

import com.google.firebase.storage.StorageReference;

/**
 * Created by q5031372 on 14/02/18.
 */

public class UserInformation
{
    // Must be public to be able to serialize on
    public String fullName;
    public String phoneNumber;
    public String addressOne;
    public String addressTwo;
    public String town;
    public String postCode;
    public boolean advertiser;
    public boolean courier;
    public String profileImage;

    public UserInformation()
    {

    }

    public UserInformation(String fullName, String phoneNumber, String addressOne, String addressTwo, String town, String postCode, boolean advertiser, boolean courier, String profileImage)
    {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.addressOne = addressOne;
        this.addressTwo = addressTwo;
        this.town = town;
        this.postCode = postCode;
        this.advertiser = advertiser;
        this.courier = courier;
        this.profileImage = profileImage;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getAddressOne()
    {
        return addressOne;
    }

    public void setAddressOne(String addressOne)
    {
        this.addressOne = addressOne;
    }

    public String getAddressTwo()
    {
        return addressTwo;
    }

    public void setAddressTwo(String addressTwo)
    {
        this.addressTwo = addressTwo;
    }

    public String getTown()
    {
        return town;
    }

    public void setTown(String town)
    {
        this.town = town;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public boolean isAdvertiser()
    {
        return advertiser;
    }

    public void setAdvertiser(boolean advertiser)
    {
        this.advertiser = advertiser;
    }

    public boolean isCourier()
    {
        return courier;
    }

    public void setCourier(boolean courier)
    {
        this.courier = courier;
    }

    public String getProfileImage()
    {
        return profileImage;
    }

    public void setProfileImage(String profileImage)
    {
        this.profileImage = profileImage;
    }
}
