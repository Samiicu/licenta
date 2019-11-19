package com.example.samuel.pentrufacultate.fragments;

import android.content.Intent;
import android.media.browse.MediaBrowser;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.samuel.pentrufacultate.activities.MainActivity;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.adapters.AdapterForCreateProcedure;
import com.example.samuel.pentrufacultate.models.RecipeModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.samuel.pentrufacultate.models.StringHelper.*;

public class AddNewRecipe extends Fragment {


    private static final String ACTION_SHOW_RECIPES ="show_recipes" ;
    TextInputEditText nameText, procedureStepText;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference mProceduresDatabase = mDatabase.child("users_data").child("recipes");
    Button addNewInputButton, saveProcedure;
    private final static String TAG = "CREATE_PROCDURE";
    HashMap<String, String> inputData = new HashMap<>();
    RecipeModel procedure;
    String userUid;
    AdapterForCreateProcedure adapter;
    int numberOfCurrentSteps;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        inputData.put("Pasul 1", "");
        inputData.put("Pasul 2", "");
        inputData.put("Pasul 3", "");


        numberOfCurrentSteps = inputData.size();
        userUid = getArguments().getString(USER_UID_EXTRA);
        return inflater.inflate(R.layout.fragment_create_procedure, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        addNewInputButton = view.findViewById(R.id.add_new_input);
        saveProcedure = view.findViewById(R.id.save_recipe);
        nameText = view.findViewById(R.id.name_edit_text);

        //////////////
        // set up the RecyclerView
        RecyclerView inputRecipesRecyclerView = view.findViewById(R.id.input_steps_recycler_view);
//        RecyclerView.ItemDecoration itemDecoration= new RecyclerView.ItemDecoration;itemDecoration.onDraw();
        inputRecipesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
            @Override
            public void onItemsAdded(@NonNull RecyclerView recyclerView, int positionStart, int itemCount) {
                super.onItemsAdded(recyclerView, positionStart, itemCount);
                inputRecipesRecyclerView.scrollToPosition(positionStart);
                inputRecipesRecyclerView.requestChildFocus(recyclerView.getChildAt(positionStart),recyclerView.getFocusedChild());
            }
        });
        inputRecipesRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        adapter = new AdapterForCreateProcedure(getContext(), inputData);
//        adapter.setClickListener(this);
        inputRecipesRecyclerView.setAdapter(adapter);
//        inputRecipesRecyclerView.getLayoutManager()


        ///////////


        addNewInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++numberOfCurrentSteps;
                inputData.put("Pasul " + numberOfCurrentSteps, "");
                adapter.notifyItemInserted(numberOfCurrentSteps - 1);


            }
        });

        saveProcedure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    ArrayList<String> dataStorageSteps = new ArrayList<>();

                    String procedureName = nameText.getText().toString();
                    for (String key : inputData.keySet()) {

                        Log.d(TAG, "Keys order: " + key);

                    }
                    for (int id = 1; id <= numberOfCurrentSteps; id++) {
                        String key = "Pasul " + id;
                        Log.d(TAG, "Values " + inputData.get(key));

                        String stepTextData = inputData.get(key);
                        if (!stepTextData.equals("")) {
                            dataStorageSteps.add(stepTextData);
                        }
                    }

                    procedure = new RecipeModel(procedureName, String.valueOf(System.currentTimeMillis()), dataStorageSteps);
                    Log.d(TAG, "prepare_to_save " + procedure.getSteps().size());
                    mProceduresDatabase.child(userUid).child(procedure.getTitle()).setValue(procedure.toJson());
                    Intent mMenuIntent = new Intent(getContext(), MainActivity.class);
                    mMenuIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mMenuIntent.setAction(ACTION_SHOW_RECIPES);
                    startActivity(mMenuIntent);
                } catch (Exception e) {
                    Log.e(TAG, "saveProcedure onClick: ", e);
                }
            }
        });
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

}
