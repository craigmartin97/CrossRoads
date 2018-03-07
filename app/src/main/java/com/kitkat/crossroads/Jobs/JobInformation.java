package com.kitkat.crossroads.Jobs;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by s6042911 on 15/02/18.
 */

public class JobInformation implements Serializable {

    private String advertName, advertDescription, jobSize, jobType, posterID, jobID, courierID, collectionDate, collectionTime;
    private String colL1, colL2, colTown, colPostcode, delL1, delL2, delTown, delPostcode;
    private boolean jobActive;

    public JobInformation()
    {

    }


    public JobInformation(String advertName, String advertDescription, String jobSize, String jobType, String posterID, String courierID, String collectionDate, String collectionTime, String colL1, String colL2, String colTown, String colPostcode, String delL1, String delL2, String delTown, String delPostcode, boolean jobActive) {
        this.advertName = advertName;
        this.advertDescription = advertDescription;
        this.jobSize = jobSize;
        this.jobType = jobType;
        this.posterID = posterID;
        this.courierID = courierID;
        this.collectionDate = collectionDate;
        this.collectionTime = collectionTime;
        this.colL1 = colL1;
        this.colL2 = colL2;
        this.colTown = colTown;
        this.colPostcode = colPostcode;
        this.delL1 = delL1;
        this.delL2 = delL2;
        this.delTown = delTown;
        this.delPostcode = delPostcode;
        this.jobActive = jobActive;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public void setCourierID(String courierID) { this.courierID = courierID; }

    public String getCourierID() { return courierID; }

    public String getJobID() {
        return jobID;
    }

    public String getAdvertName() {
        return advertName;
    }

    public String getAdvertDescription() {
        return advertDescription;
    }

    public String getJobSize() {
        return jobSize;
    }

    public String getJobType() {
        return jobType;
    }

    public String getPosterID() {
        return posterID;
    }

    public String getCollectionDate() {
        return collectionDate;
    }

    public String getCollectionTime() {
        return collectionTime;
    }

    public String getColL1() {
        return colL1;
    }

    public String getColL2() {
        return colL2;
    }

    public String getColTown() {
        return colTown;
    }

    public String getColPostcode() {
        return colPostcode;
    }

    public String getDelL1() {
        return delL1;
    }

    public String getDelL2() {
        return delL2;
    }

    public String getDelTown() {
        return delTown;
    }

    public String getDelPostcode() {
        return delPostcode;
    }

    public boolean isJobActive() {
        return jobActive;
    }
}
