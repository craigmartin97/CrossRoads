package com.kitkat.crossroads.ExternalClasses;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.CheckBox;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.kitkat.crossroads.Jobs.JobInformation;
import com.kitkat.crossroads.R;

import java.util.ArrayList;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Class stores lots of methods that other classes, activities and fragments
 * can access and call instead of reusing code multiple times.
 */
public class GenericMethods
{
    /**
     * Creating a custom toast message for all Fragments to Access
     *
     * @param message          String: message to be displayed to the user
     * @param fragmentActivity FragmentActivity: The fragment that method has been called from and to display the message in
     */
    public void customToastMessage(String message, FragmentActivity fragmentActivity)
    {
        Toast.makeText(fragmentActivity, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Creating a method to dismiss progress dialogs
     *
     * @param progressDialog - The progress dialog to be dismissed
     */
    public void dismissDialog(ProgressDialog progressDialog)
    {
        progressDialog.dismiss();
    }

    /**
     * @param host    String: The host that is to be used
     * @param item    : The item to be added to the tab
     * @param tabName : The name of the tab to be displayed
     */
    public void setupTabHost(TabHost host, int item, String tabName)
    {
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec(tabName);
        spec.setContent(item);
        spec.setIndicator(tabName);
        host.addTab(spec);
    }

    /**
     * @param host   TabHost: Creates a new tab host
     * @param tabTag String: Text to be displayed on the tab
     */
    public void createTabHost(final TabHost host, String tabTag)
    {
        host.setCurrentTabByTag(tabTag);

        // Assigning the color for each tab
        for (int i = 0; i < host.getTabWidget().getChildCount(); i++)
        {
            TextView tv = host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#FFFFFF"));
        }

        // For the current selected tab
        host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundColor(Color.parseColor("#FFFFFF")); // selected
        TextView tv = host.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
        tv.setTextColor(Color.parseColor("#2bbc9b"));

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener()
        {
            @Override
            public void onTabChanged(String tabId)
            {
                for (int i = 0; i < host.getTabWidget().getChildCount(); i++)
                {
                    host.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#2bbc9b")); // unselected
                    TextView tv = host.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
                    tv.setTextColor(Color.parseColor("#FFFFFF"));
                }

                host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundColor(Color.parseColor("#FFFFFF")); // selected
                host.getTabWidget().getChildAt(host.getCurrentTab());
                TextView tv = host.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
                tv.setTextColor(Color.parseColor("#2bbc9b"));
            }
        });
    }

    /**
     * Clear lists for MyAdverts and MyJobs, i.e. Pending, Active and Completed Jobs
     * This method is used so repeat data isn't used when the page is refreshed
     *
     * @param list list: The list that is to be cleared
     */
    public void clearLists(ArrayList<JobInformation> list)
    {
        list.clear();
    }

    /**
     * Get the Job Information from a snapshot of the FireBase database.
     * Gets all of the information regarding a specific job that is stored in the FireBase database
     *
     * @param dataSnapshot : The FireBase database that is being read from
     * @return JobInformation : All the information regarding a specific job
     */
    public JobInformation getJobInformation(DataSnapshot dataSnapshot)
    {
        return dataSnapshot.getValue(JobInformation.class);
    }

    /**
     * Make a transaction to a new fragment
     */
    public void beginTransactionToFragment(FragmentManager fragmentManager, Fragment fragmentToTransferTo)
    {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragmentToTransferTo).addToBackStack("tag").commit();
    }

    /**
     * Create new bundle for strings.
     * This is used for transferring data between two fragments and we need to transfer a string
     *
     * @param tag  : The reference key of the String being transferred
     * @param data : The data element that is being transferred to another fragment
     * @return bundle : The information that has been added to the bundle
     */
    public Bundle createNewBundleStrings(String tag, String data)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(tag, data);
        return bundle;
    }

    /**
     * Checks whether the user is primarily going to be using the app as an advertiser
     * or as a courier or both
     */
    public void checkUserPreference(boolean advertiser, boolean courier, CheckBox checkBoxAdvertiser, CheckBox checkBoxCourier)
    {
        if (advertiser && !courier)
        {
            checkBoxAdvertiser.setChecked(true);
            checkBoxCourier.setChecked(false);
        } else if (!advertiser && courier)
        {
            checkBoxAdvertiser.setChecked(false);
            checkBoxCourier.setChecked(true);
        } else if (advertiser && courier)
        {
            checkBoxAdvertiser.setChecked(true);
            checkBoxCourier.setChecked(true);
        }
    }
}
