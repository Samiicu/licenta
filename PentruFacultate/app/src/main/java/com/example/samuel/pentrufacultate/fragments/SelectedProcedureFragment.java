package com.example.samuel.pentrufacultate.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.adapters.AdapterForDisplayProcedure;
import com.example.samuel.pentrufacultate.adapters.AdapterForDisplaySteps;
import com.example.samuel.pentrufacultate.models.ProcedureModel;
import com.example.samuel.pentrufacultate.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class SelectedProcedureFragment extends Fragment {
    private static final String TAG = "APP_LOG_display";
    AdapterForDisplaySteps adapterForDisplaySteps;
    ProcedureModel mProcedure;

    private String uidCurrentUser;
    private User currentUser;
    TextView procedureName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mProcedure = ProcedureModel.fromJson(bundle.getString("ProcedureToDisplayJSON"));
        uidCurrentUser = bundle.getString("userUid");
        adapterForDisplaySteps=new AdapterForDisplaySteps(getContext(),mProcedure.getSteps());
        return inflater.inflate(R.layout.fragment_procedure_with_steps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        procedureName=view.findViewById(R.id.procedure_name);
        procedureName.setText(mProcedure.getName());
        RecyclerView recyclerView = view.findViewById(R.id.display_all_steps);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d(TAG, "onViewCreated: " + recyclerView);
//        adapter.setClickListener(this);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        recyclerView.setAdapter(adapterForDisplaySteps);
    }
}
