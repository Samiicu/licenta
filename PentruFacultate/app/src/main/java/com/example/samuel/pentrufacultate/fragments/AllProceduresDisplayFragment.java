package com.example.samuel.pentrufacultate.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.adapters.AdapterForDisplayRecipes;
import com.example.samuel.pentrufacultate.models.RecipeModel;
import com.example.samuel.pentrufacultate.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

public class AllProceduresDisplayFragment extends Fragment {
    private static final String TAG = AllProceduresDisplayFragment.class.getSimpleName();
    AdapterForDisplayRecipes adapterForDisplayProcedures;
    private ArrayList<RecipeModel> mProcedures;
    private String uidCurrentUser;
    private User currentUser;

    @Override
    public void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.i(TAG, "onHiddenChanged: ");
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        uidCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mCurrentUserDatabaseProcedures = mDatabase.child("users_data").child("recipes").child(uidCurrentUser);

        mCurrentUserDatabaseProcedures.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ProcedureModel receivedProcedure = ProcedureModel.fromJson((String) dataSnapshot.getValue());
                mProcedures.add(receivedProcedure);
                adapterForDisplayProcedures.notifyItemInserted(mProcedures.indexOf(receivedProcedure));
                Log.i(TAG, "onChildAdded: " + dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(TAG, "onChildChanged: " + dataSnapshot);
                final ProcedureModel receivedProcedure = ProcedureModel.fromJson((String) dataSnapshot.getValue());
                for (ProcedureModel procedureModel : mProcedures
                ) {
                    if (procedureModel.getName().equals(receivedProcedure.getName())) {
                        int positionBeforeRemoving = mProcedures.indexOf(procedureModel);
                        mProcedures.remove(procedureModel);
                        adapterForDisplayProcedures.notifyItemRemoved(positionBeforeRemoving);
                        mProcedures.add(receivedProcedure);
                        adapterForDisplayProcedures.notifyItemInserted(mProcedures.indexOf(receivedProcedure));
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onChildRemoved: " + dataSnapshot);
                ProcedureModel receivedProcedure = ProcedureModel.fromJson((String) dataSnapshot.getValue());
                int removedIndex = mProcedures.indexOf(receivedProcedure);
                mProcedures.remove(receivedProcedure);
                adapterForDisplayProcedures.notifyItemRemoved(removedIndex);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(TAG, "onChildMoved: " + dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        adapterForDisplayProcedures = new AdapterForDisplayRecipes(getContext(), mProcedures);


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
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        recyclerView.setAdapter(adapterForDisplayProcedures);
    }
}
