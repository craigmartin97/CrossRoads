package com.kitkat.crossroads;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.kitkat.crossroads.ExternalClasses.GenericMethods;

public class EmailCrossRoads extends Fragment
{
    private OnFragmentInteractionListener mListener;

    private EditText editTextUserQuestion;
    private Button buttonSendEmail;

    public EmailCrossRoads()
    {
    }

    public static EmailCrossRoads newInstance()
    {
        EmailCrossRoads fragment = new EmailCrossRoads();
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
        View view = inflater.inflate(R.layout.fragment_email_cross_roads, container, false);
        getViewsByIds(view);
        sendEmail();

        return view;
    }

    private FirebaseAuth getAuth()
    {
        return FirebaseAuth.getInstance();
    }

    private void getViewsByIds(View view)
    {
        editTextUserQuestion = view.findViewById(R.id.editTextUserQuestion);
        buttonSendEmail = view.findViewById(R.id.buttonSendEmail);
    }

    private void sendEmail()
    {
        buttonSendEmail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(TextUtils.isEmpty(editTextUserQuestion.getText()))
                {
                    GenericMethods genericMethods = new GenericMethods();
                    genericMethods.customToastMessage("Please Add A Message To Your Email", getActivity());
                }
                else
                {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto","crossofroadsapp@gmail.com", null));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Question From User, " + getAuth().getCurrentUser().getEmail());
                    intent.putExtra(Intent.EXTRA_TEXT, editTextUserQuestion.getText());
                    startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                }
            }
        });
    }

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

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }
}
