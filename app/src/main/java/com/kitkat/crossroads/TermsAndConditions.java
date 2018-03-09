package com.kitkat.crossroads;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class TermsAndConditions extends AppCompatActivity
{
    private WebView webView;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        /*webView.getSettings().setJavaScriptEnabled(true);*/
        webView.loadUrl("https://firebasestorage.googleapis.com/v0/b/crossroads-b1198.appspot.com/o/TermsConditions%2FTermsAndConditions.pdf?alt=media&token=694fc922-5a1a-4ee6-b130-af3f226263fc");
    }
}
