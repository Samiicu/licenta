package com.example.samuel.pentrufacultate.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.adapters.AdapterForCreateProcedure;
import com.example.samuel.pentrufacultate.models.ProcedureModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.samuel.pentrufacultate.models.StringHelper.*;

public class CreateProcedureFragment extends Fragment {


    TextInputEditText nameText, procedureStepText;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mProceduresDatabase = mDatabase.child("procedures");
    Button addNewInputButton, saveProcedure;
    private final static String TAG = "CREATE_PROCDURE";
    HashMap<String, String> inputData = new HashMap<>();
    ProcedureModel procedure;
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
        saveProcedure = view.findViewById(R.id.save_procedure);
        nameText = view.findViewById(R.id.name_edit_text);

        //////////////
        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.input_steps_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        adapter = new AdapterForCreateProcedure(getContext(), inputData);
//        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

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

                procedure = new ProcedureModel(procedureName, String.valueOf(System.currentTimeMillis()), dataStorageSteps);
                Log.d(TAG, "prepare_to_save " + procedure.getSteps().size());
                mProceduresDatabase.child(userUid).child(procedure.getName()).setValue(procedure.toJson());


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
