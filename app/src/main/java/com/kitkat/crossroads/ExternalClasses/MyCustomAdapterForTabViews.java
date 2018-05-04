package com.kitkat.crossroads.ExternalClasses;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.Account.LoginActivity;
import com.kitkat.crossroads.Jobs.JobInformation;
import com.kitkat.crossroads.Jobs.PostAnAdvertFragment;
import com.kitkat.crossroads.R;

import java.util.ArrayList;
import java.util.Locale;


public class MyCustomAdapterForTabViews extends BaseAdapter
{

    public ArrayList<JobInformation> mData = new ArrayList<>();
    public ArrayList<JobInformation> mDataOrig = new ArrayList<>();
    public ArrayList<String> mDataKeys = new ArrayList<>();
    private TabHost host;

    public LayoutInflater mInflater;
    private FragmentActivity fragmentActivity;
    private LayoutInflater layoutInflater;
    private FragmentManager fragmentManager;
    private final DatabaseConnections databaseConnections = new DatabaseConnections();


    public MyCustomAdapterForTabViews(FragmentActivity fragmentActivity, boolean isAdded, TabHost host, LayoutInflater layoutInflater, FragmentManager fragmentManager)
    {
        if (isAdded)
        {
            this.fragmentActivity = fragmentActivity;
            this.fragmentManager = fragmentManager;
            this.layoutInflater = layoutInflater;
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
        System.out.println("getItem-P:" + position);

        return mData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        System.out.println("getItemId-P:" + position);
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
        System.out.println("P:" + position);

        View row = null;
        // Bid on holder
        MyCustomAdapterForTabViews.GroupViewHolderBidOn holderBidOn;
        // Accepted holder
        final MyCustomAdapterForTabViews.GroupViewHolderAccepted holderAccepted;
        // Completed holder
        MyCustomAdapterForTabViews.GroupViewHolderCompleted holderCompleted;


        // Bid on
        if (host.getCurrentTab() == 0)
        {


            if (convertView == null)
            {

                convertView = mInflater.inflate(R.layout.job_info_bid_on, null);
                holderBidOn = new MyCustomAdapterForTabViews.GroupViewHolderBidOn();

                holderBidOn.textViewJobName = convertView.findViewById(R.id.textName);
                holderBidOn.imageViewCross = convertView.findViewById(R.id.imageViewCross);
                holderBidOn.imageViewEditPen = convertView.findViewById(R.id.imageViewEditPen);
                holderBidOn.textViewJobDescription = convertView.findViewById(R.id.textDesc);
                holderBidOn.textViewAddressFrom = convertView.findViewById(R.id.textAddressFrom);
                holderBidOn.textViewAddressTo = convertView.findViewById(R.id.textAddressTo);

                convertView.setTag(holderBidOn);

            } else
            {
                holderBidOn = (MyCustomAdapterForTabViews.GroupViewHolderBidOn) convertView.getTag();
            }


            System.out.println("P:" + position + "=" + mData.get(position).getAdvertName());
            holderBidOn.textViewJobName.setText(mData.get(position).getAdvertName());
            holderBidOn.textViewJobDescription.setText(mData.get(position).getAdvertDescription());

            //my ads
            if (mData.get(position).getPosterID().equals(databaseConnections.getCurrentUser()))
            {
                holderBidOn.textViewAddressFrom.setText(mData.get(position).getColL1() + ", " + mData.get(position).getColTown() + ", " + mData.get(position).getColPostcode());
                holderBidOn.textViewAddressTo.setText(mData.get(position).getDelL1() + ", " + mData.get(position).getDelTown() + ", " + mData.get(position).getDelPostcode());
            }
            // my jobs
            else
            {
                holderBidOn.textViewAddressFrom.setText(mData.get(position).getColTown());
                holderBidOn.textViewAddressTo.setText(mData.get(position).getDelTown());
            }


            holderBidOn.imageViewCross.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(fragmentActivity);

                    LayoutInflater inflater = fragmentActivity.getLayoutInflater();
                    View titleView = inflater.inflate(R.layout.popup_style, null);
                    TextView title = titleView.findViewById(R.id.title);
                    title.setText("Logout");
                    title.setTypeface(null, Typeface.BOLD);

                    View mView = layoutInflater.inflate(R.layout.popup_creator, null);

                    alertDialog.setCustomTitle(titleView);
                    alertDialog.setView(mView);
                    final AlertDialog dialog = alertDialog.create();
                    dialog.show();

                    TextView customText = mView.findViewById(R.id.textViewCustomText);
                    customText.setText("Are You Sure You Want To Delete " + mData.get(position).getAdvertName() + "?");

                    Button yesButton = mView.findViewById(R.id.yesButton);
                    Button noButton = mView.findViewById(R.id.noButton);

                    yesButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            // My Adverts
                            if (mData.get(position).getPosterID().equals(databaseConnections.getCurrentUser()))
                            {
                                databaseConnections.getDatabaseReference().child("Jobs").child(mDataKeys.get(position)).addListenerForSingleValueEvent(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        databaseConnections.getDatabaseReference().child("Jobs").child(mDataKeys.get(position)).child("jobStatus").setValue("Inactive");
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {

                                    }
                                });
                            }
                            // My Jobs
                            else
                            {
                                databaseConnections.getDatabaseReference().child("Bids").child(mDataKeys.get(position)).addListenerForSingleValueEvent(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        databaseConnections.getDatabaseReference().child("Bids").child(mDataKeys.get(position)).child(databaseConnections.getCurrentUser()).child("active").setValue(false);
                                        mData.remove(position);
                                        mDataKeys.remove(position);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {

                                    }
                                });
                            }

                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });

                    noButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            dialog.dismiss();
                        }
                    });
                }
            });

            // My Adverts
            if (mData.get(position).getPosterID().equals(databaseConnections.getCurrentUser()))
            {
                holderBidOn.imageViewEditPen.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        GenericMethods genericMethods = new GenericMethods();
                        PostAnAdvertFragment postAnAdvertFragment = new PostAnAdvertFragment();

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("JobInfo", mData.get(position));
                        bundle.putSerializable("JobIdKey", mDataKeys.get(position));
                        postAnAdvertFragment.setArguments(bundle);

                        genericMethods.beginTransactionToFragment(fragmentManager, postAnAdvertFragment);
                    }
                });
            }
            // My Jobs
            else
            {
                holderBidOn.imageViewEditPen.setVisibility(View.GONE);
            }

            convertView.setTag(holderBidOn);
        }
        // Accepted
        else if (host.getCurrentTab() == 1)
        {

            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.job_info_accepted, null);
                holderAccepted = new MyCustomAdapterForTabViews.GroupViewHolderAccepted();

                holderAccepted.textViewJobName = convertView.findViewById(R.id.textName);
                holderAccepted.textViewDescription = convertView.findViewById(R.id.textDesc);
                holderAccepted.textViewAddressFrom = convertView.findViewById(R.id.textAddressFrom);
                holderAccepted.textViewAddressTo = convertView.findViewById(R.id.textAddressTo);
                holderAccepted.textViewBid = convertView.findViewById(R.id.textBid);

                convertView.setTag(holderAccepted);
            } else
            {
                holderAccepted = (MyCustomAdapterForTabViews.GroupViewHolderAccepted) convertView.getTag();
            }

            holderAccepted.textViewJobName.setText(mData.get(position).getAdvertName());
            holderAccepted.textViewDescription.setText(mData.get(position).getAdvertDescription());

            //my ads
            if (mData.get(position).getPosterID().equals(databaseConnections.getCurrentUser()))
            {
                holderAccepted.textViewAddressFrom.setText(mData.get(position).getColL1() + ", " + mData.get(position).getColTown() + ", " + mData.get(position).getColPostcode());
                holderAccepted.textViewAddressTo.setText(mData.get(position).getDelL1() + ", " + mData.get(position).getDelTown() + ", " + mData.get(position).getDelPostcode());
            }
            // my jobs
            else
            {
                holderAccepted.textViewAddressFrom.setText(mData.get(position).getColTown());
                holderAccepted.textViewAddressTo.setText(mData.get(position).getDelTown());
            }

            DatabaseConnections databaseConnections = new DatabaseConnections();
            databaseConnections.getDatabaseReference().child("Bids").child(mDataKeys.get(position)).child(mData.get(position).getCourierID()).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    String acceptedBid = dataSnapshot.child("userBid").getValue(String.class);
                    holderAccepted.textViewBid.setText("£" + acceptedBid);
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });

            convertView.setTag(holderAccepted);
        }
        // Completed
        else if (host.getCurrentTab() == 2)
        {

            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.job_info_list_completed, null);

                holderCompleted = new MyCustomAdapterForTabViews.GroupViewHolderCompleted();

                holderCompleted.textViewJobName = convertView.findViewById(R.id.textName);
                holderCompleted.imageViewCross = convertView.findViewById(R.id.imageViewCross);

                convertView.setTag(holderCompleted);
            } else
            {
                holderCompleted = (MyCustomAdapterForTabViews.GroupViewHolderCompleted) convertView.getTag();
            }
            holderCompleted.textViewJobName.setText(mData.get(position).getAdvertName());
            holderCompleted.imageViewCross.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    final DatabaseConnections databaseConnections = new DatabaseConnections();

                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(fragmentActivity, R.style.datepicker);

                    LayoutInflater inflater = fragmentActivity.getLayoutInflater();
                    View titleView = inflater.inflate(R.layout.popup_style, null);
                    TextView title = titleView.findViewById(R.id.title);
                    title.setText("Delete");
                    title.setTypeface(null, Typeface.BOLD);

                    alertDialog.setCustomTitle(titleView);
                    alertDialog.setMessage("Are You Sure You Want To Delete " + mData.get(position).getAdvertName() + "?");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            // My Adverts
                            if (mData.get(position).getPosterID().equals(databaseConnections.getCurrentUser()))
                            {
                                databaseConnections.getDatabaseReference().child("Jobs").child(mDataKeys.get(position)).addListenerForSingleValueEvent(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        databaseConnections.getDatabaseReference().child("Jobs").child(mDataKeys.get(position))
                                                .child("jobStatus").setValue("Inactive");
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {

                                    }
                                });

                            }
                            // My Jobs
                            else
                            {
                                databaseConnections.getDatabaseReference().child("Jobs").child(mDataKeys.get(position)).addListenerForSingleValueEvent(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        databaseConnections.getDatabaseReference().child("Jobs").child(mDataKeys.get(position))
                                                .child("jobStatus").setValue("Inactive");
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {

                                    }
                                });

                                databaseConnections.getDatabaseReference().child("Bids").child(mDataKeys.get(position)).addListenerForSingleValueEvent(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        databaseConnections.getDatabaseReference().child("Bids").child(mDataKeys.get(position)).child(databaseConnections.getCurrentUser()).child("active").setValue(false);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {

                                    }
                                });
                            }
                            notifyDataSetChanged();
                            dialogInterface.dismiss();
                        }
                    });

                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.dismiss();
                        }
                    });

                    final AlertDialog dialog = alertDialog.create();
                    dialog.show();
                }
            });

            convertView.setTag(holderCompleted);
        }
//        } else
//        {
//            if (host.getCurrentTab() == 0)
//            {
//                row = convertView;
//            } else if (host.getCurrentTab() == 1)
//            {
//                holderAccepted = (MyCustomAdapterForTabViews.GroupViewHolderAccepted) convertView.getTag();
//            } else if (host.getCurrentTab() == 2)
//            {
//                holderCompleted = (MyCustomAdapterForTabViews.GroupViewHolderCompleted) convertView.getTag();
//            }
//        }

        return convertView;
    }

    public class GroupViewHolderBidOn
    {
        public TextView textViewJobName;
        public TextView textViewJobDescription;
        public TextView textViewAddressFrom;
        public TextView textViewAddressTo;
        public ImageView imageViewCross;
        public ImageView imageViewEditPen;
    }

    public class GroupViewHolderAccepted
    {
        public TextView textViewJobName;
        public TextView textViewDescription;
        public TextView textViewAddressFrom;
        public TextView textViewAddressTo;
        public TextView textViewBid;
    }

    public class GroupViewHolderCompleted
    {
        public TextView textViewJobName;
        public ImageView imageViewCross;
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