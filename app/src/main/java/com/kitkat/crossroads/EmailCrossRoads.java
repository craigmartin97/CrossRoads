package com.kitkat.crossroads;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.ExternalClasses.GenericMethods;

public class EmailCrossRoads extends Fragment
{
    /**
     * Widgets that allow user to enter text and send email
     */
    private EditText editTextUserQuestion;

    /**
     * Press to send the email to crossofroadsapp@gmail.com
     */
    private Button buttonSendEmail;

    public EmailCrossRoads()
    {

    }

    /**
     * @param savedInstanceState -If the fragment is being recreated from a previous saved state, this is the state.
     *                           This value may be null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    /**
     * @param inflater           Instantiates a layout XML file into its corresponding view Objects
     * @param container          A view used to contain other views, in this case, the view fragment_email_cross_roads
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     *                           This value may be null.
     * @return Returns inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_email_cross_roads, container, false);
        getViewsByIds(view);
        sendEmail();
        return view;
    }

    /**
     * @return returns an instance of the Authentication section of the Firebase database
     */
    private FirebaseAuth getAuth()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        return databaseConnections.getAuth();
    }

    /**
     * Set widgets in the inflated view to variables within this class
     *
     * @param view - the view layout of the fragment
     */
    private void getViewsByIds(View view)
    {
        editTextUserQuestion = view.findViewById(R.id.editTextUserQuestion);
        buttonSendEmail = view.findViewById(R.id.buttonSendEmail);
    }

    /**
     * set onClick operations for the Send Email button & launch out of app email service (pending permissions)
     */
    private void sendEmail()
    {
        buttonSendEmail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //make sure that text field is populated
                if (TextUtils.isEmpty(editTextUserQuestion.getText()))
                {
                    GenericMethods genericMethods = new GenericMethods();
                    genericMethods.customToastMessage("Please Add A Message To Your Email", getActivity());
                } else
                {
                    try
                    {
                        //launch a new email intent
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "crossofroadsapp@gmail.com", null));
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Question From User, " + getAuth().getCurrentUser().getEmail());
                        intent.putExtra(Intent.EXTRA_TEXT, editTextUserQuestion.getText());
                        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                    } catch (NullPointerException e)
                    {
                        Log.d("EmailCrossRoads: ", e.getMessage());
                        Toast.makeText(getActivity(), "Could Not Do Email At This Time, Please Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * onAttach             onAttach is called when a fragment is first attached to its context
     * onCreate can be called only after the fragment is attached
     *
     * @param context Allows access to application specific resources and classes, also
     *                supports application-level operations such as receiving intents, launching activities
     */
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    /**
     * When the fragment is no longer attached to the activity, set the listener to null
     */
    @Override
    public void onDetach()
    {
        super.onDetach();
    }
}
