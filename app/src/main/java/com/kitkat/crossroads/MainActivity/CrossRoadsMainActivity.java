package com.kitkat.crossroads.MainActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

/**
 * This is the main activity that fragments run from. Its key in the navigation of the application
 * as it determines where users go dependant upon their preferences. It hosts the navigation side bar
 * for users to navigate around the app. Holds other functions that other fragments can access to
 * make code more efficient.
 */
public class CrossRoadsMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    /**
     * Tag is used for debug purposes. It is displayed in the LogCat as a reference name
     */
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

    /**
     * If the user has accepted the permission
     */
    private boolean locationPermissionGranted = false;

    /**
     * Request code to identify the location permission has been asked
     */
    private final static int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    /**
     * Accessing the users locations, after they have gave permission
     */
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    /**
     * Accessing the users current location, after they have accepted the permission
     */
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    private String profileImageUrl;

    /**
     * This method is called when the activity login is displayed to the user. It creates all of the
     * widgets and functionality that the user can do in the activity.
     *
     * @param savedInstanceState - if the activity needs to be recreated it can be passed back
     */
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
     * Connecting to the users table and storing the current users unique Id
     */
    private void databaseConnections()
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        auth = databaseConnections.getAuth();
        databaseReferenceUsersTable = databaseConnections.getDatabaseReferenceUsers();
        databaseReferenceUsersTable.keepSynced(true);
        user = databaseConnections.getCurrentUser();
    }

    /**
     * When the back button is pressed close the navigation drawer.
     */
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

    /**
     * Initialize the contents of the Activity's standard options menu. You should place your menu items in to menu.
     * This is only called once, the first time the options menu is displayed. To update the menu every time it is displayed, see onPrepareOptionsMenu(Menu).
     *
     * @param menu The options menu in which you place your items.
     * @return boolean - true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected. The default implementation simply returns false to have
     * the normal processing happen (calling the item's Runnable or sending a message to its Handler as appropriate).
     * You can use this method for any items for which you would like to do processing without those other facilities.
     * Derived classes should call through to the base class for it to perform the default menu handling.
     *
     * @param item The menu item that was selected.
     * @return menuItem
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Listens to which item the user has selected, then it moves to that
     * fragment once pressed
     *
     * @param item - Which item the user has pressed
     * @return boolean true
     */
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
        } else if (id == R.id.nav_askQuestion)
        {
            fragmentTransaction.replace(R.id.content, new EmailCrossRoads()).addToBackStack(getString(R.string.tag)).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Creating one fragment transaction that allows the user to transfer to a new fragment
     *
     * @return fragmentTransaction
     */
    private FragmentTransaction getFragmentTransaction()
    {
        final android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        return fragmentTransaction;
    }

    /**
     * Setting all of the images and widgets in the top header of the navigation bar
     * These include, the profile image, name, email, logout, view profile and edit profile buttons.
     *
     * @param navigationView - The navigation bar that is being activated, where the widgets belong
     */
    private void navigationButtonActions(NavigationView navigationView)
    {
        // Getting all of the widget ids
        View headerView = navigationView.getHeaderView(0);
        final TextView navigationName = headerView.findViewById(R.id.navigationName);
        TextView navigationEmail = headerView.findViewById(R.id.navigationEmail);
        ImageView viewProfile = headerView.findViewById(R.id.imageViewProfile);
        ImageView editProfile = headerView.findViewById(R.id.imageEditPen);
        ImageView logout = headerView.findViewById(R.id.imageLogout);
        profileImage = headerView.findViewById(R.id.navigationImage);

        // Reading from the users table to get the current users details
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

        // Set email
        navigationEmail.setText(auth.getCurrentUser().getEmail());

        // View profile button
        viewProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getFragmentTransaction().replace(R.id.content, new ViewProfileFragment()).addToBackStack(getString(R.string.tag)).commit();
                onBackPressed();
            }
        });

        // Edit profile button
        editProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getFragmentTransaction().replace(R.id.content, new EditProfileFragment()).addToBackStack(getString(R.string.tag)).commit();
                onBackPressed();
            }
        });

        // Logout button
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

        // Change profile picture
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
     * Checking the users permission that they selected, accept or deny to access the users current
     * location
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
                // Set the location granted to true
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
     * Displays the content to the user and navigates them to the correct
     * page dependant on the options they have selected, if advertiser, courier or both
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

        if (user != null)
        {
            // Send the user to the relevant page dependant upon their settings.
            databaseReferenceUsersTable.child(user).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.exists())
                    {
                        boolean advertiser = dataSnapshot.child(getString(R.string.advertiser_lower)).getValue(boolean.class);
                        boolean courier = dataSnapshot.child(getString(R.string.courier_lower)).getValue(boolean.class);

                        // Selected advertiser
                        if (advertiser == true && courier == false)
                        {
                            getFragmentTransaction().replace(R.id.content, new PostAnAdvertFragment()).commit();
                        }
                        // Selected courier
                        else if (advertiser == false && courier == true)
                        {
                            getFragmentTransaction().replace(R.id.content, new FindAJobFragment()).commit();
                        }
                        // Selected both
                        else if (advertiser == true && courier == true)
                        {
                            getFragmentTransaction().replace(R.id.content, new ViewProfileFragment()).commit();
                        }
                    } else
                    {
                        startActivity(new Intent(getApplicationContext(), CreateProfileActivity.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        } else
        {
            startActivity(new Intent(this, LoginActivity.class));
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationButtonActions(navigationView);
    }

    /**
     * Return if the users location permission has been accepted or denied
     *
     * @return
     */
    public boolean getLocationPermissionGranted()
    {
        return locationPermissionGranted;
    }

    /**
     * Checks user is connected to Wifi or Mobile data, they are then prompted to turn them on
     */
    public void wifiCheck()
    {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();

        if (!wifi.isWifiEnabled() && !isConnected)
        {
            Toast.makeText(this, "Please Turn Wifi On Or Mobile Data.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Create and display a new progress dialog
     *
     * @param progressDialog - the progress dialog that is to be created
     * @param message        - The message to be displayed on the progress dialog
     */
    public void displayNewProgressDialog(ProgressDialog progressDialog, String message)
    {
        progressDialog.setMessage(message);
        progressDialog.create();
        progressDialog.show();
    }

    /**
     * Dismiss the dialog, remove from the screen
     *
     * @param progressDialog - The progress dialog that is to be dismissed
     */
    public void dismissDialog(ProgressDialog progressDialog)
    {
        progressDialog.dismiss();
    }
}