package com.kitkat.crossroads;

import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kitkat.crossroads.Account.LoginActivity;
import com.kitkat.crossroads.Profile.ViewProfileFragment;

import java.io.File;
import java.io.IOException;

public class CrossRoads extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String TAG = "ViewProfileActivity";

    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference storageReference;
    private DatabaseReference myRef;
    private String userID;
    private ImageView profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);










        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("users");
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getFragmentTransaction().replace(R.id.content, new FindAJobFragment()).commit();

        navigationButtonActions(navigationView);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (id == R.id.nav_findAJob)
        {
            fragmentTransaction.replace(R.id.content, new FindAJobFragment()).commit();
        } else if (id == R.id.nav_postAnAdvert)
        {
            fragmentTransaction.replace(R.id.content, new PostAnAdvertFragment()).commit();
        } else if (id == R.id.nav_myAdverts)
        {

        } else if (id == R.id.nav_myJobs)
        {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    customToastMessage("Successfully signed in with: " + user.getEmail());
                } else
                {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    customToastMessage("Successfully signed out.");
                }
            }
        };

        myRef.child(userID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String name = dataSnapshot.child("name").getValue(String.class);
                Log.d(TAG, "Name Is: " + name);

                navigationName.setText(name);
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
                getFragmentTransaction().replace(R.id.content, new ViewProfileFragment()).commit();
                onBackPressed();
            }
        });



        editProfile.setOnClickListener(new View.OnClickListener()
        {


            @Override
            public void onClick(View v)
            {
                getFragmentTransaction().replace(R.id.content, new EditProfileFragment()).commit();
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
    }

    private void customToastMessage(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}
