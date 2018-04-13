package com.kitkat.crossroads.Ratings;

import java.io.Serializable;

public class RatingsAndReviews implements Serializable
{
    private float starReview;
    private String review;

    public RatingsAndReviews()
    {
    }

    public RatingsAndReviews(float starReview, String review)
    {
        this.starReview = starReview;
        this.review = review;
    }

    public float getStarReview()
    {
        return starReview;
    }

    public void setStarReview(float starReview)
    {
        this.starReview = starReview;
    }

    public String getReview()
    {
        return review;
    }

    public void setReview(String review)
    {
        this.review = review;
    }
}
