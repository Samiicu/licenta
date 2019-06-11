package com.example.samuel.pentrufacultate;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samuel.pentrufacultate.fragments.ConfigurationFragment;
import com.example.samuel.pentrufacultate.fragments.CreateProcedure;
import com.example.samuel.pentrufacultate.fragments.ProceduresFragment;
import com.example.samuel.pentrufacultate.models.User;
import com.example.samuel.pentrufacultate.network.LoginHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.example.samuel.pentrufacultate.network.StringHelper.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Main Activity";
    private DrawerLayout drawer;
    private TextView mEmailDispaly, mUsernameDisplay;
    private LinearLayout displayProfile;
    private String uidCurrentUser;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!LoginHelper.isLogged()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        uidCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mCurrentUserDatabaseInfo = mDatabase.child("users").child(uidCurrentUser);

        mCurrentUserDatabaseInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue(User.class).getUsername());
                currentUser = dataSnapshot.getValue(User.class);
                updateProfileData(currentUser, getApplication());
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        usernameDisplay = ;


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProceduresFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_procedures);
        }


    }

    private void updateProfileData(User currentUser, Application application) {

        NavigationView navigationView = findViewById(R.id.nav_view);
        LinearLayout linearLayout = (LinearLayout) navigationView.getHeaderView(0);
        mUsernameDisplay = linearLayout.findViewById(R.id.username_id);
        mEmailDispaly = linearLayout.findViewById(R.id.email_id);
        mUsernameDisplay.setText(currentUser.getUsername());
        mEmailDispaly.setText(currentUser.getEmail());
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {


            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_procedures:
                Fragment displayProceduresFragment = new ProceduresFragment();
                Bundle bundleDisplay = new Bundle();
                bundleDisplay.putString(USER_UID_EXTRA,uidCurrentUser);
                displayProceduresFragment.setArguments(bundleDisplay);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,displayProceduresFragment).commit();
                break;
            case R.id.nav_createProcedure:
                Fragment createFragment = new CreateProcedure();
                Bundle bundleCreate = new Bundle();
                bundleCreate.putString(USER_UID_EXTRA, uidCurrentUser);
                createFragment.setArguments(bundleCreate);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, createFragment).addToBackStack("tag").commit();
                break;
            case R.id.nav_configuration:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ConfigurationFragment()).commit();
                break;
            case R.id.nav_logout:
                LoginHelper.logOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Share!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_send:
                Toast.makeText(this, "Send!", Toast.LENGTH_SHORT).show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}