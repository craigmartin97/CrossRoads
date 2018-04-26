package com.kitkat.crossroads.Ratings;

import java.io.Serializable;

public class RatingsAndReviews implements Serializable
{
    private float starReview;
    private String review;

    public RatingsAndReviews(float starReview, String review)
    {
        this.starReview = starReview;
        this.review = review;
    }

}
