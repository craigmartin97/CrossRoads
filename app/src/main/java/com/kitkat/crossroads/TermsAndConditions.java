package com.kitkat.crossroads;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Terms and Conditions class is used to download a PDF for the terms of service
 * for the CrossRoads application. The PDF is downloaded locally to the user device
 */
public class TermsAndConditions extends AppCompatActivity
{
    /**
     * a view that displays web pages
     */
    private WebView webView;

    /**
     * onCreate                   This method is called to display the Terms and Conditions.
     *
     * @param savedInstanceState -If the fragment is being recreated from a previous saved state, this is the state.
     *                           This value may be null.
     */
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        webView.loadUrl("https://firebasestorage.googleapis.com/v0/b/crossroads-b1198.appspot.com/o/TermsConditions%2FTermsAndConditions.pdf?alt=media&token=694fc922-5a1a-4ee6-b130-af3f226263fc");
    }
}
