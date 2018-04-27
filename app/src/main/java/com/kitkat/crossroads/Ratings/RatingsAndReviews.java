package com.kitkat.crossroads.Ratings;

import java.io.Serializable;

public class RatingsAndReviews implements Serializable
{
    /**
     * Variables used for Ratings and Reviews.
     */
    public float starReview;
    public String review;

    /**
     * Blank constructor method
     * Required for serialization
     */
    public RatingsAndReviews()
    {

    }

    /**
     * Constructor that sets the starReview and review.
     * @param starReview Number of stars assigned to the user.
     * @param review Review that the user has been given.
     */
    public RatingsAndReviews(float starReview, String review)
    {
        this.starReview = starReview;
        this.review = review;
    }
}
