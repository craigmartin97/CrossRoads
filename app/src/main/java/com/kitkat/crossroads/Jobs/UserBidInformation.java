package com.kitkat.crossroads.Jobs;

import com.kitkat.crossroads.Profile.UserInformation;

import java.io.Serializable;

/**
 * Created by q5031372 on 16/03/18.
 */

public class UserBidInformation implements Serializable
{

    public String fullName, userBid, userID, jobID;
    public boolean active;

    public UserBidInformation()
    {

    }

    public UserBidInformation(String fullName, String userBid, String userID, boolean active)
    {
        this.fullName = fullName;
        this.userBid = userBid;
        this.userID = userID;
        this.active = active;
    }

    public String getFullName()
    {
        return fullName;
    }

    public String getUserBid()
    {
        return userBid;
    }

    public String getUserID()
    {
        return userID;
    }

    public String getWholeString()
    {
        return fullName + userBid;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }
}
