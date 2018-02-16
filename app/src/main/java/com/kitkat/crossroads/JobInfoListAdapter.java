package com.kitkat.crossroads;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by s6042911 on 16/02/18.
 */

public class JobInfoListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<JobInformation> mJobList;

    public JobInfoListAdapter(Context mContext, ArrayList<JobInformation> mJobList)
    {
        this.mContext = mContext;
        this.mJobList = mJobList;
    }


    @Override
    public int getCount() {
        return mJobList.size();
    }

    @Override
    public Object getItem(int position) {
        return mJobList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(mContext, R.layout.job_info_list, null);
        TextView jobName = (TextView)v.findViewById(R.id.jobNameView);
        TextView jobFrom = (TextView)v.findViewById(R.id.jobFromView);
        TextView jobTo = (TextView)v.findViewById(R.id.jobToView);

        jobName.setText(mJobList.get(position).getJobName());
        jobFrom.setText(mJobList.get(position).getJobFrom());
        jobTo.setText(mJobList.get(position).getJobTo());

        v.setTag(mJobList.get(position).getJobUserID());


        return v;
    }
}
