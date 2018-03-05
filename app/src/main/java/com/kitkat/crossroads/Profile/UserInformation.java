package com.kitkat.crossroads.Profile;

import java.util.Date;

/**
 * Created by q5031372 on 14/02/18.
 */

public class UserInformation {

    public String name;
    public String address;
    public String dateOfBirth;
    public String phoneNumber;

    public UserInformation(String name, String address, String dateOfBirth, String phoneNumber)
    {
        this.name = name;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
    }

}
