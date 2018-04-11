package com.kitkat.crossroads.Ratings;

import java.io.Serializable;

public class RatingsAndReviews implements Serializable
{
    private float starReview;
    private String review;

    public RatingsAndReviews()
    {
    }

    public RatingsAndReviews(float startReview, String review)
    {
        this.starReview = startReview;
        this.review = review;
    }

    public float getStartReview()
    {
        return starReview;
    }

    public void setStartReview(float starReview)
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
