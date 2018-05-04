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
    public String advertName, advertDescription, jobSize, jobType, posterID, jobID, courierID, collectionDate, collectionTime;
    public String colL1, colL2, colTown, colPostcode, delL1, delL2, delTown, delPostcode;
    public String jobStatus;
    public String jobImage;

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
     * Set the jobId, when method is called the jobId variable is overridden
     * Id is usually numbers and letters combined that are assigned to a job.
     *
     * @param jobID String: Id of job
     */
    public void setJobID(String jobID)
    {
        this.jobID = jobID;
    }

    /**
     * Get the courierId, can gain access to the courierId of the object
     * Courier Id is number an letters that are assigned to a user, who is the courier
     * for the job
     *
     * @return String: Couriers Id
     */
    public String getCourierID()
    {
        return courierID;
    }

    /**
     * Get the JobId, can gain access to the courierId of the object
     * Id is usually numbers and letters combined that are assigned to a job.
     *
     * @return String: Job Id
     */
    public String getJobID()
    {
        return jobID;
    }

    /**
     * Get the adverts name, can gain access to the advert name of the object
     * Advert name, gives a brief description of what the job is about.
     *
     * @return String: Advert name, the adverts name
     */
    public String getAdvertName()
    {
        return advertName;
    }

    /**
     * Get the advert description, can gain access to the advert description of the object.
     * The advert description tells the user more about the job.
     *
     * @return String: Advert Description, the description of the advert
     */
    public String getAdvertDescription()
    {
        return advertDescription;
    }

    /**
     * Get the Job Size, can gain access to the jobSize of the object.
     * The job size can either be small, medium or large. The user can select
     * these from a preset menu.
     *
     * @return String: Job Size, the size of the job
     */
    public String getJobSize()
    {
        return jobSize;
    }

    /**
     * Get the JobType, can gain access to the jobType of the object.
     * The job type can either be a single or multiple items. The user can select these
     * from a preset list.
     *
     * @return String: Job Type, type of job
     */
    public String getJobType()
    {
        return jobType;
    }

    /**
     * Get the posterId, can gain access to the posterId of the object.
     * The poster id is the person who posted the job advert.
     *
     * @return String: Poster Id, user who posted job
     */
    public String getPosterID()
    {
        return posterID;
    }

    /**
     * Get the collection date, can gain access to the collectionDate of the object.
     * The date that the collection of the job is to take place
     *
     * @return String: Collection Date, date of collection
     */
    public String getCollectionDate()
    {
        return collectionDate;
    }

    /**
     * Get the collection time, can gain access to the collecitonTime of the object.
     * The time that the collection of the job is to take place.
     *
     * @return String: Collection Time, time of collection
     */
    public String getCollectionTime()
    {
        return collectionTime;
    }

    /**
     * Get the Collection Address Line One, can gain access to the collection address line
     * The collection address line one that the job is to be collected from.
     *
     * @return String: Collection Address Line One, first part of address
     */
    public String getColL1()
    {
        return colL1;
    }

    /**
     * Get the collection address line two, can gain access to the collection address line
     * The collection address line two that the job is to be collected from.
     *
     * @return
     */
    public String getColL2()
    {
        return colL2;
    }

    /**
     * Get the collection town, can gain access to the collection town.
     * The collection town is the town that the job is to be collected from
     *
     * @return String: Collection town, town to be collected from
     */
    public String getColTown()
    {
        return colTown;
    }

    /**
     * Get the collection postcode, can gain access to the collection postcode.
     * The collection postcode is the postcode the job is to be collected from
     *
     * @return String: Collection postcode, postal address code
     */
    public String getColPostcode()
    {
        return colPostcode;
    }


    /**
     * Get the delivery address line one, can gain access to the delivery address line one.
     * Delivery address line one, is the first part of where the job needs to be taken too.
     *
     * @return String: Delivery address line one, address line to be delivered too
     */
    public String getDelL1()
    {
        return delL1;
    }

    /**
     * Get the delivery address line two, can gain access to the delivery address line two.
     * Delivery address line two is the second part of the address where the job needs
     * to be taken too.
     *
     * @return String: Delivery address line two, address line two where the job is delivered too
     */
    public String getDelL2()
    {
        return delL2;
    }

    /**
     * Get the delivery town, can gain access to the delivery address town.
     * Delivery address town where the job is being taken too.
     *
     * @return String: Delivery town, the town where the job is being taken too.
     */
    public String getDelTown()
    {
        return delTown;
    }

    /**
     * Get the delivery postcode, can gain access to the delivery postcode.
     * Delivery postcode is the postcode where the job is being delivery too.
     *
     * @return String: DeliveryPostcode, postcode of delivery address
     */
    public String getDelPostcode()
    {
        return delPostcode;
    }

    /**
     * Get the JobStatus, can gain access to the job status.
     * Job status is the progress that the job is at. It can either be Pending, Active, Complete or Inactive
     *
     * @return String: Job Status, state that the job is in
     */
    public String getJobStatus()
    {
        return jobStatus;
    }

    /**
     * Get the job image, can gain access to the job image.
     * Job Image, is the image associated with the job. Can be displayed on pages
     *
     * @return String: URL address of the image
     */
    public String getJobImage()
    {
        return jobImage;
    }

    /**
     * Set the job image to be pushed up into the database if the
     * user hasent selected an image
     *
     * @param jobImage String: URL address of of the image
     */
    public void setJobImage(String jobImage)
    {
        this.jobImage = jobImage;
    }


    /**
     * Concatenates the wholeObject together to be printed
     * Used so we can see each element that has been successfully added to the Job Information class
     *
     * @return String: WholeString, all objects
     */
    public String getWholeString()
    {
        return advertName + advertDescription + jobSize + jobType + collectionDate + collectionTime + colL1 + colL2 + colTown + colPostcode + delL1 + delL2 + delTown + delPostcode;
    }
}
