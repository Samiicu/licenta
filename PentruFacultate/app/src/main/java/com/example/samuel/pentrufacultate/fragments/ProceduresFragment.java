package com.example.samuel.pentrufacultate.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.adapters.AdapterForCreateProcedure;
import com.example.samuel.pentrufacultate.adapters.AdapterForDisplayProcedure;
import com.example.samuel.pentrufacultate.models.ProcedureModel;
import com.example.samuel.pentrufacultate.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProceduresFragment extends Fragment {
    private static final String TAG = "APP_LOG_display";
    AdapterForDisplayProcedure adapterForDisplayProcedures;
    private ArrayList<ProcedureModel> mProcedures;
    private String uidCurrentUser;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mProcedures = new ArrayList<>();
        adapterForDisplayProcedures = new AdapterForDisplayProcedure(getContext(), mProcedures);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        uidCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mCurrentUserDatabaseProcedures = mDatabase.child("procedures").child(uidCurrentUser);

        mCurrentUserDatabaseProcedures.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e("Count ", "" + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    mProcedures.add(ProcedureModel.fromJson((String) postSnapshot.getValue()));
                    Log.d(TAG, "onDataChange: " + postSnapshot.getValue());

//
                }
                Log.d(TAG, "onViewCreated: " + mProcedures.size());
                adapterForDisplayProcedures.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return inflater.inflate(R.layout.fragment_display_procedures, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.display_all_procedure);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d(TAG, "onViewCreated: " + recyclerView);
        Log.d(TAG, "onViewCreated: " + mProcedures.size());
//        adapter.setClickListener(this);
        recyclerView.setAdapter(adapterForDisplayProcedures);
    }
}
