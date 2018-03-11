package com.kitkat.crossroads.Profile;

import java.util.Date;

/**
 * Created by q5031372 on 14/02/18.
 */

public class UserInformation {

    public String fullName;
    public String phoneNumber;
    public String addressOne;
    public String addressTwo;
    public String town;
    public String postCode;
    public boolean advertiser;
    public boolean courier;

    public UserInformation(String fullName, String phoneNumber, String addressOne, String addressTwo, String town, String postCode, boolean advertiser, boolean courier)
    {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.addressOne = addressOne;
        this.addressTwo = addressTwo;
        this.town = town;
        this.postCode = postCode;
        this.advertiser = advertiser;
        this.courier = courier;
    }

}
