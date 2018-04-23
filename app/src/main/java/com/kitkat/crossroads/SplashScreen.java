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

public class SplashScreen extends AppCompatActivity {


    private GifImageView gifImageView;
    private String menuFragment, tabView;
    private Bundle newBundle;


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
        }, 7000);
    }
    private FragmentTransaction getFragmentTransaction()
    {
        final android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        return fragmentTransaction;
    }

}

