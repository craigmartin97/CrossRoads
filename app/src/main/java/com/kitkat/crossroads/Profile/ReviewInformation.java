package com.kitkat.crossroads.Profile;

/**
 * Created by s6281752 on 17/03/18.
 */

public class ReviewInformation
{
    private double rating;
    private String review;

    public ReviewInformation()
    {

    }

    public ReviewInformation(double rating, String review)
    {
        this.rating = rating;
        this.review = review;
    }

    public double getRating()
    {
        return rating;
    }

    public String getReview()
    {
        return review;
    }
}
