package com.kitkat.crossroads.Profile;

/**
 * UserInformation class creates a new object to hold and store all of the users information.
 * The information that we ask for here goes into the FireBase database under the user table.
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
     * Blank constructor method needed for serialization
     */
    public UserInformation()
    {

    }

    /**
     * Constructs a new UserInformation object
     *
     * @param fullName     String: fullName of the user
     * @param phoneNumber  String: phoneNumber of the user
     * @param addressOne   String: addressLine One of the user
     * @param addressTwo   String: addressLine two of the user
     * @param town         String: town that the user lives in
     * @param postCode     String: the users postcode where they live
     * @param advertiser   boolean: if the user is going to be an advertiser primarily
     * @param courier      boolean: if the user is going to be a courier primarily
     * @param profileImage String: URL address of the users profile image
     * @param userEmail    String:  email address of the user
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
     * Set the users profile image. When the user uploads a profile image
     * this method is called to set the variable in this class
     *
     * @param profileImage - URL address of the image
     */
    public void setProfileImage(String profileImage)
    {
        this.profileImage = profileImage;
    }

}
