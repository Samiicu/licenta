package com.example.samuel.pentrufacultate.activities;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.fragments.AddNewRecipe;
import com.example.samuel.pentrufacultate.fragments.AllProceduresDisplayFragment;
import com.example.samuel.pentrufacultate.fragments.ConfigurationFragment;
import com.example.samuel.pentrufacultate.managers.DataManager;
import com.example.samuel.pentrufacultate.models.QrLoader;
import com.example.samuel.pentrufacultate.models.RecipeModel;
import com.example.samuel.pentrufacultate.models.StringHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.samuel.pentrufacultate.models.StringHelper.REQUEST_CODE_QR_READER;
import static com.example.samuel.pentrufacultate.models.StringHelper.RESULT_QR_READER;
import static com.example.samuel.pentrufacultate.models.StringHelper.RESULT_QR_READER_SUCCESS;
import static com.example.samuel.pentrufacultate.models.StringHelper.USER_UID_EXTRA;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {
    private static final String TAG = StringHelper.getTag(MainActivity.class, null);
    private static final String ACTION_SHOW_RECIPES = "show_recipes";


    private static final String TAG_DISPLAY_RECIPES_FRAGMENT = "display_recipes_fragment";
    private static final String TAG_CREATE_NEW_RECIPE_FRAGMENT = "create_new_recipe_fragment";


    public static Fragment mCurrentFragment;
    public FragmentManager mFragmentManager;
    private DrawerLayout drawer;
    private TextView mEmailDisplay, mUsernameDisplay;
    Toolbar toolbar;
    ImageButton mShoppingListButton;
    //    private static String uidCurrentUser;
    private FirebaseAuth auth;
    DatabaseReference mDatabase;
    DatabaseReference mLoadRecipeReference;
    DataManager mDataManager;

    FirebaseUser mFireBaseUser;

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");

        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFireBaseUser != null) {
            checkTheCredentials();
            FirebaseAuth.getInstance().getCurrentUser().getUid();
            setContentView(R.layout.activity_main);
            mFragmentManager = getSupportFragmentManager();
            mFragmentManager.addOnBackStackChangedListener(this);
            mDataManager = DataManager.getInstance(this);
            mDatabase = FirebaseDatabase.getInstance().getReference();
//            uidCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

            updateProfileData(mFireBaseUser, getApplication());

            toolbar = findViewById(R.id.toolbar);
            mShoppingListButton = toolbar.findViewById(R.id.shopping_list);
            setSupportActionBar(toolbar);


            drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();


            if (savedInstanceState == null) {
                setTitle("Toate retetele tale");
//            Toolbar toolbarTitle = getTitle()findViewById(R.id.toolbar);
//            toolbarTitle.setTitle("Proceduri");
                displayAllRecipes();
                navigationView.setCheckedItem(R.id.nav_procedures);
            }
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, "onNewIntent: ");

        super.onNewIntent(intent);
        if (intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_SHOW_RECIPES)) {
                displayAllRecipes();

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (StringHelper.REQUEST_CODE_QR_READER == requestCode) {
            if (resultCode == RESULT_QR_READER_SUCCESS) {
                if (QrLoader.isValidQrData(data)) {

                    mLoadRecipeReference = mDatabase.child(QrLoader.getRecipeUri(data));
                    mLoadRecipeReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            RecipeModel loadedRecipe = RecipeModel.fromJson((String) dataSnapshot.getValue());
                            Log.i(TAG, "onDataChange: ");
                            QrLoader.loadRecipe(loadedRecipe, mDatabase, mDataManager.getCurrentUserUid());
                            mLoadRecipeReference.removeEventListener(this);
                            mLoadRecipeReference = null;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            mLoadRecipeReference.removeEventListener(this);
                            mLoadRecipeReference = null;
                        }
                    });
                }
                Log.i(TAG, "onActivityResult: " + data.getStringExtra(RESULT_QR_READER));
            }
        }
    }


    private void updateProfileData(FirebaseUser currentUser, Application application) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        LinearLayout linearLayout = (LinearLayout) navigationView.getHeaderView(0);
        mUsernameDisplay = linearLayout.findViewById(R.id.username_id);
        mEmailDisplay = linearLayout.findViewById(R.id.email_id);
        mUsernameDisplay.setText(currentUser.getDisplayName());
        mEmailDisplay.setText(currentUser.getEmail());
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isLastFragment()) {
                moveTaskToBack(true);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_procedures:
                displayAllRecipes();
                break;
            case R.id.nav_createProcedure:
                displayCreateNewRecipe();
                break;
            case R.id.nav_configuration:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ConfigurationFragment()).commit();
                break;
            case R.id.nav_read_qr_recipe:
                startActivityForResult(new Intent(this, DecoderActivity.class), REQUEST_CODE_QR_READER);
                break;
            case R.id.nav_logout:
                auth.signOut();
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

    private void displayCreateNewRecipe() {
//        Toolbar toolbarTitle = findViewById(R.id.toolbar);
//        toolbarTitle.setTitle("O procedura noua");
        Fragment addNewRecipe = new AddNewRecipe();
        Bundle bundleCreate = new Bundle();
        bundleCreate.putString(USER_UID_EXTRA, mDataManager.getCurrentUserUid());
        addNewRecipe.setArguments(bundleCreate);
        mFragmentManager.beginTransaction().replace(R.id.fragment_container, addNewRecipe, TAG_CREATE_NEW_RECIPE_FRAGMENT).addToBackStack(TAG_CREATE_NEW_RECIPE_FRAGMENT).commit();
        mCurrentFragment = addNewRecipe;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        Log.i(TAG, "onAttachFragment: ");
        super.onAttachFragment(fragment);
    }

    private void displayAllRecipes() {
//        Toolbar toolbarTitle = findViewById(R.id.toolbar);
//        toolbarTitle.setTitle("Retetele tale");
        Fragment findFragment = mFragmentManager.findFragmentByTag(TAG_DISPLAY_RECIPES_FRAGMENT);
        if (findFragment != null) {
            for (int i = 0; i < mFragmentManager.getBackStackEntryCount(); ++i) {
                mFragmentManager.popBackStack();
            }

            if (mCurrentFragment != findFragment) {
                mFragmentManager.beginTransaction().remove(mCurrentFragment).commit();
            }
            mCurrentFragment = findFragment;
        } else {
            Fragment displayProceduresFragment = new AllProceduresDisplayFragment();
            Bundle bundleDisplay = new Bundle();
            bundleDisplay.putString(USER_UID_EXTRA, mDataManager.getCurrentUserUid());
            displayProceduresFragment.setArguments(bundleDisplay);
            mCurrentFragment = displayProceduresFragment;
            mFragmentManager.beginTransaction().replace(R.id.fragment_container, displayProceduresFragment, TAG_DISPLAY_RECIPES_FRAGMENT).commit();
        }
    }

    private void checkTheCredentials() {
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    protected void onResumeFragments() {
        Log.i(TAG, "onResumeFragments: ");
        super.onResumeFragments();
    }

    private boolean isLastFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        return fragmentManager.getBackStackEntryCount() == 0;
    }

    @Override
    public void onBackStackChanged() {
        Log.i(TAG, "onBackStackChanged: ");
        int fragmentCount = mFragmentManager.getBackStackEntryCount();
        if (fragmentCount != 0) {
            String fragmentName = mFragmentManager.getBackStackEntryAt(fragmentCount - 1).getName();
            if (fragmentName != null) {
                switch (fragmentName) {
                    case StringHelper.TAG_DISPLAY_ONE_RECIPE_FRAGMENT:
                        toolbar.setTitle("Pasii retetei");
                        mShoppingListButton.setVisibility(View.VISIBLE);
                        break;
                    case TAG_CREATE_NEW_RECIPE_FRAGMENT:
                        toolbar.setTitle("Creezi o reteta noua");
                        mShoppingListButton.setVisibility(View.GONE);
                        break;
                }
            } else {
                toolbar.setTitle("Toate retetele tale");
                mShoppingListButton.setVisibility(View.GONE);
            }
        } else {
            toolbar.setTitle("Toate retetele tale");
            mShoppingListButton.setVisibility(View.GONE);
        }
    }
}
