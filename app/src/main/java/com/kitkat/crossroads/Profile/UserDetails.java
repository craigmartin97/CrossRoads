package com.kitkat.crossroads.Profile;

/**
 * Created by q5031372 on 15/02/18.
 */

public class UserDetails
{


    public String name;
    public String address;
    public String dateOfBirth;
    public String phoneNumber;

    public UserDetails()
    {

    }

    public UserDetails(String name, String address, String dateOfBirth, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
