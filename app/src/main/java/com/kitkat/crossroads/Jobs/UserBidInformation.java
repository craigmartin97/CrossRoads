package com.kitkat.crossroads.Jobs;

import java.io.Serializable;

/**
 * Creates a new UserBidInformation object that
 * stores all of the bid information. Such as fullName, bid, userId and isActive
 */
public class UserBidInformation implements Serializable
{

    /**
     * All strings that make up a users bid
     */
    public String fullName, userBid, userID, jobID;

    /**
     * If the users bid is active or inactive, for soft delete
     */
    public boolean active;

    /**
     * Used for serialization
     */
    public UserBidInformation()
    {

    }

    /**
     * Constructor to create a new UserBid object
     *
     * @param fullName String: fullName of the user who is making a bid
     * @param userBid  String: the bid the user has entered
     * @param userID   String: the bidders id
     * @param active   boolean: if the bid is active or inactive
     */
    public UserBidInformation(String fullName, String userBid, String userID, boolean active)
    {
        this.fullName = fullName;
        this.userBid = userBid;
        this.userID = userID;
        this.active = active;
    }

    /**
     * @return
     */
    public String getFullName()
    {
        return fullName;
    }

    /**
     * @return
     */
    public String getUserBid()
    {
        return userBid;
    }

    /**
     * @return
     */
    public String getUserID()
    {
        return userID;
    }

    /**
     * @return
     */
    public String getWholeString()
    {
        return fullName + userBid;
    }

    /**
     * @return
     */
    public void setJobID(String jobID)
    {
        this.jobID = jobID;
    }

    /**
     * @return
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * @return
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }
}
