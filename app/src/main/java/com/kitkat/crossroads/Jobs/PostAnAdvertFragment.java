package com.kitkat.crossroads.Jobs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kitkat.crossroads.Account.LoginActivity;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.MapFeatures.MapFragment;
import com.kitkat.crossroads.MapFeatures.PlaceInformation;
import com.kitkat.crossroads.R;
import com.kitkat.crossroads.UploadImageFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class PostAnAdvertFragment extends Fragment
{
    /**
     * Get the authentication to the Firebase Authentication area
     */
    private FirebaseAuth auth;

    /**
     * Get the CurrentUsers ID
     */
    private String user;

    /**
     * Get the reference to the Firebase Database
     */
    private DatabaseReference databaseReference;

    /**
     * Store the jobReference
     */
    private DataSnapshot jobReference;

    /**
     * Get the reference to the Firebase Storage area to store the Jobs Image In
     */
    private StorageReference storageReference;

    private static ImageView profileImage;
    private Uri imageUri;
    private static byte[] data;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog progressDialog;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private static final String TAG = "PostAnActivityFragment";

    private EditText editTextAdName, editTextAdDescription, editTextColDate, editTextColTime;
    private EditText editTextColAddL1, editTextColAddL2, editTextColAddTown, editTextColAddPostcode;
    private EditText editTextDelAddL1, editTextDelAddL2, editTextDelAddTown, editTextDelAddPostcode;
    private Spinner editTextJobSize, editTextJobType;
    private ScrollView scrollView;

    private Button buttonPostAd, buttonUploadImages;

    private static final int Error_Dialog_Request = 9001;

    public PostAnAdvertFragment()
    {
        // Required empty public constructor
    }

    public static PostAnAdvertFragment newInstance(String param1, String param2)
    {
        PostAnAdvertFragment fragment = new PostAnAdvertFragment();
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_post_an_advert, container, false);
        // Inflate the layout for this fragment

        databaseConnections();
        getViewsByIds(view);

        if(isServicesOK())
        {
            init(view);
            initButton2(view);
        }



        editTextJobSize.setAdapter(createSpinnerAdapter(R.array.job_sizes));

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.job_types, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTextJobType.setAdapter(adapter1);


        editTextColDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {

                month = month + 1;
                Log.d(TAG, "onDateSet: date: " + year + "/" + month + "/" + dayOfMonth);

                if (dayOfMonth >= 1 && dayOfMonth <= 9)
                {
                    String newDay = "0" + dayOfMonth;
                    editTextColDate.setText(newDay + "/" + month + "/" + year);
                }

                if (month >= 1 && month <= 9)
                {
                    String newMonth = "0" + month;
                    editTextColDate.setText(dayOfMonth + "/" + newMonth + "/" + year);
                }

                if (dayOfMonth >= 1 && dayOfMonth <= 9 && month >= 1 && month <= 9)
                {
                    String newDay = "0" + dayOfMonth;
                    String newMonth = "0" + month;
                    editTextColDate.setText(newDay + "/" + newMonth + "/" + year);
                } else
                {
                    editTextColDate.setText(dayOfMonth + "/" + month + "/" + year);
                }
            }
        };

        editTextColTime.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        editTextColTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        buttonPostAd.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                if (TextUtils.isEmpty(editTextAdName.getText()))
                {
                    editTextAdName.setText("");
                    editTextAdName.setHintTextColor(Color.RED);
                    editTextAdName.setHint("Please enter Advert Name!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
                if (TextUtils.isEmpty(editTextAdDescription.getText()))
                {
                    editTextAdDescription.setText("");
                    editTextAdDescription.setHintTextColor(Color.RED);
                    editTextAdDescription.setHint("Please enter Advert Description!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
                if (TextUtils.isEmpty(editTextColDate.getText()))
                {
                    editTextColDate.setText("");
                    editTextColDate.setHintTextColor(Color.RED);
                    editTextColDate.setHint("Please enter a Collection Date!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
                if (TextUtils.isEmpty(editTextColTime.getText()))
                {
                    editTextColTime.setText("");
                    editTextColTime.setHintTextColor(Color.RED);
                    editTextColTime.setHint("Please enter a Collection Time!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
                if (TextUtils.isEmpty(editTextColAddL1.getText()))
                {
                    editTextColAddL1.setText("");
                    editTextColAddL1.setHintTextColor(Color.RED);
                    editTextColAddL1.setHint("Please enter Address Line 1!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
                if (TextUtils.isEmpty(editTextColAddTown.getText()))
                {
                    editTextColAddTown.setText("");
                    editTextColAddTown.setHintTextColor(Color.RED);
                    editTextColAddTown.setHint("Please enter a Town!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
                if ((!(editTextColAddPostcode.getText().toString().matches("^([A-PR-UWYZ](([0-9](([0-9]|[A-HJKSTUW])?)?)|([A-HK-Y][0-9]([0-9]|[ABEHMNPRVWXY])?)) ?[0-9][ABD-HJLNP-UW-Z]{2})$"))) || (TextUtils.isEmpty(editTextColAddPostcode.getText())))
                {
                    editTextColAddPostcode.setText("");
                    editTextColAddPostcode.setHintTextColor(Color.RED);
                    editTextColAddPostcode.setHint("Please enter valid Postcode!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
                if (TextUtils.isEmpty(editTextDelAddL1.getText()))
                {
                    editTextDelAddL1.setText("");
                    editTextDelAddL1.setHintTextColor(Color.RED);
                    editTextDelAddL1.setHint("Please enter Address Line 1!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
                if (TextUtils.isEmpty(editTextDelAddTown.getText()))
                {
                    editTextDelAddTown.setText("");
                    editTextDelAddTown.setHintTextColor(Color.RED);
                    editTextDelAddTown.setHint("Please enter a Town!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
                if ((!(editTextDelAddPostcode.getText().toString().matches("^([A-PR-UWYZ](([0-9](([0-9]|[A-HJKSTUW])?)?)|([A-HK-Y][0-9]([0-9]|[ABEHMNPRVWXY])?)) ?[0-9][ABD-HJLNP-UW-Z]{2})$"))) || (TextUtils.isEmpty(editTextDelAddPostcode.getText())))
                {
                    editTextDelAddPostcode.setText("");
                    editTextDelAddPostcode.setHintTextColor(Color.RED);
                    editTextDelAddPostcode.setHint("Please enter valid Postcode!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                } else
                {
                    saveJobInformation();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content, new FindAJobFragment()).addToBackStack("tag").commit();
                }
            }
        });

        // Get map info
        try
        {
            Bundle bundle = getArguments();
            if(bundle.getSerializable("JobInfo") != null)
            {
                JobInformation jobInformation = (JobInformation) bundle.getSerializable("JobInfo");
                editTextAdName.setText(jobInformation.getAdvertName());
                editTextAdDescription.setText(jobInformation.getAdvertDescription());

                ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.job_sizes, android.R.layout.simple_spinner_item);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getActivity(), R.array.job_types, android.R.layout.simple_spinner_item);
                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                for(int i = 0; i < adapter2.getCount(); i++)
                {
                    if(jobInformation.getJobSize().equals(adapter2.getItem(i)))
                    {
                        editTextJobSize.setSelection(i);
                    }
                }

                for(int i = 0; i < adapter3.getCount(); i++)
                {
                    if(jobInformation.getJobType().equals(adapter3.getItem(i)))
                    {
                        editTextJobType.setSelection(i);
                    }
                }

                editTextColDate.setText(jobInformation.getCollectionDate());
                editTextColTime.setText(jobInformation.getCollectionTime());
                editTextDelAddL1.setText(jobInformation.getDelL1());
                editTextDelAddL2.setText(jobInformation.getDelL2());
                editTextDelAddTown.setText(jobInformation.getDelTown());
                editTextDelAddPostcode.setText(jobInformation.getDelPostcode());

                if(bundle.getSerializable("JobAddress") != null)
                {
                    PlaceInformation address = (PlaceInformation) bundle.getSerializable("JobAddress");
                    editTextColAddL1.setText(address.getSubThoroughfare());
                    editTextColAddL2.setText(address.getThoroughfare());
                    editTextColAddTown.setText(address.getLocality());
                    editTextColAddPostcode.setText(address.getPostCode());
                }
            }

            if(bundle.getSerializable("JobInfoDel") != null)
            {
                JobInformation jobInformation = (JobInformation) bundle.getSerializable("JobInfoDel");
                editTextAdName.setText(jobInformation.getAdvertName());
                editTextAdDescription.setText(jobInformation.getAdvertDescription());

                ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.job_sizes, android.R.layout.simple_spinner_item);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getActivity(), R.array.job_types, android.R.layout.simple_spinner_item);
                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                for(int i = 0; i < adapter2.getCount(); i++)
                {
                    if(jobInformation.getJobSize().equals(adapter2.getItem(i)))
                    {
                        editTextJobSize.setSelection(i);
                    }
                }

                for(int i = 0; i < adapter3.getCount(); i++)
                {
                    if(jobInformation.getJobType().equals(adapter3.getItem(i)))
                    {
                        editTextJobType.setSelection(i);
                    }
                }

                editTextColDate.setText(jobInformation.getCollectionDate());
                editTextColTime.setText(jobInformation.getCollectionTime());
                editTextColAddL1.setText(jobInformation.getColL1());
                editTextColAddL2.setText(jobInformation.getColL2());
                editTextColAddTown.setText(jobInformation.getColTown());
                editTextColAddPostcode.setText(jobInformation.getColPostcode());

                PlaceInformation address = (PlaceInformation) bundle.getSerializable("JobAddress");
                editTextDelAddL1.setText(address.getSubThoroughfare());
                editTextDelAddL2.setText(address.getThoroughfare());
                editTextDelAddTown.setText(address.getLocality());
                editTextDelAddPostcode.setText(address.getPostCode());
            }


        } catch(NullPointerException e)
        {
            Log.e(TAG, e.getMessage());
        }

        buttonUploadImages.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        return view;
    }

    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        auth = databaseConnections.getAuth();
        user = databaseConnections.getCurrentUser();
        databaseReference = databaseConnections.getDatabaseReference();
        storageReference = databaseConnections.getStorageReference();

        if (auth.getCurrentUser() == null)
        {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    private void getViewsByIds(View view)
    {
        profileImage = view.findViewById(R.id.jobImage1);


        buttonPostAd = (Button) view.findViewById(R.id.buttonAddJob);
        buttonUploadImages = (Button) view.findViewById(R.id.buttonUploadImages);
        scrollView = (ScrollView) view.findViewById(R.id.advertScrollView);
        editTextAdName = (EditText) view.findViewById(R.id.editTextAdName);
        editTextAdDescription = (EditText) view.findViewById(R.id.editTextAdDescription);
        editTextJobSize = (Spinner) view.findViewById(R.id.editTextJobSize);
        editTextJobType = (Spinner) view.findViewById(R.id.editTextJobType);
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
    }

    private ArrayAdapter<CharSequence> createSpinnerAdapter(int arrayField)
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), arrayField, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // Get the current user
        UploadImageFragment imageFragment = new UploadImageFragment();
        final FirebaseUser user = auth.getCurrentUser();

        // Redirect user to there gallery and get them to select an image
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Displaying Image...");
            progressDialog.show();

            imageUri = data.getData();
            final Uri uri = data.getData();
            setUpImageTransfer(uri);
        }
    }

    /**
     * Setting image that has been selected and turning it into a bitmap.
     * Putting it into an input scream and sending it to be modified
     * @param uri
     */
    public void setUpImageTransfer(Uri uri)
    {
        progressDialog.dismiss();
        try
        {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            ContentResolver contentResolver = getActivity().getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);
            modifyOrientation(bitmap, inputStream);
        } catch (IOException e)
        {
            e.getStackTrace();
        }
    }

    /**
     * Send the image to be rotated dependant upon its needs
     *
     * @param bitmap
     * @param image_absolute_path
     * @return
     * @throws IOException
     */
    public static Bitmap modifyOrientation(Bitmap bitmap, InputStream image_absolute_path) throws IOException
    {
        android.support.media.ExifInterface exifInterface = new android.support.media.ExifInterface(image_absolute_path);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);
            default:
                return bitmap;
        }
    }

    /**
     * If the uploaded image needed to be rotated
     *
     * @param bitmap
     * @param degrees
     * @return
     */
    public static Bitmap rotate(Bitmap bitmap, float degrees)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        profileImage.setImageBitmap(bitmap1);

        profileImage.buildDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        data = byteArrayOutputStream.toByteArray();
        return bitmap1;
    }

    /**
     * If the uploaded image needed to be flipped
     *
     * @param bitmap
     * @param horizontal
     * @param vertical
     * @return
     */
    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical)
    {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        profileImage.setImageBitmap(bitmap1);

        profileImage.buildDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        data = byteArrayOutputStream.toByteArray();
        return bitmap1;
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
        String colPostcode = editTextColAddPostcode.getText().toString().trim().toUpperCase();
        String delL1 = editTextDelAddL1.getText().toString().trim();
        String delL2 = editTextDelAddL2.getText().toString().trim();
        String delTown = editTextDelAddTown.getText().toString().trim();
        String delPostcode = editTextDelAddPostcode.getText().toString().trim().toUpperCase();

        String jobStatus = "Pending";
        String courierID = " ";

        final StorageReference filePath = storageReference.child("Images").child(auth.getCurrentUser().getUid()).child(imageUri.getLastPathSegment());
        filePath.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(getActivity(), "Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed To Upload!", Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseUser user = auth.getCurrentUser();

        String posterID = user.getUid().toString().trim();

        final JobInformation jobInformation = new JobInformation(adName, adDescription, jobSize, jobType, posterID,
                courierID, colDate, colTime, colL1, colL2, colTown, colPostcode, delL1, delL2, delTown, delPostcode, jobStatus);


        databaseReference.child("Jobs").push().setValue(jobInformation);

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                jobReference = dataSnapshot.child("Jobs");

                Iterable<DataSnapshot> jobListSnapShot = jobReference.getChildren();

                for (DataSnapshot ds : jobListSnapShot)
                {
                    JobInformation j = ds.getValue(JobInformation.class);
                    if(j.equals(jobInformation))
                    {
                        databaseReference.child("Jobs").child(ds.getKey()).child("jobID").setValue(ds.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


        Toast.makeText(getActivity(), "Job Added!", Toast.LENGTH_SHORT).show();


    }

    private void init(View view)
    {
        final Button mapButton = view.findViewById(R.id.buttonMap);
        mapButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MapFragment mapFragment = new MapFragment();
                Bundle bundle = new Bundle();

                String adName = editTextAdName.getText().toString().trim();
                String adDescription = editTextAdDescription.getText().toString().trim();
                String jobSize = editTextJobSize.getSelectedItem().toString().trim();
                String jobType = editTextJobType.getSelectedItem().toString().trim();
                String colDate = editTextColDate.getText().toString().trim();
                String colTime = editTextColTime.getText().toString().trim();
                String colL1 = editTextColAddL1.getText().toString().trim();
                String colL2 = editTextColAddL2.getText().toString().trim();
                String colTown = editTextColAddTown.getText().toString().trim();
                String colPostcode = editTextColAddPostcode.getText().toString().trim().toUpperCase();
                String delL1 = editTextDelAddL1.getText().toString().trim();
                String delL2 = editTextDelAddL2.getText().toString().trim();
                String delTown = editTextDelAddTown.getText().toString().trim();
                String delPostcode = editTextDelAddPostcode.getText().toString().trim().toUpperCase();
                String courierID = " ";
                String posterID = " ";
                String jobStatus = " ";

                // job info obj
                final JobInformation jobInformation = new JobInformation(adName, adDescription, jobSize, jobType, posterID,
                        courierID, colDate, colTime, colL1, colL2, colTown, colPostcode, delL1, delL2, delTown, delPostcode, jobStatus);

                bundle.putSerializable("JobInfo", jobInformation);
                mapFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, mapFragment).commit();
            }
        });
    }

    private void initButton2(View view)
    {
        final Button mapButton = view.findViewById(R.id.buttonMap2);
        mapButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MapFragment mapFragment = new MapFragment();
                Bundle bundle = new Bundle();

                String adName = editTextAdName.getText().toString().trim();
                String adDescription = editTextAdDescription.getText().toString().trim();
                String jobSize = editTextJobSize.getSelectedItem().toString().trim();
                String jobType = editTextJobType.getSelectedItem().toString().trim();
                String colDate = editTextColDate.getText().toString().trim();
                String colTime = editTextColTime.getText().toString().trim();
                String colL1 = editTextColAddL1.getText().toString().trim();
                String colL2 = editTextColAddL2.getText().toString().trim();
                String colTown = editTextColAddTown.getText().toString().trim();
                String colPostcode = editTextColAddPostcode.getText().toString().trim().toUpperCase();
                String delL1 = editTextDelAddL1.getText().toString().trim();
                String delL2 = editTextDelAddL2.getText().toString().trim();
                String delTown = editTextDelAddTown.getText().toString().trim();
                String delPostcode = editTextDelAddPostcode.getText().toString().trim().toUpperCase();
                String courierID = " ";
                String posterID = " ";
                String jobStatus = " ";

                // job info obj
                final JobInformation jobInformation = new JobInformation(adName, adDescription, jobSize, jobType, posterID,
                        courierID, colDate, colTime, colL1, colL2, colTown, colPostcode, delL1, delL2, delTown, delPostcode, jobStatus);

                bundle.putSerializable("JobInfoDel", jobInformation);
                mapFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, mapFragment).commit();
            }
        });
    }


    public boolean isServicesOK()
    {
        Log.d(TAG, "IsServicesOK: checking google services version: ");
        int avaliable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if(avaliable == ConnectionResult.SUCCESS)
        {
            // Can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services Is Working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(avaliable))
        {
            Log.d(TAG, "isServicesOK: An error has occured but it can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), avaliable, Error_Dialog_Request);
            dialog.show();
        }
        else
        {
            Log.d(TAG, "isServicesError: Google Play Services Isnt Working, Unable To Resolve");
        }
        return false;
    }
}
