package com.kitkat.crossroads.Jobs;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.R;
import java.util.ArrayList;
import java.util.Locale;

public class MyJobsFragment extends Fragment implements SearchView.OnQueryTextListener
{
    private OnFragmentInteractionListener mListener;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private DataSnapshot bidReference;
    private DataSnapshot jobReference;

    private ListView jobListViewBidOn, jobListViewMyAcJobs, jobListViewMyComJobs;

    private ArrayList<JobInformation> jobList = new ArrayList<>();
    private ArrayList<JobInformation> jobListActive = new ArrayList<>();
    private ArrayList<JobInformation> jobListComplete = new ArrayList<>();
    private ArrayList<String> jobListKey = new ArrayList<>();

    private SearchView jobSearch;
    private TabHost host;
    private String tabTag;

    public MyJobsFragment()
    {
        // Empty
    }

    public static MyJobsFragment newInstance(String param1, String param2)
    {
        MyJobsFragment fragment = new MyJobsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        tabTag = "Active";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_my_jobs, container, false);

        getViewsByIds(view);
        setDatabaseConnections();
        setTabHosts();
        createTabHost();

        final ArrayList<String> jobsListArray = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                bidReference = dataSnapshot.child("Bids");
                jobReference = dataSnapshot.child("Jobs");

                for (DataSnapshot ds : getBidListChildren())
                {
                    Iterable<DataSnapshot> bidsSnapShot = ds.getChildren();

                    for (DataSnapshot ds1 : bidsSnapShot)
                    {
                        if (getBidInformation(ds1).getUserID().equals(auth.getCurrentUser().getUid()))
                        {
                            jobsListArray.add(ds.getKey());
                        }
                    }
                }

                for (DataSnapshot ds3 : getJobListChildren())
                {
                    if (jobsListArray.contains(ds3.getKey()))
                    {
                        if (getJobInformation(ds3).getJobStatus().equals("Pending"))
                        {
                            jobList.add(getJobInformation(ds3));
                        }
                    }
                }

                final MyJobsFragment.MyCustomAdapter adapter = createNewCustomAdapter(jobList);
                jobListViewBidOn.setAdapter(adapter);

                jobListViewBidOn.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        BidDetailsFragment bidDetailsFragment = new BidDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Job", adapter.mData.get(position));
                        bidDetailsFragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content, bidDetailsFragment).addToBackStack("tag").commit();

                    }
                });

                for (DataSnapshot ds4 : getJobListChildren())
                {
                    if (getJobInformation(ds4).getJobStatus().equals("Active") && getJobInformation(ds4).getCourierID().equals(auth.getCurrentUser().getUid()))
                    {
                        jobListKey.add(ds4.getKey());
                        jobListActive.add(getJobInformation(ds4));
                    }

                }

                final MyJobsFragment.MyCustomAdapter adapterActiveJobs = createNewCustomAdapter(jobListActive);
                adapterActiveJobs.addKeyArray(jobListKey);
                jobListViewMyAcJobs.setAdapter(adapterActiveJobs);

                jobListViewMyAcJobs.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        ActiveJobDetailsFragment activeJobDetailsFragment = new ActiveJobDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Job", adapterActiveJobs.mData.get(position));
                        bundle.putSerializable("JobId", adapterActiveJobs.mDataKeys.get(position));
                        activeJobDetailsFragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content, activeJobDetailsFragment).addToBackStack(host.getCurrentTabTag()).commit();

                    }
                });


                for (DataSnapshot ds5 : getJobListChildren())
                {
                    if (getJobInformation(ds5).getJobStatus().equals("Complete") && getJobInformation(ds5).getCourierID().equals(auth.getCurrentUser().getUid()))
                    {
                        jobListComplete.add(getJobInformation(ds5));
                    }
                }

                MyJobsFragment.MyCustomAdapter adapterCompletedJobs = createNewCustomAdapter(jobListComplete);
                jobListViewMyComJobs.setAdapter(adapterCompletedJobs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        setSearchOptions();
        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {}
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    private void getViewsByIds(View view)
    {
        host = view.findViewById(R.id.tabHost);
        jobListViewBidOn = view.findViewById(R.id.jobListViewBidOn);
        jobListViewMyAcJobs = view.findViewById(R.id.jobListViewMyActiveJobs);
        jobListViewMyComJobs = view.findViewById(R.id.jobListViewMyCompleteJobs);
        jobSearch = view.findViewById(R.id.searchViewJob);
    }

    private void setDatabaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        auth = databaseConnections.getAuth();
        databaseReference = databaseConnections.getDatabaseReference();
    }

    private void setTabHosts()
    {
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Bid On");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Bid On");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Active");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Accepted");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Completed Jobs");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Completed");
        host.addTab(spec);
    }

    private void createTabHost()
    {
        host.setCurrentTabByTag(tabTag);
        for (int i = 0; i < host.getTabWidget().getChildCount(); i++)
        {
            TextView tv = (TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#FFFFFF"));
        }

        host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundColor(Color.parseColor("#FFFFFF")); // selected
        TextView tv = (TextView) host.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
        tv.setTextColor(Color.parseColor("#2bbc9b"));

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener()
        {

            @Override
            public void onTabChanged(String tabId)
            {

                for (int i = 0; i < host.getTabWidget().getChildCount(); i++)
                {
                    host.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#2bbc9b")); // unselected
                    TextView tv = (TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
                    tv.setTextColor(Color.parseColor("#FFFFFF"));
                }

                host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundColor(Color.parseColor("#FFFFFF")); // selected
                TextView tv = (TextView) host.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
                tv.setTextColor(Color.parseColor("#2bbc9b"));

                tabTag = host.getCurrentTabTag();
            }
        });
    }

    private Iterable<DataSnapshot> getBidListChildren()
    {
        Iterable<DataSnapshot> bidListSnapShot = bidReference.getChildren();
        return bidListSnapShot;
    }

    private Iterable<DataSnapshot> getJobListChildren()
    {
        Iterable<DataSnapshot> jobListSnapShot = jobReference.getChildren();
        return jobListSnapShot;
    }

    private BidInformation getBidInformation(DataSnapshot dataSnapshot)
    {
        BidInformation bidInformation = dataSnapshot.getValue(BidInformation.class);
        return bidInformation;
    }

    private JobInformation getJobInformation(DataSnapshot dataSnapshot)
    {
        JobInformation jobInformation = dataSnapshot.getValue(JobInformation.class);
        return jobInformation;
    }

    private void setSearchOptions()
    {
        jobSearch.setIconified(false);
        jobSearch.clearFocus();
        jobSearch.setOnQueryTextListener(this);
    }

    private MyJobsFragment.MyCustomAdapter createNewCustomAdapter(ArrayList<JobInformation> jobInformation)
    {
        MyJobsFragment.MyCustomAdapter adapter = new MyJobsFragment.MyCustomAdapter();
        adapter.addArray(jobInformation);
        return adapter;
    }

    private ArrayList<JobInformation> createNewList()
    {
        ArrayList<JobInformation> arrayList = new ArrayList<>();
        return arrayList;
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
       // mAdapterBidOn.filter(text);

        return false;
    }

    interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public class MyCustomAdapter extends BaseAdapter
    {

        private ArrayList<JobInformation> mData = new ArrayList();
        private ArrayList<JobInformation> mDataOrig = new ArrayList();
        private ArrayList<String> mDataKeys = new ArrayList<>();

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

        public String getKey(int position)
        {
            return mDataKeys.get(position);
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
            System.out.println("getView " + position + " " + convertView);
            MyJobsFragment.MyCustomAdapter.GroupViewHolder holder;
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.job_info_list, null);
                holder = new MyJobsFragment.MyCustomAdapter.GroupViewHolder();
                holder.textViewName = convertView.findViewById(R.id.textName);
                holder.textViewFrom = convertView.findViewById(R.id.textFrom);
                holder.textViewTo = convertView.findViewById(R.id.textTo);

                convertView.setTag(holder);
            } else
            {
                holder = (MyJobsFragment.MyCustomAdapter.GroupViewHolder) convertView.getTag();
            }

            holder.textViewName.setText(mData.get(position).getAdvertName());
            holder.textViewFrom.setText(mData.get(position).getColL1());
            holder.textViewTo.setText(mData.get(position).getDelL1());


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
            public TextView textViewFrom;
            public TextView textViewTo;
            public Button detailsButton;
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
