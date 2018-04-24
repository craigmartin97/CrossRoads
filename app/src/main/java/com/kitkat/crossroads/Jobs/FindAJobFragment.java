package com.kitkat.crossroads.Jobs;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FindAJobFragment extends Fragment implements SearchView.OnQueryTextListener
{

    private OnFragmentInteractionListener mListener;

    private FindAJobFragment.MyCustomAdapter mAdapter;

    private ArrayList<JobInformation> jobList = new ArrayList<JobInformation>();

    private ListView jobListView;

    private Spinner sortBySpinner, filterSize;
    private Button filterButton, filterApplyButton, filterClearButton;
    private SearchView jobSearch;
    private EditText filterName, filterColDate, filterColTime, filterColFrom, filterDelTo;
    private CheckBox filterSingle, filterMultiple;

    public FindAJobFragment()
    {
        // Required empty public constructor
    }

    public static FindAJobFragment newInstance()
    {
        FindAJobFragment fragment = new FindAJobFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        final String user = databaseConnections.getCurrentUser();
        DatabaseReference jobTable = databaseConnections.getDatabaseReferenceJobs();
        jobTable.keepSynced(true);

        final View view = inflater.inflate(R.layout.fragment_find_a_job, container, false);

        final LinearLayout filterLayout = view.findViewById(R.id.filterLayout);

        jobListView = view.findViewById(R.id.jobListView1);

        sortBySpinner = view.findViewById(R.id.sortBySpinner);

        filterApplyButton = view.findViewById(R.id.filterApplyButton);
        filterClearButton = view.findViewById(R.id.filterClearButton);
        filterName = view.findViewById(R.id.edittextFilterName);
        filterColDate = view.findViewById(R.id.editTextFilterColDate);
        filterColTime = view.findViewById(R.id.editTextFilterColTime);
        filterColFrom = view.findViewById(R.id.editTextFilterColFrom);
        filterDelTo = view.findViewById(R.id.editTextFilterDelTo);

        String[] sortBy = new String[]{
                "Sort By",
                "Name",
                "Collection From",
                "Delivery To",
                "Collection Date",
                "Size"
        };

        String[] jobSizes = new String[]{
                "Job Sizes",
                "Small",
                "Medium",
                "Large",
                "Extra Large"
        };

        final List<String> sortByList = new ArrayList<>(Arrays.asList(sortBy));
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, sortByList)
        {
            @Override
            public boolean isEnabled(int position)
            {
                if (position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent)
            {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0)
                {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else
                {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(adapter1);

        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0)
                {
                    mAdapter.sortList(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        filterButton = view.findViewById(R.id.filterButton);

        filterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (filterLayout.getTag().toString().equals("Closed"))
                {
                    filterLayout.setVisibility(View.VISIBLE);
                    filterLayout.setTag("Open");
                } else
                {
                    filterLayout.setVisibility(View.GONE);
                    filterLayout.setTag("Closed");

                }

            }
        });

        filterSize = view.findViewById(R.id.filterSpinnerSize);
        final List<String> sizeList = new ArrayList<>(Arrays.asList(jobSizes));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_custom, sizeList)
        {
            @Override
            public boolean isEnabled(int position)
            {
                if (position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent)
            {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0)
                {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else
                {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0)
                {
                    // Notify the selected item text
                    Toast.makeText
                            (getActivity(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        filterSize.setAdapter(adapter);

        filterSingle = view.findViewById(R.id.singleItemCheck);
        filterSingle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (filterSingle.isChecked())
                {
                    filterMultiple.setChecked(false);
                }
            }
        });
        filterMultiple = view.findViewById(R.id.multipleItemsCheck);
        filterMultiple.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (filterMultiple.isChecked())
                {
                    filterSingle.setChecked(false);
                }
            }
        });



        jobTable.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                jobList.clear();

               /* jobReference = dataSnapshot.child("Jobs");

                Iterable<DataSnapshot> jobListSnapShot = jobReference.getChildren();*/

                mAdapter = new MyCustomAdapter();

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    JobInformation j = ds.getValue(JobInformation.class);
                    j.setJobID(ds.getKey());

                    //display only jobs that are still open to bidding
                    if (j.getJobStatus().equals("Pending") && !j.getPosterID().equals(user))
                    {
                        try
                        {
                            Date currentTime = Calendar.getInstance().getTime();
                            Date sdf = new SimpleDateFormat("dd/MM/yyyy").parse(j.getCollectionDate());
                            Date dateFormat2 = new SimpleDateFormat("hh:mm").parse(j.getCollectionTime());

                            if (!(new Date().after(sdf) && currentTime.after(dateFormat2)))
                            {
                                jobList.add(j);
                            }

                        } catch (ParseException e)
                        {
                            e.printStackTrace();
                        }

                    }
                }

                mAdapter.addArray(jobList);
                jobListView.setAdapter(mAdapter);


                jobListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        JobDetailsFragment jobDetailsFragment = new JobDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Job", mAdapter.mData.get(position));
                        jobDetailsFragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content, jobDetailsFragment).addToBackStack("tag").commit();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        jobSearch = view.findViewById(R.id.searchViewJob);
        jobSearch.setIconified(false);
        jobSearch.clearFocus();

        jobSearch.setOnQueryTextListener(this);

        filterApplyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String filterType = null;
                if (filterSingle.isChecked())
                {
                    filterType = "Single Item";
                }
                if (filterMultiple.isChecked())
                {
                    filterType = "Multiple Items";
                }


                JobInformation jobInformation = new JobInformation(filterName.getText().toString(), null, filterSize.getSelectedItem().toString(),

                        filterType, null, null, filterColDate.getText().toString(), filterColTime.getText().toString(),
                        null, null, filterColFrom.getText().toString(), null, null, null, filterDelTo.getText().toString(), null, null, null);


                mAdapter.filterArray(jobInformation);
                filterLayout.setVisibility(View.GONE);
                filterLayout.setTag("Closed");

            }
        });

        filterClearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                filterName.setText("");
                filterSize.setSelection(0);
                filterSingle.setChecked(false);
                filterMultiple.setChecked(false);
                String filterType = null;
                filterColDate.setText("");
                filterColTime.setText("");
                filterColFrom.setText("");
                filterDelTo.setText("");

                JobInformation jobInformation = new JobInformation(filterName.getText().toString(), null, filterSize.getSelectedItem().toString(),
                        filterType, null, null, filterColDate.getText().toString(), filterColTime.getText().toString(),
                        null, null, filterColFrom.getText().toString(), null, null, null, filterDelTo.getText().toString(), null, null, null);

                mAdapter.filterArray(jobInformation);
                filterLayout.setVisibility(View.GONE);
                filterLayout.setTag("Closed");

            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {

        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {

        String text = newText;
        mAdapter.filter(text);

        return false;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public class MyCustomAdapter extends BaseAdapter
    {

        private ArrayList<JobInformation> mData = new ArrayList();
        private ArrayList<JobInformation> mDataOrig = new ArrayList();

        private LayoutInflater mInflater;

        public MyCustomAdapter()
        {

            if (isAdded())
            {
                mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
        }

        public void addItem(final JobInformation item)
        {
            mData.clear();
            mDataOrig.clear();

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


        @Override
        public void registerDataSetObserver(DataSetObserver observer)
        {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer)
        {

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

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            System.out.println("getView " + position + " " + convertView);
            FindAJobFragment.MyCustomAdapter.GroupViewHolder holder;
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.job_info_accepted, null);

                holder = new FindAJobFragment.MyCustomAdapter.GroupViewHolder();
                holder.textViewName = convertView.findViewById(R.id.textName);
                holder.textViewBid = convertView.findViewById(R.id.textBid);
                holder.textViewDesc = convertView.findViewById(R.id.textDesc);
                holder.textViewFrom = convertView.findViewById(R.id.textAddressFrom);
                holder.textViewTo = convertView.findViewById(R.id.textAddressTo);

                holder.textViewBid.setVisibility(View.GONE);

                convertView.setTag(holder);
            } else
            {
                holder = (FindAJobFragment.MyCustomAdapter.GroupViewHolder) convertView.getTag();
            }

            holder.textViewName.setText(mData.get(position).getAdvertName());
            holder.textViewDesc.setText(mData.get(position).getAdvertDescription());
            holder.textViewFrom.setText(mData.get(position).getColTown());
            holder.textViewTo.setText(mData.get(position).getDelTown());

            return convertView;
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

        public class GroupViewHolder
        {
            public TextView textViewName;
            public TextView textViewDesc;
            public TextView textViewFrom;
            public TextView textViewTo;
            public TextView textViewBid;
        }

        public void filter(String charText)
        {

            ArrayList<JobInformation> jobs = new ArrayList<JobInformation>();
            ArrayList<JobInformation> jA = new ArrayList<JobInformation>();
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

        public void filterArray(JobInformation filterInfo)
        {

            ArrayList<JobInformation> jobs = new ArrayList<JobInformation>();
            ArrayList<JobInformation> jA = new ArrayList<JobInformation>();


            for (JobInformation j : mDataOrig)
            {

                if (filterInfo.getAdvertName().equals("") && filterInfo.getJobSize().equals("Job Sizes") && filterInfo.getJobType() == null && filterInfo.getCollectionDate().equals("") &&
                        filterInfo.getCollectionTime().equals("") && filterInfo.getColTown().equals("") && filterInfo.getDelTown().equals(""))
                {
                    jobs.add(j);
                    jA.add(j);
                    break;
                } else if (j.getAdvertName().toLowerCase(Locale.getDefault()).contains(filterInfo.getAdvertName().toLowerCase()) && (!filterInfo.getAdvertName().equals("")))
                {
                    jobs.add(j);
                    jA.add(j);
                    break;
                } else if (j.getJobSize().toLowerCase(Locale.getDefault()).contains(filterInfo.getJobSize().toLowerCase()) && (!filterInfo.getJobSize().equals("Job Sizes")))
                {
                    jobs.add(j);
                    jA.add(j);
                    break;
                } else if (filterInfo.getJobType() != null)
                {
                    if (j.getJobType().toLowerCase(Locale.getDefault()).contains(filterInfo.getJobType().toLowerCase()))
                    {
                        jobs.add(j);
                        jA.add(j);
                        break;
                    }
                } else if (j.getCollectionDate().toLowerCase(Locale.getDefault()).contains(filterInfo.getCollectionDate().toLowerCase()) && (!filterInfo.getCollectionDate().equals("")))
                {
                    jobs.add(j);
                    jA.add(j);
                    break;
                } else if (j.getCollectionTime().toLowerCase(Locale.getDefault()).contains(filterInfo.getCollectionTime().toLowerCase()) && (!filterInfo.getCollectionTime().equals("")))
                {
                    jobs.add(j);
                    jA.add(j);
                    break;
                } else if (j.getColTown().toLowerCase(Locale.getDefault()).contains(filterInfo.getColTown().toLowerCase()) && (!filterInfo.getColTown().equals("")))
                {
                    jobs.add(j);
                    jA.add(j);
                    break;
                } else if (j.getColPostcode().toLowerCase(Locale.getDefault()).contains(filterInfo.getColTown().toLowerCase()) && (!filterInfo.getColTown().equals("")))
                {
                    jobs.add(j);
                    jA.add(j);
                    break;
                } else if (j.getDelTown().toLowerCase(Locale.getDefault()).contains(filterInfo.getDelTown().toLowerCase()) && (!filterInfo.getDelTown().equals("")))
                {
                    jobs.add(j);
                    jA.add(j);
                    break;
                } else if (j.getDelPostcode().toLowerCase(Locale.getDefault()).contains(filterInfo.getDelTown().toLowerCase()) && (!filterInfo.getDelTown().equals("")))
                {
                    jobs.add(j);
                    jA.add(j);
                    break;
                } else
                {
                    jA.add(j);
                }

            }
            mData.clear();
            mData = jobs;
            mDataOrig = jA;

            notifyDataSetChanged();
        }


        public void sortList(int position) {

            ArrayList<JobInformation> jobsSort = new ArrayList<JobInformation>();
            jobsSort = mDataOrig;
            ArrayList<JobInformation> jAS = new ArrayList<JobInformation>();
            jAS = mDataOrig;

            if(position == 1) {
                Collections.sort(jobsSort, JobInformation.nameComparatorA);
                mData = jobsSort;
                notifyDataSetChanged();
            }
            else if(position == 2) {
                Collections.sort(jobsSort, JobInformation.colComparatorA);
                mData = jobsSort;
                notifyDataSetChanged();
            }
            else if(position == 3) {
                Collections.sort(jobsSort, JobInformation.delComparatorA);
                mData = jobsSort;
                notifyDataSetChanged();
            }
            else if(position == 4) {
                Collections.sort(jobsSort, JobInformation.dateComparatorA);
                mData = jobsSort;
                notifyDataSetChanged();
            }
            else if(position == 5) {
                Collections.sort(jobsSort, JobInformation.sizeComparatorA);
                mData = jobsSort;
                notifyDataSetChanged();
            }


        }

    }


}