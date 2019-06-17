package com.example.samuel.pentrufacultate.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.adapters.AdapterForCreateProcedure;
import com.example.samuel.pentrufacultate.models.ProcedureModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.samuel.pentrufacultate.network.StringHelper.*;

public class CreateProcedure extends Fragment {


    TextInputEditText nameText, procedureStepText;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mProceduresDatabase = mDatabase.child("procedures");
    Button addNewInputButton, saveProcedure;
    private final static String TAG = "CREATE_PROCDURE";
    ArrayList<String> defaultSteps = new ArrayList<>();
    public   static ArrayList<TextInputEditText> textStorega= new ArrayList<>();
    ProcedureModel procedure;
    String userUid;
    AdapterForCreateProcedure customAdapter;
    static int numberOfCurrentSteps;
    ListView inputsListView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        defaultSteps.add("Pasul 1");
        defaultSteps.add("Pasul 2");
        defaultSteps.add("Pasul 3");

        numberOfCurrentSteps = defaultSteps.size();
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
        inputsListView = view.findViewById(R.id.input_steps_list_view);
        customAdapter = new AdapterForCreateProcedure(getContext(), R.layout.item_step, defaultSteps);

//        adapter.setClickListener(this);
        inputsListView.setAdapter(customAdapter);

        ///////////



        addNewInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++numberOfCurrentSteps;
                Log.d(TAG, "Number of current step: "+numberOfCurrentSteps);
                defaultSteps.add("Pasul " + numberOfCurrentSteps);
                customAdapter.notifyDataSetChanged();
                //adapter.notifyItemInserted(numberOfCurrentSteps-1);
            }
        });
        saveProcedure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> dataStorageSteps = new ArrayList<>();

                String procedureName = nameText.getText().toString();
                Log.d(TAG, "Number of Steps: "+numberOfCurrentSteps);
                for (int id = 1; id <= numberOfCurrentSteps; id++) {
                    procedureStepText = (TextInputEditText) textStorega.get(id-1);
                    try {
                        String stepTextData = procedureStepText.getText().toString();
                        dataStorageSteps.add(stepTextData);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Null step " + id);
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
