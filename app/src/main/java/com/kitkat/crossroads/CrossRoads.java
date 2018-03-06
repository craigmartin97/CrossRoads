package com.kitkat.crossroads;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
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

import com.google.firebase.auth.FirebaseAuth;
import com.kitkat.crossroads.Account.LoginActivity;
import com.kitkat.crossroads.Profile.ViewProfileFragment;

public class CrossRoads extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

//    private PopupWindow popupWindow;
//    private LayoutInflater layoutInflater;
    private FirebaseAuth auth;
//    private RelativeLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();

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


        //////////////////////////////////////////


        View headerview = navigationView.getHeaderView(0);
        ImageView viewProfile = (ImageView) headerview.findViewById(R.id.imageViewProfile);
        ImageView editProfile = (ImageView) headerview.findViewById(R.id.imageEditPen);
        ImageView logout = (ImageView) headerview.findViewById(R.id.imageLogout);

        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentTransaction().replace(R.id.content, new ViewProfileFragment()).commit();
                onBackPressed();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentTransaction().replace(R.id.content, new EditProfileFragment()).commit();
                onBackPressed();
            }
        });

//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.popup_logout ,null);
//                relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
//                popupWindow = new PopupWindow(container, 400, 400, true);
//                popupWindow.showAtLocation(relativeLayout, Gravity.CENTER,500,500);
//
//                container.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        popupWindow.dismiss();
//                        return true;
//                    }
//                });
//            }
//        });



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
                AlertDialog.Builder builder = new AlertDialog.Builder(CrossRoads.this);
                View mView = getLayoutInflater().inflate(R.layout.popup_logout,null);
                TextView text = (TextView) mView.findViewById(R.id.logoutText);
                Button logoutButton = (Button) mView.findViewById(R.id.logoutButton);
                Button cancelButton = (Button) mView.findViewById(R.id.cancelButton);

                logoutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        auth.signOut();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), FindAJobFragment.class));
                    }
                });

                builder.setView(mView);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (id == R.id.nav_findAJob)
        {
            fragmentTransaction.replace(R.id.content, new FindAJobFragment()).commit();
        }
        else if (id == R.id.nav_postAnAdvert)
        {
            fragmentTransaction.replace(R.id.content, new PostAnAdvertFragment()).commit();
        }
        else if (id == R.id.nav_myAdverts) {
        }
        else if (id == R.id.nav_myJobs) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public FragmentTransaction getFragmentTransaction()
    {
        final android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        return fragmentTransaction;
    }
}
