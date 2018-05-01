package com.kitkat.crossroads.Profile;

public class UserInformation
{
    /*
     * Must be public to be able to serialize on
     */
    public String fullName;
    public String phoneNumber;
    public String addressOne;
    public String addressTwo;
    public String town;
    public String postCode;
    public boolean advertiser;
    public boolean courier;
    public String profileImage;
    public String userEmail;

    /**
     * required empty public constructor
     */
    public UserInformation()
    {

    }

    public UserInformation(String fullName, String phoneNumber, String addressOne, String addressTwo, String town, String postCode, boolean advertiser, boolean courier, String profileImage, String userEmail)
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
        this.userEmail = userEmail;
    }

    public void setProfileImage(String profileImage)
    {
        this.profileImage = profileImage;
    }

}
