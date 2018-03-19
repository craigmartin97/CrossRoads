package com.kitkat.crossroads;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.Jobs.JobDetailsActivity;
import com.kitkat.crossroads.Jobs.JobInformation;

import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyAdvertsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyAdvertsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyAdvertsFragment extends Fragment implements SearchView.OnQueryTextListener
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DataSnapshot jobReference;

    private MyAdvertsFragment.MyCustomAdapter mAdapter;

    private ArrayList<JobInformation> jobList = new ArrayList<JobInformation>();

    private ListView jobListView;

    private SearchView jobSearch;

    private String jobID;

    public MyAdvertsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyAdvertsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyAdvertsFragment newInstance(String param1, String param2)
    {
        MyAdvertsFragment fragment = new MyAdvertsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_my_adverts, container, false);

        jobListView = (ListView) view.findViewById(R.id.jobListView1);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                jobReference = dataSnapshot.child("Jobs");

                Iterable<DataSnapshot> jobListSnapshot = jobReference.getChildren();

                mAdapter = new MyAdvertsFragment.MyCustomAdapter();

                for (DataSnapshot ds : jobListSnapshot)
                {
                    JobInformation j = ds.getValue(JobInformation.class);
                    j.setJobID(ds.getKey());

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (j.getPosterID().equals(currentUser.getUid()))
                    {
                        jobList.add(j);

                    }

                    mAdapter.addArray(jobList);
                    jobListView.setAdapter(mAdapter);

                    jobListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            JobBidsFragment jobBidsFragment = new JobBidsFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("JobId", mAdapter.mData.get(position));
                            jobBidsFragment.setArguments(bundle);

                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content, jobBidsFragment).addToBackStack("tag").commit();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        jobSearch = (SearchView) view.findViewById(R.id.searchViewJob);
        jobSearch.setIconified(false);
        jobSearch.clearFocus();

        jobSearch.setOnQueryTextListener(this);

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
        private ArrayList<JobInformation> mDataOrig = new ArrayList<>();

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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            System.out.println("getView " + position + " " + convertView);
            MyAdvertsFragment.MyCustomAdapter.GroupViewHolder holder;
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.job_info_list_my_adverts, null);
                holder = new MyAdvertsFragment.MyCustomAdapter.GroupViewHolder();
                holder.textViewName = (TextView) convertView.findViewById(R.id.textName);
                holder.textViewFrom = (TextView) convertView.findViewById(R.id.textFrom);
                holder.textViewTo = (TextView) convertView.findViewById(R.id.textTo);

                //holder.detailsButton = (Button) convertView.findViewById(R.id.detailsButton);
                convertView.setTag(holder);
            } else
            {
                holder = (MyAdvertsFragment.MyCustomAdapter.GroupViewHolder) convertView.getTag();
            }

            holder.textViewName.setText(mData.get(position).getAdvertName());
            holder.textViewFrom.setText(mData.get(position).getColTown());
            holder.textViewTo.setText(mData.get(position).getDelTown());
            jobID = mData.get(position).getJobID();

//            holder.viewBidsButton.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//
//                    JobBidsFragment jobBidsFragment = new JobBidsFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("JobId", mData.get(position));
//                    jobBidsFragment.setArguments(bundle);
//
//                    FragmentManager fragmentManager = getFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.content, jobBidsFragment).commit();
//                }
//            });

//            holder.detailsButton.setOnClickListener(new View.OnClickListener()
//            {
//
//                @Override
//                public void onClick(View v)
//                {
//                    Intent intent = new Intent(getActivity(), JobDetailsActivity.class);
//                    intent.putExtra("JobDetails", mData.get(position));
//                    startActivity(intent);
//
//                    JobDetailsFragment jobDetailsFragment = new JobDetailsFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("Job", mData.get(position));
//                    bundle.putString("name", "Hello");
//                    bundle.putString("address", "123345");
//                    jobDetailsFragment.setArguments(bundle);
//                    FragmentManager fragmentManager = getFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.content, jobDetailsFragment).commit();
//                }
//            });
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
            public Button viewBidsButton;
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
}
