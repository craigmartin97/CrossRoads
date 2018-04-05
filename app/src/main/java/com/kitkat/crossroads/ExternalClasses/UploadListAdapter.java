package com.kitkat.crossroads.ExternalClasses;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kitkat.crossroads.R;

import java.util.List;

/**
 * Created by craig on 04/04/18.
 */

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder>
{
    public List<String> fileNameList;
    public List<String> fileDoneList;

    public UploadListAdapter(List<String> fileNameList, List<String> fileDoneList)
    {
        this.fileNameList = fileNameList;
        this.fileDoneList = fileDoneList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.fileNameView.setText(fileNameList.get(position));
        String fileDone = fileDoneList.get(position);

        if(fileDone.equals("Uploading"))
        {
            holder.fileDoneView.setImageResource(R.drawable.ic_loading_icon);
        }
        else
        {
            holder.fileDoneView.setImageResource(R.drawable.ic_checked_icon);
        }
    }

    @Override
    public int getItemCount()
    {
        return fileNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public View view;
        public TextView fileNameView;
        public ImageView fileDoneView;

        public ViewHolder(View itemView)
        {
            super(itemView);

            view = itemView;
            fileNameView = (TextView) view.findViewById(R.id.upload_filename);
            fileDoneView = (ImageView) view.findViewById(R.id.imageViewLoading);
        }
    }
}
