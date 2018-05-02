package com.kitkat.crossroads.Ratings;

import java.io.Serializable;

/**
 * RatingsAndReviews class is an object that the holds the reviews
 * and ratings given to a user based on a job they have completed.
 * Users can leave a star rating and a text review based on the experience they received from
 * a courier t inform others in the future about the service they should expect.
 */
public class RatingsAndReviews implements Serializable
{
    /**
     * The star review of the user
     */
    public float starReview;

    /**
     * The text feedback review that the user has been given
     */
    public String review;

    /**
     * Blank constructor method
     * Required for serialization
     */
    public RatingsAndReviews()
    {

    }

    /**
     * Creating a new rating and review object. Used for user ratings and reviews
     *
     * @param starReview - The number of stars given to the user
     * @param review     - The text review given to the user for a job
     */
    public RatingsAndReviews(float starReview, String review)
    {
        this.starReview = starReview;
        this.review = review;
    }
}
