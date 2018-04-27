package com.kitkat.crossroads.Ratings;

import java.io.Serializable;

public class RatingsAndReviews implements Serializable
{
    public float starReview;
    public String review;

    /**
     * Required for serialization
     */
    public RatingsAndReviews()
    {

    }

    public RatingsAndReviews(float starReview, String review)
    {
        this.starReview = starReview;
        this.review = review;
    }
}
