package com.kitkat.crossroads.Jobs;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Job;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by s6042911 on 15/02/18.
 */

public class JobInformation implements Serializable, Comparable
{

    private String advertName, advertDescription, jobSize, jobType, posterID, jobID, courierID, collectionDate, collectionTime;
    private String colL1, colL2, colTown, colPostcode, delL1, delL2, delTown, delPostcode;
    private String jobStatus;
    private String jobImage;

    public JobInformation()
    {

    }

    public JobInformation(String advertName, String advertDescription, String jobSize, String jobType, String posterID, String courierID, String collectionDate, String collectionTime, String colL1, String colL2, String colTown, String colPostcode, String delL1, String delL2, String delTown, String delPostcode, String jobStatus, String jobImage)
    {
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
        this.jobStatus = jobStatus;
        this.jobImage = jobImage;
    }

    public void setJobID(String jobID)
    {
        this.jobID = jobID;
    }

    public void setJobStatus(String jobStatus)
    {
        this.jobStatus = jobStatus;
    }

    public void setCourierID(String courierID)
    {
        this.courierID = courierID;
    }

    public String getCourierID()
    {
        return courierID;
    }

    public String getJobID()
    {
        return jobID;
    }

    public String getAdvertName()
    {
        return advertName;
    }

    public String getAdvertDescription()
    {
        return advertDescription;
    }

    public String getJobSize()
    {
        return jobSize;
    }

    public String getJobType()
    {
        return jobType;
    }

    public String getPosterID()
    {
        return posterID;
    }

    public String getCollectionDate()
    {
        return collectionDate;
    }

    public String getCollectionTime()
    {
        return collectionTime;
    }

    public String getColL1()
    {
        return colL1;
    }

    public String getColL2()
    {
        return colL2;
    }

    public String getColTown()
    {
        return colTown;
    }

    public String getColPostcode()
    {
        return colPostcode;
    }

    public String getDelL1()
    {
        return delL1;
    }

    public String getDelL2()
    {
        return delL2;
    }

    public String getDelTown()
    {
        return delTown;
    }

    public String getDelPostcode()
    {
        return delPostcode;
    }

    public String getJobStatus()
    {
        return jobStatus;
    }

    public String getJobImage()
    {
        return jobImage;
    }

    public void setJobImage(String jobImage)
    {
        this.jobImage = jobImage;
    }

    public String getWholeString()
    {
        return advertName + advertDescription + jobSize + jobType + collectionDate + collectionTime + colL1 + colL2 + colTown + colPostcode + delL1 + delL2 + delTown + delPostcode;
    }


    public static Comparator<JobInformation> nameComparatorA = new Comparator<JobInformation>() {

        @Override
        public int compare(JobInformation o1, JobInformation o2) {
            String jobName1 = o1.getAdvertName().toUpperCase();
            String jobName2 = o2.getAdvertName().toUpperCase();

            return jobName1.compareTo(jobName2);
        }
    };

    public static Comparator<JobInformation> colComparatorA = new Comparator<JobInformation>() {

        @Override
        public int compare(JobInformation o1, JobInformation o2) {
            String colAd1 = o1.colTown.toUpperCase();
            String colAd2 = o2.colTown.toUpperCase();

            return colAd1.compareTo(colAd2);
        }
    };

    public static Comparator<JobInformation> delComparatorA = new Comparator<JobInformation>() {

        @Override
        public int compare(JobInformation o1, JobInformation o2) {
            String delAd1 = o1.delTown.toUpperCase();
            String delAd2 = o2.delTown.toUpperCase();

            return delAd1.compareTo(delAd2);
        }
    };

    public static Comparator<JobInformation> dateComparatorA = new Comparator<JobInformation>() {

        @Override
        public int compare(JobInformation o1, JobInformation o2) {
            String date1 = o1.collectionDate.toUpperCase();
            String date2 = o2.collectionDate.toUpperCase();

            return date1.compareTo(date2);
        }
    };

    public static Comparator<JobInformation> sizeComparatorA = new Comparator<JobInformation>() {

        @Override
        public int compare(JobInformation o1, JobInformation o2) {
            String size1 = o1.jobSize.toUpperCase();
            String size2 = o2.jobSize.toUpperCase();

            return size1.compareTo(size2);
        }
    };

    @Override
    public int compareTo(@NonNull Object o) {
        return 0;
    }
}
