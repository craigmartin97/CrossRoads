package com.kitkat.crossroads;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.felipecsl.gifimageview.library.GifImageView;
import com.kitkat.crossroads.Account.LoginActivity;
import com.kitkat.crossroads.MyAdverts.MyAdvertsFragment;
import com.kitkat.crossroads.MyJobs.MyJobsFragment;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class contains all the functionality for the SplashScreen that is displayed upon startup.
 */
public class SplashScreen extends AppCompatActivity {


    private GifImageView gifImageView;
    private String menuFragment, tabView;
    private Bundle newBundle;

    /**
     *onCreate                      This method is called upon launch of the app, as the SplashScreen is set as the app Launcher within the AndroidManifest XML.
     *                              It creates all of the functionality required to display the startup screen.
     *
     * @param savedInstanceState    If the fragment is being recreated from a previous saved state, this is the state.
     *                              This value may be null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            menuFragment = bundle.getString("menuFragment");
            tabView = bundle.getString("tabView");
        }

        //Set widgets in the inflated view to variables within this class
        gifImageView = (GifImageView)findViewById(R.id.gifImageView);

        try{
            InputStream inputStream = getAssets().open("crossroadssplash.gif");
            byte[] bytes = IOUtils.toByteArray(inputStream);
            gifImageView.setBytes(bytes);
            gifImageView.startAnimation();
        }
        catch (IOException ex)
        {

        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(menuFragment != null && tabView != null)
                {
                    if(menuFragment.equals("myJobsFragment"))
                    {
                        newBundle = new Bundle();
                        newBundle.putString("tabView", tabView);
                        MyJobsFragment myJobsFragment = new MyJobsFragment();
                        myJobsFragment.setArguments(newBundle);
                        getFragmentTransaction().replace(R.id.content, myJobsFragment).commit();
                    }
                    else if(menuFragment.equals("myAdvertsFragment"))
                    {
                        newBundle = new Bundle();
                        newBundle.putString("tabView", tabView);
                        MyAdvertsFragment myAdvertsFragment = new MyAdvertsFragment();
                        myAdvertsFragment.setArguments(newBundle);
                        getFragmentTransaction().replace(R.id.content, myAdvertsFragment).commit();
                    }
                }
                else {
                    SplashScreen.this.startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    SplashScreen.this.finish();
                }
            }
        }, 1760);
    }

    /**FragmentTransaction replaces content of the current activity with the content of another fragment
     *
     * @return          - returns fragment with new content
     */
    private FragmentTransaction getFragmentTransaction()
    {
        final android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        return fragmentTransaction;
    }

}

