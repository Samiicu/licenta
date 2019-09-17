package com.example.samuel.pentrufacultate.activities;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.fragments.ConfigurationFragment;
import com.example.samuel.pentrufacultate.fragments.CreateProcedureFragment;
import com.example.samuel.pentrufacultate.fragments.AllProceduresDisplayFragment;
import com.example.samuel.pentrufacultate.models.User;
import com.example.samuel.pentrufacultate.network.LoginHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import barcode.BarcodeReaderFragment;

import static com.example.samuel.pentrufacultate.models.StringHelper.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BarcodeReaderFragment.BarcodeReaderListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String LOGIN_WITH_CREDENTIALS = "LOGIN_WITH_CREDENTIALS";
    private DrawerLayout drawer;
    private TextView mEmailDispaly, mUsernameDisplay;
    private LinearLayout displayProfile;
    private static String uidCurrentUser;
    private User currentUser;
    private FirebaseAuth auth;
    private String EXTRA_EMAIL = "email";
    private String EXTRA_PASSWORD = "password";
    private static String LOGGED = "LOGGED";
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");

        super.onCreate(savedInstanceState);
        checkForAutoLogin(getIntent());

        setContentView(R.layout.activity_main);
//        if (!LoginHelper.isLogged()) {
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//            return;
//        }

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
            setTitle("Proceduri");
//            Toolbar toolbarTitle = getTitle()findViewById(R.id.toolbar);
//            toolbarTitle.setTitle("Proceduri");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AllProceduresDisplayFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_procedures);
        }


    }

    private void updateProfileData(User currentUser, Application application) {

        NavigationView navigationView = findViewById(R.id.nav_view);
        LinearLayout linearLayout = (LinearLayout) navigationView.getHeaderView(0);
        mUsernameDisplay = linearLayout.findViewById(R.id.username_id);
        mEmailDispaly = linearLayout.findViewById(R.id.email_id);
        try {
            mUsernameDisplay.setText(currentUser.getUsername());
        } catch (Exception e) {
        }
        ;
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
        Toolbar toolbarTitle = findViewById(R.id.toolbar);
        switch (menuItem.getItemId()) {
            case R.id.nav_procedures:
                toolbarTitle.setTitle("Proceduri");
                Fragment displayProceduresFragment = new AllProceduresDisplayFragment();
                Bundle bundleDisplay = new Bundle();
                bundleDisplay.putString(USER_UID_EXTRA, uidCurrentUser);
                displayProceduresFragment.setArguments(bundleDisplay);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, displayProceduresFragment).commit();
                break;
            case R.id.nav_createProcedure:
                toolbarTitle.setTitle("O procedura noua");
                Fragment createFragment = new CreateProcedureFragment();
                Bundle bundleCreate = new Bundle();
                bundleCreate.putString(USER_UID_EXTRA, uidCurrentUser);
                createFragment.setArguments(bundleCreate);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, createFragment).addToBackStack("tag").commit();
                break;
            case R.id.nav_configuration:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ConfigurationFragment()).commit();
                break;
            case R.id.nav_read_qr_recipe:
                BarcodeReaderFragment readerFragment = BarcodeReaderFragment.newInstance(true, false, View.VISIBLE);
                readerFragment.setListener(this);
                FragmentManager supportFragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, readerFragment);
                fragmentTransaction.commitAllowingStateLoss();
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

    public static String getUserUid() {
        return uidCurrentUser;
    }

    private void checkForAutoLogin(Intent receivedIntent) {
        if (receivedIntent != null) {
            Log.i(TAG, "receivedIntent!=null: ");
            Log.i(TAG, "checkForAutoLogin: " + receivedIntent.getAction());
            if (receivedIntent.getAction().equals(LOGIN_WITH_CREDENTIALS) && !LoginHelper.isLogged()) {
//                Log.i(TAG, "checkForAutoLogin: email: "+receivedIntent.getStringExtra(EXTRA_EMAIL)+" pswd: "+receivedIntent.getStringExtra(EXTRA_PASSWORD));
                setProgressDialog();
                auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(receivedIntent.getStringExtra(EXTRA_EMAIL),
                        receivedIntent.getStringExtra(EXTRA_PASSWORD)).addOnSuccessListener(MainActivity.this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.i(TAG, "onSuccess: ");
                        Toast.makeText(getApplicationContext(), "Login with success!", Toast.LENGTH_SHORT).show();
                        LoginHelper.loggedIn();
                        hideDialog();
                    }


                }).addOnFailureListener(MainActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: ");
                        hideDialog();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                        return;
                    }
                });
            } else if (receivedIntent.getAction().equals(LOGGED)) {
                LoginHelper.loggedIn();
            } else {

                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }
        }
    }


    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Logging ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(ll);

        alertDialog = builder.create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            alertDialog.getWindow().setAttributes(layoutParams);
        }
    }

    private void hideDialog() {
        if (alertDialog != null) alertDialog.dismiss();
    }

    @Override
    public void onScanned(Barcode barcode) {
        Log.i(TAG, "onScanned: " + barcode.rawValue);
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {
        for (Barcode barcode : barcodes
        ) {
            Log.i(TAG, "onScannedMultiple: " + barcode.rawValue);
        }
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {
        Log.i(TAG, "onBitmapScanned: ");
    }

    @Override
    public void onScanError(String errorMessage) {
        Log.e(TAG, "onScanError: " + errorMessage);
    }

    @Override
    public void onCameraPermissionDenied() {
        Log.e(TAG, "onCameraPermissionDenied: ");
    }
}