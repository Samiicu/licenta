package com.example.samuel.pentrufacultate.activities;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.samuel.pentrufacultate.fragments.AddNewRecipeFragment;
import com.example.samuel.pentrufacultate.fragments.AddShoppingListFragment;
import com.example.samuel.pentrufacultate.fragments.DisplayAllRecipesFragment;
import com.example.samuel.pentrufacultate.fragments.CheckShoppingListFragment;

import com.example.samuel.pentrufacultate.managers.DataManager;
import com.example.samuel.pentrufacultate.managers.SyncProductsInformationJobService;
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

import java.util.concurrent.TimeUnit;

import static com.example.samuel.pentrufacultate.models.StringHelper.REQUEST_CODE_QR_READER;
import static com.example.samuel.pentrufacultate.models.StringHelper.RESULT_QR_READER;
import static com.example.samuel.pentrufacultate.models.StringHelper.RESULT_QR_READER_SUCCESS;
import static com.example.samuel.pentrufacultate.models.StringHelper.USER_UID_EXTRA;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {
    private static final String TAG = StringHelper.getTag(MainActivity.class, null);
    private static final String ACTION_SHOW_RECIPES = "show_recipes";


    private static final String TAG_DISPLAY_RECIPES_FRAGMENT = "display_recipes_fragment";
    private static final String TAG_CREATE_NEW_RECIPE_FRAGMENT = "create_new_recipe_fragment";
    private static final String TAG_ADD_SHOPPING_LIST_FRAGMENT = "add_shopping_list";
    private static final String TAG_CHECK_SHOPPING_LIST_FRAGMENT = "check_shopping_list";
    private static final int SYNC_MINIMUM_LATENCY = 3 * 1000;
    private static final int SYNC_MAXIMUM_DELAY = 6 * 1000;
    private static final long SYNC_MINIMUM_PERIOD = TimeUnit.HOURS.toMillis(24);


    public static Fragment mCurrentFragment;
    public FragmentManager mFragmentManager;
    private DrawerLayout drawer;
    MenuItem shoppingListSettings;
    MenuItem sendShoppingListMenuButton, checkTheShoppingListMenuButton;
    private TextView mEmailDisplay, mUsernameDisplay;
    Toolbar toolbar;
    ImageButton mShoppingListButton;
    //    private static String uidCurrentUser;
    private FirebaseAuth auth;
    DatabaseReference mDatabase;
    DatabaseReference mLoadRecipeReference;
    DataManager mDataManager;
    NavigationView navigationView;

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
            checkForUpdates();
            FirebaseAuth.getInstance().getCurrentUser().getUid();
            setContentView(R.layout.activity_main);
            mFragmentManager = getSupportFragmentManager();
            mFragmentManager.addOnBackStackChangedListener(this);
            mDataManager = DataManager.getInstance(this);
            mDatabase = FirebaseDatabase.getInstance().getReference();
            updateProfileData(mFireBaseUser);
            toolbar = findViewById(R.id.toolbar);
            mShoppingListButton = toolbar.findViewById(R.id.shopping_list);
            mShoppingListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayAddShoppingList();
                }
            });
            setSupportActionBar(toolbar);


            drawer = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            shoppingListSettings = navigationView.getMenu().getItem(3);
            sendShoppingListMenuButton = shoppingListSettings.getSubMenu().getItem(1);
            checkTheShoppingListMenuButton = shoppingListSettings.getSubMenu().getItem(0);
            sendShoppingListMenuButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", "");
                    smsIntent.putExtra("sms_body", StringHelper.prepareShoppingListForSms(mDataManager.getSelectedRecipeTitle(), mDataManager.getCurrentShoppingList()));
                    startActivity(smsIntent);
                    return false;
                }
            });

            checkTheShoppingListMenuButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    displayCheckTheShoppingListFragment();
                    return false;
                }
            });
            shoppingListSettings.setVisible(false);
            navigationView.setNavigationItemSelectedListener(this);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();


            if (savedInstanceState == null) {
                setTitle("Toate retetele tale");
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


    private void updateProfileData(FirebaseUser currentUser) {
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
            case R.id.nav_read_qr_recipe:
                startActivityForResult(new Intent(this, DecoderActivity.class), REQUEST_CODE_QR_READER);
                break;
            case R.id.nav_logout:
                auth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayCreateNewRecipe() {
        Fragment addNewRecipe = new AddNewRecipeFragment();
        Bundle bundleCreate = new Bundle();
        bundleCreate.putString(USER_UID_EXTRA, mDataManager.getCurrentUserUid());
        addNewRecipe.setArguments(bundleCreate);
        mFragmentManager.beginTransaction().replace(R.id.fragment_container, addNewRecipe, TAG_CREATE_NEW_RECIPE_FRAGMENT).addToBackStack(TAG_CREATE_NEW_RECIPE_FRAGMENT).commit();
        mCurrentFragment = addNewRecipe;
    }

    private void displayAddShoppingList() {
        Fragment addShoppingListFragment = new AddShoppingListFragment();
        Bundle bundleCreate = new Bundle();
        bundleCreate.putString(USER_UID_EXTRA, mDataManager.getCurrentUserUid());
        addShoppingListFragment.setArguments(bundleCreate);
        mFragmentManager.beginTransaction().replace(R.id.fragment_container, addShoppingListFragment, TAG_ADD_SHOPPING_LIST_FRAGMENT).addToBackStack(TAG_ADD_SHOPPING_LIST_FRAGMENT).commit();
        mCurrentFragment = addShoppingListFragment;
    }

    private void displayCheckTheShoppingListFragment() {
        Fragment checkShoppingListFragment = new CheckShoppingListFragment();
        mFragmentManager.beginTransaction().replace(R.id.fragment_container, checkShoppingListFragment, TAG_CHECK_SHOPPING_LIST_FRAGMENT).addToBackStack(TAG_CHECK_SHOPPING_LIST_FRAGMENT).commit();
        mCurrentFragment = checkShoppingListFragment;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        Log.i(TAG, "onAttachFragment: ");
        super.onAttachFragment(fragment);
    }

    private void displayAllRecipes() {
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
            Fragment displayProceduresFragment = new DisplayAllRecipesFragment();
            Bundle bundleDisplay = new Bundle();
            bundleDisplay.putString(USER_UID_EXTRA, mDataManager.getCurrentUserUid());
            displayProceduresFragment.setArguments(bundleDisplay);
            mCurrentFragment = displayProceduresFragment;
            mFragmentManager.beginTransaction().replace(R.id.fragment_container, displayProceduresFragment, TAG_DISPLAY_RECIPES_FRAGMENT).commit();
        }
    }


    private void checkForUpdates() {
        SharedPreferences generalSharedPref = getSharedPreferences("General", MODE_PRIVATE);
        long lastUpdate = generalSharedPref.getLong("last_update_of_products", 0);
        long currentTime = System.currentTimeMillis();
        if (lastUpdate + SYNC_MINIMUM_PERIOD < currentTime) {
            ComponentName componentName = new ComponentName(this, SyncProductsInformationJobService.class);
            JobInfo jobInfo = new JobInfo.Builder(StringHelper.SYNC_PRODUCTS_DATA_JOB_ID, componentName)
                    .setMinimumLatency(SYNC_MINIMUM_LATENCY)
                    .setOverrideDeadline(SYNC_MAXIMUM_DELAY)
                    .build();
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            int resultCode = jobScheduler.schedule(jobInfo);
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d(TAG, "Job scheduled!");
            } else {
                Log.d(TAG, "Job not scheduled");
            }
            generalSharedPref.edit().putLong("last_update_of_products", currentTime).apply();
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
                        navigationView.getMenu().getItem(0).setChecked(false);
                        toolbar.setSubtitle(mDataManager.getSelectedRecipeTitle());
                        shoppingListSettings.setVisible(false);
                        mShoppingListButton.setVisibility(View.VISIBLE);
                        break;
                    case TAG_CREATE_NEW_RECIPE_FRAGMENT:
                        shoppingListSettings.setVisible(false);
                        navigationView.getMenu().getItem(0).setChecked(false);
                        toolbar.setTitle("Creezi o reteta noua");
                        mShoppingListButton.setVisibility(View.GONE);
                        break;
                    case TAG_ADD_SHOPPING_LIST_FRAGMENT:
                        shoppingListSettings.setVisible(true);
                        navigationView.getMenu().getItem(0).setChecked(false);
                        toolbar.setTitle("Lista de cumparaturi pentru:");
                        toolbar.setSubtitle(mDataManager.getSelectedRecipeTitle());
                        mShoppingListButton.setVisibility(View.GONE);
                        break;
                }
            } else {
                shoppingListSettings.setVisible(false);
                navigationView.getMenu().getItem(0).setChecked(true);
                toolbar.setTitle("Toate retetele tale");
                toolbar.setSubtitle("");
                mShoppingListButton.setVisibility(View.GONE);
            }
        } else {
            shoppingListSettings.setVisible(false);
            navigationView.getMenu().getItem(0).setChecked(true);
            toolbar.setTitle("Toate retetele tale");
            toolbar.setSubtitle("");
            mShoppingListButton.setVisibility(View.GONE);
        }
    }
}
