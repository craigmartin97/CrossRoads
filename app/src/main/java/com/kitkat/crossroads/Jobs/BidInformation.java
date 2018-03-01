package com.kitkat.crossroads.Jobs;

/**
 * Created by om25_000 on 01/03/2018.
 */

public class BidInformation {

    public String jobID, UserID, Bid;



    public BidInformation() {
    }

    public BidInformation(String jobID, String userID, String bid) {
        this.jobID = jobID;
        this.UserID = userID;
        this.Bid = bid;
    }

    public String getJobID() {
        return jobID;
    }

    public String getUserID() {
        return UserID;
    }

    public String getBid() {
        return Bid;
    }
}
