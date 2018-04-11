package com.kitkat.crossroads.Ratings;

import java.io.Serializable;

public class RatingsAndReviews implements Serializable
{
    private float startReview;
    private String review;

    public RatingsAndReviews()
    {
    }

    public RatingsAndReviews(float startReview, String review)
    {
        this.startReview = startReview;
        this.review = review;
    }

    public float getStartReview()
    {
        return startReview;
    }

    public void setStartReview(float startReview)
    {
        this.startReview = startReview;
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
