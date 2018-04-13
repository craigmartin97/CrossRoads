package com.kitkat.crossroads.ExternalClasses;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ExpandableListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.kitkat.crossroads.Jobs.JobInformation;
import com.kitkat.crossroads.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GenericMethods
{
    private String tempTag;

    /**
     * Creating a custom toast message for all Fragments to Access
     *
     * @param message
     * @param fragmentActivity
     */
    public void customToastMessage(String message, FragmentActivity fragmentActivity)
    {
        Toast.makeText(fragmentActivity, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Create tab hosts
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
     * Create tab hosts, features
     */
    public void createTabHost(final TabHost host, String tabTag)
    {
        host.setCurrentTabByTag(tabTag);
        tempTag = tabTag;

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

                tempTag = host.getCurrentTabTag();
            }
        });
    }

    /**
     * Clear lists for MyAdverts and MyJobs, i.e. Pending, Active and Completed Jobs
     *
     * @param list
     */
    public void clearLists(ArrayList<JobInformation> list)
    {
        list.clear();
    }

    /**
     * Get the Job Information
     *
     * @param dataSnapshot
     * @return JobInformation
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
     * New bundle for job information
     * @param tag
     * @param jobInformation
     * @return
     */
    public Bundle createNewBundleJobInformation(String tag, JobInformation jobInformation)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(tag, jobInformation);
        return bundle;
    }

    /**
     * Create new bundle for strings
     * @param tag
     * @param data
     * @return
     */
    public Bundle createNewBundleStrings(String tag, String data)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(tag, data);
        return bundle;
    }

    public ExpandableListAdapter createExpandableListAdapter(FragmentActivity fragmentActivity, List<String> list, HashMap<String, List<String>> listHashMap, ExpandableListView expandableListView)
    {
        ExpandableListAdapter adapter = new ExpandableListAdapter(fragmentActivity, list, listHashMap);
        expandableListView.setAdapter(adapter);
        return adapter;
    }
}
