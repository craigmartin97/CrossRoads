package com.kitkat.crossroads.MainActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.kitkat.crossroads.Account.LoginActivity;
import com.kitkat.crossroads.ExternalClasses.CircleTransformation;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.Jobs.FindAJobFragment;
import com.kitkat.crossroads.Jobs.MyAdvertsFragment;
import com.kitkat.crossroads.Jobs.MyJobsFragment;
import com.kitkat.crossroads.Jobs.PostAnAdvertFragment;
import com.kitkat.crossroads.Profile.EditProfileFragment;
import com.kitkat.crossroads.Profile.ViewProfileFragment;
import com.kitkat.crossroads.R;
import com.kitkat.crossroads.UploadImageFragment;
import com.squareup.picasso.Picasso;

public class CrossRoads extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String TAG = "ViewProfileActivity";

    /**
     * Firebase auth is a connection to Firebase Database
     */
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String user;
    private ImageView profileImage;
    private boolean locationPermissionGranted = false;
    private final static int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    /**
     * Accessing the users locations, after they have gave permission
     */
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    private String profileImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        DatabaseConnections databaseConnections = new DatabaseConnections();
        auth = databaseConnections.getAuth();
        databaseReference = databaseConnections.getDatabaseReference().child("Users");
        user = databaseConnections.getCurrentUser();
        storageReference = databaseConnections.getStorageReference();

        getLocationPermission();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (id == R.id.nav_findAJob)
        {
            fragmentTransaction.replace(R.id.content, new FindAJobFragment()).addToBackStack("tag").commit();
        } else if (id == R.id.nav_postAnAdvert)
        {
            fragmentTransaction.replace(R.id.content, new PostAnAdvertFragment()).addToBackStack("tag").commit();
        } else if (id == R.id.nav_myAdverts)
        {
            fragmentTransaction.replace(R.id.content, new MyAdvertsFragment()).addToBackStack("tag").commit();
        } else if (id == R.id.nav_myJobs)
        {
            fragmentTransaction.replace(R.id.content, new MyJobsFragment()).addToBackStack("tag").commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private FragmentTransaction getFragmentTransaction()
    {
        final android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        return fragmentTransaction;
    }

    private void navigationButtonActions(NavigationView navigationView)
    {
        View headerview = navigationView.getHeaderView(0);
        final TextView navigationName = (TextView) headerview.findViewById(R.id.navigationName);
        TextView navigationEmail = (TextView) headerview.findViewById(R.id.navigationEmail);
        ImageView viewProfile = (ImageView) headerview.findViewById(R.id.imageViewProfile);
        ImageView editProfile = (ImageView) headerview.findViewById(R.id.imageEditPen);
        ImageView logout = (ImageView) headerview.findViewById(R.id.imageLogout);
        profileImage = (ImageView) headerview.findViewById(R.id.navigationImage);

        databaseReference.child(user).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String name = dataSnapshot.child("fullName").getValue(String.class);
                profileImageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                navigationName.setText(name);
                Picasso.get().load(profileImageUrl).fit().transform(new CircleTransformation()).into(profileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


        navigationEmail.setText(auth.getCurrentUser().getEmail());

        viewProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getFragmentTransaction().replace(R.id.content, new ViewProfileFragment()).addToBackStack("tag").commit();
                onBackPressed();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getFragmentTransaction().replace(R.id.content, new EditProfileFragment()).addToBackStack("tag").commit();
                onBackPressed();
            }
        });

        logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CrossRoads.this);
                View mView = getLayoutInflater().inflate(R.layout.popup_logout, null);

                alertDialog.setTitle("Logout");
                alertDialog.setView(mView);
                final AlertDialog dialog = alertDialog.create();
                dialog.show();

                TextView text = (TextView) mView.findViewById(R.id.logoutText);
                Button logoutButton = (Button) mView.findViewById(R.id.logoutButton);
                Button cancelButton = (Button) mView.findViewById(R.id.cancelButton);

                logoutButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        auth.signOut();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog.cancel();
                    }
                });
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getFragmentTransaction().replace(R.id.content, new UploadImageFragment()).addToBackStack("tag").commit();
                onBackPressed();
            }
        });
    }

    /**
     * Checking the users permission that they selected, accept or deny
     */
    public void getLocationPermission()
    {
        Log.d(TAG, "Getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this,
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if (ContextCompat.checkSelfPermission(this,
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                locationPermissionGranted = true;
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            } else
            {
                // Denied
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else
        {
            // Denied
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        Log.d(TAG, "onRequestPermissionCalled");

        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0)
                {
                    for (int i = 0; i < grantResults.length; i++)
                    {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            Log.d(TAG, "Permission Failed");
                            displayContent();
                            return;
                        }
                    }
                    displayContent();
                }
        }
    }

    private void displayContent()
    {
        Log.d(TAG, "Permission Granted");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        databaseReference.child(user).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                boolean advertiser = dataSnapshot.child("advertiser").getValue(boolean.class);
                boolean courier = dataSnapshot.child("courier").getValue(boolean.class);

                if (advertiser == true && courier == false)
                {
                    getFragmentTransaction().replace(R.id.content, new PostAnAdvertFragment()).commit();
                } else if (advertiser == false && courier == true)
                {
                    getFragmentTransaction().replace(R.id.content, new FindAJobFragment()).commit();
                } else if (advertiser == true && courier == true)
                {
                    getFragmentTransaction().replace(R.id.content, new ViewProfileFragment()).commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationButtonActions(navigationView);
    }

    public boolean getLocationPermissionGranted()
    {
        return locationPermissionGranted;
    }
}
