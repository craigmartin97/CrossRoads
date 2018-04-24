package com.kitkat.crossroads.Jobs;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.EnumClasses.JobStatus;
import com.kitkat.crossroads.EnumClasses.StatusTags;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.GenericMethods;
import com.kitkat.crossroads.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This class allows the user to search for a new job to bid on.
 * The bids are displayed in a List, and each job contains its name, description, collection and delivery addresses.
 * The jobs must be Pending, so no courier has been selected yet, not be posted by the current user and not equal to
 * today's date. The class also has search features such as a filter and text search, so the user can search
 * for specific jobs that they may be interested in.
 */
public class FindAJobFragment extends Fragment implements SearchView.OnQueryTextListener
{

    /**
     * Database reference to the FireBase database, under the Jobs table
     */
    private DatabaseReference databaseReferenceJobTable;

    /**
     * Accessing class to gain access to methods
     */
    private final GenericMethods genericMethods = new GenericMethods();

    /**
     * Storing the current users unique Id
     */
    private String user;

    /**
     * Creating a new instance of the CustomAdapter to display all of the jobs
     * users can bid on.
     */
    private FindAJobFragment.MyCustomAdapter mAdapter;

    /**
     * ArrayList is used to store all of the jobs information in to be displayed
     */
    private final ArrayList<JobInformation> jobList = new ArrayList<>();

    /**
     * Creating a new ListView, that the jobs will be displayed in
     */
    private ListView jobListView;


    private Spinner filterSize, sortBySpinner;
    private EditText filterName, filterColDate, filterColTime, filterColFrom, filterDelTo;
    private CheckBox filterSingle, filterMultiple;
    private Button filterApplyButton, filterClearButton, filterButton;
    private SearchView jobSearch;
    private LinearLayout filterLayout;

    /**
     * Called to do initial creation of a fragment.
     * This is called after onAttach(Activity) and before onCreateView(LayoutInflater, ViewGroup, Bundle),
     * but is not called if the fragment instance is retained across Activity re-creation (see setRetainInstance(boolean)).
     *
     * @param savedInstanceState Bundle: If the fragment is being re-created from a previous saved state, this is the state.
     *                           This value may be null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        databaseConnections();
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once they know their view hierarchy has been completely created.
     * The fragment's view hierarchy is not however attached to its parent at this point.
     *
     * @param inflater           LayoutInflater: The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container          ViewGroup: If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState Bundle: If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return - Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_find_a_job, container, false);

        getViewsByIds(view);
        createSortByDropDown();

        filterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (filterLayout.getTag().toString().equals(StatusTags.Closed.name()))
                {
                    filterLayout.setVisibility(View.VISIBLE);
                    filterLayout.setTag(StatusTags.Open.name());
                } else
                {
                    filterLayout.setVisibility(View.GONE);
                    filterLayout.setTag(StatusTags.Closed.name());

                }

            }
        });

        final List<String> sizeList = new ArrayList<>(Arrays.asList(createTextForJobSizes()));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_custom, sizeList)
        {
            @Override
            public boolean isEnabled(int position)
            {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent)
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
                    genericMethods.customToastMessage("Selected : " + selectedItemText, getActivity());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        filterSize.setAdapter(adapter);
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


        getJobsFromDatabase();
        setJobSearchQueries();


        applyFilter();
        clearFilter();

        return view;
    }

    /**
     * Setting all of the database connections to the FireBase database
     */
    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        user = databaseConnections.getCurrentUser();
        databaseReferenceJobTable = databaseConnections.getDatabaseReferenceJobs();
        databaseReferenceJobTable.keepSynced(true);
    }

    /**
     * Setting all of the widgets in the layout file to variables in the fragment
     */
    private void getViewsByIds(View view)
    {
        jobListView = view.findViewById(R.id.jobListView1);
        sortBySpinner = view.findViewById(R.id.sortBySpinner);
        filterApplyButton = view.findViewById(R.id.filterApplyButton);
        filterClearButton = view.findViewById(R.id.filterClearButton);
        filterName = view.findViewById(R.id.edittextFilterName);
        filterColDate = view.findViewById(R.id.editTextFilterColDate);
        filterColTime = view.findViewById(R.id.editTextFilterColTime);
        filterColFrom = view.findViewById(R.id.editTextFilterColFrom);
        filterDelTo = view.findViewById(R.id.editTextFilterDelTo);
        filterButton = view.findViewById(R.id.filterButton);
        filterSize = view.findViewById(R.id.filterSpinnerSize);
        jobSearch = view.findViewById(R.id.searchViewJob);
        filterLayout = view.findViewById(R.id.filterLayout);
        filterSingle = view.findViewById(R.id.singleItemCheck);
        filterMultiple = view.findViewById(R.id.multipleItemsCheck);
    }

    /**
     * Retrieving all of the Jobs from the FireBase database that are pending, before today's date and time
     * and not posted by the current user. They are added to the adapter and displayed in the list view.
     */
    private void getJobsFromDatabase()
    {
        databaseReferenceJobTable.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                jobList.clear();

                mAdapter = new MyCustomAdapter();

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    JobInformation j = ds.getValue(JobInformation.class);
                    assert j != null;
                    j.setJobID(ds.getKey());

                    //display only jobs that are still open to bidding
                    if (j.getJobStatus().equals(JobStatus.Pending.name()) && !j.getPosterID().equals(user))
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
    }

    /**
     * Puts text into the Sort By drop down list so the user has the option to
     * sort by different values
     *
     * @return - String: Elements the user can sort by
     */
    private String[] createTextForSortBy()
    {

        return new String[]{
                "Sort By",
                "Name",
                "Collection From",
                "Delivery To",
                "Collection Date",
                "Size"
        };


    }

    /**
     * Puts text into the Job Sizes list so the user has the option to
     * sort by different values
     *
     * @return - String: Elements the user can sort by
     */
    private String[] createTextForJobSizes()
    {

        return new String[]{
                "Job Sizes",
                "Small",
                "Medium",
                "Large",
                "Extra Large"
        };
    }

    private void createSortByDropDown()
    {
        final List<String> sortByList = new ArrayList<>(Arrays.asList(createTextForSortBy()));
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, sortByList)
        {
            @Override
            public boolean isEnabled(int position)
            {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent)
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
                    // Notify the selected item text
                    genericMethods.customToastMessage("Selected : " + selectedItemText, getActivity());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void setJobSearchQueries()
    {
        jobSearch.setIconified(false);
        jobSearch.clearFocus();
        jobSearch.setOnQueryTextListener(this);
    }

    private void applyFilter()
    {
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
                filterLayout.setTag(StatusTags.Closed.name());
            }
        });
    }

    private void clearFilter()
    {
        filterClearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                filterName.setText("");
                filterSize.setSelection(0);
                filterSingle.setChecked(false);
                filterMultiple.setChecked(false);
                filterColDate.setText("");
                filterColTime.setText("");
                filterColFrom.setText("");
                filterDelTo.setText("");

                JobInformation jobInformation = new JobInformation(filterName.getText().toString(), null, filterSize.getSelectedItem().toString(),
                        null, null, null, filterColDate.getText().toString(), filterColTime.getText().toString(),
                        null, null, filterColFrom.getText().toString(), null, null, null, filterDelTo.getText().toString(), null, null, null);

                mAdapter.filterArray(jobInformation);
                filterLayout.setVisibility(View.GONE);
                filterLayout.setTag(StatusTags.Closed.name());
            }
        });
    }


    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {

        mAdapter.filter(newText);

        return false;
    }

    class MyCustomAdapter extends BaseAdapter
    {

        private ArrayList<JobInformation> mData = new ArrayList();
        private ArrayList<JobInformation> mDataOrig = new ArrayList();

        private LayoutInflater mInflater;

        MyCustomAdapter()
        {

            if (isAdded())
            {
                mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
        }

        void addArray(final ArrayList<JobInformation> j)
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

        class GroupViewHolder
        {
            TextView textViewName;
            TextView textViewDesc;
            TextView textViewFrom;
            TextView textViewTo;
            TextView textViewBid;
        }

        void filter(String charText)
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

        void filterArray(JobInformation filterInfo)
        {

            ArrayList<JobInformation> jobs = new ArrayList<>();
            ArrayList<JobInformation> jA = new ArrayList<>();


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
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }
}