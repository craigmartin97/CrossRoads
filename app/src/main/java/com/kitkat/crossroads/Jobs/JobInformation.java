package com.kitkat.crossroads.Jobs;

import java.io.Serializable;

/**
 * Creates a new jobInformation object that stores and creates a new job.
 * Jobs must have name, description, size, type and address information.
 */
public class JobInformation implements Serializable
{

    /**
     * All strings to store the jobs information
     */
    private String advertName, advertDescription, jobSize, jobType, posterID, jobID, courierID, collectionDate, collectionTime;
    private String colL1, colL2, colTown, colPostcode, delL1, delL2, delTown, delPostcode;
    private String jobStatus;
    private String jobImage;

    /**
     * Blank constructor, needed for Serialization
     */
    public JobInformation()
    {

    }

    /**
     * Creates a new jobInformation object. Stores all information about a job and defines what each job is.
     *
     * @param advertName        String: Name of the job
     * @param advertDescription String: Description of the job
     * @param jobSize           String: Size of the job
     * @param jobType           String: Type of the job
     * @param posterID          String: Users Id who posted job
     * @param courierID         String: The accepted courier of the job
     * @param collectionDate    String: Day the job is meant to take place
     * @param collectionTime    String: Time the job is meant to take place
     * @param colL1             String: Address line where the job is collected from
     * @param colL2             String: Address line two where the job is collected from
     * @param colTown           String: Town, where the job is collected from
     * @param colPostcode       String: Postcode, where the job is collected from
     * @param delL1             String: Address line where the job is delivered to
     * @param delL2             String: Address line two where the job is delivered to
     * @param delTown           String: Town, where the job is being delivered to
     * @param delPostcode       String: Postcode, where the job is being delivered to
     * @param jobStatus         String: If the job is Pending, Active, Complete or Inactive
     * @param jobImage          String: URL address of the Image for the job
     */
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

    /**
     * Set the jobId
     *
     * @param jobID
     */
    public void setJobID(String jobID)
    {
        this.jobID = jobID;
    }

    /**
     * Get the courierId
     *
     * @return String: Couriers Id
     */
    public String getCourierID()
    {
        return courierID;
    }

    /**
     * Get the JobId
     *
     * @return String: Job Id
     */
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

    public String getWholeString()
    {
        return advertName + advertDescription + jobSize + jobType + collectionDate + collectionTime + colL1 + colL2 + colTown + colPostcode + delL1 + delL2 + delTown + delPostcode;
    }
}
