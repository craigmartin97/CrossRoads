package com.kitkat.crossroads.Jobs;

import com.kitkat.crossroads.Profile.UserInformation;

import java.io.Serializable;

/**
 * Created by q5031372 on 16/03/18.
 */

public class UserBidInformation implements Serializable
{

    public String fullName, userBid, userID, jobID, courierId;

    public UserBidInformation()
    {

    }

    public UserBidInformation(String fullName, String userBid, String userID, String jobID)
    {
        this.fullName = fullName;
        this.userBid = userBid;
        this.userID = userID;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getUserBid()
    {
        return userBid;
    }

    public void setUserBid(String userBid)
    {
        this.userBid = userBid;
    }

    public String getUserID()
    {
        return userID;
    }

    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    public String getWholeString()
    {
        return fullName + userBid;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getCourierId()
    {
        return courierId;
    }

    public void setCourierId(String courierId)
    {
        this.courierId = courierId;
    }
}
