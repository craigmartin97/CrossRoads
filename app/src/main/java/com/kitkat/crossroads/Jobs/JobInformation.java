package com.kitkat.crossroads.Jobs;

import java.io.Serializable;

/**
 * Created by s6042911 on 15/02/18.
 */

public class JobInformation implements Serializable {

    public String jobName, jobDescription, jobTo, jobFrom, jobUserID;
    public boolean jobActive;

    public JobInformation()
    {

    }


    public JobInformation(String jobName, String jobDescription, String jobTo, String jobFrom, boolean jobActive, String jobUserID)
    {
        this.jobName = jobName;
        this.jobDescription = jobDescription;
        this.jobTo = jobTo;
        this.jobFrom = jobFrom;
        this.jobActive = jobActive;
        this.jobUserID = jobUserID;
    }



    public String getJobName() {
        return jobName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public String getJobTo() {
        return jobTo;
    }

    public String getJobFrom() {
        return jobFrom;
    }

    public String getJobUserID() {
        return jobUserID;
    }

    public boolean isJobActive() {
        return jobActive;
    }
}
