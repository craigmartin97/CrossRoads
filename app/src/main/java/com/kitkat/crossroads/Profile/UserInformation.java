package com.kitkat.crossroads.Profile;

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
    public StorageReference profileImage;

    public UserInformation(String fullName, String phoneNumber, String addressOne, String addressTwo, String town, String postCode, boolean advertiser, boolean courier, StorageReference profileImage)
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
}
