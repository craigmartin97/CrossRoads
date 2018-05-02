package com.kitkat.crossroads.MainActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.Account.LoginActivity;
import com.kitkat.crossroads.EmailCrossRoads;
import com.kitkat.crossroads.ExternalClasses.CircleTransformation;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;
import com.kitkat.crossroads.Jobs.FindAJobFragment;
import com.kitkat.crossroads.MyAdverts.MyAdvertsFragment;
import com.kitkat.crossroads.MyJobs.MyJobsFragment;
import com.kitkat.crossroads.Jobs.PostAnAdvertFragment;
import com.kitkat.crossroads.Profile.CreateProfileActivity;
import com.kitkat.crossroads.Profile.EditProfileFragment;
import com.kitkat.crossroads.Profile.ViewProfileFragment;
import com.kitkat.crossroads.R;
import com.kitkat.crossroads.UploadImageFragment;
import com.squareup.picasso.Picasso;

public class CrossRoadsMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String TAG = "ViewProfileActivity";

    /**
     * FireBase auth establishes a connection to the FireBase authentication
     * for users login validation
     */
    private FirebaseAuth auth;

    /**
     * Database reference to establish a connection to the FireBase database users table
     */
    private DatabaseReference databaseReferenceUsersTable;

    /**
     * Gets the current users unique id
     */
    private String user;

    /**
     * Widget to display the users profile image
     */
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

        databaseConnections();
        getLocationPermission();
    }

    /**
     * Establish connections to the fireBase database
     */

    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        auth = databaseConnections.getAuth();
        databaseReferenceUsersTable = databaseConnections.getDatabaseReferenceUsers();
        databaseReferenceUsersTable.keepSynced(true);
        user = databaseConnections.getCurrentUser();
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
            fragmentTransaction.replace(R.id.content, new FindAJobFragment()).addToBackStack(getString(R.string.tag)).commit();
        } else if (id == R.id.nav_postAnAdvert)
        {
            fragmentTransaction.replace(R.id.content, new PostAnAdvertFragment()).addToBackStack(getString(R.string.tag)).commit();
        } else if (id == R.id.nav_myAdverts)
        {
            fragmentTransaction.replace(R.id.content, new MyAdvertsFragment()).addToBackStack(getString(R.string.tag)).commit();
        } else if (id == R.id.nav_myJobs)
        {
            fragmentTransaction.replace(R.id.content, new MyJobsFragment()).addToBackStack(getString(R.string.tag)).commit();
        } else if(id == R.id.nav_askQuestion)
        {
            fragmentTransaction.replace(R.id.content, new EmailCrossRoads()).addToBackStack(getString(R.string.tag)).commit();
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
        final TextView navigationName = headerview.findViewById(R.id.navigationName);
        TextView navigationEmail = headerview.findViewById(R.id.navigationEmail);
        ImageView viewProfile = headerview.findViewById(R.id.imageViewProfile);
        ImageView editProfile = headerview.findViewById(R.id.imageEditPen);
        ImageView logout = headerview.findViewById(R.id.imageLogout);
        profileImage = headerview.findViewById(R.id.navigationImage);

        databaseReferenceUsersTable.child(user).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String name = dataSnapshot.child(getString(R.string.full_name_table)).getValue(String.class);
                profileImageUrl = dataSnapshot.child(getString(R.string.profile_image_table)).getValue(String.class);

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
                getFragmentTransaction().replace(R.id.content, new ViewProfileFragment()).addToBackStack(getString(R.string.tag)).commit();
                onBackPressed();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getFragmentTransaction().replace(R.id.content, new EditProfileFragment()).addToBackStack(getString(R.string.tag)).commit();
                onBackPressed();
            }
        });

        logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CrossRoadsMainActivity.this, R.style.datepicker);

                LayoutInflater inflater = getLayoutInflater();
                View titleView = inflater.inflate(R.layout.popup_style, null);
                TextView title = titleView.findViewById(R.id.title);
                title.setText("Logout");
                title.setTypeface(null, Typeface.BOLD);
                alertDialog.setCustomTitle(titleView);

                alertDialog.setMessage(R.string.sure_logout);

                alertDialog.setPositiveButton(getString(R.string.logout), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        auth.signOut();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                });
                alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });

                final AlertDialog dialog = alertDialog.create();

                dialog.show();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getFragmentTransaction().replace(R.id.content, new UploadImageFragment()).addToBackStack(getString(R.string.tag)).commit();
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
                            Log.d(TAG, getString(R.string.permission_failed));
                            displayContent();
                            return;
                        }
                    }
                    displayContent();
                    locationPermissionGranted = true;
                }
        }
    }

    /**
     *
     */
    private void displayContent()
    {

        Log.d(TAG, getString(R.string.permission_granted));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        wifiCheck();

        if(user != null)
        {
            databaseReferenceUsersTable.child(user).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.exists())
                    {
                        boolean advertiser = dataSnapshot.child(getString(R.string.advertiser_lower)).getValue(boolean.class);
                        boolean courier = dataSnapshot.child(getString(R.string.courier_lower)).getValue(boolean.class);

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
                    else
                    {
                        startActivity(new Intent(getApplicationContext(), CreateProfileActivity.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }
        else
        {
            startActivity(new Intent(this, LoginActivity.class));
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationButtonActions(navigationView);
    }

    public boolean getLocationPermissionGranted()
    {
        return locationPermissionGranted;
    }

    /**
     * Checks user is connected to Wifi
     */
    public void wifiCheck()
    {
        WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifi.isWifiEnabled())
        {
            Toast.makeText(this, "Please Turn On Your Wifi.", Toast.LENGTH_LONG).show();
        }
    }
}