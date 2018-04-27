package com.kitkat.crossroads.Profile;


/**
 * Object that contains the users information.
 */
public class UserInformation
{
    /**
     * Variables used for the users information.
     * Must be public for serializable.
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
     * Blank constructor method.
     */
    public UserInformation()
    {

    }

    /**
     * Constructor that sets all values in the object.
     * @param fullName Full name of the user.
     * @param phoneNumber Phone number of the user.
     * @param addressOne First line of the users address.
     * @param addressTwo Second line of the users address.
     * @param town Town of the users address.
     * @param postCode Postcode of the users address.
     * @param advertiser boolean of whether the user is an advertiser.
     * @param courier boolean of whether the user is a courier.
     * @param profileImage String for the profile image download URL.
     * @param userEmail String of the users email address.
     */
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

    /**
     * Set method for profileImage
     * @param profileImage String for the profile image download URL.
     */
    public void setProfileImage(String profileImage)
    {
        this.profileImage = profileImage;
    }

}
