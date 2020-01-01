package com.example.samuel.pentrufacultate.managers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samuel.pentrufacultate.adapters.AdapterForDisplayRecipes;
import com.example.samuel.pentrufacultate.adapters.SwipeToDeleteCallback;
import com.example.samuel.pentrufacultate.models.RecipeModel;
import com.example.samuel.pentrufacultate.products.storage.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

public class DataManager {
    private static final String ROOT_DATA_BASE_REF = "root_data_base";
    private static final String USER_RECIPES_DATA_BASE_REF = "recipes_data_base";
    private static final String PATH_USERS_DATA = "users_data";
    private static final String PATH_RECIPES = "recipes";
    private static volatile DataManager dataManagerInstance;

    private ArrayList<RecipeModel> mRecipesData;
    private AdapterForDisplayRecipes mAdapterForDisplayRecipes;
    private HashMap<String, DatabaseReference> firebaseReferences;
    //    private DatabaseReference mDatabase;

    private DatabaseHelper databaseHelper;
    private DatabaseReference mLoadRecipeDatabase;
    //    private DatabaseReference mCurrentUserDatabaseProcedures;
    private FirebaseUser currentUser;
    private RecyclerView mLayoutDisplayAllRecipes;
    private ChildEventListener mChildEventListenerRecips;
    private CharSequence selectedRecipeTitle;

    public static DataManager getInstance(Context context) {
        if (dataManagerInstance == null) {
            dataManagerInstance = new DataManager();
            dataManagerInstance.databaseHelper = new DatabaseHelper(context);
            dataManagerInstance.firebaseReferences = new HashMap<>();
            dataManagerInstance.firebaseReferences.put(ROOT_DATA_BASE_REF, FirebaseDatabase.getInstance().getReference());
            dataManagerInstance.currentUser = FirebaseAuth.getInstance().getCurrentUser();
            dataManagerInstance.firebaseReferences.put(USER_RECIPES_DATA_BASE_REF, dataManagerInstance.
                    firebaseReferences.get(ROOT_DATA_BASE_REF).
                    child(PATH_USERS_DATA).child(PATH_RECIPES).child(dataManagerInstance.getCurrentUserUid()));
//            dataManagerInstance.mDatabase = FirebaseDatabase.getInstance().getReference();
//            dataManagerInstance.mCurrentUserDatabaseProcedures = dataManagerInstance.mDatabase.
//                    child(PATH_USERS_DATA).child(PATH_RECIPES).child(dataManagerInstance.getCurrentUserUid());
            dataManagerInstance.mRecipesData = new ArrayList<>();
            dataManagerInstance.mAdapterForDisplayRecipes = new AdapterForDisplayRecipes(context, dataManagerInstance.mRecipesData);

        }
        return dataManagerInstance;
    }

    //private constructor.
    private DataManager() {
    }

    private static void getInstance() {
        //Double check locking pattern
        if (dataManagerInstance == null) { //Check for the first time

            synchronized (DataManager.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (dataManagerInstance == null) dataManagerInstance = new DataManager();
            }
        }

    }

    public static int getIndexOfRecipe(String receivedProcedureTitle) {
        for (int i = 0; i < dataManagerInstance.mRecipesData.size(); i++) {
            if (dataManagerInstance.mRecipesData.get(i).getTitle().equals(receivedProcedureTitle)) {
                return i;
            }
        }
        return -1;
    }

    public RecipeModel getRecipeWithTitle(String receivedProcedureTitle) {
        for (RecipeModel recipe : this.mRecipesData) {
            if (recipe.getTitle().equals(receivedProcedureTitle)) {
                return recipe;
            }

        }
        return new RecipeModel("", "", new ArrayList<>());
    }


    public void addListenerForDbRecipes() {
        // create and add the eventChildListener just once per DataManagerInstance
        if (this.mChildEventListenerRecips == null) {
            // create
            mChildEventListenerRecips = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    RecipeModel receivedProcedure = RecipeModel.fromJson((String) dataSnapshot.getValue());
                    dataManagerInstance.mRecipesData.add(receivedProcedure);
                    dataManagerInstance.mAdapterForDisplayRecipes.notifyItemInserted(dataManagerInstance.mRecipesData.indexOf(receivedProcedure));
                    Log.i(TAG, "onChildAdded: " + dataSnapshot);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.i(TAG, "onChildChanged: " + dataSnapshot);
                    final RecipeModel receivedProcedure = RecipeModel.fromJson((String) dataSnapshot.getValue());
                    for (RecipeModel procedureModel : dataManagerInstance.mRecipesData
                    ) {
                        if (procedureModel.getTitle().equals(receivedProcedure.getTitle())) {
                            int positionBeforeRemoving = dataManagerInstance.mRecipesData.indexOf(procedureModel);
                            dataManagerInstance.mRecipesData.remove(procedureModel);
                            dataManagerInstance.mAdapterForDisplayRecipes.notifyItemRemoved(positionBeforeRemoving);
                            dataManagerInstance.mRecipesData.add(receivedProcedure);
                            dataManagerInstance.mAdapterForDisplayRecipes.notifyItemInserted(dataManagerInstance.mRecipesData.indexOf(receivedProcedure));
                        }
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Log.i(TAG, "onChildRemoved: " + dataSnapshot);
                    RecipeModel receivedProcedure = RecipeModel.fromJson((String) dataSnapshot.getValue());
                    int removedIndex = getIndexOfRecipe(receivedProcedure.getTitle());
                    if (removedIndex != -1) {
                        dataManagerInstance.mRecipesData.remove(removedIndex);
                        dataManagerInstance.mLayoutDisplayAllRecipes.removeViewAt(removedIndex);
                        dataManagerInstance.mAdapterForDisplayRecipes.notifyItemRemoved(removedIndex);
                        dataManagerInstance.mAdapterForDisplayRecipes.notifyItemRangeChanged(removedIndex, dataManagerInstance.mRecipesData.size());
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.i(TAG, "onChildMoved: " + dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            // add
            dataManagerInstance.firebaseReferences.get(USER_RECIPES_DATA_BASE_REF).addChildEventListener(mChildEventListenerRecips);
        }

    }

    public String getCurrentUserUid() {
        return dataManagerInstance.currentUser.getUid();
    }

    public AdapterForDisplayRecipes getAdapterForDisplayRecipes() {
        return dataManagerInstance.mAdapterForDisplayRecipes;
    }

    public RecyclerView addLayoutForDisplayAllRecipes(RecyclerView recyclerViewListOfRecipes, Context context) {

        this.mLayoutDisplayAllRecipes = recyclerViewListOfRecipes;
        this.mLayoutDisplayAllRecipes.refreshDrawableState();
        this.mLayoutDisplayAllRecipes.setLayoutManager(new LinearLayoutManager(context));
        DividerItemDecoration itemDecor = new DividerItemDecoration(context, VERTICAL);
        this.mLayoutDisplayAllRecipes.addItemDecoration(itemDecor);
        this.mLayoutDisplayAllRecipes.setAdapter(this.mAdapterForDisplayRecipes);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(context));
        itemTouchHelper.attachToRecyclerView(this.mLayoutDisplayAllRecipes);
        return this.mLayoutDisplayAllRecipes;

    }

    public void removeRecipeFromDatabase(RecipeModel recipe) {
        Log.i(TAG, "removeRecipeFromDatabase: ");
        this.firebaseReferences.get(USER_RECIPES_DATA_BASE_REF)
                .child(recipe.getTitle()).removeValue();
    }

    public void saveSelectedRecipeTitle(CharSequence selectedRecipeTitle) {
        this.selectedRecipeTitle = selectedRecipeTitle;
    }

    public CharSequence getSelectedRecipeTitle() {
        return selectedRecipeTitle;
    }

    public DatabaseHelper getLocalDB() {
        return databaseHelper;
    }
}
