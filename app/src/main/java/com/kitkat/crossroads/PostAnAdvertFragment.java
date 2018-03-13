package com.kitkat.crossroads;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kitkat.crossroads.Account.LoginActivity;
import com.kitkat.crossroads.Jobs.JobInformation;
import com.kitkat.crossroads.Jobs.JobsActivity;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostAnAdvertFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostAnAdvertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostAnAdvertFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private static final String TAG = "PostAnActivityFragment";


    private FirebaseAuth auth;

    private EditText editTextAdName, editTextAdDescription, editTextColDate, editTextColTime;
    private EditText editTextColAddL1, editTextColAddL2, editTextColAddTown, editTextColAddPostcode;
    private EditText editTextDelAddL1, editTextDelAddL2, editTextDelAddTown, editTextDelAddPostcode;
    private Spinner editTextJobSize, editTextJobType;


    private Button buttonPostAd;

    private DatabaseReference databaseReference;

    public PostAnAdvertFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostAnAdvertFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostAnAdvertFragment newInstance(String param1, String param2)
    {
        PostAnAdvertFragment fragment = new PostAnAdvertFragment();
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
        View view = inflater.inflate(R.layout.fragment_post_an_advert, container, false);
        // Inflate the layout for this fragment

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null)
        {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = auth.getCurrentUser();

        buttonPostAd = (Button) view.findViewById(R.id.buttonAddJob);

        editTextAdName = (EditText) view.findViewById(R.id.editTextAdName);
        editTextAdDescription = (EditText) view.findViewById(R.id.editTextAdDescription);
        editTextJobSize = (Spinner) view.findViewById(R.id.editTextJobSize);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.job_sizes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTextJobSize.setAdapter(adapter);
        editTextJobType = (Spinner) view.findViewById(R.id.editTextJobType);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.job_types, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        editTextJobType.setAdapter(adapter1);
        editTextColDate = (EditText) view.findViewById(R.id.editTextJobColDate);
        editTextColTime = (EditText) view.findViewById(R.id.editTextJobColTime);
        editTextColAddL1 = (EditText) view.findViewById(R.id.editTextJobColL1);
        editTextColAddL2 = (EditText) view.findViewById(R.id.editTextJobColL2);
        editTextColAddTown = (EditText) view.findViewById(R.id.editTextJobColTown);
        editTextColAddPostcode = (EditText) view.findViewById(R.id.editTextJobColPostcode);
        editTextDelAddL1 = (EditText) view.findViewById(R.id.editTextJobDelL1);
        editTextDelAddL2 = (EditText) view.findViewById(R.id.editTextJobDelL2);
        editTextDelAddTown = (EditText) view.findViewById(R.id.editTextJobDelTown);
        editTextDelAddPostcode = (EditText) view.findViewById(R.id.editTextJobDelPostcode);

        editTextColDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                month = month + 1;
                                Log.d(TAG, "onDateSet: date: " + year + "/" + month + "/" + dayOfMonth);

                               if(dayOfMonth >= 1 && dayOfMonth <= 9)
                                {
                                    String newDay = "0" + dayOfMonth;
                                    editTextColDate.setText(newDay + "/" + month + "/" + year);
                                }

                                if(month >= 1 && month <= 9)
                                {
                                    String newMonth = "0" + month;
                                    editTextColDate.setText(dayOfMonth + "/" + newMonth + "/" + year);
                                }

                                if(dayOfMonth >= 1 && dayOfMonth <= 9 && month >= 1 && month <= 9)
                                {
                                    String newDay = "0" + dayOfMonth;
                                    String newMonth = "0" + month;
                                    editTextColDate.setText(newDay + "/" + newMonth + "/" + year);
                                }
                                else
                                {
                                    editTextColDate.setText(dayOfMonth + "/" + month + "/" + year);
                                }
                            }
        };



        buttonPostAd.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                saveJobInformation();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, new FindAJobFragment()).commit();
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

    private void saveJobInformation()
    {
        String adName = editTextAdName.getText().toString().trim();
        String adDescription = editTextAdDescription.getText().toString().trim();
        String jobSize = editTextJobSize.getSelectedItem().toString().trim();
        String jobType = editTextJobType.getSelectedItem().toString().trim();
        String colDate = editTextColDate.getText().toString().trim();
        String colTime = editTextColTime.getText().toString().trim();
        String colL1 = editTextColAddL1.getText().toString().trim();
        String colL2 = editTextColAddL2.getText().toString().trim();
        String colTown = editTextColAddTown.getText().toString().trim();
        String colPostcode = editTextColAddPostcode.getText().toString().trim();
        String delL1 = editTextDelAddL1.getText().toString().trim();
        String delL2 = editTextDelAddL2.getText().toString().trim();
        String delTown = editTextDelAddTown.getText().toString().trim();
        String delPostcode = editTextDelAddPostcode.getText().toString().trim();

        Boolean jobActive = true;
        String courierID = " ";

        FirebaseUser user = auth.getCurrentUser();

        String posterID = user.getUid().toString().trim();

        JobInformation jobInformation = new JobInformation(adName, adDescription, jobSize, jobType, posterID,
                courierID, colDate, colTime, colL1, colL2, colTown, colPostcode, delL1, delL2, delTown, delPostcode, jobActive);


        databaseReference.child("Jobs").push().setValue(jobInformation);

        Toast.makeText(getActivity(), "Job Added!", Toast.LENGTH_SHORT).show();


    }
}
