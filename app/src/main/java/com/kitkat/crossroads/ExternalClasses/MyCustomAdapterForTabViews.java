package com.kitkat.crossroads.ExternalClasses;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TabHost;
import android.widget.TextView;

import com.kitkat.crossroads.Jobs.JobInformation;
import com.kitkat.crossroads.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyCustomAdapterForTabViews extends BaseAdapter
{
    public ArrayList<JobInformation> mData = new ArrayList<>();
    public ArrayList<JobInformation> mDataOrig = new ArrayList<>();
    public ArrayList<String> mDataKeys = new ArrayList<>();
    private TabHost host;

    public LayoutInflater mInflater;

    public MyCustomAdapterForTabViews(FragmentActivity fragmentActivity, boolean isAdded, TabHost host)
    {
        if (isAdded)
        {
            mInflater = (LayoutInflater) fragmentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        this.host = host;
    }

    public void addItem(final JobInformation item)
    {
        mData.add(item);
        mDataOrig.add(item);
    }


    public void addArray(final ArrayList<JobInformation> j)
    {
        mData.clear();
        mDataOrig.clear();
        mData = j;
        mDataOrig = j;
    }

    public void addKeyArray(final ArrayList<String> k)
    {
        mDataKeys.clear();
        mDataKeys = k;
    }

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer)
    {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer)
    {

    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        // Bid on holder
        MyCustomAdapterForTabViews.GroupViewHolderBidOn holderBidOn;
        // Accepted holder
        final MyCustomAdapterForTabViews.GroupViewHolderAccepted holderAccepted;
        // Completed holder
        MyCustomAdapterForTabViews.GroupViewHolderCompleted holderCompleted;

        if (convertView == null)
        {
            // Bid on
            if (host.getCurrentTab() == 0)
            {
                convertView = mInflater.inflate(R.layout.job_info_bid_on, null);
                holderBidOn = new MyCustomAdapterForTabViews.GroupViewHolderBidOn();

                holderBidOn.textViewJobName = convertView.findViewById(R.id.textName);
                holderBidOn.textViewJobDescription = convertView.findViewById(R.id.textDesc);
                holderBidOn.textViewAddressFrom = convertView.findViewById(R.id.textAddressFrom);
                holderBidOn.textViewAddressTo = convertView.findViewById(R.id.textAddressTo);

                holderBidOn.textViewJobName.setText(mData.get(position).getAdvertName());
                holderBidOn.textViewJobDescription.setText(mData.get(position).getAdvertDescription());
                holderBidOn.textViewAddressFrom.setText(mData.get(position).getColL1() + ", " + mData.get(position).getColTown() + ", " + mData.get(position).getColPostcode());
                holderBidOn.textViewAddressTo.setText(mData.get(position).getDelL1() + ", " + mData.get(position).getDelPostcode() + ", " + mData.get(position).getDelPostcode());

                convertView.setTag(holderBidOn);
            }
            // Accepted
            else if (host.getCurrentTab() == 1)
            {
                convertView = mInflater.inflate(R.layout.job_info_accepted, null);
                holderAccepted = new MyCustomAdapterForTabViews.GroupViewHolderAccepted();

                holderAccepted.textViewJobName = convertView.findViewById(R.id.textName);
                holderAccepted.textViewDescription = convertView.findViewById(R.id.textDesc);
                holderAccepted.textViewAddressFrom = convertView.findViewById(R.id.textAddressFrom);
                holderAccepted.textViewAddressTo = convertView.findViewById(R.id.textAddressTo);

                holderAccepted.textViewJobName.setText(mData.get(position).getAdvertName());
                holderAccepted.textViewDescription.setText(mData.get(position).getAdvertDescription());
                holderAccepted.textViewAddressFrom.setText(mData.get(position).getColL1() + ", " + mData.get(position).getColTown() + ", " + mData.get(position).getColPostcode());
                holderAccepted.textViewAddressTo.setText(mData.get(position).getDelL1() + ", " + mData.get(position).getDelPostcode() + ", " + mData.get(position).getDelPostcode());

                convertView.setTag(holderAccepted);
            }
            // Completed
            else if (host.getCurrentTab() == 2)
            {
                convertView = mInflater.inflate(R.layout.job_info_list_completed, null);

                holderCompleted = new MyCustomAdapterForTabViews.GroupViewHolderCompleted();

                holderCompleted.textViewJobName = convertView.findViewById(R.id.textName);
                holderCompleted.textViewJobName.setText(mData.get(position).getAdvertName());

                convertView.setTag(holderCompleted);
            }
        } else
        {
            if (host.getCurrentTab() == 0)
            {
                holderBidOn = (MyCustomAdapterForTabViews.GroupViewHolderBidOn) convertView.getTag();
            } else if (host.getCurrentTab() == 1)
            {
                holderAccepted = (MyCustomAdapterForTabViews.GroupViewHolderAccepted) convertView.getTag();
            } else if (host.getCurrentTab() == 2)
            {
                holderCompleted = (MyCustomAdapterForTabViews.GroupViewHolderCompleted) convertView.getTag();
            }
        }

        return convertView;
    }

    public class GroupViewHolderBidOn
    {
        public TextView textViewJobName;
        public TextView textViewJobDescription;
        public TextView textViewAddressFrom;
        public TextView textViewAddressTo;
    }

    public class GroupViewHolderAccepted
    {
        public TextView textViewJobName;
        public TextView textViewDescription;
        public TextView textViewAddressFrom;
        public TextView textViewAddressTo;
    }

    public class GroupViewHolderCompleted
    {
        public TextView textViewJobName;
    }

    public void filter(String charText)
    {
        ArrayList<JobInformation> jobs = new ArrayList<>();
        ArrayList<JobInformation> jA = new ArrayList<>();
        charText = charText.toLowerCase(Locale.getDefault());

        if (charText.length() == 0)
        {
            mData = mDataOrig;
        } else
        {
            for (JobInformation j : mDataOrig)
            {
                if (j.getWholeString().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    jobs.add(j);
                    jA.add(j);
                } else
                {
                    jA.add(j);
                }
            }
            mData.clear();
            mData = jobs;
            mDataOrig = jA;
        }

        notifyDataSetChanged();
    }
}