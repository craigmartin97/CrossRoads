package com.kitkat.crossroads.ExternalClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.kitkat.crossroads.Jobs.JobInformation;
import com.kitkat.crossroads.R;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Craig on 24/03/2018.
 */

public abstract class MyCustomAdapter extends BaseAdapter
{
    private ArrayList<JobInformation> mData = new ArrayList<>();
    private ArrayList<JobInformation> mDataOrig = new ArrayList<>();
    private ArrayList<String> mDataKeys = new ArrayList<>();

    private LayoutInflater mInflater;

//    public MyCustomAdapter()
//    {
//        if (isAdded())
//        {
//            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        }
//    }

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
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        MyCustomAdapter.GroupViewHolder holder;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.job_info_list, null);
            holder = new MyCustomAdapter.GroupViewHolder();
            holder.textViewName = convertView.findViewById(R.id.textName);
            holder.textViewFrom = convertView.findViewById(R.id.textFrom);
            holder.textViewTo = convertView.findViewById(R.id.textTo);

            convertView.setTag(holder);
        } else
        {
            holder = (MyCustomAdapter.GroupViewHolder) convertView.getTag();
        }

        holder.textViewName.setText(mData.get(position).getAdvertName());
        holder.textViewFrom.setText(mData.get(position).getColL1());
        holder.textViewTo.setText(mData.get(position).getDelL1());

        return convertView;
    }

    public class GroupViewHolder
    {
        public TextView textViewName;
        public TextView textViewFrom;
        public TextView textViewTo;
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
